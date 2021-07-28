package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.ParkingSlotState
import java.util.*

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

    override fun getByToken(token: String): Optional<ParkingSlot> {
        return Optional.ofNullable(slots.values.find { if(it.user?.token != null)  it.user?.token.equals(token) else false})
    }

    override fun getFirstFree(): Optional<ParkingSlot> {
        return Optional.ofNullable(slots.values.find { it.slotstate.equals(ParkingSlotState.FREE) })
    }

    override fun getReservedForUser(userId: Long): Optional<ParkingSlot> {
        return Optional.ofNullable(slots.values.find { it.user?.id == userId })
    }

    override fun getReservedForUserByToken(token: String): Optional<ParkingSlot> {
        return Optional.ofNullable(slots.values.find { it.user?.token?.equals(token) ?: false })
    }


}