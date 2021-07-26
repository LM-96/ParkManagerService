package it.unibo.parkmanagerservice.notification

data class Notification(
    var destination : String?,
    val type : NotificationType?,
    var subject : String?,
    var content : String?,
    var contentType : String?
)