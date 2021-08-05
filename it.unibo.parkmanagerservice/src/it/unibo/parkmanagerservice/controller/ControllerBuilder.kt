package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.DoorsManager
import it.unibo.parkmanagerservice.persistence.DoorQueue
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository
import it.unibo.parkmanagerservice.persistence.UserRepository

/**
 * This object is a singleton builder for the controller of the APP-SERVER.
 */
object ControllerBuilder {

    /**
     * The singleton controller. It is null if the controller has never been
     * builded.
     */
    @JvmStatic private var SINGLETON : ParkManagerServiceController? = null

    /**
     * This method returns the created instance of the controller.
     * The invocation of this method throw an [IllegalStateException] if the controller
     * was not created first by call [inject] or [createK] methods.
     * @return the controller previously created.
     * @throws [IllegalStateException] if the controller has not been built before.
     */
    @JvmStatic fun get() : ParkManagerServiceController {
        if(SINGLETON == null) {
            throw IllegalStateException("ParkManagerServiceControllers | Call method create or inject before")
            System.exit(-1)
        }
        return SINGLETON!!
    }

    /**
     * Inject a controller into the builder so, the [get] method will return
     * the injected controller. If the controller is already built, this
     * method has not effect.
     * @param[controller] the controller to inject
     * @return this builder
     */
    @JvmStatic fun inject(controller: ParkManagerServiceController) : ControllerBuilder {
        if(SINGLETON == null)
            SINGLETON = controller

        return this
    }

    /**
     * Create a new controller using the repositories, the door queues and the doors manager
     * passed as argument so, the [get] method will return the newly buil controller. If the
     * controller is already built, this method as no effect.
     * @param[userRepo] the [UserRepository] that controller will use to manage users;
     * @param[slotRepo] the [ParkingSlotRepository] that controller will use to manage slots;
     * @param[indoorQueue] the [DoorQueue] associated to the indoor;
     * @param[outdoorQueue] the [DoorQueue] associated to the outdoor;
     * @param[doors] the [DoorsManager] of the two doors.
     * @return this builder
     */
    @JvmStatic fun createK(userRepo : UserRepository, slotRepo : ParkingSlotRepository,
                           indoorQueue : DoorQueue, outdoorQueue : DoorQueue,
                           doors : DoorsManager) : ControllerBuilder {
        if(SINGLETON == null)
            SINGLETON = KParkManagerServiceController(userRepo, slotRepo,
                indoorQueue, outdoorQueue, doors)

        return this
    }

}