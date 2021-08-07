package it.unibo.parkimanagerservice.test.utils;

public interface CoapObserver {
	
	public void startObserve();
	public String nextChange()  throws InterruptedException;
	public String nextChangeIn(long millis) throws InterruptedException;
	public void clear();
	public boolean unreadChanges();

}
