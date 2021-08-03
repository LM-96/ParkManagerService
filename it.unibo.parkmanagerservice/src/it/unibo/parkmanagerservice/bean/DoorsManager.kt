package it.unibo.parkmanagerservice.bean

interface DoorsManager {

    fun getState(doorType: DoorType) : DoorState
    fun setState(doorType: DoorType, doorState: DoorState)
    fun getUserAtDoor(doorType: DoorType) : User?
    fun setUserAtDoor(doorType: DoorType, user: User?)
    fun reserveForUser(doorType: DoorType, user: User)
    fun setFreeWithNoUser(doorType: DoorType)

}