package it.unibo.parkingslot

interface ParkingSlotManager {
	
	fun getFreeSlot() : Int
	fun occupySlot(slotnum : Int) : String
	fun freeSlot(slotnum : Int)
	fun getSlotState(slotnum : Int) : ParkingSlotState
	
	fun freeSlotByToken(token : String) : Int
	
}