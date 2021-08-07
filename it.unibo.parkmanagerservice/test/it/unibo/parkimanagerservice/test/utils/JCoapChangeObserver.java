package it.unibo.parkimanagerservice.test.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

public class JCoapChangeObserver implements CoapObserver {
	
	private BlockingQueue<String> queue;
	
	private CoapClient client;
	private String actor;
	
	public JCoapChangeObserver(String ip, String context, String destActor) {
		
		client = new CoapClient();
		client.setURI("coap://" + ip + "/" + context + "/" + destActor);
		
		queue = new ArrayBlockingQueue<>(10);
		this.actor = destActor;
	}
	
	public void startObserve() {
		client.observe(new CoapHandler() {
			
			private String lastState = "";
			private String currState = "";

			@Override
			public void onLoad(CoapResponse response) {
				currState = response.getResponseText();
				if(!currState.equals(lastState)) {
					queue.add(response.getResponseText());
					System.out.println("JCoapObserver[" + actor + "] | " + response.getResponseText());
					lastState = currState;
				}
				
			}

			@Override
			public void onError() {
				System.out.println("JCoapObserver[\" + actor + \"] | ERROR ");
				queue.add("Coap ERROR");
				
			}
			
		});
	}
	
	public String nextChange() throws InterruptedException {
		return queue.take();
	}
	
	public String nextChangeIn(long millis) throws InterruptedException {
		return queue.poll(millis, TimeUnit.MILLISECONDS);
	}
	
	public void clear() {
		System.out.println("JCoapObserver[" + actor + "] | cleaned");
		queue.clear();
	}
	
	public boolean unreadChanges() {
		return !queue.isEmpty();
	}

}
