package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.User

interface DoorQueue {

    fun addUser(user : User)
    fun getNextUser() : User?
    fun remaining() : Int

}