package it.unibo.trolley

import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker
import itunibo.planner.plannerUtil
import it.unibo.parkmanagerservice.actorchat.ActorChatter
import it.unibo.parkmanagerservice.actorchat.TcpActorChatter
import mapRoomKotlin.Box
import mapRoomKotlin.mapUtil
import org.json.JSONObject

fun main() {
	val JSON = JSONObject("{\"indoor\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"outdoor\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"1\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"2\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"3\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"4\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"5\":{\"state\":\"FREE\", \"user\":\"\"}, " +
			" \"6\":{\"state\":\"FREE\", \"user\":\"\"}}")
	JSON.put
	println(JSON)
	JSON.put("state", "WORKING")
	println(JSON)
}