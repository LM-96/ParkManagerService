package it.unibo.parkmanagerservice.notification

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object CCNotificationDeque {

    private val mutex = Mutex()
    private val notifications = ArrayDeque<Notification>()

    suspend fun put(notification : Notification) {
        mutex.withLock {
            notifications.add(notification)
        }
    }

    suspend fun get() : Notification? {
        mutex.withLock {
            return notifications.removeFirstOrNull()
        }
    }
}