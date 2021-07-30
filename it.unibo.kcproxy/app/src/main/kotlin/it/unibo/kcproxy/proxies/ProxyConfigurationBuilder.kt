package it.unibo.kcproxy.proxies

import it.unibo.kcproxy.configuration.ConfigEntry
import it.unibo.kcproxy.proxiesS.WsCoroutinedProxy

object ProxyConfigurationBuilder {

    @JvmStatic fun buildProxies(configEntries : Collection<ConfigEntry>) : Collection<CoapProxy> {
        val proxies = mutableListOf<CoapProxy>()
        configEntries.forEach() {
            when(it.fwprotocol) {
                "tcp" -> {
                    when(it.proxytype) {
                        "coroutined" -> {
                            proxies.add(TcpCoroutinedCoapProxy(it.coapUrl, it.fwport!!))
                            println("ProxyConfigurationBuilder | Builded TCP-coroutined proxy by configutation $it")
                        }
                        else -> println("ProxyConfigurationBuilder | Detected unsupported configuration $it")
                    }
                }
                "ws" -> {
                    when(it.proxytype) {
                        "coroutined" -> {
                            proxies.add(WsCoroutinedProxy(it.coapUrl, it.fwwsurl!!))
                            println("ProxyConfigurationBuilder | Builded WS-coroutined proxy by configutation $it")
                        }
                    }
                }
                else -> println("ProxyConfigurationBuilder| Detected unsupported configuration $it")
            }
        }

        return proxies
    }
}