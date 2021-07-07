package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.ctxcarparking.MainCtxcarparkingKt;
import it.unibo.parkimanagerservice.test.utils.ActorSpeaker;
import it.unibo.parkimanagerservice.test.utils.JCoapObserver;
import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker;
import it.unibo.parkingslot.ParkingSlotManager;
import it.unibo.parkingslot.ParkingSlotState;
import it.unibo.parkingslot.SimpleParkingSlotManager;
import it.unibo.parkingstate.DoorState;
import it.unibo.parkingstate.MockState;

public class FirstTestPlan {
	
	private static JCoapObserver obs;
	private static MockState state;
	private static ActorSpeaker speaker;
	
	@BeforeClass
	public static void init() throws InterruptedException, UnknownHostException, IOException {
		
		/* STARTING COAP OBSERVER ************************************************************/
		obs = new JCoapObserver("localhost:8000", "ctxcarparking", "parkingmanagerservice");
		obs.startObserve();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				MainCtxcarparkingKt.main();				
			}
			
		}).start();
		assertTrue(obs.nextChange().equals("work")); //Wait until system is started
		
		/* STATE SINGLETON AND TCP SPEAKER ***************************************************/
		state = MockState.INSTANCE;
		speaker = new TcpActorSpeaker("localhost", 8000, "parkingmanagerservice");
	}
	
	@AfterClass
	public static void finalizing() throws IOException {
		speaker.close();
	}
	
	/*
	 * Test the system when a client wants to enter but no slot are available
	 * -> The system sends to the client a 0 slotnum
	 */
	@Test
	public void notifyInterestNoSlotFree() throws IOException, InterruptedException {
		
		/* **** Simulate no parking slot available ********************************/
		/**/ ParkingSlotManager mgr = new SimpleParkingSlotManager(1);          /**/
		/**/ mgr.occupySlot(1);                                                 /**/
		/**/ state.setParkingSlotManager(mgr);                                  /**/
		/* ************************************************************************/
		
		//Send the request to enter
		speaker.sendRequest("enter", "hello");
		
		//Parse the slotnum produced by the system
		String res = obs.nextChange();
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check SLOTNUM == 0
		assertTrue(slotNum == 0);
		
		//System must go to work
		assertTrue(obs.nextChange().contains("work"));
	}
	
	/*
	 * Test the system when a client wants to enter and there is one slot free
	 * -> the system reserves the slot and notifies the user if can enter car
	 */
	@Test
	public void notifyInterestSlotFree() throws IOException, InterruptedException {
		
		/* **** Simulate one parking slot free ************************************/
		/**/ ParkingSlotManager mgr = new SimpleParkingSlotManager(1);          /**/
		/**/ state.setParkingSlotManager(mgr);                                  /**/
		/* ************************************************************************/
		
		//Send the request to enter
		speaker.sendRequest("enter", "hello");
		
		//Parse the slotnum produced by the system
		String res = obs.nextChange();
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check SLOTNUM == 1
		assertTrue(slotNum == 1);
		
		//Check slot reservation
		assertEquals(mgr.getSlotState(slotNum), ParkingSlotState.RESERVED);
		
		//Check the system send to the client if he can enter the car
		assertTrue(obs.nextChange().contains("canEnterCar"));
		
		//System must go to work
		assertTrue(obs.nextChange().contains("work"));
		
		//Check if the slot it is been reserved
		assertTrue(mgr.getSlotState(1) == ParkingSlotState.RESERVED);
	}
	
	/*
	 * Check the system when a client has received the SLOTNUM, the indoor
	 * is free then press CARENTER -> the system sends the token and occupied
	 * the slot
	 */
	@Test
	public void carEnterIndoorFree() throws IOException, InterruptedException {
		
		/* **** Simulate one parking slot free ************************************/
		/**/ ParkingSlotManager mgr = new SimpleParkingSlotManager(1);          /**/
		/**/ state.setParkingSlotManager(mgr);                                  /**/
		/* ************************************************************************/
		
		//Simulate INDOOR is free
		state.setIndoorState(DoorState.FREE);
		
		//NotifyInterest (already checked)
		speaker.sendRequest("enter", "hello");
		String res = obs.nextChange();
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check the system says to the user that can enter
		assertEquals(obs.nextChange(), "canEnterCar(OK)");
		
		//The user has entered is car into the Indoor-Area. Simulate CARENTER pression
		String tokenMsg = speaker.sendRequest("carenter", Integer.toString(slotNum));
		
		//Check the system response with token message
		assertTrue(tokenMsg.contains("token") && tokenMsg.contains("reply"));
		assertEquals(obs.nextChange(), "reply with TOKEN=1");
		
		//Check the system has occupied the slor
		assertEquals(mgr.getSlotState(slotNum), ParkingSlotState.OCCUPIED);
		
		
	}
	
	/*
	 * - utente preme carenter e indoor area è libera -> riceve token + slot occupato
	 * - utente non preme carenter dentro itocc -> slot libero
	 * - indoor area occupata -> utente riceve canEnterCar(WAIT)
	 * - invio token per pickup corretto -> outdoor area occupata, posto liberato, utente notificato
	 * - invio token scorretto -> richiesta nuovo token, outdoor area libera
	 * - invio token corretto ma outdoor area occupata -> canPickUp(WAIT)
	 */

	@Test
	public void testIndoorEngaged() throws InterruptedException {
	
		/*state.setIndoorState(DoorState.OCCUPIED);
		System.out.println("First test");*/
	}

}
