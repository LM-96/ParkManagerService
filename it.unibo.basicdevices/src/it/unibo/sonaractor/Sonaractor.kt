/* Generated by AN DISI Unibo */ 
package it.unibo.sonaractor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonaractor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
				var DISTANCE : Int
				var STATE : String
				var JSONSTATE : String
				var POLLING_TIME : Long = 1000
				var THESHOLD_DISTANCE = it.unibo.basicsonar.Sonar.getThesholdDistance()
				val sonar = it.unibo.basicdevices.DeviceManager.requestDevice("outsonar")
				
				if(sonar == null) {
					println("$name | unable to use the sonar")
					System.exit(-1)
				}
				
				sonar as it.unibo.basicsonar.Sonar
				
				DISTANCE = sonar.readDistance()
				if(DISTANCE > THESHOLD_DISTANCE) STATE="off"
				else STATE = "on"
				
				JSONSTATE = "{\"data\":\"$DISTANCE\",\"state\":\"$STATE\"}"
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
						updateResourceRep( JSONSTATE  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("$name | last state : ${JSONSTATE}")
					}
					 transition(edgeName="t04",targetState="setpolling",cond=whenDispatch("dopolling"))
				}	 
				state("setpolling") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("dopolling(TIME)"), Term.createTerm("dopolling(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 POLLING_TIME = payloadArg(0).toLong()  
						}
						println("$name | started polling with time $POLLING_TIME")
					}
					 transition( edgeName="goto",targetState="polling", cond=doswitch() )
				}	 
				state("polling") { //this:State
					action { //it:State
						 
									DISTANCE = sonar.readDistance()
									
									if(STATE.equals("off") && DISTANCE < THESHOLD_DISTANCE) {
										STATE = "on"
						emit("sonaron", "sonaron(ON)" ) 
						
									} else if(STATE.equals("on") && DISTANCE > THESHOLD_DISTANCE) {
										STATE = "off"
										
						emit("sonaroff", "sonaroff(OFF)" ) 
						
									}
									
									JSONSTATE = "{\"data\":\"$DISTANCE\",\"state\":\"$STATE\"}"
						updateResourceRep( JSONSTATE  
						)
						stateTimer = TimerActor("timer_polling", 
							scope, context!!, "local_tout_sonaractor_polling", POLLING_TIME )
					}
					 transition(edgeName="t05",targetState="polling",cond=whenTimeout("local_tout_sonaractor_polling"))   
					transition(edgeName="t06",targetState="work",cond=whenDispatch("stoppolling"))
					transition(edgeName="t07",targetState="setpolling",cond=whenDispatch("dopolling"))
				}	 
			}
		}
}
