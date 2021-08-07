package it.unibo.parkmanagerservice.trolley

import it.unibo.parkmanagerservice.bean.DoorType
import org.json.JSONException
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

object SlotMap {

    private val CONFIG_FILE = "configs/slots.json"
    private val slotpos = mutableMapOf<Int, Pair<Int, Int>>()
    private val allowedposbyslot = mutableMapOf<Int, Pair<Int, Int>>()
    private val directions = mutableMapOf<Int, String>()

    init {
        val file = Paths.get(CONFIG_FILE)
        if(!Files.exists(file)) {
            println("SlotMap | Unable to find the configuration file at ${file.toAbsolutePath()}")
            System.exit(-1)
        }
        println("SlotMap | Config file opened")

        var json : JSONObject
        var slotnum = 0
        Files.lines(file).forEach {
            try {
                json = JSONObject(it)
                slotnum = json.getInt("slotnum")
                slotpos[slotnum] = Pair(json.getInt("x"), json.getInt("y"))
                allowedposbyslot[slotnum] = Pair(json.getInt("allowedX"), json.getInt("allowedY"))
                directions[slotnum] = json.getString("direction")
            } catch (e : JSONException) {
                println("SlotMap | ${e.localizedMessage}")
                System.exit(-1)
            }
        }

        println("SlotMap | Configuration completed")
    }

    fun getPositionOfSlot(slotnum : String) : Pair<Int, Int>? {
        return slotpos[slotnum.toInt()]
    }

    fun getAdiacentAllowedPositionFromSlot(slotnum : String) : Pair<Int, Int>? {
        return allowedposbyslot[slotnum.toInt()]
    }

    fun getAllSlotnum() : IntArray {
        return slotpos.keys.stream().mapToInt{ it -> it}.toArray()
    }

    fun getDirectionForSlot(slotnum : String) : String? {
        return directions[slotnum.toInt()]
    }

    fun isNearSlot(x : Int, y : Int) : Int? {
        val pos = Pair(x, y)
        return allowedposbyslot.entries.find { it.value == pos }?.key
    }

    fun getDirection(x : Int, y : Int) : String? {
        val pos = Pair(x, y)
        val key = allowedposbyslot.entries.find { it.value == pos }?.key ?: return null
        return directions[key]
    }

}