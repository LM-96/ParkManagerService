package it.unibo.parkimanagerservice.test.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpActorSpeaker implements ActorSpeaker {
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	private String actor;
	private int msgId;
	
	public TcpActorSpeaker(String contextIp, int contextPort, String actor) throws UnknownHostException, IOException {
		socket = new Socket(contextIp, contextPort);
		
		writer = new PrintWriter(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		setActor(actor);
	}
	

	@Override
	public void setActor(String actor) {
		this.actor = actor;
		msgId = 1;
	}

	@Override
	public String sendRequest(String requestType, String content) throws IOException {
		writer.write("msg(" + requestType + ",request,tcpactorspeaker," + actor +
				"," + requestType + "(" + content + ")," +
				(msgId++) + ")\n");
		writer.flush();
		return reader.readLine();
	}

	@Override
	public void sendDispatch(String dispatchType, String content) {
		writer.write("msg(" + dispatchType + ",dispatch,tcpactorspeaker," + actor +
				"," + dispatchType + "(" + content + ")," +
				(msgId++) + ")\n");
		writer.flush();
	}
	
	@Override
	public void close() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
	}

}
