package it.unibo.parkmanagerservice.notification

data class Notification(
    var destination : String? = null,
    var type : NotificationType? = null,
    var subject : String = "",
    var content : String ="",
    var contentType : String = ""
)