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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.Throws

@kotlinx.coroutines.ExperimentalCoroutinesApi
class CoroutinedTcpCoapProxy(coapUrl : String, fwport : Int) : CoapProxy {

    private val coapUrl = coapUrl
    private val fwport = fwport
    private val scope = CoroutineScope(EmptyCoroutineContext + CoroutineName("proxy[$coapUrl]"))

    private var coapClient : CoapClient? = null
    private var socket : ServerSocket? = null
    private var channel : BroadcastChannel<String>? = null

    private var observer : CoapObserver? = null
    private var started = AtomicBoolean(false)
    private var acceptServer : AcceptServer? = null

    init {
        println("CoroutinedTcpCoapProxy[$coapUrl] | Created coroutine scope $scope")
    }


    @Throws(IllegalStateException::class,IOException::class)
    override fun startProxy() {
        if(started.compareAndSet(false, true)) {
            coapClient = CoapClient()
            coapClient!!.setURI(coapUrl)

            socket = ServerSocket(fwport)
            socket!!.reuseAddress = true
            channel = BroadcastChannel(10)
            acceptServer = AcceptServer(scope, socket!!, channel!!)
            acceptServer!!.start()


            observer = CoapObserver(scope, coapClient!!, channel!!)
            observer!!.start()

            println("CoroutinedTcpCoapProxy[$coapUrl] | Started TCP proxy at port $fwport")
        } else
            throw java.lang.IllegalStateException("CoroutinedTcpCoapProxy[$coapUrl] | The proxy is already started")


    }

    @Throws(IllegalStateException::class)
    override fun stopProxy() {
        if(started.compareAndSet(true, false)) {
            socket = null
            channel = null
            acceptServer = null
            observer = null
            coapClient?.shutdown()
            scope.cancel()

            println("CoroutinedTcpCoapProxy[$coapUrl] | Ended TCP proxy at port $fwport")
        } else
            throw IllegalStateException("CoroutinedTcpCoapProxy[$coapUrl] | The proxy is not started")
    }

    override fun getForwardPort(): Int {
        return fwport
    }

    override fun getCoapUrl(): String {
        return coapUrl
    }

}

private class CoroutineSocketWriter(scope : CoroutineScope, channel : ReceiveChannel<String>, socket : Socket) {
    private var socket = socket
    private var channel = channel
    private var scope = scope

    private var writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    fun start() {
        scope.launch {
            try {
                var string : String
                while(!channel.isClosedForReceive) {
                    string = channel.receive()
                    writer.write(string + "\n")
                    writer.flush()
                }
            }
            catch (socket : SocketException) {
                println("CoroutineSocketWriter | Connection closed for socket $socket")
            }
            catch (close : CancellationException) { }
        }
    }
}

private class CoapObserver(scope: CoroutineScope, client : CoapClient, channel : BroadcastChannel<String>) {

    private val client = client
    private val channel = channel
    private val scope = scope
    private val queue = ArrayBlockingQueue<String>(10)

    fun start() {
        client.observe(object : CoapHandler {
            override fun onLoad(response: CoapResponse?) {
                queue.put(response?.responseText)
                println("CoapObserver[${client.uri}] | Received update: \'${response?.responseText}\'")
            }

            override fun onError() {
                queue.put("CoapHandler error")
            }

        })

        scope.launch {
            while(true) {
                var string = queue.take()
                channel.send(string)
            }
        }
    }

}

private class AcceptServer(scope : CoroutineScope, serverSocket: ServerSocket, channel : BroadcastChannel<String>) {
    private val serverSocket = serverSocket
    private val scope = scope
    private val channel = channel

    fun start() {
        scope.launch {
            var socket : Socket
            while (true) {
                socket = serverSocket.accept()
                CoroutineSocketWriter(scope, channel.openSubscription(), socket).start()

                println("AcceptServer[$scope] | Accepted connection $socket.")
            }
        }

    }
}