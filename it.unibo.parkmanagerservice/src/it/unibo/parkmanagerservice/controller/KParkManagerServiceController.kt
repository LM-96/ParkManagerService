package it.unibo.parkmanagerservice.controller

import it.unibo.parkingstate.DoorState
import it.unibo.parkmanagerservice.bean.*
import it.unibo.parkmanagerservice.persistence.DoorQueue
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository
import it.unibo.parkmanagerservice.persistence.UserRepository
import org.json.JSONObject

class KParkManagerServiceController(
    userRepo : UserRepository,
    slotRepo : ParkingSlotRepository,
    indoorQueue : DoorQueue,
    outdoorQueue : DoorQueue,
    doors : DoorsManager
) : ParkManagerServiceController {

    private val userRepo = userRepo
    private val slotRepo = slotRepo
    private val doorQueues =
        mapOf(Pair<DoorType, DoorQueue>(DoorType.INDOOR, indoorQueue),
            Pair<DoorType, DoorQueue>(DoorType.OUTDOOR, outdoorQueue))
    private val doors = doors

    override fun createUser(json : String) : Pair<User?, ParkManagerError?> {
        println("Controller | createUser($json)")
        val jsonobj = JSONObject(json)

        if(!jsonobj.has("name"))
            return Pair(null, ParkManagerError(ErrorType.NO_NAME, "You must insert a name"))

        if(!jsonobj.has("surname"))
            return Pair(null, ParkManagerError(ErrorType.NO_SURNAME, "You must insert a surname"))

        if(!jsonobj.has("email"))
            return Pair(null, ParkManagerError(ErrorType.NO_MAIL, "You must insert a name"))

        val user = User(name = jsonobj.getString("name"),
            surname = jsonobj.getString("surname"), mail = jsonobj.getString("mail"),
            state = UserState.CREATED
            )
        userRepo.create(user)

        println("Controller | Created user [${user.toString()}]")
        return Pair(user, null)

    }

    override fun createUser(name: String, surname: String, mail: String): User {
        var user = User(name = name, surname = surname, mail = mail, state = UserState.CREATED)
        userRepo.create(user)

        println("Controller | Created user [${user.toString()}]")
        return user
    }

    override fun destroyUser(user: User) {
        userRepo.delete(user)
        println("Controller | Deleted user [${user.toString()}]")
    }

    override fun assignSlotToUser(user: User): Long {
        val slotOpt = slotRepo.getFirstFree()
        if(slotOpt.isPresent) {
            val slot = slotOpt.get()
            slot.slotstate = ParkingSlotState.RESERVED
            slot.user = user
            slotRepo.update(slot)

            user.state = UserState.INTERESTED
            userRepo.update(user)

            println("Controller | Reserved slotnum=${slot.slotnum} to user [${user.toString()}]")
            return slot.slotnum
        }

        println("Controller | No slot available. Slotnum = 0")
        return 0
    }

    override fun reserveDoorForUserOrEnqueue(door: DoorType, user: User): Boolean {
        var res = false
        if(doors.getState(door) == DoorState.FREE) {
            doors.reserveForUser(door, user)
            when(door) {
                DoorType.INDOOR -> user.state = UserState.INDOOR_RESERVED
                DoorType.OUTDOOR -> user.state = UserState.OUTDOOR_RESERVED
            }
            userRepo.update(user)
            res = true

            println("Controller | Reserved ${door.toString()} to user [${user.toString()}]")
        } else {
            doorQueues.get(door)!!.addUser(user)
            when(door) {
                DoorType.INDOOR -> user.state = UserState.INTERESTED
                DoorType.OUTDOOR -> user.state = UserState.WANTS_TO_GO
            }
            userRepo.update(user)
            res = false

            println("Controller | ${door.toString()} is already engaged. Enqueued user [${user.toString()}]")
        }

        return res
    }

    override fun reserveDoorForNextUser(door: DoorType): User? {
        var user : User? = null
        val queue = doorQueues.get(door)!!
        if(doors.getState(door) == DoorState.FREE && queue.remaining() > 0) {
            user = doorQueues.get(door)!!.getNextUser()

            while(user!!.state != UserState.INTERESTED && queue.remaining() > 0)
                user = queue.getNextUser()!!

            if(user!!.state == UserState.INTERESTED) {
                doors.reserveForUser(door, user!!)
                user.state = UserState.INDOOR_RESERVED
                userRepo.update(user)

                println("Controller | Reserved ${door.toString()} to user [${user.toString()}]")
            }
        }

        return user
    }

    override fun setSomeoneOnDoor(door: DoorType) : User? {
        doors.setState(door, DoorState.OCCUPIED)
        return doors.getUserAtDoor(door)
    }

    override fun setFreeDoor(door: DoorType) {
        doors.setState(door, DoorState.FREE)
    }

    override fun setCarOfUserAtIndoorParked(): User? {
        val user = doors.getUserAtDoor(DoorType.INDOOR)

        if(doors.getState(DoorType.INDOOR) == DoorState.OCCUPIED &&
                user != null) {
            doors.setFreeWithNoUser(DoorType.INDOOR)
            user.state = UserState.PARKED
            userRepo.update(user)

            var slot = slotRepo.getReservedForUser(user.id).get()
            slot.slotstate = ParkingSlotState.OCCUPIED
            slotRepo.update(slot)

            println("Controller | Registered parked car for user [${user.toString()}]")
            return user
        }

        return null
    }

    override fun setCarOfUserAtOutdoorLeaved(): User? {
        TODO("Not yet implemented")
    }

    override fun assignTokenToUserAtIndoor(slotnum : String, mail : String): Pair<User?, ParkManagerError?>{
        val user : User? = doors.getUserAtDoor(DoorType.INDOOR)

        if(user == null) {
            println("Controller | Unable to assign token: no user at indoor")
            return Pair(user,
                ParkManagerError(
                    ErrorType.DOOR_NOT_RESERVED,
                    "The indoor is not reserved. Please take a reservation."))
        }
        val slot = slotRepo.getReservedForUser(user.id)
        if(!slot.isPresent) {
            println("Controller | Unable to assign token: user into the indoor has no slot reserved")
            return Pair(user, ParkManagerError(
                    ErrorType.NO_RESERVATION_FOR_USER,
                    "There is no reservation. Please take a slotnum before.") )
        }

        if(doors.getState(DoorType.INDOOR) == DoorState.OCCUPIED) {
            if(user.mail.equals(mail) && slot.get().slotnum == slotnum.toLong()) {

                user.token = "#${user.id}S${slot.get().slotnum}"
                println("Controller | assigned token=${user.token} to user [${user.toString()}]")
                return Pair(user, null)

            } else {
                println("Controller | Unable to assign token: invalid slotnum=$slotnum or mail $mail")
                return Pair(user, ParkManagerError(ErrorType.INVALID_MAIL,
                "The mail or the slotnum is not valid. Please insert correct data"))
            }
        } else {
            println("Controller | Unable to assign token: car is not at the indoor for user[${user.toString()}]")
            return Pair(user, ParkManagerError(ErrorType.NO_USER_AT_DOOR,
                "Please move the car to the indoor before press CARENTER"))
        }
    }

    override fun analyzeToken(token: String, mail: String): Pair<ParkingSlot?, ParkManagerError?> {
        val slotOpt = slotRepo.getByToken(token)
        if(!slotOpt.isPresent) {
            println("Controller | Unable to find slot assigned to user with token=$token")
            return Pair(null, ParkManagerError(ErrorType.INVALID_TOKEN,
                "Invalid token. Please try again"))
        }

        val slot = slotOpt.get()
        val user = slot.user!!
        if(!user.mail.equals(mail)) {
            println("Controller | Mail not valid. Expected mail=${user.mail} instead of $mail")
            return Pair(slot, ParkManagerError(ErrorType.INVALID_TOKEN, "Invalid token or mail. Please try again."))
        }

        println("Controller | Correct token for slot [${slot.toString()}]")
        slot.slotstate = ParkingSlotState.ALMOST_FREE
        slotRepo.update(slot)

        return Pair(slot, null)
    }

    override fun freeSlotUsedByUserAtOutdoor() : User? {
        val user = doors.getUserAtDoor(DoorType.OUTDOOR)
        if(user == null) {
            println("Controller | Unable to free a parking slot: no user at the outdoor")
        } else {
            val slotOpt = slotRepo.getReservedForUser(user!!.id)
            if(slotOpt.isPresent) {
                val slot = slotOpt.get()
                slot.slotstate = ParkingSlotState.FREE
                slotRepo.update(slot)

                println("Controller | Free slot[${slot.toString()}]")
            } else {
                println("Controller | Request to free slot not used")
            }
        }
        return user;
    }

    override fun getDoorsManager(): DoorsManager {
        return doors
    }

    override fun getDoorQueue(door: DoorType): DoorQueue {
        return doorQueues.get(door)!!
    }

    override fun getSlotReservedForUser(user: User): ParkingSlot? {
        return slotRepo.getReservedForUser(user.id).orElse(null)
    }


}