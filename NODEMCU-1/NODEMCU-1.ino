#include <WiFiManager.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#define FIREBASE_HOST "iotbasedautomatedsystemcsi-default-rtdb.asia-southeast1.firebasedatabase.app" //--> URL address of your Firebase Realtime Database.
#define FIREBASE_AUTH "JuMhCEXJkDaWcdVixQmJ6GXrxxZ4U5BIn0pqOMaF" //--> Your firebase database secret code.

String room1 = "";
String room2 = "";
String room3 = "";
String faculty = "";
String compLab1 = "";
String compLab2 = "";
String library = "";
String lobby = "";

void setup() {
    WiFi.mode(WIFI_STA);

    Serial.begin(115200);

    //WiFiManager, Local intialization. Once its business is done, there is no need to keep it around
    WiFiManager wm;

    pinMode(D0, OUTPUT);
    pinMode(D1, OUTPUT);
    pinMode(D2, OUTPUT);
    pinMode(D3, OUTPUT);
    pinMode(D4, OUTPUT);
    pinMode(D5, OUTPUT);
    pinMode(D6, OUTPUT);
    pinMode(D7, OUTPUT);
  
    digitalWrite(D0, HIGH);
    digitalWrite(D1, HIGH);
    digitalWrite(D2, HIGH);
    digitalWrite(D3, HIGH);
    digitalWrite(D4, HIGH);
    digitalWrite(D5, HIGH);
    digitalWrite(D6, HIGH);
    digitalWrite(D7, HIGH);

    bool res;
    res = wm.autoConnect("IOT-Based Control System","admin1234"); // password protected ap

    if(!res) {
        Serial.println("Failed to connect");
        // ESP.restart();
    } 
    else {
        //if you get here you have connected to the WiFi    
        Serial.println("connected...yeey :)");

        //----------------------------------------Firebase Realtime Database Configuration.
        Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
        Serial.println("Firebase Begin");
        //----------------------------------------
    }

}

void loop() {

    //-----------------------------------Get LED Status from Firebase
  room1=Firebase.getString("room1"); 
  room2=Firebase.getString("room2"); 
  room3=Firebase.getString("room3"); 
  faculty=Firebase.getString("faculty"); 
  compLab1=Firebase.getString("compLab1"); 
  compLab2=Firebase.getString("compLab2"); 
  library=Firebase.getString("library");
  lobby=Firebase.getString("lobby");  

  //-----------------------------------------For LED
  // handle error  
  if (room1=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D0,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D0,HIGH);  
 }

   if (room2=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D1,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D1,HIGH);  
 }

 if (room3=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D2,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D2,HIGH);  
 }

 if (faculty=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D3,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D3,HIGH);  
 }

 if (compLab1=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D4,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D4,HIGH);  
 }

 if (compLab2=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D5,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D5,HIGH);  
 }

 if (library=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D6,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D6,HIGH);  
 }

 if (lobby=="1") {  
      Serial.print("LED is ON");  
      digitalWrite(D7,LOW);
      if(Firebase.failed()){
        Serial.print("LED Error");
        Serial.println(Firebase.error());    
        return;  
        delay(500); 
        }  
       
  }  
 else{  
   Serial.println("LED is OFF");  
   digitalWrite(D7,HIGH);  
 }
  
    
}
