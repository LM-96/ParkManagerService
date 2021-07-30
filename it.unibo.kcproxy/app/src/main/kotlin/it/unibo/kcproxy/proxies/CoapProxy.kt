package it.unibo.kcproxy.proxies

interface CoapProxy {

    fun startProxy()
    fun stopProxy()
    fun getForwardPort() : Int
    fun getCoapUrl() : String

}