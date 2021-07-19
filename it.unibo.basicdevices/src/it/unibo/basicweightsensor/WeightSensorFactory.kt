package it.unibo.basicweightsensor

object WeightSensorFactory {
	
	fun create(id : String, type : WeightSensorType, address : String?) : WeightSensor? {
		return when(type) {
			WeightSensorType.VIRTUAL -> if(address != null) WsWeightSensor(id, address) else null
		}
	}
}