package it.unibo.basicsonar

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice

abstract class Sonar(id : String) : AbstractDevice(DeviceType.SONAR, id) {
	
	abstract fun readDistance() : Int
}