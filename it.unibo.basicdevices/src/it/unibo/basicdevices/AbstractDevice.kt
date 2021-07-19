package it.unibo.basicdevices

abstract class AbstractDevice(deviceType : DeviceType, name : String) {
	
	private val deviceType = deviceType
	private val id = name
	
	fun getDeviceType() : DeviceType {
		return deviceType
	}
	
	fun getDeviceId() : String {
		return id
	}
	
	override fun equals(other : Any?) : Boolean {
		if(this == other)
			return true
		if(other?.javaClass != javaClass)
			return false
		
		other as AbstractDevice
		
		if(other.deviceType != deviceType)
			return false;
		if(other.id != id)
			return false;
		
		return true;
	}
	
	
	
}