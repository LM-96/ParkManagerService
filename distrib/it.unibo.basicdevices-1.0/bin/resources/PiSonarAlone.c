#include <iostream>
#include <wiringPi.h>
#include <fstream>
#include <cmath>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>


#define TRIG 0
#define ECHO 2

#define DELAY 30

#define CLOSE 18
#define MEDIUM 21
#define FAR 60

#define POS_LEFT 0.055
#define POS_RIGHT 0.24
#define POS_FORWARD 0.14
using namespace std;
/*
g++  SonarAlone.c -l wiringPi -o  SonarAlone
 */
 
 int echo = ECHO;
 int trig = TRIG;
 int delay_t = DELAY;
 
void setup() {
	wiringPiSetup();
	pinMode(trig, OUTPUT);
	pinMode(echo, INPUT);
	//TRIG pin must start LOW
	digitalWrite(trig, LOW);
	delay(30);
}

int getCM() {
	//Send trig pulse
	digitalWrite(trig, HIGH);
	delayMicroseconds(20);
	digitalWrite(trig, LOW);

	//Wait for echo start
	while(digitalRead(echo) == LOW);

	//Wait for echo end
	long startTime = micros();
	while(digitalRead(echo) == HIGH);
	long travelTime = micros() - startTime;

	//Get distance in cm
	int distance = travelTime / 58;

	return distance;
}

int verifyUint(char* str) {
	int pin=0;
	for(int i=0; str[i]!= '\0'; i++){
		if( (str[i] < '0') || (str[i] > '9') ){
			return -1;
		}
	}
	return atoi(str);
}

void handle_sigint(int signo)  {
	if(signo == SIGINT) {
		cout << endl << " SonarAlone | killed " << endl;
		exit(EXIT_SUCCESS);
	}
}

void handle_sigusr1(int signo) {}

void parseArgs(int argc, char* argv[], bool* single, bool* interr) {
	int c, num;
	while((c = getopt(argc, argv, "e:t:d:si")) != -1) {
		switch(c) {
			case 'e':
				num = verifyUint(optarg);
				if(num != -1) echo = num;
				
				break;
				
			case 't':
				num = verifyUint(optarg);
				if(num != -1) trig = num;
				
				break;
				
			case 'd':
				num = verifyUint(optarg);
				if(num != -1) delay_t = num;
				
				break;
				
			case 's':
				*single = true;
				break;
				
			case 'i':
				*interr = true;
				break;

			default:
				break;
		}
	}
	
	/*cout << " SonarAlone | configuration [ PIN_ECHO:" << echo << ", PIN_TRIG:"
				<< trig << ", DELAY:" << delay_t << ", SIGLE_MEASURE:" << *single << ", INTERRUPT:" << *interr << " ] " << endl;*/
}

int main(int argc, char* argv[]) {
	
	int cm;
	char ch;
	bool single = false, interr = false;
	sigset_t signal_set;
	int sig_number;
	
	parseArgs(argc, argv, &single, &interr);
	
	setup();
	signal(SIGINT, handle_sigint);
	if(interr) {
		signal(SIGUSR1, handle_sigusr1);
		sigemptyset (&signal_set);
		sigaddset (&signal_set, SIGUSR1);
	}
	cout << getpid() << endl;
	
	do {
		if(interr) {
			//printf(" SonarAlone | waiting for signal...\n");
			sigwait(&signal_set, &sig_number);
		}
		cout << getCM() << endl;
		delay(delay_t);
	} while(!single);
	
 	return 0;
}