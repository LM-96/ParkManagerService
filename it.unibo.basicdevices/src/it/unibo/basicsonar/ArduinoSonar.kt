package it.unibo.basicsonar

import jssc.SerialPort
import jssc.SerialPortEventListener
import jssc.SerialPortEvent

class ArduinoSonar(id : String, serialPort : String, echoPin : Int, trigPin : Int) : Sonar(id) {
	
	private val sp = SerialPort(serialPort)
	private val echoPin = echoPin
	private val trigPin = trigPin
	
	init {
		if(!sp.isOpened()) {
			sp.openPort()
			sp.setParams(SerialPort.BAUDRATE_9600,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE)
			sp.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN or SerialPort.FLOWCONTROL_RTSCTS_OUT)
			//sp.addEventListener(ASPortReader(), SerialPort.MASK_RXCHAR)
			
			sp.writeString("whoareyou")
			if(!sp.isOpened() || !sp.readString().contains("ARDUINO")) {
				println("ArduinoSonar | Unable to connect to Arduino")
				System.exit(-1)
			}
			
			sp.writeString("c-e$echoPin-t$trigPin")
			
			if(!sp.readString().equals("y")) {
				println("ArduinoSonar | Unable to configure the port of the sonar")
				System.exit(-1)
			}
				
		}
	}
	
	override fun readDistance() : Int {
		sp.writeString("m")
		return sp.readString().toInt()
	}
	
}

private class ASPortReader() : SerialPortEventListener {
	
	override fun serialEvent(event : SerialPortEvent) {
		println("ArduinoSonar | Event ${event.toString()}")
	}
	
}

