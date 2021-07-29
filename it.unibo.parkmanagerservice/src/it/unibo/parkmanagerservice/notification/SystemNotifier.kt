package it.unibo.parkmanagerservice.notification

import org.json.JSONObject
import java.beans.ExceptionListener
import java.nio.file.Files
import java.nio.file.Paths

object SystemNotifier {

    @JvmStatic private var SINGLETON : Notifier? = null
    @JvmStatic private val CONFIG_FILE = "configs/notifier.json"
    @JvmStatic fun get() : Notifier {
        if(SINGLETON == null) {
            var configFile = Paths.get(CONFIG_FILE)
            if(!Files.exists(configFile)) {
                println("SystemNotifier | Unable to find notifier configuration file at ${configFile.toAbsolutePath().toString()}.\n\tIt will be used the FakeNotifier")
                SINGLETON = FakeNotifier()
            }

            try {
                var json = JSONObject(Files.newBufferedReader(configFile).readLine())
                var type = json.getString("type")
                when(type) {
                    "fake" -> SINGLETON = FakeNotifier()
                    "mail" -> SINGLETON = MailNotifier()
                }
                println("SystemNotifier | used notifier $type from configuration file")
            } catch (e : Exception) {
                println("SystemNotifier | Error reading notifier configuration file. It will be used the FakeNotifier")
                SINGLETON = FakeNotifier()
                e.printStackTrace()
            }
        }

        return SINGLETON!!
    }

}