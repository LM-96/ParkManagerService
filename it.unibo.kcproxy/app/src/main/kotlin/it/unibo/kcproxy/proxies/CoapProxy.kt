package it.unibo.kcproxy.proxies

interface CoapProxy {

    fun startProxy()
    fun stopProxy()
    fun getForwardUrl() : String
    fun getCoapUrl() : String

    fun isStarted() : Boolean

}