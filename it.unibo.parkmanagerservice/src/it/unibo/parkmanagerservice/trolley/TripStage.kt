package it.unibo.parkmanagerservice.trolley

data class TripStage (
    val type: TripStageType,
    val destination : Pair<Int, Int>
)