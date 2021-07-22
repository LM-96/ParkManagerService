package it.unibo.basicweightsensor

import it.unibo.basicsonar.Sonar

object WeightSensorFactory {
	
	fun create(id : String, type : WeightSensorType, address : String?, sonar : Sonar?, treshold : Int?) : WeightSensor? {
		
		return when(type) {
			WeightSensorType.VIRTUAL -> if(address != null) WsWeightSensor(id, address) else null
			WeightSensorType.SONAR_FAKE_WS -> if(sonar != null && treshold != null) SonarFakeWeightSensor(id, sonar, treshold) else null
		}
	}
}