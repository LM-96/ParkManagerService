package it.unibo.basicsonar

import kotlinx.coroutines.channels.Channel
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.concurrent.ArrayBlockingQueue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CPiSonar(id : String, echoPin : Int, trigPin : Int) : Sonar(id) {
	
	companion object {
		@JvmStatic val EXECUTABLE = "resources/PiSonarAlone"
		@JvmStatic val SOURCE = "resources/PiSonarAlone.c"
	}
	
	private val echoPin = echoPin
	private val trigPin = trigPin
	private val proc : Process
	private val sonarAloneChan : SonarAloneSource
	private val pid : Int
	
	init {
		proc = Runtime.getRuntime().exec("$EXECUTABLE -t $trigPin -e $echoPin -i")
		sonarAloneChan = SonarAloneSource(proc.getInputStream())
		pid = sonarAloneChan.getNext()
	}
	
	override fun readDistance() : Int {
		Runtime.getRuntime().exec("kill -10 $pid")
		return sonarAloneChan.getNext()
	}
	
}

private class SonarAloneSource(inputStream : InputStream) {
	
	private val input = BufferedReader(InputStreamReader(inputStream))
	private val queue = ArrayBlockingQueue<Int>(1)
	
	init{
		start()
	}
	
	fun getNext() : Int {
		return queue.take()
	}
	
	private fun start() {
		GlobalScope.launch {
			var line = input.readLine()
			while(line != null) {
				queue.put(line.toInt())			
			}
		}
	}
}