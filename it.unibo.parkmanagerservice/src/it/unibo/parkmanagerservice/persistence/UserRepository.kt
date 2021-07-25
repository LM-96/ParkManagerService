package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.User
import it.unibo.parkmanagerservice.bean.UserState

interface UserRepository {

    fun create(user : User)
    fun update(user : User)
    fun retrieve(id : Long) : User?
    fun delete(user : User)

    //Return all user in this state
    fun getByState(state : UserState) : List<User>

    //Return the first used entered in this state
    fun getFirstInState(state : UserState) : User?

}