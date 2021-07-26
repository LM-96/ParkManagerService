package it.unibo.parkmanagerservice.notification

import it.unibo.parkmanagerservice.bean.User

interface Notifier {

    fun sendNotification(notification: Notification)
    fun sendNotificationToUserWithDefaultContent(user: User, type: NotificationType, args : Array<String>)

}