package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.*
import it.unibo.parkmanagerservice.persistence.DoorQueue

/**
 * The interface that defines the methods that a controller
 * of APP-SERVER must have.
 */
interface ParkManagerServiceController {

    //fun createUser(json : String) : Pair<User?, ParkManagerError?>

    /**
     * Create a user and save it into the data structures of the system.
     * @param[name] the name of the user;
     * @param[surname] the surname of the user;
     * @param[mail] the mail of the user.
     */
    fun createUser(name : String, surname : String, mail : String) : User

    /**
     * Destroy a user that is already present in the data structures of the system.
     * After this call, the user does not exist yet into the data structures.
     * @param[user] the user to destroy.
     */
    fun destroyUser(user : User)

    /**
     * Assign and return a slotnum to the user passed as argument.
     * So, this method first searches a free slotnum and then assigns it to the specified user.
     * After this invocation, the found slot is also setted to [ParkingSlotState.RESERVED]
     * while the user state is changed to [UserState.INTERESTED] and the relationship
     * between user and slot is saved. These changes are
     * saved into the data structures of the system.
     * @param[user] the user to assign the slotnum.
     */
    fun assignSlotToUser(user : User) : Long

    /**
     * Reserve the door passed as argument to the specified user, if possibile.
     * If the door is not free, the user is queued and his state is set to
     * [UserState.INTERESTED] or [UserState.WANTS_TO_GO] depending on the door required.
     * All changes of user and door state are persistent, also the association
     * beetwen the door reservation and the relative user.
     * @param[door] the required door;
     * @param[user] the user that wants door reservation
     */
    fun reserveDoorForUserOrEnqueue(door : DoorType, user : User) : Boolean

    /**
     * Takes an user from the queue associated to the [door] trying to reserve
     * this door to him. If the door is not at state [DoorState.FREE] or
     * if no user are in the queue, this method has no effect then returns null. In addition
     * to this, this method check if the state of the user is coherent (then
     * if he is really waiting the door).
     *
     * If the door is [DoorState.FREE] and there is a user waiting for it,
     * this method reserve the door to the user also updating its state in
     * the data structures (the user state becomes [UserState.OUTDOOR_RESERVED] or
     * [UserState.INDOOR_RESERVED], the door [DoorState.RESERVED] and the association
     * within door reservation and user is saved) and
     * returns the user who has the reservation.
     *
     * @param[door] the door wanted.
     * @return the user who has the door reservation or null if there
     * are no user into the door queue.
     */
    fun reserveDoorForNextUser(door : DoorType) : User?

    /**
     * This method takes and returns the user who the [door] is reserved and
     * set the door to [DoorState.OCCUPIED] by him.
     * If no user has requested the door reservation, this method returns null.
     *
     * @param[door] the door that has been occupied.
     * @return the user who occupy the door or null if no user has
     * reserved the door before.
     */
    fun setSomeoneOnDoor(door : DoorType) : User?

    /**
     * Set the specified [door] to [DoorState.FREE] and remove all user
     * that has reserved or occupied the door. After this invocation,
     * the door is completely free.
     *
     * @param[door] the door to be free.
     */
    fun setFreeDoor(door : DoorType)

    /**
     * Set the car of the the user at the indoor as parked.
     * This method checks if there is an user on the indoor
     * (the door must be [DoorState.OCCUPIED]) then marks the
     * door as [DoorState.FREE] and the user as [UserState.PARKED].
     * In addition, this method set the slot associated to the user as occupied.
     * It returns the user who has been parked if he was previously
     * on the door, null otherwise.
     * The state changes of the door, the user and the slot are persistent.
     *
     * @return the user who has been parked or null if no user
     * were at the door.
     */
    fun setCarOfUserAtIndoorParked() : User?

    /**
     * Set the car of the user at the outdoor as lived.
     * This method checks if there is an user on the outdoor
     * (the door must be [DoorState.OCCUPIED]) then marks the
     * door as [DoorState.FREE] and the user as [UserState.PICKEDUP].
     * It returns the user who leaved the door if he was previously
     * on it, null otherwise.
     * Notice that this method does not touch the slot previosly associated
     * to the user (see [freeSlotUsedByUserAtOutdoor] to do this).
     *
     * @return the user who leaved the outdoor or null if no user
     * were at the door.
     */
    fun setCarOfUserAtOutdoorLeaved() : User?

    /**
     * Assign and return a token to the user that has occupied the outdoor.
     * The association between slotnum and user is persistent and saved into
     * the data structure of the system.
     * This method can also return serveral errors:
     *
     * - [ErrorType.DOOR_NOT_RESERVED] if the indoor has not previously been reserved or occupied;
     *
     * - [ErrorType.NO_RESERVATION_FOR_USER] if the user has not taken a slotnum first;
     *
     * - [ErrorType.INVALID_MAIL] is the given mail is different from that of the user that
     * has reserved the indoor;
     *
     * - [ErrorType.NO_USER_AT_DOOR] if the door is not at the state [DoorState.OCCUPIED],
     * then if the user has not positioned his car to the indoor.
     *
     * If all is correct and everything is alright with no error, this method
     * return a [Pair] with also the first argument that corresponds to the user
     * who has the new token. Otherwise, if was an error, the second argument of the
     * returned pair is not null.
     * If the user receive the token, the association between user and token is saved
     * to the data structure of the system.
     *
     * @param[slotnum] the slotnum obtained with the Notify Interest;
     * @param[mail] the mail of the user that requests the token.
     * @return the user who has the token and, eventually, the error; in this case
     * the second argument of the returned pair is not null.
     */
    fun assignTokenToUserAtIndoor(slotnum : String, mail : String) : Pair<User?, ParkManagerError?>

    /**
     * Analyze a token in order to verify its fairness. If the token is valid,
     * tho slot associated to the user who has the token is set to [ParkingSlotState.ALMOST_FREE]
     * because of this operation must be invoked only when an user wants to pick up his car.
     * This update is persistent.
     * This method returns a [Pair] containing the [ParkingSlot] of the user who has the token
     * and, eventually, en error that is null if everything go alright.
     * This method can return a not null error if:
     *
     * - [ErrorType.INVALID_TOKEN]: the token is not valid, so no parking slot is found or
     * the given mail is not that of the user associated to the token.
     *
     * @param[token] the token to analyze;
     * @param[mail] the mail of the user who has the token.
     */
    fun analyzeToken(token : String, mail : String) : Pair<ParkingSlot?, ParkManagerError?>

    /**
     * This method makes the slot associated to the user who is at the outdoor free.
     * It should be called only after the transport trolley has effectively taken out
     * the car by the slot.
     * After the invoke of this method, the selected slot was set to [ParkingSlotState.FREE]
     * and the relationship between it and the previously occupant user is deleted.
     * These updates are persistent.
     *
     * It returns a [Pair] containing the user that was occupying the slot before this
     * invoke and the same slot.
     * If no user was at the indoor the returned pair contains two null value while if
     * the user - for some internal error - is not associated to a slot, the pair
     * contains the found user but a null slot.
     *
     * @return a [Pair] containing the user at the outdoor and the slot previously
     * associated to him; both value are null if no user is at the outdoor or only the
     * second is null if the user has not slot occupied.
     */
    fun freeSlotUsedByUserAtOutdoor() : Pair<User?, ParkingSlot?>

    /**
     * Return the [DoorsManager] used by the controller.
     *
     * @return the [DoorsManager] used by the controller.
     */
    fun getDoorsManager() : DoorsManager

    /**
     * Return the [DoorQueue] associated with the [door] passed as argument.
     *
     * @param[door] the door who has the queue.
     * @return the queue associated to the door
     */
    fun getDoorQueue(door: DoorType) : DoorQueue

    /**
     * Return the [ParkingSlot] reserved to the [user] passed as argument or
     * null if no slot is reserved for the user.
     *
     * @param[user] the user who has the slot reserved
     * @return the slot associated to the user or null if no slot
     * is reserved for the user
     */
    fun getSlotReservedForUser(user : User) : ParkingSlot?


}