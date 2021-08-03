package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.ctxcarparking.MainCtxcarparkingKt;
import it.unibo.parkimanagerservice.test.utils.ActorSpeaker;
import it.unibo.parkimanagerservice.test.utils.JCoapObserver;
import it.unibo.parkimanagerservice.test.utils.TcpActorSpeaker;
import it.unibo.parkingslot.ParkingSlotManager;
import it.unibo.parkingslot.ParkingSlotState;
import it.unibo.parkingslot.SimpleParkingSlotManager;
import it.unibo.parkmanagerservice.bean.DoorState;
import it.unibo.parkingstate.MockState;

public class FirstTestPlan {
	
	private static JCoapObserver pmsObs;
	private static JCoapObserver icObs;
	private static JCoapObserver dcObs;
	private static MockState state;
	private static ActorSpeaker pmsSpk;
	private static ActorSpeaker icSpk;
	private static ActorSpeaker dcSpk;
	
	@BeforeClass
	public static void init() throws InterruptedException, UnknownHostException, IOException {
		
		/* STARTING COAP OBSERVERS ***********************************************************/
		pmsObs = new JCoapObserver("localhost:8000", "ctxcarparking", "parkingmanagerservice");
		pmsObs.startObserve();
		icObs = new JCoapObserver("localhost:8000", "ctxcarparking", "itocccounter");
		icObs.startObserve();
		dcObs = new JCoapObserver("localhost:8000", "ctxcarparking", "dtfreecounter");
		dcObs.startObserve();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				MainCtxcarparkingKt.main();				
			}
			
		}).start();
		assertEquals("work", pmsObs.nextChange()); //Wait until system is started
		assertEquals("work", icObs.nextChange());
		assertEquals("work", dcObs.nextChange());
		
		/* STATE SINGLETON AND TCP SPEAKER ***************************************************/
		state = MockState.INSTANCE;
		pmsSpk = new TcpActorSpeaker("localhost", 8000, "parkingmanagerservice");
		icSpk = new TcpActorSpeaker("localhost", 8000, "itocccounter");
		dcSpk = new TcpActorSpeaker("localhost", 8000, "dtfreecounter");
		
	}
	
	@AfterClass
	public static void finalizing() throws IOException {
		pmsSpk.close();
		icSpk.close();
	}
	
	/*
	 * This method is useful to avoid any possible collisions
	 * (N.B. the annotation Before is intended before each test)
	 */
	@Before
	public void beforeEach() throws IOException, InterruptedException {
		icObs.clear();
		pmsObs.clear();
		dcObs.clear();
		
		icSpk.sendDispatch("stopCount", "STOP");
		dcSpk.sendDispatch("stopCount", "STOP");
		assertEquals("work", icObs.nextChange());
		assertEquals("work", dcObs.nextChange());
		
		icObs.clear();
		pmsObs.clear();
		dcObs.clear();
	}
	
	/*
	 * Test the system when a client wants to enter but no slot are available
	 * -> The system sends to the client a 0 slotnum
	 */
	@Test
	public void testNotifyInterestNoSlotFree() throws IOException, InterruptedException {
		
		/* **** Simulate no parking slot available ********************************/
		/**/ state.reset();                                                     /**/
		/**/ ParkingSlotManager mgr = state.getParkingSlotManager();            /**/
		/**/ mgr.occupySlot(1);                                                 /**/
		/**/ state.setParkingSlotManager(mgr);                                  /**/
		/* ************************************************************************/
		
		//Send the request to enter
		pmsSpk.sendRequest("enter", "hello");
		
		//Parse the slotnum produced by the system
		String res = pmsObs.nextChange();
		assertEquals("reply with SLOTNUM=0", res);
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check SLOTNUM == 0
		assertTrue(slotNum == 0);
		
		//System must go to work
		assertTrue(pmsObs.nextChange().contains("work"));
		
		//ITOCC counter must have no changes
		assertTrue(!icObs.unreadChanges());
	}
	
	/*
	 * Test the system when a client wants to enter and there is one slot free
	 * -> the system reserves the slot and the idoor-area and notifies the user if can enter car
	 */
	@Test
	public void testNotifyInterestSlotFree() throws IOException, InterruptedException {
		
		/* **** Simulate one parking slot free ************************************/
		/**/ state.reset();                                                     /**/
		/**/ ParkingSlotManager mgr = state.getParkingSlotManager();            /**/
		/**/ state.setParkingSlotManager(mgr);                                  /**/
		/* ************************************************************************/
		
		//Send the request to enter
		pmsSpk.sendRequest("enter", "hello");
		
		//Parse the slotnum produced by the system
		String res = pmsObs.nextChange();
		assertEquals("reply with SLOTNUM=1", res);
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check SLOTNUM == 1
		assertTrue(slotNum == 1);
		
		//Check slot reservation
		assertEquals(ParkingSlotState.RESERVED, mgr.getSlotState(slotNum));
		
		//Check the system send to the client if he can enter the car
		assertTrue(pmsObs.nextChange().contains("canEnterCar"));
		assertEquals(DoorState.OCCUPIED, state.getIndoorState());
		
		//System must go to work and must have started the ITOCC counter
		assertTrue(pmsObs.nextChange().contains("work"));
		assertEquals("count", icObs.nextChange());
		
		//Check if the slot it is been reserved
		assertTrue(mgr.getSlotState(1) == ParkingSlotState.RESERVED);
		
		//Stop the counter (test ended)
		icSpk.sendDispatch("stopCount", icObs.nextChange());
		assertEquals("work", icObs.nextChange());
	}
	
	/*
	 * Check the system when a client has received the SLOTNUM, the indoor
	 * is reserved for him then presses CARENTER -> the system sends the token and occupied
	 * the slot
	 */
	@Test
	public void testCarEnterIndoorFree() throws IOException, InterruptedException {
		
		/* **** Simulate one parking slot free ************************************/
		/**/ state.reset();                                                     /**/
		/**/ ParkingSlotManager mgr = state.getParkingSlotManager();            /**/
		/* ************************************************************************/
		assertEquals(DoorState.FREE, state.getIndoorState());
		
		//NotifyInterest (already checked)
		pmsSpk.sendRequest("enter", "hello");
		String res = pmsObs.nextChange();
		assertEquals("reply with SLOTNUM=1", res);
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check the system says to the user that can enter and start the counter
		assertEquals("canEnterCar(OK)", pmsObs.nextChange());
		assertEquals("work", pmsObs.nextChange());
		assertEquals("count", icObs.nextChange());
		
		//Simulate the user has entered his car
		state.setWeightOnSensor(10000.0);
		
		//The user has entered is car into the Indoor-Area. Simulate CARENTER pression
		String tokenMsg = pmsSpk.sendRequest("carenter", Integer.toString(slotNum));
		
		//Check the system replies with token message and stopped the counter
		assertTrue(tokenMsg.contains("token") && tokenMsg.contains("reply"));
		assertEquals("reply with TOKEN=1", pmsObs.nextChange());
		assertEquals("work", icObs.nextChange());
		
		//Check the system has occupied the slot
		assertEquals(ParkingSlotState.OCCUPIED, mgr.getSlotState(slotNum));
		
		//System must go to work
		assertEquals("work", pmsObs.nextChange());	
	}

	/*
	 * Check the system when indoor-area is occupied and a client request to enter
	 * -> the system sends the slotnum and notifies  the user to wait entering the car
	 */
	@Test
	public void testNotifyInterestSlotFreeButIndoorAlreadyEngaged() throws InterruptedException, IOException {
	
		/* **** Simulate one parking slot reserved ********************************/
		/**/ ParkingSlotManager mgr = new SimpleParkingSlotManager(2);          /**/
		/**/ mgr.getFreeSlot();                                                 /**/
		/**/ state.reset(mgr);                                                  /**/
		/* ************************************************************************/
		assertEquals(ParkingSlotState.RESERVED, state.getParkingSlotManager().getSlotState(1));
		assertEquals(ParkingSlotState.FREE, state.getParkingSlotManager().getSlotState(2));
		
		//Simulate indoor area already engaged
		state.setIndoorState(DoorState.OCCUPIED);
		
		//Simulate new client request
		pmsSpk.sendRequest("enter", "hello");
		String res = pmsObs.nextChange();
		assertEquals("reply with SLOTNUM=2", res);
		int slotNum = Integer.parseInt(res.split("=")[1].trim());
		
		//Check slotnum (1 is occupied, that it must be 2)
		assertEquals(2, slotNum);
		assertEquals(ParkingSlotState.RESERVED, mgr.getSlotState(2));
		
		//Check if the system notifies user to wait for entering car
		assertEquals("canEnterCar(WAIT)", pmsObs.nextChange());
		
		//System must go to work
		assertTrue(pmsObs.nextChange().contains("work"));	
	}
	
	/*
	 * Check the system when a user does not enter the car end
	 * itocc is reached -> the counter go into the state reached
	 */
	@Test
	public void testUserNotEnterCarInItocc() throws IOException, InterruptedException {
		
		/* **** Simulate one parking slot free ************************************/
		/**/ state.reset();                                                     /**/
		/* ************************************************************************/
		
		//NotifyInterest (already checked)
		pmsSpk.sendRequest("enter", "hello");
		String res = pmsObs.nextChange(); //slotnum
		assertEquals("reply with SLOTNUM=1", res);
		
		//Check the system says to the user that can enter
		assertEquals("canEnterCar(OK)", pmsObs.nextChange());
		assertEquals("count", icObs.nextChange());
		
		//Wait for itocc reached
		String newState = icObs.nextChangeIn(3000);
		assertTrue(newState != null);
		assertEquals("ITOCC", newState);

		//MANCA IL TEST DEL PARKING SLOT LIBERATO
		
		//Check system and counter work
		assertEquals("work", pmsObs.nextChange());
		assertEquals("work", icObs.nextChange());
	}
	
	/*
	 * Check the system when a user send the token to pickup his car
	 * but it is invalid -> the system notifies the user that the token is invalid
	 */
	@Test
	public void testSubmitTokenInvalid() throws IOException, InterruptedException {
		
		/* **** Simulate parked car ***********************************************/
		/**/ state.reset();                                                     /**/
		/**/ int slotnum = state.getParkingSlotManager().getFreeSlot();         /**/
		/**/ String token = state.getParkingSlotManager().occupySlot(slotnum);  /**/
		/* ************************************************************************/
		
		//Generate invalid token
		String wrongToken = token += Math.random()*10;
		
		//Send pickup request with invalid token
		pmsSpk.sendRequest("pickup", wrongToken);
		
		//Check system response
		assertEquals("canPickup(INVALIDTOK)", pmsObs.nextChange());
		assertEquals("work", pmsObs.nextChange());
		
		//Check system has not started the DTFREE count
		assertFalse(dcObs.unreadChanges());
		
	}
	
	/*
	 * Check the system when a user sends the correct token to pickup his car
	 * and the outdoor area is free -> the system notifies the user for pickup
	 * car, reserves the outdoor and free the slot
	 */
	@Test
	public void testSubmitToken() throws IOException, InterruptedException {
		
		/* **** Simulate parked car ***********************************************/
		/**/ state.reset();                                                     /**/
		/**/ int slotnum = state.getParkingSlotManager().getFreeSlot();         /**/
		/**/ String token = state.getParkingSlotManager().occupySlot(slotnum);  /**/
		/* ************************************************************************/
		
		//Send request to pickup car
		pmsSpk.sendRequest("pickup", token);
		
		//Check system has validated the request, has occupied the outdoor,
		//has made the slot free, and has started the DTFREE counter
		assertEquals("canPickup(OK)", pmsObs.nextChange());
		assertEquals(DoorState.OCCUPIED, state.getOutdoorState());
		assertEquals(ParkingSlotState.FREE, state.getParkingSlotManager().getSlotState(slotnum));
		assertEquals("count", dcObs.nextChange());
		
		//System must go to work
		assertEquals("work", pmsObs.nextChange());
		
		//Stop the counter
		dcSpk.sendDispatch("stopCount", "STOP");
		assertEquals("work", dcObs.nextChange());
		
	}
	
	/*
	 * Check the system when a user sends the correct token to pickup his car
	 * and the outdoor area is already engaged -> the system notifies the user to wait
	 */
	@Test
	public void testSubmitTokenButOutdoorEngaged() throws IOException, InterruptedException {
		
		/* **** Simulate two parked car *******************************************/
		/**/ ParkingSlotManager mgr = new SimpleParkingSlotManager(2);          /**/
		/**/ state.reset(mgr);                                                  /**/
		/**/ int slotnum1 = mgr.getFreeSlot();                                  /**/
		/**/ String token1 = mgr.occupySlot(slotnum1);                          /**/
		/**/ int slotnum2 = mgr.getFreeSlot();                                  /**/
		/**/ String token2 = mgr.occupySlot(slotnum2);                          /**/
		/* ************************************************************************/
		assertEquals(ParkingSlotState.OCCUPIED, mgr.getSlotState(1));
		assertEquals(ParkingSlotState.OCCUPIED, mgr.getSlotState(2));
		
		//Simulate the first client has requested to pick up (Submit Token, already checked)
		pmsSpk.sendRequest("pickup", token1);
		assertEquals("canPickup(OK)", pmsObs.nextChange());
		assertEquals(DoorState.OCCUPIED, state.getOutdoorState());
		assertEquals(ParkingSlotState.FREE, mgr.getSlotState(slotnum1));
		assertEquals("count", dcObs.nextChange());
		assertEquals("work", pmsObs.nextChange());
		
		//Simulate a second client requests for pickup
		pmsSpk.sendRequest("pickup", token2);
		
		//Check the system says the client to wait
		assertEquals("canPickup(WAIT)", pmsObs.nextChange());
		
		//Stop the counter
		dcSpk.sendDispatch("stopCount", "STOP");
		assertEquals("work", dcObs.nextChange());
		
		//System must go to work
		assertEquals("work", pmsObs.nextChange());
	}
	
	/*
	 * Check the system when a user sends the correct token to pickup his car
	 * and the outdoor area is free, the user does not take the car and the dtfree counter is reached ->
	 * the system notifies the user for pickup and dtfree counter go to state count
	 */
	@Test
	public void testUserNotPickUpInDtfree() throws IOException, InterruptedException {
		/* **** Simulate parked car ***********************************************/
		/**/ state.reset();                                                     /**/
		/**/ int slotnum = state.getParkingSlotManager().getFreeSlot();         /**/
		/**/ String token = state.getParkingSlotManager().occupySlot(slotnum);  /**/
		/* ************************************************************************/
		
		//Send request to pickup car
		pmsSpk.sendRequest("pickup", token);
		
		//Check system has validated the request, has occupied the outdoor,
		//has made the slot free, and has started the DTFREE counter
		assertEquals("canPickup(OK)", pmsObs.nextChange());
		assertEquals(DoorState.OCCUPIED, state.getOutdoorState());
		assertEquals(ParkingSlotState.FREE, state.getParkingSlotManager().getSlotState(slotnum));
		assertEquals("count", dcObs.nextChange());
		
		//System must go to work
		assertEquals("work", pmsObs.nextChange());
		
		//Wait until DTFREE is reached
		String newState = dcObs.nextChangeIn(3000);
		assertTrue(newState != null);
		assertEquals("DTFREE", newState);
		assertEquals("work", dcObs.nextChange());

		//MANCA CHE IL MANAGER TOGLIE LA MACCHINA QUINDI FAR SI CHE SIA LIBERA L'OUTDOOR
	}

}
