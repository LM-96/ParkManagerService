/* Generated by AN DISI Unibo */ 
package it.unibo.parkingmanagerservice

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Parkingmanagerservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		  val SLOT_MANAGER = it.unibo.parkingslot.SimpleParkingSlotManager(1)
				var indoorFree = true
				var outdorFree = true  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("$name | waiting for request...")
					}
					 transition(edgeName="t3",targetState="handleEnter",cond=whenRequest("enter"))
					transition(edgeName="t4",targetState="handleCarEnter",cond=whenRequest("carenter"))
					transition(edgeName="t5",targetState="handlePickup",cond=whenRequest("pickup"))
				}	 
				state("handleEnter") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 var SLOTNUM = SLOT_MANAGER.getFreeSlot()  
						println("$name | replying enter request win [SLOTNUM = $SLOTNUM]")
						answer("enter", "slotnum", "slotnum($SLOTNUM)"   )  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleCarEnter") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("carenter(SLOTNUM)"), Term.createTerm("carenter(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								  var TOKEN = payloadArg(0).toInt()
												SLOT_MANAGER.occupySlot(TOKEN)	 
								println("$name | generated TOKEN=$TOKEN")
								answer("carenter", "token", "token($TOKEN)"   )  
								println("$name | trolley will take the car")
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handlePickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("pickup(TOKEN)"), Term.createTerm("pickup(TOKEN)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	var TOKEN = payloadArg(0) 
												var slotnum_free = SLOT_MANAGER.freeSlotByToken(TOKEN)  
								println("$name | trolley will transport car in the outdoor")
								answer("pickup", "canPickup", "canPickup(OK)"   )  
								println("$name | slot $slotnum_free is going to be free")
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
