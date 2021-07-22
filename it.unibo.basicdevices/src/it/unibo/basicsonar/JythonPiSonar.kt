package it.unibo.basicsonar

import org.python.util.PythonInterpreter;
import org.python.core.PyInteger
import org.python.core.PyCode
import org.python.core.PyFunction

class JythonPiSonar(id: String, echoPin : Int, trigPin : Int) : Sonar(id) {
	
	private val echoPin = echoPin //17
	private val trigPin = trigPin //27
	private val pyGetCM : PyFunction

	init {
		val python = PythonInterpreter()
		python.exec("from resources import PiSonar")
		println("${python.get("PiSonar.welcome()")}")
		python.exec("PiSonar.setup($echoPin, $trigPin)")
		pyGetCM = python.get("PiSonar.getCM") as PyFunction

	}

	override fun readDistance() : Int {
		return (pyGetCM.__call__() as PyInteger).asInt()
	}
	
}