package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.User

interface DoorQueue {

    fun addUser(user : User)
    fun pullNextUser() : User?
    fun remaining() : Int

}