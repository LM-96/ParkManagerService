package it.unibo.parkmanagerservice.bean

import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

data class Timers(
    val ITOCC : Long,
    val DTFREE : Long
) {
    companion object {
        @JvmStatic private var SINGLETON : Timers? = null
        @JvmStatic private val CONFIG_FILE = "configs/timers.json"
        @JvmStatic fun get() : Timers {
            if(SINGLETON == null) {
                val configFile = Paths.get(CONFIG_FILE)
                if(!Files.exists(configFile)) {
                    println("Timers | Unable to get configuration file at ${configFile.toAbsolutePath()}.\n\tIt will be used the default configuration (120 seconds)")
                    SINGLETON = Timers(120000L, 120000L)
                }

                val json : JSONObject
                try {
                    json = JSONObject(Files.newBufferedReader(configFile).readLine())
                    SINGLETON = Timers(json.getLong("itocc_sec")*1000, json.getLong("dtfree_sec")*1000)
                } catch(e : Exception) {
                    e.printStackTrace()
                    println("Timers | Error while reading configuration file at ${configFile.toAbsolutePath()}.\n\tIt will be used the default configuration (120 seconds)")
                    SINGLETON = Timers(120000L, 120000L)
                }
            }
            return SINGLETON!!
        }
    }
}
