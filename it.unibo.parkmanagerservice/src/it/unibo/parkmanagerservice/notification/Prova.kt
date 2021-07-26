package it.unibo.parkmanagerservice.notification

import it.unibo.parkmanagerservice.bean.User
import org.json.JSONObject
import org.json.JSONStringer
import org.json.JSONTokener

fun main(args : Array<String>) {
    val notifier = MailNotifier()
    //val user1 = User(id = 0, name = "Luca", surname = "Marchegiani", mail ="luca.marchegiani.96@gmail.com")
    //val user1 = User(0,  "Alessandro", "Tolli", "alessandrotolli995@gmail.com")
    val user1 = User(id=1, name="Simone", surname="Mattioli", mail = "simo.mattioli1998@gmail.com")
    //notifier.sendNotificationToUserWithDefaultContent(user1, NotificationType.PICKUP, arrayOf("0"))
    //notifier.sendNotificationToUserWithDefaultContent(user2, NotificationType.TOKEN, arrayOf("0"))
    NotificationChannel.channel.re
    }