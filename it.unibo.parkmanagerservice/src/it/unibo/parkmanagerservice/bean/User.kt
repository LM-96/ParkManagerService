package it.unibo.parkmanagerservice.bean

import org.hibernate.usertype.UserType
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.*

data class User (

    var id : Long = 0,
    var name : String,
    var surname : String,
    var mail : String,
    var state : UserState,
    var time : Timestamp? = null,
    var token : String? = null
) {
    companion object {
        private var ADMIN : User? = null
        private val CONFIG_FILE = "configs/admin.json"
        init {
            val file = Paths.get(CONFIG_FILE)
            if(!Files.exists(file)) {
                println("User | Unable to find admin configuration file at ${file.toAbsolutePath()}")
                println("User | Admin will not be callable")
            } else {
                try {
                    val json = JSONObject(Files.newBufferedReader(file).readLine())
                    ADMIN = User(Long.MAX_VALUE, json.getString("name"),
                        json.getString("surname"), json.getString("mail"), UserState.CREATED)
                } catch (e : Exception) {
                    println("User | Error while parsing admin data: ${e.localizedMessage}")
                }
            }
        }

        fun getAdmin() : User? {
            return ADMIN?.copy()
        }
    }
}
