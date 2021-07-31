#include <SoftwareSerial.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h>

int ECHO = 0;
int TRIG = 0;

char* IDSK = "ARDUINO"

int substringToInt(char* string, int startIndex, int endIndex);
void setup();
int getCM();
	

int main(int argc, char* argv[]) {
	printf("ArduinoSonarAlone | started\n");
	
	Serial.begin(9600);
	
	char* cmd;
	while(!strcmp((cmd = Serial.readString()), "exit")) {
		
		printf("ArduinoSonarAlone | received request [%s]\n", cmd);
		
		if(strcmp(cmd, "w") == 0) {
			printf("   |\n   -> sending welcome message\n");
			Serial.println(IDSK);
		}
		
		else if(strcmp(cmd, "m") == 0) {
			int measure = getCM();
			printf("   |\n   -> sending measure: %d\n". measure);
			Serial.println(measure);
		}
		
		else if(cmd[0] == 'c') {
			int startIndex = 2;
			int endIndex = 0;
			bool valid = true;
			
			for(endIndex = startIndex; endIndex < strlen(cmd) && valid; endIndex++) {
				if(cmd[endIndex] == '-') {
					int pin = substringToInt(cmd, startIndex+1, endIndex);
					if(pin == 0 && cmd[startIndex+1] != '0') {
						printf("   |\n   -> bad configuration ignored\n");
						valid = false;
					}
					else {
						if(cmd[startIndex] == 'e') {
							printf("   |\n   -> ECHO = %d\n", pin);
							ECHO = pin;
						}
						if(cmd[startIndex] == 't') {
							printf("   |\n   -> TRIG = %d\n", pin);
							TRIG = pin;
						}
					}
					
					startIndex = endIndex + 1;
				}
			}
			setup();
		}
		
		else {
			printf("ArduinoSonarAlone | invalid command \"%s\"\n");
	}

	
}

//N.B: endIndex is exclusive.
int substringToInt(char* string, int startIndex, int endIndex) {
	int len = endIndex - startIndex;
	char* number = (char *) malloc(len + 1);
	memccpy(number, startIndex, len);
	number[len] = '\0'
	
	return atoi(nunber);
}

void setup() {
	pinMode(TRIG, OUTPUT);
	pinMode(ECHO, INPUT);
	digitalWrite(TRIG, LOW);
	delay(30);
}

int getCM() {
	digitalWrite(TRIG, HIGH);
	delay(20);
	digitalWrite(TRIG, LOW);
	
	while(digitalRead(ECHO) == LOW);
	
	long startTime = micros();
	while(digitalRead(echo) == HIGH);
	long travelTime = micros() - startTime;

	//Get distance in cm
	return travelTime / 58;
}