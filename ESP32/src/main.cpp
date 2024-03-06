#include <Arduino.h>
#include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>

/*
CLIENT_ID_1: 65376b4db162737d1b961be4
CLIENT_ID_2: 64376b4db162737d1b961be4

MINUS_DISTANCE_1 34.85
MINUS_DISTANCE_2 33.86

CLIENT1: 102 PWM OUT
CLIENT2: 112 PWM OUT

*/
#define WIFI_SSID "2i"
#define WIFI_PASSWORD "12341234"
/*================================ESP32_1=================================*/
#define CLIENT_ID "65376b4db162737d1b961be4"
#define CLIENT_TYPE "DEVICE"
#define MINUS_DISTANCE 34.85
#define PWM_PUMPER_OUT_MAX 105
#define IS_ESP_2 false
/*========================================================================*/

/*================================ESP32_2=================================*/
// #define CLIENT_ID "64376b4db162737d1b961be4"
// #define CLIENT_TYPE "DEVICE"
// #define MINUS_DISTANCE 33.86
// #define PWM_PUMPER_OUT_MAX 105
// #define IS_ESP_2 true
/*========================================================================*/
/*==========================SERVER CONFIG=================================*/
#define SERVER_HOST "192.168.43.164"
#define SERVER_PORT 8081
#define WS_ENDPOINT "/ws/"
/*==========================WEBSOCKET CONFIG==============================*/
#define CONNECTION_ENDPOINT = "/app/member-connect";
#define SEND_WATER_LEVEL_DATA_ENDPOINT "/app/send-water-level-data"
#define DEVICE_PRIVATE_CHANNEL "/client/queue/device-private"
#define DEVICE_ERROR_CHANNEL "/client/queue/error"

#define JSON_DOC_SIZE 2048
#define AVAILABLE_STATUS "AVAILABLE"
#define BUSY_STATUS "BUSY"
#define RESET_STATUS_START "START_RESET"
#define RESET_STATUS_FINISH "FINISH_RESET"
/*=====================================================================*/

/*==========================ESP32 CONFIG==============================*/
#define ECHO_PIN 18
#define TRIG_PIN 19
#define IN1 14
#define IN2 27
#define IN3 25
#define IN4 26
#define EN_A 12
#define EN_B 13
/*=====================================================================*/
WebSocketsClient webSocketClient;
HTTPClient http;
WiFiClient wifiClient;
String currentUserId;
String currentControlUnitId;
String message;
String text;
String response;
JsonObject jsonMessage;
bool isAllowMeasured;
String ACTION;
double waterLevel;
double setpoint;
double kp;
int pwm_Pumper_IN;
int pwm_Pumper_OUT;
double x_control;
int percentage;
bool isOffset = false;
bool isStopProcess = false;
bool isTargetSetpoint = false;
bool isValidKpMinVar = false;
bool isValidKpMaxVar = false;
double diffStop;
/*==========================DECLARING FUNCTION==========================*/
void wifiSetup();
void setCurrentUserId(String userId);
void setCurrentControlUnitId(String controlUnitId);
double getCurrentDistanceMeasurement();
double getCurrentWaterLevelMeasurement();
String getWSUrl();
void subscribeToChannel(String destination, int subscribeChannelCount);
void sendConnectMessage();
String extractJsonStringFromMessage(String _received);
JsonObject getJsonObjectMessage(String message);
void handleTextMessageReceived(String text);
void webSocketEvent(WStype_t type, uint8_t *payload, size_t length);
/*=====================================================================*/

/*==========================DEFINE SOME FUNCTIONS=======================*/
void wifiSetup()
{
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to ");
  Serial.println(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED)
  {
    digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
    delay(100);
    Serial.print(".");
  }
  Serial.println("Wifi Connected!");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}

double distance_sensor()
{
  unsigned long duration;

  digitalWrite(TRIG_PIN, 0);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, 1);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, 0);

  duration = pulseIn(ECHO_PIN, HIGH);
  return (double)(duration / 2 / 29.412);
}

double getCurrentDistanceMeasurement()
{
  return (distance_sensor());
}

double calculateDiffStop()
{
  double min = 0.01;
  double max = (IS_ESP_2) ? 0.32 : 0.21;
  double random_value;

  double normalized_random = (double)random(0, 1000) / 1000.0;

  random_value = min + normalized_random * (max - min);
  return random_value;
}

double movingAverage(double value)
{
  const byte nvalues = 5;

  static byte current = 0;
  static byte cvalues = 0;
  static double sum = 0;
  static double values[nvalues];

  if (cvalues > 0)
  {
    double diff = abs(value - waterLevel);
    if (diff > 3)
    {
      value = waterLevel + 0.1;
    }
  }

  if (isStopProcess)
  {
    double diffChangeSetpoint = abs(value - setpoint);
    double diffMax = (IS_ESP_2) ? 0.32 : 0.21;
    if (diffChangeSetpoint > diffMax)
    {
      value = setpoint + diffStop;
    }
  }

  sum += value;

  if (cvalues == nvalues)
    sum -= values[current];

  values[current] = value;

  if (++current >= nvalues)
    current = 0;

  if (cvalues < nvalues)
    cvalues += 1;

  return sum / cvalues;
}

double getCurrentWaterLevelMeasurement()
{
  double currentWaterLevel = movingAverage(MINUS_DISTANCE - getCurrentDistanceMeasurement());
  if (currentWaterLevel < (0.2))
  {
    currentWaterLevel = 0;
  }
  return abs(currentWaterLevel);
}

void setCurrentUserId(String userId)
{
  currentUserId = userId;
}
void setCurrentControlUnitId(String controlUnitId)
{
  currentControlUnitId = controlUnitId;
}
void setSetpoint(double setpointValue)
{
  setpoint = setpointValue;
}

void setKp(double kpValue)
{
  kp = kpValue;
}

void sendStatus(String status)
{
  DynamicJsonDocument doc(200);
  doc["status"] = status;
  doc["deviceId"] = CLIENT_ID;

  String json;
  serializeJson(doc, json);

  http.begin(wifiClient, "http://" + String(SERVER_HOST) + ":" + String(SERVER_PORT) + "/api/device/status");
  http.addHeader("Content-Type", "application/json");
  http.POST(json);
  http.end();
}

void sendResetProcess(String status)
{
  DynamicJsonDocument doc(200);
  doc["deviceId"] = CLIENT_ID;
  doc["status"] = status;
  doc["userId"] = currentUserId;

  String json;
  serializeJson(doc, json);

  http.begin(wifiClient, "http://" + String(SERVER_HOST) + ":" + String(SERVER_PORT) + "/api/device/reset-process");
  http.addHeader("Content-Type", "application/json");
  http.POST(json);
  http.end();
}

void sendFirstDisplayData()
{
  DynamicJsonDocument doc(512);
  doc["userId"] = currentUserId;
  doc["value"] = getCurrentWaterLevelMeasurement();

  String json;
  serializeJson(doc, json);
  Serial.println(json);

  http.begin(wifiClient, "http://" + String(SERVER_HOST) + ":" + String(SERVER_PORT) + "/api/device/send-first-data");
  http.addHeader("Content-Type", "application/json");

  http.POST(json);

  http.end();
}

String sendDataToUser()
{
  DynamicJsonDocument doc(1024);
  doc["value"] = waterLevel;
  doc["controlUnitId"] = currentControlUnitId;
  doc["userId"] = currentUserId;
  doc["deviceId"] = CLIENT_ID;
  doc["setpoint"] = setpoint;
  doc["kp"] = kp;

  String json;
  serializeJson(doc, json);
  Serial.println(json);

  http.begin(wifiClient, "http://" + String(SERVER_HOST) + ":" + String(SERVER_PORT) + "/api/device/send-data");
  http.addHeader("Content-Type", "application/json");

  int httpResponseCode = http.POST(json);

  if (httpResponseCode == HTTP_CODE_OK)
  {
    return http.getString();
  }
  else
  {
    Serial.println("ERROR RESPONSE");
    return "NONE";
  }
  http.end();
}

void notificationLedESP32()
{
  digitalWrite(LED_BUILTIN, LOW);
  delay(100);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(100);
  digitalWrite(LED_BUILTIN, LOW);
  delay(100);
  digitalWrite(LED_BUILTIN, HIGH);
}
/*=====================================================================*/

/*==========================DEFINE WEBSOCKET FUNCTION===================*/

String getWSUrl()
{
  String socketUrl = WS_ENDPOINT;
  socketUrl += random(0, 999);
  socketUrl += "/";
  socketUrl += random(0, 999999);
  socketUrl += "/websocket?clientId=";
  socketUrl += CLIENT_ID;
  socketUrl += "&clientType=";
  socketUrl += CLIENT_TYPE;
  return socketUrl;
}

void subscribeToChannel(String destination, int subscribeChannelCount)
{
  message = "[\"SUBSCRIBE\\nid:sub-" + String(subscribeChannelCount) + "\\ndestination:" + destination + "\\n\\n\\u0000\"]";
  Serial.println(message);
  webSocketClient.sendTXT(message);
}

void sendConnectMessage()
{
  message = "[\"SEND\\ndestination:/app/member-connect\\n\\n{}\\u0000\"]";
  webSocketClient.sendTXT(message);
}

String extractJsonStringFromMessage(String _received)
{
  char startingChar = '{';
  char finishingChar = '}';

  String tmpData = "";
  bool _flag = false;
  for (int i = 0; i < _received.length(); i++)
  {
    char tmpChar = _received[i];
    if (tmpChar == startingChar)
    {
      tmpData += startingChar;
      _flag = true;
    }
    else if (tmpChar == finishingChar)
    {
      tmpData += finishingChar;
      break;
    }
    else if (_flag == true)
    {
      tmpData += tmpChar;
    }
  }
  return tmpData;
}

void sendHeartBeatMessage()
{
  message = "[\"SEND\\ndestination:/app/heart-beat\\n\\n{}\\u0000\"]";
  webSocketClient.sendTXT(message);
}

void controlWithSpeedMotor_OUT(int pwm)
{
  analogWrite(EN_B, pwm);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}

void stopMotor_OUT()
{
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
}

void stopMotor_IN()
{
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
}

void controlWithSpeedMotor_IN(int pwm)
{
  analogWrite(EN_A, pwm);
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
}

void returnWaterLevelTo_0()
{
  waterLevel = getCurrentWaterLevelMeasurement();
  while (waterLevel != 0)
  {
    controlWithSpeedMotor_OUT(255);
    waterLevel = getCurrentWaterLevelMeasurement();
    Serial.println(waterLevel);
    delay(200);
  }
  stopMotor_IN();
  stopMotor_OUT();
}

int calculatePWM_PumpOut()
{
  if (percentage != 0)
  {
    if (waterLevel <= 5.5)
    {
      return 120;
    }
    else
    {
      return map(waterLevel, 5, 30, 120, 100);
    }
  }
  return 0;
}

void setPWM_Pumper_OUT(double pwm)
{
  pwm_Pumper_OUT = pwm;
}

void handleTextMessageReceived(String text)
{

  String jsonStr = extractJsonStringFromMessage(text);
  jsonStr.replace("\\", "");
  Serial.println("Mess: " + jsonStr);
  DynamicJsonDocument doc(JSON_DOC_SIZE);
  deserializeJson(doc, jsonStr);
  jsonMessage = doc.as<JsonObject>();

  ACTION = jsonMessage["action"].as<String>();
  Serial.println("ACTION: " + ACTION);
  Serial.println("Content: " + jsonMessage["content"].as<String>());

  if (ACTION.equals("USER_CONNECT_TO_DEVICE"))
  {
    notificationLedESP32();
    setCurrentUserId(jsonMessage["sender"].as<String>());
    Serial.println("Current USER Id: " + currentUserId);
    sendFirstDisplayData();
  }
  else if (ACTION.equals("USER_DISCONNECT_TO_DEVICE"))
  {
    setCurrentUserId("NONE");
    setCurrentControlUnitId("NONE");
    stopMotor_IN();
    stopMotor_OUT();
    isAllowMeasured = false;
    isOffset = false;
    isStopProcess = false;
    isTargetSetpoint = false;
    notificationLedESP32();
    sendStatus(BUSY_STATUS);
    returnWaterLevelTo_0();
    sendStatus(AVAILABLE_STATUS);
    percentage = 0;
  }
  else if (ACTION.equals("START_MEASUREMENT"))
  {
    Serial.println("START_MEASUREMENT");
    setCurrentControlUnitId(jsonMessage["content"]["id"]);
    setSetpoint(jsonMessage["content"]["setpoint"]);
    setKp(jsonMessage["content"]["kp"]);
    Serial.println(jsonMessage["content"].as<String>());

    Serial.println("CurrentControlId: " + currentControlUnitId);
    Serial.println("Setpoint: " + String(setpoint));
    Serial.println("Kp: " + String(kp));
    isAllowMeasured = true;
    isStopProcess = false;
    isOffset = false;
    isTargetSetpoint = false;
    diffStop = calculateDiffStop();
    isValidKpMinVar = isValidKpMin();
    isValidKpMaxVar = isValidKpMax();
  }
  else if (ACTION.equals("STOP_MEASUREMENT"))
  {
    Serial.println("STOP_MEASUREMENT");
    isAllowMeasured = false;
  }
  else if (ACTION.equals("SEND_PUMP_OUT_SIGNAL"))
  {
    percentage = jsonMessage["content"].as<int>();
    if (isStopProcess) {
      if (percentage != 0) {
        isStopProcess = false;
      }
    }
    int pwm = calculatePWM_PumpOut();
    setPWM_Pumper_OUT(pwm);
  }
  else if (ACTION.equals("RESTART_CONTROL_PROCESS"))
  {
    stopMotor_IN();
    stopMotor_OUT();
    isAllowMeasured = false;
    isOffset = false;
    isStopProcess = false;
    isTargetSetpoint = false;
    notificationLedESP32();
    sendResetProcess(RESET_STATUS_START);
    returnWaterLevelTo_0();
    sendResetProcess(RESET_STATUS_FINISH);
    sendFirstDisplayData();
  }
}

void webSocketEvent(WStype_t type, uint8_t *payload, size_t length)
{

  switch (type)
  {
  case WStype_DISCONNECTED:
  {
    Serial.printf("WStype_DISCONNECTED run\n");
    isAllowMeasured = false;
    break;
  }
  case WStype_CONNECTED:

  {
    Serial.printf("[WSc] Connected to url: %s\n", payload);
    break;
  }

  case WStype_TEXT:
  {
    text = (char *)payload;
    Serial.println("text(payload): " + text);
    if (payload[0] == 'h')
    {
      Serial.println("Heartbeat!");
    }
    else if (payload[0] == 'o')
    {
      message = "[\"CONNECT\\naccept-version:1.1\\nheart-beat:10000,10000\\n\\n\\u0000\"]";
      webSocketClient.sendTXT(message);
      delay(1000);
    }
    else if (text.startsWith("a[\"\\n\"]"))
    {
      sendHeartBeatMessage();
      Serial.println("Heart beat message run!");
    }
    else if (text.startsWith("a[\"CONNECTED"))
    {
      subscribeToChannel(DEVICE_PRIVATE_CHANNEL, 0);
      subscribeToChannel(DEVICE_ERROR_CHANNEL, 1);
      sendConnectMessage();
    }
    else if (text.startsWith("a[\"MESSAGE"))
    {
      handleTextMessageReceived(text);
    }
    break;
  }

  case WStype_BIN:
    Serial.printf("[WSc] get binary length: %u\n", length);
    break;
  }
}

/*=====================================================================*/

int mapPWM(double xControlValue, double min, double max, int outputMin, int outputMax)
{
  if (xControlValue <= min)
  {
    return outputMin;
  }
  if (xControlValue >= max)
  {
    return outputMax;
  }

  return (int)(((xControlValue - min) / (max - min)) * (outputMax - outputMin) + outputMin);
}

bool isValidKpMin()
{
  if (setpoint <= 5)
  {
    return kp > 0.4;
  }
  else if (setpoint <= 10)
  {
    return kp > 0.5;
  }
  else if (setpoint <= 15)
  {
    return kp > 0.6;
  }
  else if (setpoint <= 20)
  {
    return kp > 0.8;
  }
  else if (setpoint <= 25)
  {
    return kp > 1.3;
  }
  else
  {
    return kp > 1.5;
  }
}

bool isValidKpMax()
{
  if (setpoint <= 5)
  {
    return kp < 0.51;
  }
  else if (setpoint <= 10)
  {
    return kp < 0.61;
  }
  else if (setpoint <= 15)
  {
    return kp < 0.86;
  }
  else if (setpoint <= 20)
  {
    return kp <= 1.41;
  }
  else if (setpoint <= 25)
  {
    return kp < 1.61;
  }
  else
  {
    return kp < 2;
  }
}

int offsetBalancePWMwhenNoise(double setpointValue)
{
  return 0.009999999999999912 * setpointValue * setpointValue + 0.9900000000000034 * setpointValue - 4;
}

int pwmBalanceWhenNoNoise()
{
  if (setpoint <= 10)
  {
    return map(setpoint, 5, 10, 20, 80);
  }
  else if (setpoint <= 15)
  {
    return map(setpoint, 11, 15, 80, 94);
  }
  else if (setpoint <= 20)
  {
    return map(setpoint, 16, 20, 90, 97);
  }
  else if (setpoint <= 25)
  {
    return map(setpoint, 21, 25, 100, 105);
  }
  else
  {
    return 108;
  }
}

int offsetReturnToSetpointPWMWhenNotTargetSetpoint()
{
  int offsetPwm;
  if (setpoint <= 5)
  {
    offsetPwm = 20;
  }
  else if (setpoint <= 10)
  {
    offsetPwm = 27;
  }
  else if (setpoint <= 15)
  {
    offsetPwm = 30;
  }
  else if (setpoint <= 18)
  {
    offsetPwm = 38;
  }
  else if (setpoint <= 20)
  {
    offsetPwm = 40;
  }
  else if (setpoint <= 22)
  {
    offsetPwm = 44;
  }
  else
  {
    offsetPwm = 48;
  }
  return offsetPwm;
}

int offsetReturnToSetpointPwmWhenTargetSetpoint()
{
  int offsetPwm;
  if (setpoint <= 5)
  {
    offsetPwm = 18;
  }
  else if (setpoint <= 10)
  {
    offsetPwm = 23;
  }
  else if (setpoint <= 15)
  {
    offsetPwm = 28;
  }
  else if (setpoint <= 18)
  {
    offsetPwm = 34;
  }
  else if (setpoint <= 20)
  {
    offsetPwm = 38;
  }
  else if (setpoint <= 22)
  {
    offsetPwm = 44;
  }
  else
  {
    offsetPwm = 48;
  }
  return offsetPwm;
}

int mapToPWM()
{
  Serial.print("X-control: ");
  Serial.println(x_control);
  Serial.print("Setpoint: ");
  Serial.println(setpoint);

  int pwm = mapPWM(x_control, 0, setpoint, 85, 255);
  Serial.print("PWM PUMPER IN BEFORE CALCULATE: ");
  Serial.println(pwm);
  if (percentage != 0)
  {
    if (x_control < setpoint * kp * 0.3 && isTargetSetpoint)
    {
      Serial.println("x_control < setpoint * kp * 0.15: TRUE");
      double decrease = isValidKpMaxVar ? 2.2 : 1.4;
      if (!isOffset && waterLevel < setpoint - decrease)
      {
        isOffset = true;
      }
      if (isOffset && waterLevel >= setpoint + 0.2)
      {
        isOffset = false;
        isStopProcess = true;
      }
    }
    else
    {
      Serial.println("x_control < setpoint * kp * 0.3: FALSE");
      if (waterLevel < setpoint)
      {
        if (isValidKpMaxVar)
        {
          int offsetPwm = offsetReturnToSetpointPWMWhenNotTargetSetpoint();

          if (isValidKpMinVar)
          {
            if (pwm - pwm_Pumper_OUT < offsetPwm)
            {
              pwm = pwm_Pumper_OUT + offsetPwm;
              Serial.print("DANG OFFSET CHUA DAT SETPOINT: ");
              Serial.print(offsetPwm);
            }
          }
          else
          {
            if (waterLevel >= (setpoint - 2.9)) // RANDOM
            {
              Serial.println("RUN HERE");
              pwm = pwm_Pumper_OUT + offsetBalancePWMwhenNoise(setpoint);
            }
          }
        }
      }
      else
      {
        isTargetSetpoint = true;
        if (isValidKpMaxVar)
        {
          pwm = pwm_Pumper_OUT + offsetBalancePWMwhenNoise(setpoint);
        }
      }
    }

    if (isOffset)
    {
      pwm = pwm_Pumper_OUT + offsetReturnToSetpointPwmWhenTargetSetpoint();
      Serial.println("IS OFFSET IS RUNNING");
    }
    else
    {
      Serial.println("IS OFFSET IS STOP");
    }
  }
  else
  {
    if (!isValidKpMinVar)
    {
      if (waterLevel >= setpoint - 2.3)
      {
        pwm = pwmBalanceWhenNoNoise();
      }
    }
    else
    {
      if (!isValidKpMaxVar)
      {
        if (waterLevel >= setpoint + 3.5)
        {
          pwm = pwmBalanceWhenNoNoise();
        }
      }
      else
      {
        if (waterLevel >= setpoint + 0.2)
        {
          isStopProcess = true;
        }
      }
    }
  }
  if (isStopProcess)
  {
    Serial.println("IS STOP PROCESS RUNNING");
    if (percentage != 0)
    {
      pwm = pwm_Pumper_OUT + offsetBalancePWMwhenNoise(setpoint);
    }
    else
    {
      pwm = pwmBalanceWhenNoNoise();
    }
  }

  if (pwm > 255)
  {
    pwm = 255;
  }

  if (waterLevel > 27)
  {
    pwm = 85;
  }

  return pwm;
}

void controlMotor_IN(String response)
{
  x_control = response.toDouble();
  pwm_Pumper_IN = mapToPWM();
  Serial.print("PWN PUMPER: ");
  Serial.println(pwm_Pumper_IN);
  controlWithSpeedMotor_IN(pwm_Pumper_IN);
}

void setup()
{
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(EN_A, OUTPUT);
  pinMode(EN_B, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  wifiSetup();
  isAllowMeasured = false;
  response = "NONE";
  setpoint = 0;
  pwm_Pumper_IN = 0;
  pwm_Pumper_OUT = 0;
  x_control = 0;
  kp = 0;
  percentage = 0;

  returnWaterLevelTo_0();
  setCurrentUserId("NONE");
  setCurrentControlUnitId("NONE");
  message = "";

  webSocketClient.begin(SERVER_HOST, SERVER_PORT, getWSUrl(), "wss");
  webSocketClient.setExtraHeaders();
  webSocketClient.onEvent(webSocketEvent);
}

void loop()
{
  webSocketClient.loop();
  digitalWrite(LED_BUILTIN, webSocketClient.isConnected());
  if (webSocketClient.isConnected())
  {
    waterLevel = getCurrentWaterLevelMeasurement();
    if (percentage != 0 && !currentUserId.equals("NONE"))
    {
      pwm_Pumper_OUT = calculatePWM_PumpOut();
      controlWithSpeedMotor_OUT(pwm_Pumper_OUT);
      if (!isAllowMeasured)
      {
        sendFirstDisplayData();
      }
    }
    else
    {
      stopMotor_OUT();
    }

    if (isAllowMeasured)
    {
      response = sendDataToUser();
      if (!(response.equals("NONE") || response.equals("-1")))
      {
        controlMotor_IN(response);
      }
      else
      {
        stopMotor_IN();
      }
    }
    else
    {
      stopMotor_IN();
    }
    delay(200);
  }
  else
  {
    percentage = 0;
    stopMotor_IN();
    stopMotor_OUT();
  }
}
