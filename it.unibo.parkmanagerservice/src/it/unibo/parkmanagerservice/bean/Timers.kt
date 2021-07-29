package it.unibo.parkmanagerservice.bean

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantLock

data class Timers(
    val ITOCC : Long,
    val DTFREE : Long,
    val INDOOR_POLLING : Long,
    val OUTDOOR_POLLING : Long
) {
    companion object {
        @JvmStatic private var SINGLETON : Timers? = null
        @JvmStatic private val CONFIG_FILE = "configs/timers.json"
        @JvmStatic private val lock = ReentrantLock()

        @JvmStatic fun get() : Timers {
            lock.lock()
            try {
                if(SINGLETON == null) {
                    val configFile = Paths.get(CONFIG_FILE)
                    if(!Files.exists(configFile)) {
                        println("Timers | Unable to get configuration file at ${configFile.toAbsolutePath()}.\n\tIt will be used the default configuration (120 seconds for ITOCC/DTFREE, 1 second for POLLING)")
                        SINGLETON = Timers(120000L, 120000L, 1000L, 1000L)
                    }

                    val json : JSONObject
                    try {
                        json = JSONObject(Files.newBufferedReader(configFile).readLine())
                        SINGLETON = Timers(
                            json.getLong("itocc_sec")*1000,
                            json.getLong("dtfree_sec")*1000,
                            json.getLong("indoor_polling_msec"),
                            json.getLong("outdoor_polling_msec")
                        )
                    } catch(e : Exception) {
                        e.printStackTrace()
                        println("Timers | Error while reading configuration file at ${configFile.toAbsolutePath()}.\n\tIt will be used the default configuration (120 seconds for ITOCC/DTFREE, 1 second for POLLING)")
                        SINGLETON = Timers(120000L, 120000L, 1000L, 1000L)
                    }
                }
                return SINGLETON!!
            } finally {
                lock.unlock()
            }
        }
    }
}
