#include <Arduino.h>
#include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>

#define WIFI_SSID "Tam_Nguyen"
#define WIFI_PASSWORD "cuongTHINH9999@"

#define CLIENT_ID "65376b4db162737d1b961be4"
#define CLIENT_TYPE "DEVICE"

/*==========================SERVER CONFIG==============================*/
#define SERVER_HOST "192.168.1.6"
#define SERVER_PORT 8080
#define WS_ENDPOINT "/ws/"
/*==========================WEBSOCKET CONFIG==============================*/
#define CONNECTION_ENDPOINT = "/app/member-connect";
#define SEND_WATER_LEVEL_DATA_ENDPOINT "/app/send-water-level-data"
#define DEVICE_PRIVATE_CHANNEL "/client/queue/device-private"
#define DEVICE_ERROR_CHANNEL "/client/queue/error"

#define JSON_DOC_SIZE 2048
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
unsigned long previousMillis;
unsigned long currentMillis;
String ACTION;
double waterLevel;
double setpoint;
int pwm_Pumper;
double x_control;

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

double getCurrentDistanceMeasurement()
{
  unsigned long duration;

  digitalWrite(TRIG_PIN, 0);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, 1);
  delayMicroseconds(5);
  digitalWrite(TRIG_PIN, 0);

  duration = pulseIn(ECHO_PIN, HIGH);
  return (duration / 2 / 29.412);
}

double getCurrentWaterLevelMeasurement()
{
  return getCurrentDistanceMeasurement();
}

void setCurrentUserId(String userId)
{
  currentUserId = userId;
}
void setCurrentControlUnitId(String controlUnitId)
{
  currentControlUnitId = controlUnitId;
}

String sendDataToUser(String controlUnitId, String userId, bool isFirstSend)
{
  DynamicJsonDocument doc(1024);
  doc["value"] = getCurrentWaterLevelMeasurement();
  doc["controlUnitId"] = controlUnitId;
  doc["userId"] = userId;
  doc["deviceId"] = CLIENT_ID;

  String uri = isFirstSend ? "/send-first-data" : "/send-data";
  String json;
  serializeJson(doc, json);
  Serial.println(json);

  http.begin(wifiClient, "http://" + String(SERVER_HOST) + ":8080/api/device" + uri);
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
    sendDataToUser(currentControlUnitId, currentUserId, true);
    // Send to user connect successfully!
  }
  else if (ACTION.equals("USER_DISCONNECT_TO_DEVICE"))
  {
    setCurrentUserId("NONE");
    setCurrentControlUnitId("NONE");
    isAllowMeasured = false;
    notificationLedESP32();
    // Send to user disconnect successfully!
  }
  else if (ACTION.equals("START_MEASUREMENT"))
  {
    setCurrentControlUnitId(jsonMessage["content"]);
    Serial.println("START_MEASUREMENT");
    Serial.println("CurrentControlId: " + currentControlUnitId);
    isAllowMeasured = true;
  }
  else if (ACTION.equals("STOP_MEASUREMENT"))
  {
    Serial.println("STOP_MEASUREMENT");
    isAllowMeasured = false;
  }
  else if (ACTION.equals("SEND_PUMP_OUT_SIGNAL"))
  {
    int percentage = jsonMessage["content"].as<int>();
    if (percentage == 0)
    {
      stopMotor_OUT();
    }
    else
    {
      int pwm = map(percentage, 0, 100, 0, 255);
      controlWithSpeedMotor_OUT(pwm);
    }
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

void controlMotor_IN(String response)
{
  DynamicJsonDocument doc(512);
  deserializeJson(doc, response);
  JsonObject controlSignalObj = doc.as<JsonObject>();
  x_control = controlSignalObj["xcontrol"];
  setpoint = controlSignalObj["setpoint"];

  pwm_Pumper = map(x_control, 0.03, 0.6 * setpoint, 125, 350);

  Serial.print("X-control: ");
  Serial.println(x_control);
  Serial.print("Setpoint: ");
  Serial.println(setpoint);
  if (pwm_Pumper > 255)
  {
    pwm_Pumper = 255;
  }
  Serial.print("PWN PUMPER: ");
  Serial.println(pwm_Pumper);
  controlWithSpeedMotor_IN(pwm_Pumper);
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
  previousMillis = 0;
  response = "NONE";
  setpoint = 0;
  pwm_Pumper = 0;
  x_control = 0;

  stopMotor_IN();
  stopMotor_OUT();
  setCurrentUserId("NONE");
  setCurrentControlUnitId("NONE");
  message = "";
  waterLevel = getCurrentWaterLevelMeasurement();

  webSocketClient.begin(SERVER_HOST, SERVER_PORT, getWSUrl(), "wss");
  webSocketClient.setExtraHeaders();
  webSocketClient.onEvent(webSocketEvent);
}

void loop()
{
  digitalWrite(LED_BUILTIN, webSocketClient.isConnected());
  webSocketClient.loop();
  if (webSocketClient.isConnected())
  {
    if (isAllowMeasured)
    {
      response = sendDataToUser(currentControlUnitId, currentUserId, false);
      if (!(response.equals("NONE") || response.equals("-1")))
      {
        Serial.println("Response: " + response);
        controlMotor_IN(response);
      }
    }
    else
    {
      stopMotor_IN();
    }
  }
  else
  {
    stopMotor_IN();
    stopMotor_OUT();
  }
}
