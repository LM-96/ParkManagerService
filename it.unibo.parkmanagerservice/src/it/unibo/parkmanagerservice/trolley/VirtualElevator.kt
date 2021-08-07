package it.unibo.parkmanagerservice.trolley

import okhttp3.*

class VirtualElevator(sonarUrl : String, weightsensorUrl : String) : TrolleyElevator {

    private val sonarUrl = sonarUrl
    private val weightsensorUrl = weightsensorUrl

    private val sonarWr = WebSocketWriter(sonarUrl)
    private val weightsensorWr = WebSocketWriter(weightsensorUrl)

    override fun loadCar() {
        TODO("Not yet implemented")
    }

    override fun leaveCar() {
        TODO("Not yet implemented")
    }

}

private class WebSocketWriter(url : String) {

    private val url = url;
    private val client = OkHttpClient()
    private var ws : WebSocket? = null
    private val listener = SimpleWebSocketListener()

    fun connect() {
        val req = Request.Builder().url(url).build()
        ws = client.newWebSocket(req, listener)
    }

    fun write(text : String) {
        ws?.send(text)
    }

}

private class SimpleWebSocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        println("VirtualElevator | WebSocket opened: ${webSocket}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
    }

}