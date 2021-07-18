package it.unibo.basicweightsensor

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice

abstract class WeightSensor(id : String) : AbstractDevice(DeviceType.WEIGHT_SENSOR, id){
	
	abstract fun readWeight() : Double
}