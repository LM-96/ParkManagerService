package it.unibo.parkmanagerservice.notification

import it.unibo.parkmanagerservice.bean.User
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

object DefaultNotificationFactory {

    @JvmStatic private val CONFIG_FILE = "configs/notification.json"
    @JvmStatic private var LOGO : String? = null
    @JvmStatic private val basicNotifications = mutableMapOf<NotificationType, Notification>()

    init {
        val configFile = Paths.get(CONFIG_FILE)
        if(!Files.exists(configFile)) {
            println("DefaultNotificationFactory | Unable to find configuration file at ${configFile.toAbsolutePath()}")
            System.exit(-1)
        }
        val reader = Files.newBufferedReader(configFile)
        var json : JSONObject
        var line = reader.readLine()
        var notification : Notification

        while(line != null) {
            json = JSONObject(line)

            if(json.has("logo")) {
                val logoFile = Paths.get(json.getString("logo"))
                if(!Files.exists(logoFile)) {
                    println("DefaultNotificationFactory | Unable to load logo at file ${logoFile.toAbsolutePath()}")
                } else {
                    LOGO = Files.lines(logoFile).collect(Collectors.joining("\n"))
                }
            }

            if(json.has("type")) {
                notification = Notification()
                type = NotificationType.valueOf(json.getString("type").toUpperCase())


                when(type) {
                    NotificationType.SLOTNUM -> {

                    }

                    NotificationType.TOKEN -> {

                    }

                    NotificationType.PICKUP -> {

                    }
                }
            }

        }
    }

    @JvmStatic fun createForUser(user : User, notificationType: NotificationType, args : Array<String>) : Notification? {
        when(notificationType) {
            NotificationType.SLOTNUM -> Notification(destination = user.mail, type = notificationType,
            subject = "Invio SLOTNUM ParkManagerService")
        }
    }

}