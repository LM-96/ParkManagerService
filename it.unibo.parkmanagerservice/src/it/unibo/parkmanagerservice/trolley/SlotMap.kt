package it.unibo.parkmanagerservice.trolley

import org.json.JSONException
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

object SlotMap {

    private val CONFIG_FILE = "configs/slots.json"
    private val slotpos = mutableMapOf<Int, Pair<Int, Int>>()
    private val allowedposbyslot = mutableMapOf<Int, Pair<Int, Int>>()

    init {
        val file = Paths.get(CONFIG_FILE)
        if(!Files.exists(file)) {
            println("SlotMap | Unable to find the configuration file at ${file.toAbsolutePath()}")
            System.exit(-1)
        }
        println("SlotMap | Config file opened")

        var json : JSONObject
        Files.lines(file).forEach {
            try {
                json = JSONObject(it)
                slotpos[json.getInt("slotnum")] = Pair(json.getInt("x"), json.getInt("y"))
                allowedposbyslot[json.getInt("slotnum")] = Pair(json.getInt("allowedX"), json.getInt("allowedY"))
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

}