package it.unibo.parkmanagerservice.bean

import java.util.*
import javax.persistence.*

data class ParkingSlot(

    val slotnum : Long,
    var slotstate : ParkingSlotState,
    var user : User?

)
