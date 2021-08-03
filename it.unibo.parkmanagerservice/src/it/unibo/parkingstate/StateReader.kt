package it.unibo.parkingstate

import it.unibo.parkmanagerservice.bean.DoorState

interface StateReader {
	
	fun getOutdoorState() : DoorState
	fun getIndoorState() : DoorState
	fun getFanState() : PowerState
	fun getTemperature() : Double
	fun getTrolleyState() : TrolleyState
	fun getWeightFromSensor() : Double
	fun getDistanceFromSonar() : Double
}