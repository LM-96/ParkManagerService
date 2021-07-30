package it.unibo.kcproxy.proxies

import it.unibo.kcproxy.configuration.ConfigEntry

object ProxyConfigurationBuilder {

    @JvmStatic fun buildProxies(configEntries : Collection<ConfigEntry>) : Collection<CoapProxy> {
        val proxies = mutableListOf<CoapProxy>()
        configEntries.forEach() {
            when(it.fwprotocol) {
                "tcp" -> {
                    when(it.proxytype) {
                        "coroutined" -> {
                            proxies.add(CoroutinedTcpCoapProxy(it.coapUrl, it.fwport))
                            println("ProxyConfigurationBuilder | Builded proxy by configutation $it")
                        }
                        else -> println("ProxyConfigurationBuilder | Detected unsupported configuration $it")
                    }
                }
                else -> println("ProxyConfigurationBuilder| Detected unsupported configuration $it")
            }
        }

        return proxies
    }
}