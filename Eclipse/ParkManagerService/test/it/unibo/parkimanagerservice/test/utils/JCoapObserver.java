package it.unibo.parkimanagerservice.test.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

public class JCoapObserver {
	
	private BlockingQueue<String> queue;
	
	private CoapClient client;
	
	public JCoapObserver(String ip, String context, String destActor) {
		
		client = new CoapClient();
		client.setURI("coap://" + ip + "/" + context + "/" + destActor);
		
		queue = new ArrayBlockingQueue<>(10);
	}
	
	public void startObserve() {
		client.observe(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("JCoapObserver | " + response.getResponseText());
				queue.add(response.getResponseText());
				
			}

			@Override
			public void onError() {
				System.out.println("JCoapObserver | ERROR ");
				queue.add("Coap ERROR");
				
			}
			
		});
	}
	
	public String nextChange() throws InterruptedException {
		return queue.take();
	}

}
