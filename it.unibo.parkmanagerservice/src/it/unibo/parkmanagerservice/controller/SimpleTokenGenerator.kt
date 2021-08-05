package it.unibo.parkmanagerservice.controller

import it.unibo.parkmanagerservice.bean.ParkingSlot
import it.unibo.parkmanagerservice.bean.User

/**
 * A simple class that implements [TokenGenerator] in order to generate token
 * without any complicated logic.
 */
class SimpleTokenGenerator : TokenGenerator {

    /**
     * Generate a token only using the [user] and the [slot] passed as arguments.
     * The token generated has the pattern '#UID$SLOTNUM' in wich:
     *
     * - UID is the id of the user;
     * - SLOTNUM is the slotnum of the slot.
     *
     * Notice that this method only generate the token but not persists
     * or saves it.
     */
    override fun generateToken(user: User, slot: ParkingSlot) : String {
        val token = "#${user.id}S${slot.slotnum}"
        println("TokenGenerator | Generated token \"$token\" for user [$user]")
        return token
    }
}