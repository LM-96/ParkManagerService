package it.unibo.parkmanagerservice.trolley

import mapRoomKotlin.mapUtil

import mapRoomKotlin.Box
import itunibo.planner.plannerUtil
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.FileInputStream

object MapLoader {
	private val room = mapUtil.map

	fun loadMapFromBin(file : String) {
		plannerUtil.loadRoomMap(file)
	}

	fun loadMapFromTxt(file : String) {
		val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
		var line = reader.readLine()

		var r = 0
		var toks : List<String>
		var cols = 0
		var box : Box? = null
		var s : String

		while(line != null) {

			line = line.trim()
			if(line.endsWith(","))
				line = line.substring(0..(line.length - 2))

			toks = line.trim().split(",")
			if(r == 0)
				cols = toks.size - 1
			else
				if((toks.size-1) != cols) {
					println("MapLoader | Bad formatted txt. Aborting")
					System.exit(-1)
				}

			for(c in 0..cols) {
				s = toks[c].trim()
				if(s.length == 1) box = parseBox(s.get(0))
				else if(s.length == 2 && s.get(0) == '|') box = parseBox(s.get(1))
				else {
					println("MapLoader | Invalid length for sequence \'$s\' in line ${r+1}")
					System.exit(-1)
				}

				if(box != null)
					room.put(c, r, box)
				if(box == null) {
					println("MapLoader | Invalid sequence \'$s\' in line ${r+1}")
					System.exit(-1)
				}
			}

			line = reader.readLine()
			r++
		}
	}

	private fun parseBox(c : Char) : Box? {
		when(c) {
			'0' -> return Box(false, true, false)
			'r' -> return Box(false, false, true)
			'1' -> return Box(false, false, false)
			'X' -> return Box(true, false, false)
		}

		return null
	}
}