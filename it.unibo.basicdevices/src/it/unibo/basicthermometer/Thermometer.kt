package it.unibo.basicthermometer

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice

abstract class Thermometer(id : String) : AbstractDevice(DeviceType.THERMOMETER, id) {
	
	companion object {
		@JvmStatic val CRITICAL_TEMP = 25
	}
	
	abstract fun readTemperature() : Double
}