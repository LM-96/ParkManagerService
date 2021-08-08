package it.unibo.parkmanagerservice.actorchat

interface EventWriter {

    fun writeEvent(type : String, content : String)
}