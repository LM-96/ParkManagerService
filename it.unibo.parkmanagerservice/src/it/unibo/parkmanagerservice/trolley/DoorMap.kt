package it.unibo.parkmanagerservice.trolley

import it.unibo.parkmanagerservice.bean.DoorType
import org.json.JSONException
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

object DoorMap {

    private val CONFIG_FILE = "configs/doorsmap.json"
    private val doorpos = mutableMapOf<DoorType, Pair<Int, Int>>()
    private val allowedposbydoor = mutableMapOf<DoorType, Pair<Int, Int>>()
    private val directions = mutableMapOf<DoorType, String>()

    init {
        val file = Paths.get(CONFIG_FILE)
        if(!Files.exists(file)) {
            println("DoorMap | Unable to find the configuration file at ${file.toAbsolutePath()}")
            System.exit(-1)
        }
        println("DoorMap | Config file opened")

        var json : JSONObject
        var door = DoorType.INDOOR
        Files.lines(file).forEach {
            try {
                json = JSONObject(it)
                door = DoorType.valueOf(json.getString("door"))
                doorpos[door] = Pair(json.getInt("x"), json.getInt("y"))
                allowedposbydoor[door] = Pair(json.getInt("allowedX"), json.getInt("allowedY"))
                directions[door] = json.getString("direction")
            } catch (e : JSONException) {
                println("DoorMap | ${e.localizedMessage}")
                System.exit(-1)
            }
        }

        println("DoorMap | Configuration completed")
    }

    fun getPositionOfDoor(door : DoorType) : Pair<Int, Int>? {
        return doorpos[door]
    }

    fun getAdiacentAllowedPositionFromDoor(door : DoorType) : Pair<Int, Int>? {
        return allowedposbydoor[door]
    }

    fun getDirectionForDoor(door : DoorType) : String? {
        return directions[door]
    }

    fun isNearDoor(x : Int, y : Int) : DoorType? {
        val pos = Pair(x, y)
        return allowedposbydoor.entries.find { it.value == pos }?.key
    }

    fun getDirection(x : Int, y : Int) : String? {
        val pos = Pair(x, y)
        val key = allowedposbydoor.entries.find { it.value == pos }?.key ?: return null
        return directions[key]
    }
}