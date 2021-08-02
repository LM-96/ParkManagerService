package it.unibo.basicdevices

/**
 * An abstract class representing a <b>device</b> with a name and a type.
 *
 * @property deviceType the type of the device (see [it.unibo.basicdevices.DeviceType]
 * @property id the name of the device, also used as an id
 * @constructor create an instance of the abstract device
 */
abstract class AbstractDevice(deviceType : DeviceType, id : String) {
	
	private val deviceType = deviceType
	private val id = id

	/**
	 * A getter method to get the type of the device
	 * @return the [it.unibo.basicdevices.DeviceType] associated to this device
	 */
	fun getDeviceType() : DeviceType {
		return deviceType
	}

	/**
	 * A getter method to get the id of the device
	 * @return the id of the device as a String
	 */
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