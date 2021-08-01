package it.unibo.kcproxy.proxies

import it.unibo.kcproxy.configuration.ConfigEntry
import it.unibo.kcproxy.proxiesS.WsCoroutinedProxy

object ProxyConfigurationBuilder {

    @JvmStatic fun buildProxies(configEntries : Collection<ConfigEntry>) : Collection<CoapProxy> {
        return configEntries
            .map { ProxyFactory.create(it.fwprotocol, it.proxytype, it.coapUrl, it.fwwsurl, it.fwport) }
            .filterNotNull()
            .toList()
    }
}