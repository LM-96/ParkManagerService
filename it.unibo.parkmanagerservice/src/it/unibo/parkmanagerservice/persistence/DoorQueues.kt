package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.DoorType

object DoorQueues {

    @JvmStatic private var indoorQueue : DoorQueue? = null
    @JvmStatic private var outdoorQueue : DoorQueue? = null

    @JvmStatic fun getIndoorQueue() : DoorQueue {
        if(indoorQueue == null)
            indoorQueue = LocalDoorQueue()

        return indoorQueue!!
    }

    @JvmStatic fun getOutdoorQueue() : DoorQueue {
        if(outdoorQueue == null)
            outdoorQueue = LocalDoorQueue()

        return outdoorQueue!!
    }

    @JvmStatic fun getQueue(door : DoorType) : DoorQueue {
        when(door) {
            DoorType.INDOOR -> return getIndoorQueue()
            DoorType.OUTDOOR -> return getOutdoorQueue()
        }
    }

}