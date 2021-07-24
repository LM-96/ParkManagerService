package it.unibo.parkmanagerservice.actorchat

import it.unibo.kactor.ApplMessage

interface MessageOutputStream {
	
	fun write(msg : ApplMessage)
}