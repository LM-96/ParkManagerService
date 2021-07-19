package it.unibo.basicsonar

import org.json.JSONObject

import it.unibo.basicdevices.supports.WebSocketValueUpdater
import it.unibo.basicdevices.AbstractDevice

class WsSonar(id : String, address : String) : Sonar(id) {
	
	val updater = WebSocketValueUpdater<Int>(0, address, {s : String -> JSONObject(s).getInt("data")})
	
	init {
		updater.start()
	}
	
	override fun readDistance() : Int {
		return updater.getActual()
	}
}