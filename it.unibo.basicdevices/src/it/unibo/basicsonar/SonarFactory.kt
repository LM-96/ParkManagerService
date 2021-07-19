package it.unibo.basicsonar

object SonarFactory {
	
	fun create(id : String, type : SonarType, address : String?) : Sonar? {
		return when(type) {
			SonarType.VIRTUAL -> if(address != null) WsSonar(id, address) else null
		}
	}
}