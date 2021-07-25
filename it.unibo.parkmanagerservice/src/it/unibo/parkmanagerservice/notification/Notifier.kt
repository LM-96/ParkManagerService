package it.unibo.parkmanagerservice.notification

interface Notifier {

    fun sendNotification(obj : String?, type : NotifyType, link : String?, content : String)

}