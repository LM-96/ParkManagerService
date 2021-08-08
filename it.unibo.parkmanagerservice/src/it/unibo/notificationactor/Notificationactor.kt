/* Generated by AN DISI Unibo */ 
package it.unibo.notificationactor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Notificationactor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
				val NOTIFIER = it.unibo.parkmanagerservice.notification.SystemNotifier.get()
				val DEQUE = it.unibo.parkmanagerservice.notification.CCNotificationDeque
				var NOTIFICATION : it.unibo.parkmanagerservice.notification.Notification? = null
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name | started")
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("$name | working")
					}
					 transition(edgeName="t017",targetState="handleNotificationToSend",cond=whenDispatch("notifyuser"))
				}	 
				state("handleNotificationToSend") { //this:State
					action { //it:State
						 
									NOTIFICATION = DEQUE.get()
									if(NOTIFICATION != null) {
										NOTIFIER.sendNotification(NOTIFICATION!!)
										println("$name | Notification sent")
									}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitchGuarded({ NOTIFICATION == null  
					}) )
					transition( edgeName="goto",targetState="handleNotificationToSend", cond=doswitchGuarded({! ( NOTIFICATION == null  
					) }) )
				}	 
			}
		}
}
