package it.unibo.parkmanagerservice.controller

/**
 * An enum class that fast specifies the nature of an error thrown in a
 * method of [ParkManagerServiceController]
 */
enum class ErrorType {
    INVALID_SLOTNUM, INVALID_TOKEN, INVALID_MAIL, NO_USER_AT_DOOR, DOOR_NOT_RESERVED,
    NO_RESERVATION_FOR_USER, NO_NAME, NO_SURNAME, NO_MAIL, MAIL_ALREADY_EXISTS
}

/**
 * A data class that memorizes an error thrown in a
 * method of [ParkManagerServiceController]
 */
data class ParkManagerError(
    val type : ErrorType,
    val msg : String) {

}