package it.unibo.parkmanagerservice.notification

import kotlinx.coroutines.channels.Channel


object NotificationChannel {

    @JvmStatic val channel = Channel<Notification>()

}