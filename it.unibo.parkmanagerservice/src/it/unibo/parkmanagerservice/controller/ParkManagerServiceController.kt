package it.unibo.parkmanagerservice.controller

import it.unibo.parkingstate.DoorState
import it.unibo.parkmanagerservice.bean.*
import it.unibo.parkmanagerservice.notification.Notification
import it.unibo.parkmanagerservice.persistence.DoorQueue
import it.unibo.parkmanagerservice.persistence.UserRepository

interface ParkManagerServiceController {

    fun createUser(json : String) : Pair<User?, ParkManagerError?>
    fun createUser(name : String, surname : String, mail : String) : User
    fun destroyUser(user : User)
    fun assignSlotToUser(user : User) : Long
    fun reserveDoorForUserOrEnqueue(door : DoorType, user : User) : Boolean
    fun reserveDoorForNextUser(door : DoorType) : User?
    fun setSomeoneOnDoor(door : DoorType) : User?
    fun setFreeDoor(door : DoorType)
    fun setCarOfUserAtIndoorParked() : User?
    fun setCarOfUserAtOutdoorLeaved() : User?
    fun assignTokenToUserAtIndoor(slotnum : String, mail : String) : Pair<User?, ParkManagerError?>
    fun analyzeToken(token : String, mail : String) : Pair<ParkingSlot?, ParkManagerError?>
    fun freeSlotUsedByUserAtOutdoor() : Pair<User?, ParkingSlot?>

    fun getDoorsManager() : DoorsManager
    fun getDoorQueue(door: DoorType) : DoorQueue
    fun getSlotReservedForUser(user : User) : ParkingSlot?


}