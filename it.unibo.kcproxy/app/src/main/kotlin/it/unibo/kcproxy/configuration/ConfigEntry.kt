package it.unibo.kcproxy.configuration

data class ConfigEntry (
    val resource : String,
    val coapUrl : String,
    val fwport : Int,
    val fwprotocol : String,
    val proxytype : String
        )