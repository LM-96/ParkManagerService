package it.unibo.parkmanagerservice.trolley

class CompleteTripBuilder(toInX : Int, toInY : Int, toOutX : Int, toOutY : Int) {

    private var stages = ArrayDeque<TripStage>()
    private var toInPos = Pair(toInX, toInY)
    private var toOutPos = Pair(toOutX, toOutY)

    fun addStage(type : TripStageType, xDest : Int, yDest : Int) : CompleteTripBuilder {
        stages.add(TripStage(type, Pair(xDest, yDest)))
        return this
    }

    fun addLoadCar() : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.LOAD_CAR, Pair(-1, -1)))
        return this
    }

    fun addUnloadCar() : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.UNLOAD_CAR, Pair(-1, -1)))
        return this
    }

    fun addReturnToHome() : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.MOVING_TO_HOME, Pair(0, 0)))
        return this
    }

    fun addMoveToSlot(x : Int, y : Int) : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.MOVING_TO_SLOT, Pair(x, y)))
        return this
    }

    fun addMoveToIn() : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.MOVING_TO_IN, toInPos))
        return this
    }

    fun addMoveToOut() : CompleteTripBuilder {
        stages.add(TripStage(TripStageType.MOVING_TO_OUT, toOutPos))
        return this
    }

    fun clear() : CompleteTripBuilder {
        stages.clear()
        return this
    }

    fun addParkTrip(parkX : Int, parkY : Int) : CompleteTripBuilder {
        return this.addMoveToIn().addLoadCar().addMoveToSlot(parkX, parkY)
                        .addUnloadCar().addReturnToHome()
    }

    fun addPickupTrip(pickX : Int, pickY : Int) : CompleteTripBuilder {
        return this.addMoveToSlot(pickX, pickY).addLoadCar()
                        .addMoveToOut().addUnloadCar().addReturnToHome()
    }

    fun build() : Iterator<TripStage> {
        return stages.iterator()
    }

}