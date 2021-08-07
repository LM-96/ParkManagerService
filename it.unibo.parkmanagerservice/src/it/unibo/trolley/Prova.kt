package it.unibo.trolley

import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker
import itunibo.planner.plannerUtil
import it.unibo.parkmanagerservice.actorchat.ActorChatter
import it.unibo.parkmanagerservice.actorchat.TcpActorChatter
import mapRoomKotlin.Box
import mapRoomKotlin.mapUtil
import org.json.JSONObject

fun main() {
	val JSON = JSONObject()
	JSON.put("state", "IDLE")

	val POS = JSONObject()
	POS.put("x", 0)
	POS.put("y", 0)

	JSON.put("position", POS)

	println(JSON)
}