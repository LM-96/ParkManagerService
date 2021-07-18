package it.unibo.basicthermometer

object ThermometerFactory {
	
	fun create(id : String, type : ThermometerType, address : String?) : Thermometer? {
		return when(type) {
			ThermometerType.VIRTUAL -> if(address != null) WsThermometer(id, address) else null
		}
	}
}