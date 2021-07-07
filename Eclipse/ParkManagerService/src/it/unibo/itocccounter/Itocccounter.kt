/* Generated by AN DISI Unibo */ 
package it.unibo.itocccounter

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Itocccounter ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 	
				val state : it.unibo.parkingstate.StateReader = it.unibo.parkingstate.MockState
				val ITOCC = 2000L
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("$name | working...")
					}
					 transition(edgeName="t3",targetState="count",cond=whenDispatch("startItoccCounter"))
				}	 
				state("count") { //this:State
					action { //it:State
						println("$name | start ITOCC count...")
						stateTimer = TimerActor("timer_count", 
							scope, context!!, "local_tout_itocccounter_count", ITOCC )
					}
					 transition(edgeName="t4",targetState="reached",cond=whenTimeout("local_tout_itocccounter_count"))   
					transition(edgeName="t5",targetState="count",cond=whenDispatch("startItoccCounter"))
				}	 
				state("reached") { //this:State
					action { //it:State
						 if(state.getIndoorState().equals(`it.unibo.parkingstate`.DoorState.FREE)) { 
						println("$name | ITOCC reached and indoor is free... Client should be notified")
						 }  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
