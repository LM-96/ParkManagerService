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
import it.unibo.basicsonar.Sonar
import it.unibo.basicfan.Fan
import it.unibo.basicweightsensor.WeightSensor
import it.unibo.basicthermometer.Thermometer
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import org.json.JSONException

/**
 * A singleton class used by the others entities to obtain a certain device.
 * This class is thread-safe.
 *
 * The constructor of this singleton parses the JSON configuration file and builds all presented devices.
 * The file must be located in 'configs/devices.conf' and every line contains a
 * JSON entry that represents the device.
 * All entry must have the two keys <b>device</b> and <b>id</b>.
 */
object DeviceManager {
	
	@JvmStatic val CONFIG_FILE = "configs/devices.conf"
	
	private val devices = mutableMapOf<String, AbstractDevice?>()
	private val lock = ReentrantLock()
	
	init {
		lock.lock()
		try {
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
					if(json.has("id")) {
						id = json.getString("id")
						if(json.has("type")) {
							device = DeviceType.valueOf(json.getString("device").toUpperCase())
							println("DeviceManager | Found device \"$id\" [$device]")
						
							when(device) {
								DeviceType.FAN -> devices.put(id, getFan(json))
								DeviceType.SONAR -> devices.put(id, getSonar(json))
								DeviceType.WEIGHT_SENSOR -> devices.put(id, getWeightSensor(json))
								DeviceType.THERMOMETER -> devices.put(id, getThermometer(json))
							}
						} else {
							println("DeviceManager | Configuratio file error: the device with id=$id does not have type")
							System.exit(-1)
						}
					} else {
						println("DeviceManager | Configuration file error: a device does not have the id")
						System.exit(-1)
					}
					
				}
				line = reader.readLine()
				
			}
			
			println("DeviceManager | All devices has been loaded")
		} finally {lock.unlock()}
	}

	/**
	 * This function can be used by the other entities to obtain a certain device from its [id].
	 * Obviously, the requested device must be present in the configuration file with the correct
	 * sintax.
	 * @param id the id of the requested device
	 * @return the requested device if present correctly in the configuration file,
	 * null otherwise
	 */
	fun requestDevice(id : String) : AbstractDevice? {
		lock.lock()
		try {
			println("DeviceManager | Requested device with id=\"$id\"")
			return devices.get(id)
		} finally {lock.unlock()}
		
	}
	
	private fun getFan(json : JSONObject) : Fan? {
		val res = FanFactory.create(json.getString("id"), FanType.valueOf(json.getString("type").toUpperCase()),
									if(json.has("address")) json.getString("address") else null)
		
		if(res != null) println("   -> Fan correcty loaded")
		else println("   -> unable to load Fan [config=${json.toString()}]")
		
		return res
	}
	
	private fun getSonar(json : JSONObject) : Sonar? {
		val res = SonarFactory.create(json.getString("id"),
						SonarType.valueOf(json.getString("type").toUpperCase()),
						if(json.has("address")) json.getString("address") else null,
						if(json.has("echo")) json.getInt("echo") else null,
						if(json.has("trig")) json.getInt("trig") else null)
		
		if(res != null) println("   -> Sonar correcty loaded")
		else println("   -> unable to load Sonar [config=${json.toString()}]")
		
		return res
	}
	
	private fun getWeightSensor(json : JSONObject) : WeightSensor? {
		var sonar : Sonar? = null
		if(json.has("sonar")) {
			var sonarJson = json.getJSONObject("sonar")
			if(sonarJson != null)
				sonar = getSonar(sonarJson)
		}
		
		val res = WeightSensorFactory.create(json.getString("id"),
				WeightSensorType.valueOf(json.getString("type").toUpperCase()),
				if(json.has("address")) json.getString("address") else null, sonar,
				if(json.has("teshold")) json.getInt("teshold") else null)
		
		if(res != null) println("   -> Weight Sensor correcty loaded")
		else println("   -> unable to load Weight Sensor [config=${json.toString()}]")
		
		return res
	}
	
	private fun getThermometer(json : JSONObject) : Thermometer? {
		val res = ThermometerFactory.create(json.getString("id"),
				ThermometerType.valueOf(json.getString("type").toUpperCase()),
				if(json.has("address")) json.getString("address") else null)
		
		if(res != null) println("   -> Thermometer correcty loaded")
		else println("   -> unable to load Thermometer [config=${json.toString()}]")
		
		return res
	}
	
}