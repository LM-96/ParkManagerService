package it.unibo.basicfan

import it.unibo.basicdevices.supports.LockableValue
import it.unibo.basicdevices.supports.WebSocketValueUpdater
import org.json.JSONObject

class WsFan(id: String, address : String) : Fan(id) {
	
	val updater = WebSocketValueUpdater<FanState>(FanState.OFF, address,
		{s : String -> FanState.valueOf(JSONObject(s).getString("data"))})
	
	init {
		updater.start()
	}
	
	override fun set(newState : FanState) {
		when(newState) {
			FanState.ON -> updater.say("{\"data\": \"ON\"}")
			FanState.OFF -> updater.say("{\"data\": \"OFF\"}")
		}		
	}
	
	override fun powerOn() {
		updater.say("{\"data\": \"ON\"}")
	}
	
	override fun powerOff() {
		updater.say("{\"data\": \"OFF\"}")
	}
	
}