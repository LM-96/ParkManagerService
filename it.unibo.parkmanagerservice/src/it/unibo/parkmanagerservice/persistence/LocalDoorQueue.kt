package it.unibo.parkmanagerservice.persistence

import it.unibo.parkmanagerservice.bean.User

class LocalDoorQueue : DoorQueue {

    private val queue = ArrayDeque<User>()

    override fun addUser(user: User) {
        queue.add(user)
    }

    override fun pullNextUser(): User? {
        return queue.removeFirstOrNull()
    }

    override fun remaining(): Int {
        return queue.size
    }
}