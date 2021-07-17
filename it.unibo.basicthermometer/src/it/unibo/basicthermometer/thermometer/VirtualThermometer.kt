package it.unibo.basicthermometer.thermometer

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

class VirtualThermometer () : Thermometer {
	
	var temperature : Double = 0.0
	
	
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun VirtualThermometer.module() {
		install(WebSockets)
    routing {
        webSocket("/chat") {
            send("You are connected!")
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                send("You said: $receivedText")
            }
        }
    }
}
	
	override fun readTemperature() : Double {
		return temperature
	}
	
}