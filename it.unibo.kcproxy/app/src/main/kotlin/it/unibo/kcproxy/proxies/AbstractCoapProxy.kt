package it.unibo.kcproxy.proxies

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.Throws

abstract class AbstractCoapProxy(coapUrl : String) : CoapProxy {

    private val coapUrl = coapUrl
    private var started = AtomicBoolean(false)
    private var coapClient : CoapClient? = null

    protected abstract fun implStartProxy()
    protected abstract fun onCoap(msg : String)
    protected abstract fun getProxyDescription() : String
    protected abstract fun implStopProxy()

    @Throws(IllegalStateException::class, IOException::class)
    override fun startProxy() {
        if(started.compareAndSet(false, true)) {
            coapClient = CoapClient()
            coapClient!!.setURI(coapUrl)

            implStartProxy()
            CoapListener(coapClient!!, this::onCoap)

            println("CoroutinedTcpCoapProxy[$coapUrl] | Started Proxy [${getProxyDescription()}]")
        } else
            throw java.lang.IllegalStateException("CoroutinedTcpCoapProxy[$coapUrl] | The proxy is already started")
    }

    @Throws(IllegalStateException::class)
    override fun stopProxy() {
        if(started.compareAndSet(true, false)) {

            implStopProxy()
            coapClient?.shutdown()

            println("CoroutinedTcpCoapProxy[$coapUrl] | Terminated Proxy [${getProxyDescription()}]")
        } else
            throw IllegalStateException("CoroutinedTcpCoapProxy[$coapUrl] | The proxy is not started")
    }

    override fun getCoapUrl(): String {
        return coapUrl
    }

    fun isStarted() : Boolean {
        return started.get()
    }

    private class CoapListener(client : CoapClient, onCoap : ((String) -> (Unit))) {
        private val client = client
        private var onCoap = onCoap

        fun start() {
            client.observe(object : CoapHandler {
                override fun onLoad(response: CoapResponse?) {
                    if(response?.responseText != null) {
                        onCoap.invoke(response.responseText)
                        println("CoapListener[${client.uri}] | Received update: \'${response?.responseText}\'")
                    }
                }

                override fun onError() {
                    onCoap.invoke("Coap Error")
                    println("CoapListener[${client.uri}] | Coap error")
                }

            })
        }
    }


}