package it.unibo.parkingstate

import it.unibo.parkingslot.SimpleParkingSlotManager
import it.unibo.parkingslot.ParkingSlotManager

object MockState : StateReader {
	
	private var indoor = DoorState.FREE
	private var outdoor = DoorState.FREE
	private var fan = PowerState.POWERED_OFF
	private var temperature = 0.0
	private var trolley = TrolleyState.IDLE
	
	private var slotMgr : ParkingSlotManager = SimpleParkingSlotManager(1)
	
	init {
		println("\t\tMockstate: init...")
	}
	
	fun setParkingSlotManager(parkingSlotManager : ParkingSlotManager) {
		this.slotMgr = parkingSlotManager
	}
	
	fun getParkingSlotManager() : ParkingSlotManager {
		return slotMgr;
	}
	
	fun setIndoorState(state: DoorState) {
		this.indoor = state
				println("\t\tMockState: set indoor to $indoor")
	}
	
	fun setOutdoorState(state: DoorState) {
		this.outdoor = state
				println("\t\tMockState: set outdoor to $indoor")
	}
	
	fun setFanState(state: PowerState) {
		this.fan = state
	}
	
	fun setTemperature(temperature: Double) {
		this.temperature = temperature
	}
	
	fun setTrolleyState(state: TrolleyState) {
		this.trolley = state
	}
	
	override fun getOutdoorState() : DoorState {
		return outdoor
	}
	
	override fun getIndoorState() : DoorState {
		return indoor
	}
	
	override fun getFanState() : PowerState {
		return fan
	}
	
	override fun getTemperature() : Double {
		return temperature
	}
	
	override fun getTrolleyState() : TrolleyState {
		return trolley
	}
	
	
}