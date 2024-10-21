#include <WiFiManager.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#include <PZEM004Tv30.h>
PZEM004Tv30 pzem1(14, 12); // GPIO14(D5) to Tx PZEM004; GPIO12(D6) to Rx PZEM004
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27, 16, 2);

#define FIREBASE_HOST "test-nodemcu-f12a2-default-rtdb.firebaseio.com" //--> URL address of your Firebase Realtime Database.
#define FIREBASE_AUTH "nQtBnAAIS23vvcSOryCtjk9xAiHrdu9TFhYJs9Vg" //--> Your firebase database secret code.

#define ON_Board_LED 2

int X=0;

String volt = "";
String curr = "";
String pwr = "";
String ener = "";

void setup() {
    WiFi.mode(WIFI_STA); 

    Serial.begin(115200);

    lcd.init();
    lcd.backlight();
    Serial.println("\nSYSTEM IS ONLINE");

    //WiFiManager, Local intialization. Once its business is done, there is no need to keep it around
    WiFiManager wm;
    
    bool res;

    res = wm.autoConnect("IOT-Based Energy Monitoring","admin1234"); // password protected ap

    if(!res) {
        Serial.println("Failed to connect");
        // ESP.restart();
    } 
    else {
        //if you get here you have connected to the WiFi    
        Serial.println("connected...yeey :)");

        //----------------------------------------Firebase Realtime Database Configuration.
        Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
        //----------------------------------------

        
    }

}

void loop() {

    volt = pzem1.voltage();
  //volt = zeroIfNan(volt);
  curr = pzem1.current();
  //curr = zeroIfNan(curr);
  pwr = pzem1.power();
  //pwr = zeroIfNan(pwr);
  ener = pzem1.energy() / 1000; //kwh
  //ener = zeroIfNan(ener);

  delay(1000);
  Serial.println("");
  Serial.println("Voltage        :"+volt+"V");
  Serial.println("Current        :"+curr+"A");
  Serial.println("Power Active   :"+pwr+"W");
  Serial.println("Energy         :"+ener+"kWh");
  Serial.println("---------- END ----------");
  Serial.println("");
  update_lcd_display();

}

void update_lcd_display()
{
  lcd.clear();
  if(X==1)
  {
  lcd.setCursor(0,0);
  lcd.print("V:"+String(volt)+"V");
  Firebase.setString("voltage",volt);
  lcd.setCursor(0,1);
  lcd.print("I:"+String(curr)+"A");
  Firebase.setString("current",curr);
  X=0;
  }
  else
  {
  lcd.setCursor(0,0);
  lcd.print("P:"+String(pwr)+"W");
  Firebase.setString("power",pwr);
  lcd.setCursor(0,1);
  lcd.print("E:"+String(ener)+"kWh");
  Firebase.setString("kwh",ener);
  X=1;
  }
    
}
