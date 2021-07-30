package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.DoorType
import it.unibo.parkmanagerservice.bean.DoorsManager
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository

class JSONizer {

    var slotRepo : ParkingSlotRepository? = null
    var doorsManager: DoorsManager? = null

    fun stateToJsonString() : String {
        val builder = StringBuilder()

        if(slotRepo != null) {
            slotRepo!!.getAll().forEach { builder.append(
                "{\"slotnum\":\"${it.slotnum}\", \"slotstate\":\"${it.slotstate}\", \"user\":\"${it.user?.mail ?: ""}\"}\n"
            ) }
        }

        if(doorsManager != null) {
            DoorType.values().forEach {
                builder.append("{\"door\":\"${it.toString()}\", \"state\":\"${doorsManager!!.getState(it)}\", \"user\":\"${doorsManager?.getUserAtDoor(it) ?: ""}\"}\n")
            }
        }
        return builder.toString()
    }

}