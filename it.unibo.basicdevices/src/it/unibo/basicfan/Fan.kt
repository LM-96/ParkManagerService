package it.unibo.basicfan

import it.unibo.basicdevices.AbstractDevice
import it.unibo.basicdevices.DeviceType

abstract class Fan(id : String) : AbstractDevice(DeviceType.FAN, id) {
	
	abstract fun set(newState : FanState)
	abstract fun powerOn()
	abstract fun powerOff()
}