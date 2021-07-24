package it.unibo.parkmanagerservice.actorchat

import java.net.Socket
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.concurrent.atomic.AtomicInteger

class TcpActorChatter(contextIp : String, contextPort : Int, actor : String) : ActorChatter {
	
	private val sock = Socket(contextIp, contextPort)
	private val outSock = BufferedWriter(OutputStreamWriter(sock.getOutputStream()))
	private val inSock = BufferedReader(InputStreamReader(sock.getInputStream()))
	private val id = AtomicInteger()
	
	
}

private class BufferedMessageInputStream(input : BufferedReader) {
	
}