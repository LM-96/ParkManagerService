package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.User

/**
 * An interface for a token generator. An implementation of this
 * class should be used by an implementation of [ParkManagerServiceController]
 * in order to generate a token.
 */
interface TokenGenerator {

    /**
     * A singleton object to get the [TokenGenerator] used by the
     * system.
     */
    companion object {
        private var SINGLETON : TokenGenerator? = null

        /**
         * Return the unique [TokenGenerator] of the system as a singleton.
         *
         * @return the unique [TokenGenerator] of the system as a singleton.
         */
        fun get() : TokenGenerator {
            if(SINGLETON == null)
                SINGLETON = SimpleTokenGenerator()

            return SINGLETON!!
        }
    }

    /**
     * Generate a token for the given relationship [user]-[slot] and return it.
     * This method must only be used to generate token: it does not have persistence
     * logic, so the tooken is only returned and not memorized.
     */
    fun generateToken(user : User, slot : ParkingSlot) : String
}