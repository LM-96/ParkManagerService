package it.unibo.parkingstate

interface StateReader {
	
	fun getOutdoorState() : DoorState
	fun getIndoorState() : DoorState
	fun getFanState() : PowerState
	fun getTemperature() : Double
	fun getTrolleyState() : TrolleyState
}