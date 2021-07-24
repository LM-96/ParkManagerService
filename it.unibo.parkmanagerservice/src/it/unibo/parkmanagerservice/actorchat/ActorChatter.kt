package it.unibo.parkmanagerservice.actorchat

import it.unibo.kactor.ApplMessage

interface ActorChatter {
	
	fun getMessageInputStream() : MessageInputStream
	fun getMessageOutputStream() : MessageOutputStream
	
	fun newRequestWriter() : RequestWriter
	fun newDispatchWriter() : DispatchWriter
	
	fun sendRequest(type : String, args : Array<String>)
	fun sendDispatch(type : String, args : Array<String>)
	fun readLastResponse() : ApplMessage
	
}