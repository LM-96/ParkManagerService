package it.unibo.parkmanagerservice.bean

import org.hibernate.usertype.UserType
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

)
