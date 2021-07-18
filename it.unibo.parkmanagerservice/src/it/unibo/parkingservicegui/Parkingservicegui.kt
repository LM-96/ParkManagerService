/* Generated by AN DISI Unibo */ 
package it.unibo.parkingservicegui

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Parkingservicegui ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 	var RECEIVED_SLOTNUM = 0
				var MY_TOKEN = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
					}
					 transition( edgeName="goto",targetState="requestToEnter", cond=doswitch() )
				}	 
				state("requestToEnter") { //this:State
					action { //it:State
						request("enter", "enter(hello)" ,"parkingmanagerservice" )  
						println("$name | sended request to enter")
					}
					 transition(edgeName="t0",targetState="receivedSlotnum",cond=whenReply("slotnum"))
				}	 
				state("receivedSlotnum") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("slotnum(SLOTNUM)"), Term.createTerm("slotnum(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RECEIVED_SLOTNUM = payloadArg(0).toInt()  
								println("$name | received reply with SLOTNUM=$RECEIVED_SLOTNUM")
						}
					}
					 transition( edgeName="goto",targetState="moveTheCar", cond=doswitchGuarded({ RECEIVED_SLOTNUM > 0  
					}) )
					transition( edgeName="goto",targetState="noAvailableSlot", cond=doswitchGuarded({! ( RECEIVED_SLOTNUM > 0  
					) }) )
				}	 
				state("moveTheCar") { //this:State
					action { //it:State
						delay(2000) 
						println("$name | client has moved the car in the INDOOR")
						request("carenter", "carenter($RECEIVED_SLOTNUM)" ,"parkingmanagerservice" )  
						println("$name | client has press CARENTER")
					}
					 transition(edgeName="t1",targetState="receivedToken",cond=whenReply("token"))
				}	 
				state("receivedToken") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("token(TOKEN)"), Term.createTerm("token(TOKEN)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 MY_TOKEN = payloadArg(0).toInt()  
								println("$name | received reply with TOKEN=$MY_TOKEN")
						}
					}
					 transition( edgeName="goto",targetState="requestToPickUp", cond=doswitch() )
				}	 
				state("requestToPickUp") { //this:State
					action { //it:State
						delay(5000) 
						request("pickup", "pickup($MY_TOKEN)" ,"parkingmanagerservice" )  
						println("$name | client has required to pickup the car")
					}
					 transition(edgeName="t2",targetState="waitPickupConfirm",cond=whenReply("canPickup"))
				}	 
				state("waitPickupConfirm") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("$name | client is going to pick up his car")
					}
					 transition( edgeName="goto",targetState="pickup", cond=doswitch() )
				}	 
				state("pickup") { //this:State
					action { //it:State
						delay(5000) 
						println("$name | client has picked up his car")
					}
				}	 
				state("noAvailableSlot") { //this:State
					action { //it:State
						println("$name | no slot available... i go elsewhere or retry later")
					}
				}	 
			}
		}
}