package it.unibo.parkmanagerservice.bean

data class LocalDoorState (

    var indoor: DoorState = DoorState.FREE,
    var userIndoor : User? = null,
    var outdoor: DoorState = DoorState.FREE,
    var userOutdoor : User? = null,

    ) : DoorsManager {
    companion object {
        @JvmStatic private var SINGLETON : LocalDoorState? = null
        @JvmStatic fun get() : LocalDoorState {
            if(SINGLETON == null)
                SINGLETON = LocalDoorState()

            return SINGLETON!!
        }
    }

    override fun getState(doorType: DoorType): DoorState {
        when(doorType) {
            DoorType.INDOOR -> return indoor
            DoorType.OUTDOOR -> return outdoor
        }
    }

    override fun setState(doorType: DoorType, doorState: DoorState) {
       when(doorType) {
           DoorType.INDOOR -> indoor = doorState
           DoorType.OUTDOOR -> outdoor = doorState
       }

        println("LocalDoorState | Setted ${doorType.toString()} to ${doorState.toString()}")
    }

    override fun getUserAtDoor(doorType: DoorType): User? {
        when(doorType) {
            DoorType.INDOOR -> return userIndoor
            DoorType.OUTDOOR -> return userOutdoor
        }
    }

    override fun setUserAtDoor(doorType: DoorType, user: User?) {
        when(doorType) {
            DoorType.INDOOR -> userIndoor = user
            DoorType.OUTDOOR -> userOutdoor = user
        }
        println("LocalDoorState | Setted ${doorType.toString()} with user [${user.toString()}]")
    }

    override fun reserveForUser(doorType: DoorType, user: User) {
        when(doorType) {
            DoorType.INDOOR -> {
                indoor = DoorState.RESERVED
                userIndoor = user
            }
            DoorType.OUTDOOR -> {
                outdoor = DoorState.RESERVED
                userOutdoor = user
            }
        }
        println("LocalDoorState | ${doorType.toString()}=RESERVED for user [${user.toString()}]")
    }

    override fun setFreeWithNoUser(doorType: DoorType) {
        when(doorType) {
            DoorType.INDOOR -> {
                indoor = DoorState.FREE
                userIndoor = null
            }

            DoorType.OUTDOOR -> {
                outdoor = DoorState.FREE
                userOutdoor = null
            }
        }

        println("LocalDoorState | ${doorType.toString()}=FREE, user=null")
    }
}
