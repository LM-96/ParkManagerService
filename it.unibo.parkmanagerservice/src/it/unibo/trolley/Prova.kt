/*package it.unibo.trolley

import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker
import itunibo.planner.plannerUtil
import it.unibo.parkmanagerservice.actorchat.ActorChatter
import it.unibo.parkmanagerservice.actorchat.TcpActorChatter
import mapRoomKotlin.Box
import mapRoomKotlin.mapUtil

fun main() {
		var chatter = ActorChatter.Companion.newChatterFor("localhost", 8020, "basicrobot")
		loadMapFromTxt("resources/parking_map.txt")
		plannerUtil.initAI()
		plannerUtil.setGoal(4, 4)
		var plan = plannerUtil.doPlan()
		for(p in plan!!.iterator()) {
			if(p.toString().equals("w")) {
				chatter.sendRequest("step", "200")
				println("Prova | received step res = ${chatter.readLastResponse()}")
			} else
				chatter.sendDispatch("cmd", p.toString())
			plannerUtil.doMove(p.toString())
			plannerUtil.showMap()
		}

		//plannerUtil.resetActions()
		plannerUtil.setGoal(2, 0)
		plan = plannerUtil.doPlan()
		for(p in plan!!.iterator()) {
			if(p.toString().equals("w")) {
				chatter.sendRequest("step", "200")
				println("Prova | received step res = ${chatter.readLastResponse()}")
			} else
				chatter.sendDispatch("cmd", p.toString())
			plannerUtil.doMove(p.toString())
			plannerUtil.showMap()
		}
		
	}*/