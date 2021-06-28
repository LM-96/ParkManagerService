package it.unibo.parkingslot

class SimpleParkingSlotManager(maxSlot : Int) : ParkingSlotManager {
	
	private val slots = Array<ParkingSlotState>(maxSlot){ParkingSlotState.FREE}
	private val tokens = Array<String>(maxSlot){""}
	private val totalSlot = maxSlot
	
	override fun getFreeSlot() : Int {
		for(i in 0..totalSlot) {
			if(slots[i] == ParkingSlotState.FREE) {
				slots[i] = ParkingSlotState.RESERVED
				return i+1
			}
		}
		
		return 0
	}
	
	override fun occupySlot(slotnum : Int) : String {
		slots[slotnum - 1] = ParkingSlotState.OCCUPIED
		tokens[slotnum - 1] = slotnum.toString()
		
		return tokens[slotnum - 1]
	}
	
	override fun freeSlot(slotnum : Int) {
		slots[slotnum - 1] = ParkingSlotState.FREE;
	}
	
	override fun getSlotState(slotnum : Int) : ParkingSlotState {
		return slots[slotnum - 1]
	}
	
	override fun freeSlotByToken(token : String) : Int {
		for(i in 0..totalSlot) {
			if(tokens[i].equals(token)) {
				if(slots[i] == ParkingSlotState.OCCUPIED) {
					slots[i] == ParkingSlotState.FREE
					tokens[i] = ""
					
					return i+1; 
				}
			}
		}
		
		return -1;
	}
}