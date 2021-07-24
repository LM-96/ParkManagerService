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
	
	override fun sendRequest(type : String, content : String) {
		outSock.write("msg($type,request,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
	}
	
	override fun sendDispatch(type : String, content : String) {
		outSock.write("msg($type,dispatch,tcpactorchatter,$actor,$type($content),${counter.addAndGet(1)})\n")
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
	}
	
}

private class BufferedMessageOutputStream(writer : BufferedWriter) : MessageOutputStream {
	
	private val writer = writer
	
	override fun write(msg : ApplMessage) {
		writer.write(msg.toString().trim() + "\n")
	}
	
}

private class BufferedMessageInputStream(reader: BufferedReader) : MessageInputStream {
	private val reader = reader
	
	override fun read() : ApplMessage {
		return MsgParser.createMessage(reader.readLine())
	}
}

private class MsgParser {
	companion object {
		@JvmStatic fun createMessage(msg : String) : ApplMessage {
			 //sysUtil.colorPrint( "ApplMessage | CREATE: $msg " )
            val jsonContent = msg.split("{")[1].split("}")[0]
            val content = "{$jsonContent}"
            //sysUtil.colorPrint( "ApplMessage | jsonContent: $jsonContent " )
            val msgNoContent = msg.replace(jsonContent,"xxx")  //xxx will become item[4]
            //sysUtil.colorPrint( "ApplMessage | msgNoContent=$msgNoContent " )
            val body  = msgNoContent.replace("msg","").replace("(","").replace(")","")
            val items = body.split(",")
            return ApplMessage(items[0],items[1], items[2], items[3], content, items[5] )
		}
	}
}