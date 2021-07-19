package it.unibo.basicthermometer

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice

abstract class Thermometer(id : String) : AbstractDevice(DeviceType.THERMOMETER, id) {
	
	abstract fun readTemperature() : Double
}