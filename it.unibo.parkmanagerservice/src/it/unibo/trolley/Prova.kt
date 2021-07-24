package it.unibo.trolley

import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker
import itunibo.planner.plannerUtil
	
	fun main() {
		val speaker = TcpActorSpeaker("localhost", 8020, "basicrobot")
		loadMapFromTxt("resources/parkin_map.txt")
		plannerUtil.setGoal(2, 1)
		var i = plannerUtil.get_actionSequence()
		if(i != null) {
			while(i.hasNext()) {
				//speaker.sendDispatch("cmd", i.next().toString())
				println(i.next().toString())
			}
		}
	}