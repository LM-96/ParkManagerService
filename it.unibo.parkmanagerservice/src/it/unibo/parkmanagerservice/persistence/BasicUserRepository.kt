package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.LocalDoorState
import it.unibo.parkmanagerservice.bean.User
import it.unibo.parkmanagerservice.bean.UserState
import java.sql.SQLException
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.jvm.Throws

class BasicUserRepository : UserRepository {

    private val users = mutableMapOf<Long, User>()
    private val idgen = AtomicLong(0)
    private val localState = LocalDoorState.get()

    @Throws(SQLException::class)
    override fun create(user: User) {
        user.id = idgen.getAndIncrement()
        if(users.values.find { it.mail.equals(user.mail) } != null) {
            throw SQLException("Mail already exists. Unable to add a new user with the same mail")
        }

        users.put(user.id, user)
    }

    override fun update(user: User) {
    }

    override fun retrieve(id: Long): User? {
        return users.get(id)
    }

    override fun delete(user : User) {
        users.remove(user.id)
    }

    override fun getByState(state: UserState): List<User> {
        return users.values.filter { it.state == state }
    }

    override fun getFirstInState(state: UserState): User? {
        return users.values.filter { it.state == state }.sortedBy { it.time }.firstOrNull()
    }

    override fun getByMail(mail: String): User? {
        return users.values.find { it.mail.equals(mail) }
    }

    override fun getByToken(token : String) : Optional<User> {
        return Optional.ofNullable(users.values.find { it.token.equals(token) })
    }

}