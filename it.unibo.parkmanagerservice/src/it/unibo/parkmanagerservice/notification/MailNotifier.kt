package it.unibo.parkmanagerservice.notification

import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailNotifier : Notifier {

    private val CONFIG_FILE = "configs/mail.json"

    private var FROM : String = ""
    private var PASS : String = ""
    private var HOST : String = ""
    private var PORT : Int = 0
    private val PROPS = System.getProperties()

    private var SESSION : Session

    init {
        val configFilePath = Paths.get(CONFIG_FILE)

        if(!Files.exists(configFilePath)) {
            println("MailNotifier | Unable to find configuration file in \"${configFilePath.toAbsolutePath()}")
            System.exit(-1)
        }

        println("MailNotifier | Found configuration file")
        val json = JSONObject(Files.newBufferedReader(configFilePath).readLine())

        if(json.has("mail")) FROM = json.getString("mail")
        else {
            println("MailNorifier | Unable to find mail information")
            System.exit(-1)
        }

        if(json.has("pass")) PASS = json.getString("pass")
        else {
            println("MailNorifier | Unable to find password information")
            System.exit(-1)
        }

        if(json.has("host")) HOST = json.getString("host")
        else {
            println("MailNorifier | Unable to find host information")
            System.exit(-1)
        }

        if(json.has("port")) PORT = json.getInt("port")
        else {
            println("MailNorifier | Unable to find host port information")
            System.exit(-1)
        }

        if(json.has("auth")) PROPS.put("mail.smtp.auth", json.getString("auth"))
        else {
            println("MailNorifier | Unable to find auth information")
            System.exit(-1)
        }

        if(json.has("ssl-enabled")) PROPS.put("mail.smtp.ssl.enabled", json.getString("auth"))
        else {
            println("MailNorifier | Unable to find auth information")
            System.exit(-1)
        }

        PROPS.put("mail.smtp.host", HOST)
        PROPS.put("mail.smtp.port", PORT)

        SESSION = Session.getInstance(PROPS, object : Authenticator() {
            override fun getPasswordAuthentication() :  PasswordAuthentication {
                return PasswordAuthentication(FROM, PASS)
            }
        })
    }

    override fun sendNotification(notification: Notification) {
        var msg = MimeMessage(SESSION)
        msg.setFrom(InternetAddress(FROM))
        msg.addRecipient(Message.RecipientType.TO, InternetAddress(notification.destination))
        msg.setSubject(notification.subject)
        if(notification.contentType.equals("text")) msg.setText(notification.content)
        else
            msg.setContent(notification.content, notification.contentType)
        Transport.send(msg)
    }


}