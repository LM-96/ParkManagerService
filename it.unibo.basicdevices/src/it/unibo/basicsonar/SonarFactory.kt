package it.unibo.basicsonar

import java.nio.file.Paths
import java.nio.file.Files

object SonarFactory {
	
	fun create(id : String, type : SonarType, address : String?, echo : Int?, trig : Int?) : Sonar? {
		return when(type) {
			SonarType.VIRTUAL -> if(address != null) WsSonar(id, address) else null
			SonarType.JYTHON_PI -> if(echo != null && trig != null) JythonPiSonar(id, echo, trig) else null
			SonarType.C_PI -> if(echo != null && trig != null) {
				checkCPiCompiled()
				CPiSonar(id, echo, trig)
			} else null
			SonarType.SP_ARDUINO -> if(address != null && echo != null && trig != null) ArduinoSonar(id, address, echo, trig) else null
		}
	}
	
	//Auto compilation of C code for Raspberry
	private fun checkCPiCompiled() {
		val exec = Paths.get(CPiSonar.Companion.EXECUTABLE)
		if(!Files.exists(exec)) {
			println("SonarFactory | unable to find the executable for C sonar on Raspberry")
			val source = Paths.get(CPiSonar.Companion.SOURCE)
			if(Files.exists(source)) {
				Runtime.getRuntime().exec("g++  ${CPiSonar.Companion.SOURCE} -l wiringPi -o  ${CPiSonar.Companion.EXECUTABLE}")
				if(Files.exists(source))
					println("SonarFactory | ${CPiSonar.Companion.SOURCE} compiled into ${CPiSonar.Companion.EXECUTABLE}")
				else {
					println("SonarFactory | unable to compile ${CPiSonar.Companion.SOURCE}")
					System.exit(-1)
				}
			} else {
				println("SonarFactory | unable to find source code into ${CPiSonar.Companion.SOURCE}")
				System.exit(-1)
			}
		}
	}
}