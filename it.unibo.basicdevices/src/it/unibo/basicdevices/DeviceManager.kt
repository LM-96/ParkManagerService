package it.unibo.basicdevices

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import org.json.JSONObject

import it.unibo.basicfan.FanFactory
import it.unibo.basicfan.FanType
import it.unibo.basicthermometer.ThermometerFactory
import it.unibo.basicthermometer.ThermometerType
import it.unibo.basicweightsensor.WeightSensorFactory
import it.unibo.basicweightsensor.WeightSensorType
import it.unibo.basicsonar.SonarFactory
import it.unibo.basicsonar.SonarType
import java.io.File

object DeviceManager {
	
	@JvmStatic val CONFIG_FILE = "config.json"
	
	private val devices = mutableMapOf<String, AbstractDevice?>()
	
	init {
		println("DeviceManager | Starting")
		val configFilePath = Paths.get(CONFIG_FILE)
		
		if(!Files.exists(configFilePath)) {
			println("DeviceManager | File ${configFilePath.toAbsolutePath().toString()} does not exists. Cannot start system.")
			System.exit(-1)
		}
		println("DeviceManager | Found configuration file at ${configFilePath.toAbsolutePath().toString()}")
		
		val reader = Files.newBufferedReader(Paths.get(CONFIG_FILE))
		println("DeviceManager | Config file opened")
		
		var line : String? = reader.readLine()
		var json : JSONObject
		
		var device : DeviceType
		var id : String
		
		while(line != null) {
			
			if(!line.startsWith("#")) {
				json = JSONObject(line)
				device = DeviceType.valueOf(json.getString("device").toUpperCase())
				id = json.getString("id")
				
				println("DeviceManager | Found device \"$id\" [$device]")
				
				when(device) {
					DeviceType.FAN -> devices.put(id,
						FanFactory.create(id, FanType.valueOf(json.getString("type").toUpperCase()),
											json.getString("address")
							))
					
					DeviceType.SONAR -> devices.put(id,
						SonarFactory.create(id, SonarType.valueOf(json.getString("type").toUpperCase()),
											json.getString("address")
							))
					
					DeviceType.WEIGHT_SENSOR -> devices.put(id,
						WeightSensorFactory.create(id, WeightSensorType.valueOf(json.getString("type").toUpperCase()),
											json.getString("address")
							))
					
					DeviceType.THERMOMETER -> devices.put(id,
						ThermometerFactory.create(id, ThermometerType.valueOf(json.getString("type").toUpperCase()),
											json.getString("address")
							))
				}
			}
			
			line = reader.readLine()
			
		}
		
		println("DeviceManager | All devices has been loaded")
	}
	
	fun requestDevice(id : String) : AbstractDevice? {
		println("DeviceManager | Requested device with id=\"$id\"")
		return devices.get(id)
	}
	
}