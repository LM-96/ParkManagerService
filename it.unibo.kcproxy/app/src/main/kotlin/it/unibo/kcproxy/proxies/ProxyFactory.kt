package it.unibo.kcproxy.proxies

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute
import kotlin.jvm.Throws

object ProxyFactory {

    private val red = AnsiFormat(Attribute.RED_TEXT())

    @Throws(IllegalArgumentException::class)
    fun create(protocol : String, type : String, coapaddr: String, fwwsurl : String?, fwport : Int?) : CoapProxy? {
        when(protocol) {
            "tcp" -> {
                if(fwport == null) {
                    println(red.format("ProxyFactory | Requested Tcp Proxy for \'$coapaddr\' but port is null"))
                    return null
                }
                when(type) {
                    "coroutined" -> return TcpCoroutinedCoapProxy(coapaddr, fwport!!)
                }
            }
            "ws" -> {
                if(fwwsurl == null) {
                   println(red.format("ProxyFactory | Requested WebSocket Proxy for \'$coapaddr\' but WebSocket address is null"))
                    return null
                }
                when(type) {
                    "coroutined" -> return WsCoroutinedProxy2(coapaddr, fwwsurl!!)
                }
            }
        }

        println(red.format("ProxyFactory | Requested invalid Proxy for \'$coapaddr\' [type=$type, protocol=$protocol]"))
        return null
    }

}