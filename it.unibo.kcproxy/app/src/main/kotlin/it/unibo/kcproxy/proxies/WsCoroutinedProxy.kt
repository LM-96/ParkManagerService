package it.unibo.kcproxy.proxiesS

import it.unibo.kcproxy.proxies.AbstractCoroutinedCoapProxy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.*


class WsCoroutinedProxy(coapUrl : String, fwwsurl: String) : AbstractCoroutinedCoapProxy(coapUrl) {

    private var ws : WebSocket? = null
    private val fwwsurl = fwwsurl
    private var client : OkHttpClient? = null
    private var confirmationChan : Channel<Char> = Channel(10)
    private var listener : CouroutinedWsListener? = null

    override fun implStartProxy2() {
        client = OkHttpClient()
        listener = CouroutinedWsListener(confirmationChan)
        val request = Request.Builder().url(fwwsurl).build()
        ws = client!!.newWebSocket(request, listener!!)

        var received : Char? = null
        val confirm = getScope().launch {
            withTimeout(2000) {
                received = confirmationChan.receive()
            }
        }
        runBlocking {
            confirm.join()
            received ?: {
                println("WsCoroutinedProxy | Unable to connect to websocket")
                super.stopProxy()
            }
        }

        if(received!! != 's') {
            println("WsCoroutinedProx | Error during connection with websocket")
        }

        CoroutineWsWriter(getScope(), newCoapReceiveChannel(), ws!!).start()
    }

    override fun implStopProxy2() {
        ws!!.close(1000, "terminated")
    }

    override fun getProxyDescription(): String {
        return "Type: Coroutine, Protocol: WS, Port: $fwwsurl"
    }

    override fun getForwardUrl(): String {
        return fwwsurl
    }
}

private class CouroutinedWsListener(confirmationChan : Channel<Char>) : WebSocketListener() {

    val confirmationChan = confirmationChan

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        println("CouroutinedWsListener | Connected ws $webSocket")

        runBlocking {
            confirmationChan.send('s')
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        runBlocking {
            confirmationChan.send('c')
        }
    }
}

private class CoroutineWsWriter(scope : CoroutineScope, channel : ReceiveChannel<String>, ws : WebSocket) {
    private var ws = ws
    private var channel = channel
    private var scope = scope

    fun start() {
        scope.launch {
            var string : String
            while(!channel!!.isClosedForReceive) {
                string = channel!!.receive()
                ws.send(string)
            }
        }
    }
}