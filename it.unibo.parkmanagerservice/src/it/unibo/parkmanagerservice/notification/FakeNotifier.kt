package it.unibo.parkmanagerservice.notification

import it.unibo.parkmanagerservice.bean.User
import java.util.stream.Collectors

class FakeNotifier : Notifier {
    override fun sendNotification(notification: Notification) {
        println("FakeNotifier | Sended notification ${notification.toString()}")
    }

    override fun sendNotificationToUserWithDefaultContent(user: User, type: NotificationType, args: Array<String>) {
        sendNotification(Notification(
            destination = user.toString(),
            type = type,
            content = args.toList().stream().collect(Collectors.joining("\n"))))
    }
}