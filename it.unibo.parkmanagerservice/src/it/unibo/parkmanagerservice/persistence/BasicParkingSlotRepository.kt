package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.ParkingSlotState

class BasicParkingSlotRepository : ParkingSlotRepository {

    private val slots = mutableMapOf<Long, ParkingSlot>()

    override fun create(slot: ParkingSlot) {
        slots.put(slot.slotnum, slot)
    }

    override fun retrieve(slotnum: Long): ParkingSlot? {
        return slots.get(slotnum)
    }

    override fun update(slot: ParkingSlot) {  }

    override fun delete(slot : ParkingSlot) {
        slots.remove(slot.slotnum)
    }

    override fun getByState(state: ParkingSlotState): List<ParkingSlot> {
        return slots.values.filter { it.slotstate.equals(state) }
    }

    override fun getByToken(token: String): ParkingSlot? {
        return slots.values.find { if(it.token != null)  it.token.equals(token) else false}
    }

    override fun getFirstFree(): ParkingSlot? {
        return slots.values.find { it.slotstate.equals(ParkingSlotState.FREE) }
    }


}