package it.unibo.basicfan

import it.unibo.basicdevices.supports.LockableValue
import it.unibo.basicdevices.supports.WebSocketValueUpdater
import org.json.JSONObject

class WsFan(id: String, address : String) : Fan(id) {
	
	val updater = WebSocketValueUpdater<FanState>(FanState.OFF, address,
		{s : String -> FanState.fromBoolean(JSONObject(s).getBoolean("data"))})
	
	init {
		updater.start()
	}
	
	override fun set(newState : FanState) {
		when(newState) {
			FanState.ON -> updater.say("{\"data\": \"true\"}")
			FanState.OFF -> updater.say("{\"data\": \"false\"}")
		}		
	}
	
	override fun powerOn() {
		updater.say("{\"cmd\": \"true\"}")
	}
	
	override fun powerOff() {
		updater.say("{\"cmd\": \"false\"}")
	}
	
	override fun getState() : FanState {
		return updater.getActual()
	}
	
}