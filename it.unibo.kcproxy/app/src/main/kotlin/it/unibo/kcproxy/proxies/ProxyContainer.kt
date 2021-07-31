package it.unibo.kcproxy.proxies

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute

object ProxyContainer {

    private val proxies = mutableListOf<CoapProxy>()
    private val red = AnsiFormat(Attribute.RED_TEXT())

    fun register(coapProxy: CoapProxy) : ProxyContainer {
        proxies.add(coapProxy)

        return this
    }

    fun registerAll(coapProxies : Collection<CoapProxy>) : ProxyContainer {
        proxies.addAll(coapProxies)

        return this
    }

    fun getByUrl(coapUrl : String) : CoapProxy? {
        return proxies.find { it.getCoapUrl().equals(coapUrl) }
    }

    fun getByForwardUrl(url : String) : CoapProxy? {
        return proxies.find { it.getForwardUrl().equals(url) }
    }

    fun remove(coapProxy: CoapProxy) {
        proxies.remove(coapProxy)
    }

    fun startAll() : Pair<Int, Int> {
        var started = 0
        proxies.forEach {
            try {
                it.startProxy()
                started++
            } catch (e : Exception) {
                println(red.format("ProxyContainer | Unable to start proxy for \'${it.getCoapUrl()}\' at url ${it.getForwardUrl()}: ${e.localizedMessage}"))
                it.stopProxy()
            }
        }

        return Pair(started, proxies.size)
    }

    fun stopAll() {
        proxies.forEach { if(it.isStarted()) it.stopProxy() }
    }

}