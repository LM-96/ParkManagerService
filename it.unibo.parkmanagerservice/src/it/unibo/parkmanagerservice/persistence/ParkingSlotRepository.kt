package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.ParkingSlotState

interface ParkingSlotRepository {

    fun create(slot : ParkingSlot)
    fun retrieve(slotnum : Long) : ParkingSlot?
    fun update(slot : ParkingSlot)
    fun delete(slot : ParkingSlot)

    fun getByState(state : ParkingSlotState) : List<ParkingSlot>
    fun getByToken(token : String) : ParkingSlot?
    fun getFirstFree() : ParkingSlot?
}