package it.unibo.basicsonar

import kotlinx.coroutines.channels.Channel
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.concurrent.ArrayBlockingQueue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
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
		println("CPiSonar | started $EXECUTABLE with pid=$pid")
	}
	
	override fun readDistance() : Int {
		Runtime.getRuntime().exec("kill -10 $pid").waitFor()
		return sonarAloneChan.getNext()
	}
	
}

private class SonarAloneSource(inputStream : InputStream) : Thread() {
	
	private val input = BufferedReader(InputStreamReader(inputStream))
	private val queue = ArrayBlockingQueue<Int>(5)
	
	init{
		start()
	}
	
	fun getNext() : Int {
		return queue.take()
	}
	
	override fun run() {
		var line = input.readLine()
		while(line != null) {
			println("CPiSonar | readed data from PiSonarAlone:$line")
			queue.put(line.toInt())
			line = input.readLine()	
		}
	}
}