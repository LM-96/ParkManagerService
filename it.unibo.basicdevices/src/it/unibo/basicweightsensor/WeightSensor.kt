package it.unibo.basicweightsensor

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice
import java.nio.file.Paths
import java.nio.file.Files
import org.json.JSONObject

abstract class WeightSensor(id : String) : AbstractDevice(DeviceType.WEIGHT_SENSOR, id){
	
	companion object {
		@JvmStatic private val CONFIG_FILE = "configs/weightsensor.conf"
		@JvmStatic private var MIN_WEIGHT = 0.1
		
		init {
			val configFilePath = Paths.get(CONFIG_FILE)
			
			if(!Files.exists(configFilePath)) {
				println("WeightSensor | File ${configFilePath.toAbsolutePath().toString()} does not exists:\n\tit will be used the default configuration for the weight sensor.")
				System.exit(-1)
			}
			println("WeightSensor | Found configuration file at ${configFilePath.toAbsolutePath().toString()}")
			
			val reader = Files.newBufferedReader(Paths.get(CONFIG_FILE))
			println("WeightSensor | Config file opened")
			
			val json = JSONObject(reader.readLine())
			if(json.has("minweight")) {
				MIN_WEIGHT = json.getDouble("minweight")
				println("WeightSensor | Found configuration for min weight [$MIN_WEIGHT]")
			}
			else println("WeightSensor | No configuration for weight sensor: using default value [$MIN_WEIGHT]")
		}
		
		fun getMinWeight() : Double {
			return MIN_WEIGHT
		}
	}
	
	abstract fun readWeight() : Double
}