package it.unibo.parkmanagerservice.notification

interface Notifier {

    fun sendNotification(notification: Notification)
    fun sendNotificationWithDefaultContent(dest : String, type: NotificationType)

}