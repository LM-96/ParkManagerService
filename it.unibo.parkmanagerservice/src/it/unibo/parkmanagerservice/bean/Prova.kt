package it.unibo.parkmanagerservice.bean

import it.unibo.parkmanagerservice.persistence.BasicParkingSlotRepository

fun main(args : Array<String>) {
    var slotRepo = BasicParkingSlotRepository()
    var slot = ParkingSlot(1, ParkingSlotState.FREE, null)
    slotRepo.create(slot)
}