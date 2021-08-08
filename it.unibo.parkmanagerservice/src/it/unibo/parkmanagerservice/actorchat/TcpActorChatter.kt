package it.unibo.parkmanagerservice.actorchat

import java.net.Socket
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.concurrent.atomic.AtomicInteger
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.MsgUtil

class TcpActorChatter(contextIp : String, contextPort : Int, actor : String) : ActorChatter {
	
	private val counter = AtomicInteger(0)
	private val actor = actor
	
	private val sock = Socket(contextIp, contextPort)
	private val outSock = BufferedWriter(OutputStreamWriter(sock.getOutputStream()))
	private val inSock = BufferedReader(InputStreamReader(sock.getInputStream()))
	private val output = BufferedMessageOutputStream(outSock)
	private val input = BufferedMessageInputStream(inSock)
	
	
	override fun getMessageInputStream() : MessageInputStream {
		return input
	}
	
	override fun getMessageOutputStream() : MessageOutputStream {
		return output
	}
	
	override fun newRequestWriter() : RequestWriter {
		return DedicatedRequestWriter(outSock, counter, actor)
	}
	
	override fun newDispatchWriter() : DispatchWriter {
		return DedicatedDispatchWriter(outSock, counter, actor)
	}

	override fun newEventWriter(): EventWriter {
		return DedicatedEventWriter(outSock, counter, actor)
	}

	override fun sendRequest(type : String, content : String) {
		outSock.write("msg($type,request,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
		outSock.flush()
	}
	
	override fun sendDispatch(type : String, content : String) {
		outSock.write("msg($type,dispatch,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
		outSock.flush()
	}

	override fun sendEvent(type: String, content: String) {
		outSock.write("msg($type,event,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
		outSock.flush()
	}

	override fun readLastResponse() : ApplMessage {
		return input.read()
	}
}

private class DedicatedRequestWriter(writer : BufferedWriter, counter : AtomicInteger, actor : String) : RequestWriter {
	
	private val actor = actor
	private val counter = counter
	private val writer = writer
	
	override fun writeRequest(type : String, content : String) {
		writer.write("msg($type,request,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
	}
	
}

private class DedicatedDispatchWriter(writer : BufferedWriter, counter : AtomicInteger, actor : String) : DispatchWriter {
	
	private val actor = actor
	private val counter = counter
	private val writer = writer
	
	override fun writeDispatch(type : String, content : String) {
		writer.write("msg($type,dispatch,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
		writer.flush()
	}
	
}

private class DedicatedEventWriter(writer : BufferedWriter, counter : AtomicInteger, actor : String) : EventWriter {

	private val actor = actor
	private val counter = counter
	private val writer = writer

	override fun writeEvent(type : String, content : String) {
		writer.write("msg($type,event,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
	}

}

private class BufferedMessageOutputStream(writer : BufferedWriter) : MessageOutputStream {
	
	private val writer = writer
	
	override fun write(msg : ApplMessage) {
		writer.write(msg.toString().trim() + "\n")
		writer.flush()
	}
	
}

private class BufferedMessageInputStream(reader: BufferedReader) : MessageInputStream {
	private val reader = reader
	
	override fun read() : ApplMessage {
		return ApplMessage(reader.readLine())
	}
}