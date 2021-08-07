package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.*
import org.json.JSONObject

class StateJSONIZER() {
    private val doorsManager = LocalDoorState.get()

    val JSON = JSONObject("{\"indoor\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"outdoor\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"1\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"2\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"3\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"4\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"5\":{\"state\":\"FREE\", \"user\":\"\"}, " +
            " \"6\":{\"state\":\"FREE\", \"user\":\"\"}}")

    fun updateDoor(door : DoorType) : StateJSONIZER {
        JSON.put(door.toString().toLowerCase(), JSONObject()
            .put("state", doorsManager.getState(door))
            .put("user", doorsManager.getUserAtDoor(door)?.mail ?: ""))

        return this
    }

    fun updateSlot(slot : ParkingSlot) : StateJSONIZER {
        JSON.put(slot.slotnum.toString(), JSONObject()
            .put("state", slot.slotstate))
            .put("user", slot.user?.mail ?: "")

        return this
    }

    fun updateSlotReserved(slotnum : Long, user : User) : StateJSONIZER{
        JSON.put(slotnum.toString(), JSONObject()
            .put("state", ParkingSlotState.RESERVED)
            .put("user", user.mail))

        return this
    }

    fun updateSlotOccupied(slotnum : Long) : StateJSONIZER {
        val user = (JSON.getJSONObject(slotnum.toString())).getString("user")
        JSON.put(slotnum.toString(), JSONObject()
            .put("state", ParkingSlotState.RESERVED)
            .put("user", user))

        return this
    }

    fun updateSlotAlmostFree(slotnum : Long) : StateJSONIZER {
       val user = (JSON.getJSONObject(slotnum.toString())).getString("user")
        JSON.put(slotnum.toString(), JSONObject()
            .put("state", ParkingSlotState.ALMOST_FREE)
            .put("user", user))

        return this
    }

    fun updateSlotFree(slotnum : Long) : StateJSONIZER {
        JSON.put(slotnum.toString(), JSONObject()
            .put("state", ParkingSlotState.ALMOST_FREE)
            .put("user", ""))

        return this
    }

    override fun toString() : String {
        return JSON.toString()
    }
}