package it.unibo.trolley

import mapRoomKotlin.mapUtil

import mapRoomKotlin.Box
import itunibo.planner.plannerUtil
import mapRoomKotlin.RoomMap
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.FileInputStream

val room = mapUtil.map

fun loadMapFromBin(file : String) {
	plannerUtil.loadRoomMap(file)
}

fun loadMapFromTxt(file : String) {
	val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
	var line = reader.readLine()
	
	while(line != null) {
		
		
		line = reader.readLine()
	}
}

private fun parseLine(line : String) {
	if(!line.startsWith("|")) {
		println("MapLoader | Invalid line \"$line\". Please give a valid TXT")
		System.exit(-1)
	}
	
	val cells = line.split(",")
	for(c in cells) {
		when(c.trim()) {
			"0" -> mapUtil.map.put()
		}
			
	}
}

fun main() {
	
	plannerUtil.loadRoomMap("resources/parking_map")
	mapUtil.showMap()
	
	
}