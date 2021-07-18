package it.unibo.basicthermometer

import it.unibo.basicdevices.supports.WebSocketValueUpdater
import org.json.JSONObject
import it.unibo.basicdevices.AbstractDevice
import it.unibo.basicdevices.DeviceType

class WsThermometer(id : String, address : String) : Thermometer(id) {
	
	val updater = WebSocketValueUpdater<Double>(0.0, address, {s : String -> JSONObject(s).getDouble("data")})
	
	init {
		updater.start()
	}
	
	override fun readTemperature() : Double {
		return updater.getActual()
	}
}