import RPi.GPIO as GPIO
from time import sleep
from time import time

ECHO = 0
TRIG = 0

def welcome():
    return "PiSonar.py | Welcome from python"

def setup(echo, trig):
    ECHO = echo
    TRIG = trig
    
    GPIO.setup(TRIG, GPIO.OUT)
    GPIO.setup(ECHO, GPIO.IN)
    GPIO.output(TRIG, GPIO.LOW)
    
    sleep(0.03)
    
def getCM():
    GPIO.output(TRIG, GPIO.HIGH)
    sleep(0.03)
    GPIO.output(TRIG, GPIO.LOW)
    
    while GPIO.input(ECHO) == GPIO.LOW:
        continue
        
    start = time() * 1000
    while GPIO.input(ECHO) == GPIO.HIGH:
        continue
    totTime = (time() * 1000) - start
    return int(totTime / 58)
    
    