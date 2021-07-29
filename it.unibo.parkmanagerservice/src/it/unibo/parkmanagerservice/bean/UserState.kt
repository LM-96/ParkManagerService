package it.unibo.parkmanagerservice.bean

enum class UserState {
    CREATED, INTERESTED, INDOOR_RESERVED, PARKED, WANTS_TO_GO, OUTDOOR_RESERVED, PICKEDUP;

    companion object {
        @JvmStatic fun getByDoorReservationState(door : DoorType) : UserState {
            when(door) {
                DoorType.INDOOR -> return UserState.INDOOR_RESERVED
                DoorType.OUTDOOR -> return UserState.OUTDOOR_RESERVED
            }
        }

        @JvmStatic fun getByDoorWanted(door : DoorType) : UserState {
            when(door) {
                DoorType.INDOOR -> return UserState.INTERESTED
                DoorType.OUTDOOR -> return UserState.WANTS_TO_GO
            }
        }
    }
}