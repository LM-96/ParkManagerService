package it.unibo.parkmanagerservice.actorchat

interface RequestWriter {
	
	fun writeRequest(type : String, content : String)
	
}