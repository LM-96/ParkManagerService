package it.unibo.kcproxy.proxies

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.*
import kotlin.coroutines.EmptyCoroutineContext

class WsCoroutinedProxy2(coapUrl : String, fwwsurl: String) : AbstractCoapProxy(coapUrl) {

    private val scope = CoroutineScope(EmptyCoroutineContext + CoroutineName("proxy[$coapUrl]"))
    private var ws : WebSocket? = null
    private val fwwsurl = fwwsurl
    private var client : OkHttpClient? = null
    private var confirmationChan : Channel<Char> = Channel(10)
    private var listener : CouroutinedWsListener? = null

    override fun implStartProxy() {
        client = OkHttpClient()
        listener = CouroutinedWsListener(confirmationChan)
        val request = Request.Builder().url(fwwsurl).build()
        ws = client!!.newWebSocket(request, listener!!)

        var received : Char? = null
        val confirm = scope.launch {
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

        if(received != 's') {
            println("WsCoroutinedProx | Error during connection with websocket")
        }
    }

    override fun onCoap(msg: String) {
        scope.launch {
            ws!!.send(msg)
        }
    }

    override fun getProxyDescription(): String {
        return "Type: Coroutine, Protocol: WS, Port: $fwwsurl"
    }

    override fun implStopProxy() {
        ws!!.close(1000, "terminated")
        scope.cancel()
        client = null
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
    }
}
