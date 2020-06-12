/**
   BasicHTTPClient.ino

    Created on: 24.05.2015

*/

#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

#include <ESP8266HTTPClient.h>

#define USE_SERIAL Serial

ESP8266WiFiMulti WiFiMulti;

// DHT22 /////////////////////////////////////////

#include <DHT.h>;

//Constants
#define DHTPIN 0     // what pin we're connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302)
DHT dht(DHTPIN, DHTTYPE); //// Initialize DHT sensor for normal 16mhz Arduino
//Variables
float hum;  //Stores humidity value
float temp; //Stores temperature value
void tempSensor(){
    hum = dht.readHumidity();
    temp= dht.readTemperature(false);
    //Print temp and humidity values to serial monitor
    USE_SERIAL.print("Humidity: ");
    USE_SERIAL.print(hum);
    USE_SERIAL.print(" %, Temp: ");
    USE_SERIAL.print(temp);
    USE_SERIAL.println(" Celsius");
}

// Sound Pin ///////////////////////////////////////////////////////////
const int soundPin = 5;
int soundDetect;

void soundsing(){
  soundDetect = soundDetect || !digitalRead(soundPin);
  USE_SERIAL.printf("soundPin: %d\n", soundDetect);
}
// Sensor Pin ///////////////////////////////////////////////////////////
const int sensorPin = 4;
void sensing(){
  int val = digitalRead(sensorPin);
  USE_SERIAL.printf("sensorPin: %d\n", val);
}

void setup() {
  pinMode(soundPin, INPUT);
  pinMode(sensorPin, INPUT);
  dht.begin();

  
  USE_SERIAL.begin(115200);
  // USE_SERIAL.setDebugOutput(true);

  USE_SERIAL.println();
  USE_SERIAL.println();
  USE_SERIAL.println();

  for (uint8_t t = 4; t > 0; t--) {
    USE_SERIAL.printf("[SETUP] WAIT %d...\n", t);
    USE_SERIAL.flush();
    delay(1000);
  }

  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("SINGTEL-096A", "caezoengah");
  
}

unsigned long long passed = 0;
const int timeout = 500;
void loop() {

  if (millis() - passed > timeout){
    wifi();
    passed = millis();
  }
  
  tempSensor();
  soundsing();
  sensing();
}

void wifi(){
  // https://randomnerdtutorials.com/esp8266-nodemcu-http-get-post-arduino/
  // wait for WiFi connection
  if ((WiFiMulti.run() == WL_CONNECTED)) {

    HTTPClient http;

    USE_SERIAL.print("[HTTP] begin...\n");
    // configure traged server and url
    http.begin("http://192.168.1.82:5000/form");      //Specify request destination
    //http.addHeader("Content-Type", "text/plain");  
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
   
    USE_SERIAL.print("[HTTP] POST...\n");
    // start connection and send HTTP header
    //int httpCode = http.GET();
    int httpCode = http.POST("&sound="+String(soundDetect)+
                               "&sensor="+String(digitalRead(sensorPin))+
                               "&humidity="+String(hum)+
                               "&temperature="+String(temp)
                               );   //Send the request
    soundDetect=0;
    // httpCode will be negative on error
    if (httpCode > 0) {
      String payload = http.getString();                  //Get the response payload
      USE_SERIAL.println(httpCode);   //Print HTTP return code
      //USE_SERIAL.println(payload);    //Print request response payload
 
   http.end();  //Close connection
    } else {
      USE_SERIAL.printf("[HTTP] POST failed, error: %s\n", http.errorToString(httpCode).c_str());
    }

    http.end();
  }else{
    USE_SERIAL.printf("[Wifi] Connecting\n");
  }
}
