package it.unibo.basicweightsensor

import it.unibo.basicsonar.Sonar

class SonarFakeWeightSensor(id : String, sonar : Sonar, treshold : Int) : WeightSensor(id) {
	
	companion object {
		@JvmStatic val EXTIMATED_CAR_WEIGHT = 10000.0
	}
	
	private val sonar = sonar
	private val treshold = treshold
	
	override fun readWeight() : Double {
		if(sonar.readDistance() < treshold)
			return EXTIMATED_CAR_WEIGHT
		else
			return 0.0
	}
	
}