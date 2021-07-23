package it.unibo.basicsonar

import it.unibo.basicdevices.DeviceType
import it.unibo.basicdevices.AbstractDevice
import java.nio.file.Paths
import java.nio.file.Files
import org.json.JSONObject

abstract class Sonar(id : String) : AbstractDevice(DeviceType.SONAR, id) {
	
	companion object {
		@JvmStatic private val CONFIG_FILE = "configs/sonar.conf"
		@JvmStatic private var MIN_DISTANCE = 1000
		
		init {
			val configFilePath = Paths.get(CONFIG_FILE)
			
			if(!Files.exists(configFilePath)) {
				println("Sonar | File ${configFilePath.toAbsolutePath().toString()} does not exists:\n\tit will be used the default configuration for the sonar.")
				System.exit(-1)
			}
			println("Sonar | Found configuration file at ${configFilePath.toAbsolutePath().toString()}")
			
			val reader = Files.newBufferedReader(Paths.get(CONFIG_FILE))
			println("Sonar | Config file opened")
			
			val json = JSONObject(reader.readLine())
			if(json.has("mindist")) {
				MIN_DISTANCE = json.getInt("mindist")
				println("Sonar | Found configuration for min distance [$MIN_DISTANCE]")
			}
			else println("Sonar | No configuration for min distance: using default value [$MIN_DISTANCE]")
		}
	}
	
	abstract fun readDistance() : Int
}