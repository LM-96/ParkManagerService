package it.unibo.kcproxy.proxies

object ProxyContainer {

    private val proxies = mutableListOf<CoapProxy>()

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

    fun getByPort(port : Int) : CoapProxy? {
        return proxies.find { it.getForwardPort() == port }
    }

    fun remove(coapProxy: CoapProxy) {
        proxies.remove(coapProxy)
    }

    fun startAll() {
        proxies.forEach {
            try {
                it.startProxy()
            } catch (e : Exception) {
                println("ProxyContainer | Unable to start proxy for \'${it.getCoapUrl()}\' at port ${it.getForwardPort()}: ${e.localizedMessage}")
            }
        }
    }

    fun stopAll() {
        proxies.forEach { it.stopProxy() }
    }

}