package it.unibo.basicweightsensor

import it.unibo.basicdevices.supports.WebSocketValueUpdater
import org.json.JSONObject

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice

class WsWeightSensor(id : String, address : String) : WeightSensor(id) {
	
	val updater = WebSocketValueUpdater<Double>(0.0, address, {s : String -> JSONObject(s).getDouble("data")})
	
	init {
		updater.start()
	}
	
	override fun readWeight() : Double {
		return updater.getActual()
	}
	
}