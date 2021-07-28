package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.ParkingSlotState

object ParkingRepositories {

    @JvmStatic private var userRepo : UserRepository? = null
    @JvmStatic private var slotRepo : ParkingSlotRepository? = null

    @JvmStatic fun createBasics(numOfSlot : Int) {
        if(userRepo == null)
            userRepo = BasicUserRepository()
        if(slotRepo == null) {
            slotRepo = BasicParkingSlotRepository()

            for (i in 1..numOfSlot) {
                (slotRepo as BasicParkingSlotRepository).create(
                    ParkingSlot(slotnum = i.toLong(), slotstate = ParkingSlotState.FREE, null))
            }
        }
    }

    @JvmStatic fun getUserRepository() : UserRepository? {
        return userRepo
    }

    @JvmStatic fun getParkingSlotRepository() : ParkingSlotRepository? {
        return slotRepo
    }

}