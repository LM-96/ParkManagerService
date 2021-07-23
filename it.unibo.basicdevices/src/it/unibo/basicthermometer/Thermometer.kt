package it.unibo.basicthermometer

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice
import java.nio.file.Paths
import java.nio.file.Files
import org.json.JSONObject

abstract class Thermometer(id : String) : AbstractDevice(DeviceType.THERMOMETER, id) {
	
	companion object {
		@JvmStatic private val CONFIG_FILE = "configs/thermometer.conf"
		@JvmStatic private var CRITICAL_TEMP = 25
		@JvmStatic private var POLLING_MSEC : Long = 1000
		
		init {
			val configFilePath = Paths.get(CONFIG_FILE)
			
			if(!Files.exists(configFilePath)) {
				println("Thermometer | File ${configFilePath.toAbsolutePath().toString()} does not exists:\n\tit will be used the default configuration for the thermometer.")
				System.exit(-1)
			}
			println("Thermometer | Found configuration file at ${configFilePath.toAbsolutePath().toString()}")
			
			val reader = Files.newBufferedReader(Paths.get(CONFIG_FILE))
			println("Thermometer | Config file opened")
			
			val json = JSONObject(reader.readLine())
			if(json.has("critical_temperature")) {
				CRITICAL_TEMP = json.getInt("critical_temperature")
				println("Thermometer | Found configuration for critical temperature [$CRITICAL_TEMP]")
			}
			else println("Thermometer | No configuration for critical temperature: using default value [$CRITICAL_TEMP]")
			
			if(json.has("polling_msec")) {
				POLLING_MSEC = json.getLong("polling_msec")
				println("Thermometer | Found configuration for polling time [$POLLING_MSEC]")
			}
			else println("Thermometer | No configuration for polling millis: using default value [$POLLING_MSEC]")
		}
		
		fun getCriticalTemp() : Int {
			return CRITICAL_TEMP
		}
		
		fun getPollingMillis() : Long {
			return POLLING_MSEC
		}
	}
	
	abstract fun readTemperature() : Double
}