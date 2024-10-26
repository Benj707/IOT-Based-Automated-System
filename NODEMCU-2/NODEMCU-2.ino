#include <WiFiManager.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>
#include <PZEM004Tv30.h>
#include <LiquidCrystal_I2C.h>
#include <NTPClient.h>  // NTP client library
#include <WiFiUdp.h>

PZEM004Tv30 pzem1(14, 12); // GPIO14(D5) to Tx PZEM004; GPIO12(D6) to Rx PZEM004
LiquidCrystal_I2C lcd(0x27, 16, 2); // Set the LCD address to 0x27
#define FIREBASE_HOST "iotbasedautomatedsystemcsi-default-rtdb.asia-southeast1.firebasedatabase.app" //--> URL address of your Firebase Realtime Database.
#define FIREBASE_AUTH "JuMhCEXJkDaWcdVixQmJ6GXrxxZ4U5BIn0pqOMaF" //--> Your firebase database secret code.

float VOLTAGE, CURRENT, POWER, energy1;

int X = 0;
int readingCount = 0; // To keep track of how many readings were taken
int currentMonth = -1; // Track the current month

WiFiUDP ntpUDP;            // Create a UDP object for NTPClient
NTPClient timeClient(ntpUDP, "time.google.com", 28800, 60000); // Set timezone offset and update interval (UTC + 8 hours for timezone)


void setup() {
  Serial.begin(115200);
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SYSTEM STARTED");
  lcd.setCursor(0, 1);
  lcd.print("CONNECT TO WIFI");
  Serial.println("\nSYSTEM STARTED");

  WiFi.mode(WIFI_STA);

  // WiFiManager, Local initialization.
  WiFiManager wm;
  wm.setBreakAfterConfig(true);
  
  bool res;
  res = wm.autoConnect("IOT-Based Energy Monitoring", "admin1234"); // password protected AP

  if (!res) {
    Serial.println("Failed to connect");
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("OOPS! TRY AGAIN");
    delay(3000);
    ESP.restart();
    delay(5000);
  } else {
    Serial.println("connected... yeey :)");
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("CONNECTED...");

    // Firebase Realtime Database Configuration
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    delay(5000);
  }

  // Start NTP Client
  timeClient.begin();

  // Update the NTP client to get the current time
  updateNTPTime();
}

void loop() {

  VOLTAGE = pzem1.voltage();
  VOLTAGE = zeroIfNan(VOLTAGE);
  CURRENT = pzem1.current();
  CURRENT = zeroIfNan(CURRENT);
  POWER = pzem1.power();
  POWER = zeroIfNan(POWER);
  energy1 = pzem1.energy() / 1000; // kWh
  energy1 = zeroIfNan(energy1);
  
  delay(1000);
  
  Serial.printf("Voltage        : %.2f V\n", VOLTAGE);
  Serial.printf("Current        : %.2f A\n", CURRENT);
  Serial.printf("Power Active   : %.2f W\n", POWER);
  Serial.printf("Energy         : %.2f kWh\n", energy1);

  update_lcd_display();

  Firebase.setFloat("voltage", VOLTAGE);
  Firebase.setFloat("kWh", energy1);
  Firebase.setFloat("current", CURRENT);
  Firebase.setFloat("power", POWER);
  

  // Update NTP time at regular intervals
  timeClient.update();
  
  // Retrieve the current month
  int month = getCurrentMonth();

  // Check if the month has changed
  if (month != currentMonth) {
    // The month has changed, upload the data for the previous month
    uploadMonthlyData(currentMonth);

    // Reset accumulators for the new month
    energy1 = 0;
    readingCount = 0;

    // Update the current month
    currentMonth = month;
  }
  else{
    Serial.println("MONTH DOESN'T CHANGE YET");
    
    // Format Firebase path: /sensorData/YYYY-MonthName
    String yearMonth = getFormattedYearMonth(month);

    float energy1Consumed = energy1;
    
    // Upload the monthly data to Firebase
    String energyPath = "/monthlyReading/" + yearMonth + "/totalEnergy";

    // Convert energyPath from String to const char* using .c_str()
    Firebase.setFloat(energyPath.c_str(), energy1Consumed);

    // Check if there was an error
    if (Firebase.failed()) {
      Serial.print("Failed to upload data to Firebase. Reason: ");
      Serial.println(Firebase.error());
    } else {
      Serial.println("Monthly data uploaded successfully to Firebase!");
    }
    Serial.print("Total Energy: ");
    Serial.println(energy1);
    }

  // Delay for 1 minute (60000 milliseconds)
  delay(60000);
}

// Function to update NTP time and retrieve the current month
void updateNTPTime() {
  // Wait for the NTP client to get a valid time
  while (!timeClient.update()) {
    timeClient.forceUpdate();  // Force an update if initial sync fails
    delay(500); // Brief delay to allow the NTP update to happen
  }

  // Get the current month after successful NTP sync
  currentMonth = getCurrentMonth();
}

// Function to retrieve the current month (1-12)
int getCurrentMonth() {
  time_t epochTime = timeClient.getEpochTime(); // Get the epoch time from NTP
  struct tm *timeinfo = localtime(&epochTime);  // Convert to local time
  
  return timeinfo->tm_mon + 1;  // tm_mon is 0-11, so we add 1 to get 1-12
}

// Function to upload the accumulated monthly data to Firebase
void uploadMonthlyData(int month) {
  if (readingCount >= 0) {
    // Calculate the average values for the month
    float energy1Consumed = energy1;

    // Format Firebase path: /sensorData/YYYY-MonthName
    String yearMonth = getFormattedYearMonth(month);

    // Print values for debugging before upload
    Serial.println("Uploading Monthly Data:");
    Serial.print("Year-Month: ");
    Serial.println(yearMonth);
    Serial.print("Total Energy Consumed: ");
    Serial.println(energy1Consumed);

    // Upload the monthly data to Firebase
    String energyPath = "/monthlyReading/" + yearMonth + "/totalEnergy";

    // Convert energyPath from String to const char* using .c_str()
    Firebase.setFloat(energyPath.c_str(), energy1Consumed);

    // Check if there was an error
    if (Firebase.failed()) {
      Serial.print("Failed to upload data to Firebase. Reason: ");
      Serial.println(Firebase.error());
    } else {
      Serial.println("Monthly data uploaded successfully to Firebase!");
    }

    // Reset accumulators after uploading
    energy1 = 0;
    readingCount = 0;
  } else {
    // If no readings were taken, skip upload and log the issue
    Serial.println("No data collected this month. Skipping Firebase upload.");
  }
}

// Function to get formatted year and month (YYYY-MM)
String getFormattedYearMonth(int month) {
  time_t epochTime = timeClient.getEpochTime(); // Get the epoch time from NTP
  struct tm *timeinfo = localtime(&epochTime);  // Convert to local time
  
  char buffer[16];
  strftime(buffer, 16, "%Y-", timeinfo);
  String yearMonth = String(buffer) + (month < 10 ? "0" : "") + String(month);
  Serial.println("Year Month" + yearMonth);
  return yearMonth;
}

void update_lcd_display() {
  lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("V:" + String(VOLTAGE) + "V");
    lcd.setCursor(0, 1);
    lcd.print("E:" + String(energy1) + "kWh");
}

float zeroIfNan(float v) {
  if (isnan(v)) v = 0;
  return v;
}
