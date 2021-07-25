package it.unibo.parkmanagerservice.bean

import org.hibernate.usertype.UserType
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name="USERS")
data class User (

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="ID")
    var id : Long = 0,

    @Column(name="NAME", nullable = false)
    var name : String,

    @Column(name="SURNAME", nullable = false)
    var surname : String,

    @Column(name="MAIL", nullable = false)
    var mail : String,

    @Column(name="STATE", nullable = true)
    @Enumerated(value = EnumType.STRING)
    var state : UserState? = null,

    @Column(name="TIME", nullable = true)
    var time : Timestamp? = null

)
