/* Generated by AN DISI Unibo */ 
package it.unibo.thermometeractor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Thermometeractor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 	val thermometer = it.unibo.basicdevices.DeviceManager.requestDevice("thermometer")
				
				if(thermometer == null) {
					println("$name | unable to use the thermometer")
					System.exit(-1)
				}
				
				thermometer as it.unibo.basicthermometer.Thermometer
				var temp = 0.0
				var tempState = `it.unibo.basicthermometer`.TemperatureState.NORMAL
				val POLLING_TIME = it.unibo.basicthermometer.Thermometer.getPollingMillis()
				val CRITICAL_TEMP = it.unibo.basicthermometer.Thermometer.getCriticalTemp()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						 
									temp = thermometer.readTemperature()
									if(temp >= CRITICAL_TEMP
										&& tempState == `it.unibo.basicthermometer`.TemperatureState.NORMAL) {
											tempState = `it.unibo.basicthermometer`.TemperatureState.CRITICAL		
						emit("criticaltemp", "criticaltemp(CRITICAL)" ) 
						 } else if(temp < CRITICAL_TEMP
										&& tempState == `it.unibo.basicthermometer`.TemperatureState.CRITICAL) {
											tempState = `it.unibo.basicthermometer`.TemperatureState.NORMAL
						emit("criticaltemp", "criticaltemp(NORMAL)" ) 
						 }  
						updateResourceRep( temp.toString()  
						)
						stateTimer = TimerActor("timer_work", 
							scope, context!!, "local_tout_thermometeractor_work", POLLING_TIME )
					}
					 transition(edgeName="t00",targetState="work",cond=whenTimeout("local_tout_thermometeractor_work"))   
					transition(edgeName="t01",targetState="work",cond=whenDispatch("updatethermometerstate"))
				}	 
			}
		}
}
