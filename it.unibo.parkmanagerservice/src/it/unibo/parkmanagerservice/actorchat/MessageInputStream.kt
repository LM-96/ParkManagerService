package it.unibo.parkmanagerservice.actorchat

import it.unibo.kactor.ApplMessage

interface MessageInputStream {
	
	fun read() : ApplMessage
}