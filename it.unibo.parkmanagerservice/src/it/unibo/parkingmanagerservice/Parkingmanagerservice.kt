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
		  
				it.unibo.parkmanagerservice.persistence.ParkingRepositories.createBasics(6)
				val CONTROLLER = it.unibo.parkmanagerservice.controller.ControllerBuilder.createK(
					it.unibo.parkmanagerservice.persistence.ParkingRepositories.getUserRepository()!!,
					it.unibo.parkmanagerservice.persistence.ParkingRepositories.getParkingSlotRepository()!!,
					it.unibo.parkmanagerservice.persistence.DoorQueues.getIndoorQueue(),
					it.unibo.parkmanagerservice.persistence.DoorQueues.getOutdoorQueue(),
					it.unibo.parkmanagerservice.bean.LocalDoorState.get()).get()
					
				
				
				val CHANNEL = it.unibo.parkmanagerservice.notification.NotificationChannel.CHANNEL
				var JSON : String = ""
				var USERERR : Pair<it.unibo.parkmanagerservice.bean.User?, it.unibo.parkmanagerservice.controller.ParkManagerError?>
				var SLOTERR : Pair<it.unibo.parkmanagerservice.bean.ParkingSlot?, it.unibo.parkmanagerservice.controller.ParkManagerError?>
				var USERSLOT : Pair<it.unibo.parkmanagerservice.bean.User?,it.unibo.parkmanagerservice.bean.ParkingSlot?>
				var USER : it.unibo.parkmanagerservice.bean.User?
				var SLOTNUM : Long = 0
				var INDOOR = it.unibo.parkmanagerservice.bean.DoorType.INDOOR
				var OUTDOOR = it.unibo.parkmanagerservice.bean.DoorType.OUTDOOR
				var NOTIFICATION : it.unibo.parkmanagerservice.notification.Notification
				var SLOT : it.unibo.parkmanagerservice.bean.ParkingSlot?
				val INDOOR_POLLING = it.unibo.parkmanagerservice.bean.Timers.get().INDOOR_POLLING
				val OUTDOOR_POLLING = it.unibo.parkmanagerservice.bean.Timers.get().OUTDOOR_POLLING
				val ITOCC = it.unibo.parkmanagerservice.bean.Timers.get().ITOCC
				val DTFREE = it.unibo.parkmanagerservice.bean.Timers.get().DTFREE
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
						updateResourceRep( "{\"door\":\"indoor\",\"state\":\"FREE\"}"  
						)
						updateResourceRep( "{\"door\":\"outdoor\",\"state\":\"FREE\"}"  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("$name | waiting for request...")
						updateResourceRep( "work"  
						)
					}
					 transition(edgeName="t0",targetState="handleEnter",cond=whenRequest("enter"))
					transition(edgeName="t1",targetState="handleCarEnter",cond=whenRequest("carenter"))
					transition(edgeName="t2",targetState="handlePickup",cond=whenRequest("pickup"))
					transition(edgeName="t3",targetState="handleSomeoneInIndoor",cond=whenEvent("weighton"))
					transition(edgeName="t4",targetState="handleIndoorReturnFree",cond=whenEvent("weightoff"))
					transition(edgeName="t5",targetState="handleSomeoneInOutdoor",cond=whenEvent("sonaron"))
					transition(edgeName="t6",targetState="handleOutdoorReturnFree",cond=whenEvent("sonaroff"))
				}	 
				state("handleEnter") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 SLOTNUM = 0  
						if( checkMsgContent( Term.createTerm("enter(NAME,SURNAME,MAIL)"), Term.createTerm("enter(NAME,SURNAME,MAIL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												try {
													USER = CONTROLLER.createUser(payloadArg(0), payloadArg(1), payloadArg(2))
													SLOTNUM = CONTROLLER.assignSlotToUser(USER!!)
													if(SLOTNUM > 0) {
														
														if(CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER!!)) {
															JSON = "{\"slotnum\":\"$SLOTNUM\",\"indoor\":\"FREE\",\"time\":\"${ITOCC}\"}"
								updateResourceRep( "{\"door\":\"indoor\",\"state\":\"RESERVED\"}"  
								)
								updateResourceRep( "{\"slot\":\"${SLOTNUM}\",\"user\":\"${USER!!.mail}\",\"state\":\"RESERVED\"}"  
								)
								forward("dopolling", "dopolling($INDOOR_POLLING)" ,"weightsensoractor" ) 
								forward("startItoccCounter", "startItoccCounter(START)" ,"itocccounter" ) 
								
														} else JSON = "{\"slotnum\":\"$SLOTNUM\",\"indoor\":\"OCCUPIED\"}"
													}
												} catch(e : java.sql.SQLException) {
													JSON = "{\"err\":\"${e.getLocalizedMessage()}\"}"
												}
						}
						println("$name | reply with slotnum(${JSON!!})")
						answer("enter", "slotnum", "slotnum($JSON)"   )  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleSomeoneInIndoor") { //this:State
					action { //it:State
						forward("stopCount", "stopCount(STOP)" ,"itocccounter" ) 
						 
									USER = CONTROLLER.setSomeoneOnDoor(INDOOR)!!
						updateResourceRep( "{\"door\":\"indoor\",\"state\":\"OCCUPIED\"}"  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleIndoorReturnFree") { //this:State
					action { //it:State
						 
									USER = CONTROLLER.setCarOfUserAtIndoorParked()
									NOTIFICATION = `it.unibo.parkmanagerservice`.notification.DefaultNotificationFactory.createForUser(
												USER!!,
												`it.unibo.parkmanagerservice`.notification.NotificationType.TOKEN,
												arrayOf(USER!!.token!!))
									CHANNEL.send(NOTIFICATION)
						forward("notifyuser", "notifyuser(NOTIFY)" ,"notificationactor" ) 
						forward("stoppolling", "stoppolling(STOP)" ,"weightsensoractor" ) 
						updateResourceRep( "{\"door\":\"indoor\",\"state\":\"FREE\"}"  
						)
					}
					 transition( edgeName="goto",targetState="enterNext", cond=doswitchGuarded({ (CONTROLLER.getDoorQueue(INDOOR).remaining()) > 0  
					}) )
					transition( edgeName="goto",targetState="work", cond=doswitchGuarded({! ( (CONTROLLER.getDoorQueue(INDOOR).remaining()) > 0  
					) }) )
				}	 
				state("enterNext") { //this:State
					action { //it:State
						 
									USER = CONTROLLER.reserveDoorForNextUser(INDOOR)
									if(USER != null) {	
										NOTIFICATION = `it.unibo.parkmanagerservice`.notification.DefaultNotificationFactory.createForUser(
											USER!!,
											`it.unibo.parkmanagerservice`.notification.NotificationType.SLOTNUM,
											arrayOf(CONTROLLER.getSlotReservedForUser(USER!!)!!.slotnum.toString(), ITOCC.toString()))
										CHANNEL.send(NOTIFICATION)
						forward("notifyuser", "notifyuser(NOTIFY)" ,"notificationactor" ) 
						forward("dopolling", "dopolling($INDOOR_POLLING)" ,"weightsensoractor" ) 
						forward("startItoccCounter", "startItoccCounter(START)" ,"itocccounter" ) 
						updateResourceRep( "{\"door\":\"indoor\",\"state\":\"RESERVED\"}"  
						)
						updateResourceRep( "{\"slot\":\"${SLOTNUM}\",\"user\":\"${USER!!.mail}\",\"state\":\"RESERVED\"}"  
						)
						 	}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleCarEnter") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("carenter(SLOTNUM,MAIL)"), Term.createTerm("carenter(SLOTNUM,MAIL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	
												USERERR = CONTROLLER.assignTokenToUserAtIndoor(payloadArg(0), payloadArg(1))
												if(USERERR.first != null && USERERR.second == null) {
													JSON = "{\"token\":\"${USERERR.first!!.token!!.toString()}\"}"
								forward("stopCount", "stopCount(STOP)" ,"itocccounter" ) 
								forward("parkcar", "parkcar($SLOTNUM)" ,"trolley" ) 
								updateResourceRep( "{\"slot\":\"${SLOTNUM}\",\"user\":\"${USERERR.first!!.mail}\",\"state\":\"OCCUPIED\"}"  
								)
								
												} else JSON = "{\"err\":\"${USERERR!!.second!!.msg}\"}"
								println("$name | reply to CARENTER with $JSON")
								answer("carenter", "token", "token($JSON)"   )  
								updateResourceRep( "reply to CARENTER with $JSON"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handlePickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("pickup(TOKEN,MAIL)"), Term.createTerm("pickup(TOKEN,MAIL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	
												SLOTERR = CONTROLLER.analyzeToken(payloadArg(0), payloadArg(1))
												if(SLOTERR.first != null && SLOTERR.second == null) {
													SLOTNUM = SLOTERR.first!!.slotnum
													if(CONTROLLER.reserveDoorForUserOrEnqueue(OUTDOOR, SLOTERR.first!!.user!!)) {
														JSON = "{\"msg\":\"The transport trolley will transport your car to the outdoor: you will get a notification when your car is ready. Plase stay near the ourdoor\"}"
								updateResourceRep( "{\"slot\":\"${SLOTNUM}\",\"user\":\"${SLOTERR.first!!.user!!.mail}\",\"state\":\"ALMOST_FREE\"}"  
								)
								forward("pickupcar", "pickupcar($SLOTNUM)" ,"trolley" ) 
								forward("startDtfreeCounter", "startDtfreeCounter($OUTDOOR_POLLING)" ,"dtfreecounter" ) 
								
													} else
														JSON = "{\"msg\":\"The outdoor is already engaged. When possible, the trolley will transport your car to the outdoor. You will be notified as soon.\"}"
												} else
													JSON = "{\"msg\":\"$SLOTERR.second!!\"}"
								println("$name | reply with canPickup(${JSON!!})")
								answer("pickup", "canPickup", "canPickup($JSON)"   )  
								updateResourceRep( "canPickup($JSON)"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleSomeoneInOutdoor") { //this:State
					action { //it:State
						
									USERSLOT = CONTROLLER.freeSlotUsedByUserAtOutdoor()
									USER = USERSLOT.first
									if(USER!! != null) {
										NOTIFICATION = `it.unibo.parkmanagerservice`.notification.DefaultNotificationFactory.createForUser(
												USER!!,
												`it.unibo.parkmanagerservice`.notification.NotificationType.PICKUP,
												arrayOf<String>(DTFREE.toString())
											)
										CHANNEL.send(NOTIFICATION)
						forward("notifyuser", "notifyuser(NOTIFY)" ,"notificationactor" ) 
						updateResourceRep( "{\"door\":\"outdoor\",\"state\":\"OCCUPIED\"}"  
						)
						updateResourceRep( "{\"slot\":\"${USERSLOT.second!!.slotnum}\",\"user\":\"${USER!!.mail}\",\"state\":\"FREE\"}"  
						)
						 }  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleOutdoorReturnFree") { //this:State
					action { //it:State
						 CONTROLLER.setFreeDoor(OUTDOOR)  
						updateResourceRep( "{\"door\":\"outdoor\",\"state\":\"FREE\"}"  
						)
					}
					 transition( edgeName="goto",targetState="exitNext", cond=doswitchGuarded({ (CONTROLLER.getDoorQueue(OUTDOOR).remaining()) > 0  
					}) )
					transition( edgeName="goto",targetState="work", cond=doswitchGuarded({! ( (CONTROLLER.getDoorQueue(OUTDOOR).remaining()) > 0  
					) }) )
				}	 
				state("exitNext") { //this:State
					action { //it:State
						 
									USER = CONTROLLER.reserveDoorForNextUser(INDOOR)
									if(USER != null) {	
						forward("dopolling", "dopolling($OUTDOOR_POLLING)" ,"sonaractor" ) 
						forward("startDtfreeCounter", "startDtfreeCounter(START)" ,"dtfreecounter" ) 
						updateResourceRep( "{\"door\":\"outdoor\",\"state\":\"RESERVED\"}"  
						)
						updateResourceRep( "{\"slot\":\"${SLOTNUM}\",\"user\":\"${USER!!.mail}\",\"state\":\"ALMOST_FREE\"}"  
						)
						 	}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
