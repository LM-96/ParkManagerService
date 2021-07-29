package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.DoorsManager
import it.unibo.parkmanagerservice.persistence.DoorQueue
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository
import it.unibo.parkmanagerservice.persistence.UserRepository

object ControllerBuilder {

    @JvmStatic private var SINGLETON : ParkManagerServiceController? = null

    @JvmStatic fun get() : ParkManagerServiceController {
        if(SINGLETON == null) {
            throw IllegalStateException("ParkManagerServiceControllers | Call method create or inject before")
            System.exit(-1)
        }
        return SINGLETON!!
    }

    @JvmStatic fun inject(controller: ParkManagerServiceController) : ControllerBuilder {
        if(SINGLETON == null)
            SINGLETON = controller

        return this
    }

    @JvmStatic fun createK(userRepo : UserRepository, slotRepo : ParkingSlotRepository,
                           indoorQueue : DoorQueue, outdoorQueue : DoorQueue,
                           doors : DoorsManager) : ControllerBuilder {
        SINGLETON = KParkManagerServiceController(userRepo, slotRepo,
            indoorQueue, outdoorQueue, doors)

        return this
    }

}