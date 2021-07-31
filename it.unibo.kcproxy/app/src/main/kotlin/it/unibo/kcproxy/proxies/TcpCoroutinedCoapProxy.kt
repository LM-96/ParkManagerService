package it.unibo.kcproxy.proxies

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import kotlin.jvm.Throws
import kotlin.reflect.KFunction

class TcpCoroutinedCoapProxy(coapUrl : String, fwport : Int) : AbstractCoroutinedCoapProxy(coapUrl){

    private var serverSocket : ServerSocket? = null
    private val fwport = fwport
    private var acceptServer : AcceptServer? = null

    @Throws(IllegalStateException::class, IOException::class)
    override fun implStartProxy2() {
        serverSocket = ServerSocket(fwport)
        serverSocket!!.reuseAddress = true
        acceptServer = AcceptServer(getScope(), serverSocket!!, this::newCoapReceiveChannel)

        acceptServer!!.start()

    }

    override fun implStopProxy2() {
        serverSocket!!.close()
    }

    override fun getProxyDescription(): String {
        return "Type: Coroutine, Protocol: TCP, Port: $fwport"
    }

    override fun getForwardUrl(): String {
        return "${InetAddress.getLocalHost().hostAddress}/$fwport"
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
                while(!channel!!.isClosedForReceive) {
                    string = channel!!.receive()
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

private class AcceptServer(scope : CoroutineScope, serverSocket: ServerSocket, channelBuilder : () -> (ReceiveChannel<String>)) {
    private val serverSocket = serverSocket
    private val scope = scope
    private val channelBuilder = channelBuilder

    fun start() {
        scope.launch {
            var socket : Socket
            try {
                while (true) {
                    socket = serverSocket.accept()
                    CoroutineSocketWriter(scope, channelBuilder.invoke(), socket).start()

                    println("AcceptServer[$scope] | Accepted connection $socket.")
                }
            } catch (ioe : IOException) {
                if(serverSocket.isClosed)
                    println("TcpCoroutinedCoapProxy | Closed socket [$serverSocket]")
                else
                    ioe.printStackTrace()
            }
        }

    }
}