package it.unibo.parkmanagerservice.notification

import it.unibo.parkmanagerservice.bean.User
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

object DefaultNotificationFactory {

    @JvmStatic private val CONFIG_FILE = "configs/notification.json"
    @JvmStatic private val basicNotifications = mutableMapOf<NotificationType, Notification>()

    init {
        val configFile = Paths.get(CONFIG_FILE)
        if(!Files.exists(configFile)) {
            println("DefaultNotificationFactory | Unable to find configuration file at ${configFile.toAbsolutePath()}")
            System.exit(-1)
        }
        println("DefaultNotificationFactory | Found configuration file")

        val reader = Files.newBufferedReader(configFile)
        var json : JSONObject
        var line = reader.readLine()
        var notification : Notification

        while(if(line!=null) !line.startsWith("#") else false) {
            json = JSONObject(line)

            if(json.has("type")) {
                notification = Notification()
                notification.type = NotificationType.valueOf(json.getString("type").toUpperCase())
                println("DefaultNotificationFactory | Found pattern for notification ${notification.type}")

                if(json.has("subject")) notification.subject = json.getString("subject")
                else println("DefaultNotificationFactory | Unable to find subject for ${notification.type} in ${configFile.toAbsolutePath()}")

                if(json.has("content-type")) {
                    notification.contentType = json.getString("content-type")
                    if(notification.contentType.equals("text/html")) {
                       if(json.has("html")) {
                           var htmlFile = Paths.get(json.getString("html"))
                           if(!Files.exists(htmlFile)) println("DefaultNotificationFactory | Unable to load html content for notification ${notification.type} in ${configFile.toAbsolutePath()}")
                           else notification.content = "${Files.lines(htmlFile).collect(Collectors.joining("\n"))}"
                       } else println("DefaultNotificationFactory | ${notification.type} in ${configFile.toAbsolutePath()} has \"text/html\" content but html file is not specified")
                    } else if(notification.contentType.equals("text")) {
                        if(json.has("text")) notification.content == json.getString("text")
                        else println("DefaultNotificationFactory | ${notification.type} in ${configFile.toAbsolutePath()} has \"text\" content but text is not specified")
                    } else println("DefaultNotificationFactory | Content Type for ${notification.type} in ${configFile.toAbsolutePath()} can not be managed")
                } else println("DefaultNotificationFactory | Content Type for${notification.type} in ${configFile.toAbsolutePath()} is not specified")

                if(json.has("linkpattern")) {
                    notification.content = notification.content.replace("${'$'}LINK", json.getString("linkpattern"))
                }

                basicNotifications.put(notification.type!!, notification)
            }

            line = reader.readLine()
        }
        println("DefaultNotificationFactory | Configuration ended")
    }

    @JvmStatic fun createForUser(user : User, notificationType: NotificationType, args : Array<String>) : Notification {
        var res : Notification? = null
        if(basicNotifications.containsKey(notificationType)) {
            res = basicNotifications.get(notificationType)!!.copy()
            res.type = notificationType
            res.destination = user.mail
            res.content = res.content.replace("${'$'}USERMAIL",user.mail)
            when(notificationType) {
                NotificationType.SLOTNUM -> {
                    res.content = "${res.content
                        .replace("${'$'}SLOTNUM", args[0])
                        .replace("${'$'}SEC", args[1])
                        .replace("${'$'}BTNAME", "Vai a CARENTER")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.TOKEN -> {
                    res.content = "${res.content
                        .replace("${'$'}TOKEN", args[0])
                        .replace("${'$'}BTNAME", "Vai a PICKUP")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.PICKUP -> {
                    res.content = "${res.content
                        .replace("${'$'}SEC", args[0])
                        .replace("${'$'}BTNAME", "ParkManagerService")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.LOSEN_RIGHT -> {
                    res.content = "${res.content
                        .replace("${'$'}BTNAME", "ParkManagerService")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.ADMIN_DTFREE_REACHED -> {
                    res.content = "${res.content
                        .replace("${'$'}BTNAME", "ParkManagerService")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.USER_DTFREE_REACHED-> {
                    res.content = "${res.content
                        .replace("${'$'}BTNAME", "ParkManagerService")
                        .replace("${'$'}NAME", user.name)
                        .replace("${'$'}SURNAME", user.surname)
                    }"
                }

                NotificationType.GENERAL -> {
                    res.content = "${res.content
                        .replace("${'$'}BTNAME", "ParkManagerService")
                        .replace("${'$'}TEXT", args[0])
                    }"
                }
            }
        }

        return res!!
    }

}