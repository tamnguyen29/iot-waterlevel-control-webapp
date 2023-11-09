#include <Arduino.h>
#include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>

#define WIFI_SSID "Tam_Nguyen"
#define WIFI_PASSWORD "cuongTHINH9999@"

#define CLIENT_ID "65376b4db162737d1b961be4"
#define CLIENT_TYPE "DEVICE"

/*==========================SERVER CONFIG==============================*/
#define SERVER_HOST "192.168.1.6"
#define SERVER_PORT 8080
#define WS_ENDPOINT "/ws/"

#define CONNECTION_ENDPOINT = "/app/member-connect";
#define SEND_WATER_LEVEL_DATA_ENDPOINT "/app/send-water-level-data"
#define DEVICE_PRIVATE_CHANNEL "/client/queue/device-private"
#define DEVICE_ERROR_CHANNEL "/client/queue/error"

#define JSON_DOC_SIZE 2048
/*=====================================================================*/

/*==========================ESP32 CONFIG==============================*/
#define ECHO_PIN 18
#define TRIG_PIN 19
#define INTERVAL 1000
/*=====================================================================*/
WebSocketsClient webSocketClient;
String currentUserId;
String currentControlUnitId;
bool isAllowMeasured;
unsigned long previousMillis;
unsigned long currentMillis;

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
/*=====================================================================*/

/*==========================DEFINE WEBSOCKET FUNCTION===================*/
void sendDataToUser(double waterLevel, String controlUnitId, String userId)
{
  if (!(controlUnitId.equals("NONE") || userId.equals("NONE")))
  {
    String messageData = "[\"SEND\\ndestination:/app/send-water-level-data\\n\\n{\\\"value\\\":\\\"" +
                         String(waterLevel) + "\\\",\\\"controlUnitId\\\":\\\"" +
                         controlUnitId + "\\\",\\\"userId\\\":\\\"" +
                         userId + "\\\"}\\u0000\"]";
    Serial.println("Send data to server: " + messageData);
    webSocketClient.sendTXT(messageData);
  }
}

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
  Serial.println("Socket url: " + socketUrl);
  return socketUrl;
}

void subscribeToChannel(String destination, int subscribeChannelCount)
{
  String msg = "[\"SUBSCRIBE\\nid:sub-" + String(subscribeChannelCount) + "\\ndestination:" + destination + "\\n\\n\\u0000\"]";
  Serial.println(msg);
  webSocketClient.sendTXT(msg);
}

void sendConnectMessage()
{
  String connectMessage = "[\"SEND\\ndestination:/app/member-connect\\n\\n{}\\u0000\"]";
  webSocketClient.sendTXT(connectMessage);
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

JsonObject getJsonObjectMessage(String message)
{
  String jsonStr = extractJsonStringFromMessage(message);
  jsonStr.replace("\\", "");
  Serial.println("Json string: " + jsonStr);
  DynamicJsonDocument doc(JSON_DOC_SIZE);
  deserializeJson(doc, jsonStr);
  return doc.as<JsonObject>();
}

void sendHeartBeatMessage() {
    String message = "[\"SEND\\ndestination:/app/heart-beat\\n\\n{}\\u0000\"]";
    webSocketClient.sendTXT(message);
}

void handleTextMessageReceived(String text)
{
  if (text.startsWith("a[\"\\n\"]"))
  {
    sendHeartBeatMessage();
  }
  else if (text.startsWith("a[\"MESSAGE"))
  {
    JsonObject message = getJsonObjectMessage(text);
    String action = message["action"];

    if (action.equals("USER_CONNECT_TO_DEVICE"))
    {
      setCurrentUserId(message["sender"].as<String>());
      Serial.println("Current USER Id: " + currentUserId);
      //Send to user connect successfully!
    }
    else if (action.equals("USER_DISCONNECT_TO_DEVICE"))
    {
      currentUserId = "NONE";
      isAllowMeasured = false;
      //Send to user disconnect successfully!
      // Stop measurement
    }
    else if (action.equals("START_MEASUREMENT"))
    {
      setCurrentControlUnitId(message["content"].as<String>());
      Serial.println("START_MEASUREMENT");
      Serial.println("CurrentControlId: " + currentControlUnitId);
      isAllowMeasured = true;
    }
    else if (action.equals("STOP_MEASUREMENT"))
    {
      Serial.println("STOP_MEASUREMENT");
      isAllowMeasured = false;
    }
    else if (action.equals("SEND_SIGNAL_CONTROL"))
    {
      Serial.print("SIGNAL CONTROL: ");
      double signal = (double) message["content"];
      Serial.println(String(signal));
    }
  }
}

void webSocketEvent(WStype_t type, uint8_t *payload, size_t length)
{

  switch (type)
  {
  case WStype_DISCONNECTED:
    Serial.printf("WStype_DISCONNECTED run\n");
    break;
  case WStype_CONNECTED:

  {
    Serial.printf("[WSc] Connected to url: %s\n", payload);

    break;
  }

  case WStype_TEXT:
  {
    String text = (char *)payload;
    Serial.println("WStype_TEXT(payload: String): " + text);

    if (payload[0] == 'h')
    {
      Serial.println("Heartbeat!");
    }
    else if (payload[0] == 'o')
    {
      String connectMSG = "[\"CONNECT\\naccept-version:1.1\\nheart-beat:10000,10000\\n\\n\\u0000\"]";
      webSocketClient.sendTXT(connectMSG);
      delay(1000);
    }
    else if (text.startsWith("a[\"\\n\"]"))
    {

      String message = "[\"SEND\\ndestination:/app/ping\\n\\n{}\\u0000\"]";
      webSocketClient.sendTXT(message);
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

void setup()
{
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIG_PIN, OUTPUT);
  wifiSetup();

  isAllowMeasured = false;
  previousMillis = 0;

  setCurrentUserId("NONE");
  setCurrentControlUnitId("NONE");

  webSocketClient.begin(SERVER_HOST, SERVER_PORT, getWSUrl(), "wss");
  webSocketClient.setExtraHeaders();
  webSocketClient.onEvent(webSocketEvent);
}

void loop()
{
  digitalWrite(LED_BUILTIN, WiFi.status() == WL_CONNECTED);
  webSocketClient.loop();

  if (webSocketClient.isConnected())
  {
    if (isAllowMeasured)
    {
      currentMillis = millis();
      if (currentMillis - previousMillis > INTERVAL)
      {
        Serial.print("Water level: ");
        double waterLevel = getCurrentWaterLevelMeasurement();
        Serial.println(waterLevel);
        sendDataToUser(waterLevel, currentControlUnitId, currentUserId);
        previousMillis = millis();
      }
    }
    // else
    // {
    //   Serial.println("STOP");
    // }
  }
}
