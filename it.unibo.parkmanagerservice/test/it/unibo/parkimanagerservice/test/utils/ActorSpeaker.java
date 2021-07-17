package it.unibo.parkimanagerservice.test.utils;

import java.io.Closeable;
import java.io.IOException;

public interface ActorSpeaker extends Closeable, AutoCloseable {
	
	public void setActor(String actor);
	
	public String sendRequest(String requestType, String content) throws IOException;
	public void sendDispatch(String dispatchType, String content) throws IOException;
	
	@Override
	public default void close() throws IOException {}

}
