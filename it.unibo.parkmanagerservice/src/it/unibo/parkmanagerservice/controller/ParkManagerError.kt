package it.unibo.parkmanagerservice.controller

enum class ErrorType {
    INVALID_SLOTNUM, INVALID_TOKEN, INVALID_MAIL, NO_USER_AT_DOOR, DOOR_NOT_RESERVED,
    NO_RESERVATION_FOR_USER, NO_NAME, NO_SURNAME, NO_MAIL
}

data class ParkManagerError(
    val type : ErrorType,
    val msg : String) {

}