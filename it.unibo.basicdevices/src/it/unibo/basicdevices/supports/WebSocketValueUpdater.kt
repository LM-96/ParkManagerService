package it.unibo.basicdevices.supports

import okhttp3.WebSocketListener
import okhttp3.WebSocket
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.Request
import org.json.JSONObject

class WebSocketValueUpdater<T>(value : T, address : String, converter : (String) -> T) : AbstractValueUpdater<T>(value) {
	
	private val address = address
	private var ws : WebSocket? = null
	private val client = OkHttpClient()
	private val listener = ValueWebSocketListener(this.value, converter)
	private val working = AtomicBoolean(false)
	
	override fun start() {
		if(!working.getAndSet(true)) {
			val request = Request.Builder()
				.url(address)
				.build()
			ws = client.newWebSocket(request, listener)
			
		}
	}
	
	override fun suspend() {
		if(working.getAndSet(false)) {
			ws?.close(1000, "appl_terminated")
		}
	}
	
	override fun close() {
		suspend()
		
		client.dispatcher.executorService.shutdown()
		client.connectionPool.evictAll()
	}
	
	fun say(text : String) {
		ws?.send(text)
	}
	
}

private class ValueWebSocketListener<T>(value : LockableValue<T>, converter : (String) -> T) : WebSocketListener() {
	
	val value = value
	val converter = converter
	lateinit var url : String
	
	override fun onOpen(webSocket : WebSocket, response : Response) {
		url = response.request.url.toString()
		println("WebSocketValueUpdater[${url}] | WebSocket connected")
	}
	
	override fun onMessage(webSocket: WebSocket, text: String) {
		println("WebSocketValueUpdater[${url}] | received text [$text]")
		value.safeSet(converter(text))
	}
	
}