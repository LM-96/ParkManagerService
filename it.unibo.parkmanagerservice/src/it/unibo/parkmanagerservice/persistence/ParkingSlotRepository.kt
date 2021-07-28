package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.ParkingSlotState
import it.unibo.parkmanagerservice.bean.User
import java.util.*

interface ParkingSlotRepository {

    fun create(slot : ParkingSlot)
    fun retrieve(slotnum : Long) : ParkingSlot?
    fun update(slot : ParkingSlot)
    fun delete(slot : ParkingSlot)

    fun getByState(state : ParkingSlotState) : List<ParkingSlot>
    fun getByToken(token : String) : Optional<ParkingSlot>
    fun getFirstFree() : Optional<ParkingSlot>
    fun getReservedForUser(userId : Long) : Optional<ParkingSlot>
    fun getReservedForUserByToken(token : String) : Optional<ParkingSlot>
}