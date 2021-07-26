package it.unibo.parkmanagerservice.notification

    fun main(args : Array<String>) {
        val notifier = MailNotifier()
        notifier.sendNotification(mailTo = "luca.marchegiani.96@gmail.com", null, NotificationType.GENERAL, null, "")
        println("sended")
    }