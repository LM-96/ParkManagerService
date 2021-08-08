package it.unibo.kcproxy.proxies

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

import kotlin.jvm.Throws

abstract class AbstractCoapProxy(coapUrl : String) : CoapProxy {

    private val coapUrl = coapUrl
    private var started = AtomicBoolean(false)
    private var coapClient : CoapClient? = null

    protected abstract fun implStartProxy()
    protected abstract fun onCoap(msg : String)
    protected abstract fun getProxyDescription() : String
    protected abstract fun implStopProxy()

    private val red = AnsiFormat(Attribute.RED_TEXT())
    private val violet = AnsiFormat(Attribute.TEXT_COLOR(150, 90, 150))

    @Throws(IllegalStateException::class, IOException::class)
    override fun startProxy() {
        if(started.compareAndSet(false, true)) {
            coapClient = CoapClient()
            coapClient!!.setURI(coapUrl)

            implStartProxy()
            CoapListener(coapClient!!, this::onCoap).start()

            println(violet.format("AbstractCoapProxy[$coapUrl] | Started Proxy [${getProxyDescription()}]"))
        } else
            throw java.lang.IllegalStateException("AbstractCoapProxy[$coapUrl] | The proxy is already started")
    }

    @Throws(IllegalStateException::class)
    override fun stopProxy() {
        if(started.compareAndSet(true, false)) {

            implStopProxy()
            coapClient?.shutdown()

            println(violet.format("AbstractCoapProxy[$coapUrl] | Terminated Proxy [${getProxyDescription()}]"))
        } else
            throw IllegalStateException("AbstractCoapProxy[$coapUrl] | The proxy is not started")
    }

    override fun getCoapUrl(): String {
        return coapUrl
    }

    override fun isStarted() : Boolean {
        return started.get()
    }

    private class CoapListener(client : CoapClient, onCoap : ((String) -> (Unit))) {
        private val client = client
        private var onCoap = onCoap
        private val yellow = AnsiFormat(Attribute.YELLOW_TEXT())
        private val red = AnsiFormat(Attribute.RED_TEXT())

        fun start() {
            client.observe(object : CoapHandler {
                override fun onLoad(response: CoapResponse?) {
                    if(response?.responseText != null) {
                        onCoap.invoke(response.responseText)
                        println(yellow.format("CoapListener[${client.uri}] | Received update: \'${response?.responseText}\'"))
                    }
                }

                override fun onError() {
                    //onCoap.invoke("Coap Error")
                    println(red.format("CoapListener[${client.uri}] | Coap error... have you started the server?"))
                }

            })
        }
    }

}