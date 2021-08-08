package it.unibo.parkmanagerservice.actorchat

import it.unibo.kactor.ApplMessage

interface ActorChatter {
	
	companion object {
		fun newChatterFor(contextIp : String, contextPort : Int, actorName : String) : ActorChatter {
			return TcpActorChatter(contextIp, contextPort, actorName)
		}
	}
	
	fun getMessageInputStream() : MessageInputStream
	fun getMessageOutputStream() : MessageOutputStream
	
	fun newRequestWriter() : RequestWriter
	fun newDispatchWriter() : DispatchWriter
	fun newEventWriter() : EventWriter
	
	fun sendRequest(type : String, content : String)
	fun sendDispatch(type : String, content : String)
	fun sendEvent(type : String, content : String)
	fun readLastResponse() : ApplMessage
	
}