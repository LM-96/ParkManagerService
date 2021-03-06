/* Generated by AN DISI Unibo */ 
package it.unibo.fanactor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Fanactor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
				val fan = it.unibo.basicdevices.DeviceManager.requestDevice("fan")
				lateinit var STATE : it.unibo.basicfan.FanState
				var JSON : String
				
				if(fan == null) {
					println("$name | unable to use the fan")
					System.exit(-1)
				}
				
				fan as it.unibo.basicfan.Fan
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
						 
									fan.powerOff() 
									STATE = `it.unibo.basicfan`.FanState.OFF
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						 JSON = "{\"data\":\"${STATE}\"}" 
						updateResourceRep( STATE.toString()  
						)
						println("$name | fan state : ${STATE.toString()}")
					}
					 transition(edgeName="t02",targetState="poweron",cond=whenDispatch("fanon"))
					transition(edgeName="t03",targetState="poweroff",cond=whenDispatch("fanoff"))
				}	 
				state("poweron") { //this:State
					action { //it:State
						 	fan.powerOn() 
									STATE = `it.unibo.basicfan`.FanState.ON
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("poweroff") { //this:State
					action { //it:State
						 	fan.powerOff()
									STATE = `it.unibo.basicfan`.FanState.OFF
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
