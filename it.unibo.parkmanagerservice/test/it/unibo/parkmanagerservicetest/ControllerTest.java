package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.parkmanagerservice.bean.DoorState;
import it.unibo.parkmanagerservice.bean.DoorType;
import it.unibo.parkmanagerservice.bean.DoorsManager;
import it.unibo.parkmanagerservice.bean.LocalDoorState;
import it.unibo.parkmanagerservice.bean.ParkingSlot;
import it.unibo.parkmanagerservice.bean.ParkingSlotState;
import it.unibo.parkmanagerservice.bean.User;
import it.unibo.parkmanagerservice.bean.UserState;
import it.unibo.parkmanagerservice.controller.ControllerBuilder;
import it.unibo.parkmanagerservice.controller.ErrorType;
import it.unibo.parkmanagerservice.controller.ParkManagerServiceController;
import it.unibo.parkmanagerservice.controller.ParkManagerError;
import it.unibo.parkmanagerservice.persistence.DoorQueue;
import it.unibo.parkmanagerservice.persistence.DoorQueues;
import it.unibo.parkmanagerservice.persistence.ParkingRepositories;
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository;
import it.unibo.parkmanagerservice.persistence.UserRepository;
import kotlin.Pair;

public class ControllerTest {
	
	private static ParkManagerServiceController CONTROLLER;
	private static UserRepository USER_REPO;
	private static ParkingSlotRepository SLOT_REPO;
	private static DoorQueue INDOOR_QUEUE;
	private static DoorQueue OUTDOOR_QUEUE;
	private static DoorsManager DOORS_MGR;
	private static User USER1 = new User(0, "name1", "surname1", "mail1", UserState.CREATED, null, null),
			USER2 = new User(0, "name2", "surname2", "mail2", UserState.CREATED, null, null),
			USER3 = new User(0, "name3", "surname3", "mail3", UserState.CREATED, null, null);
	private static ParkingSlot SLOT1, SLOT2;
	private static DoorType INDOOR = DoorType.INDOOR;
	private static DoorType OUTDOOR = DoorType.OUTDOOR;
	private static Map<DoorType, DoorQueue> QUEUES = new HashMap<DoorType, DoorQueue>();
	
	@BeforeClass
	public static void setup() {
		ParkingRepositories.createBasics(2);
		USER_REPO = ParkingRepositories.getUserRepository();
		SLOT_REPO = ParkingRepositories.getParkingSlotRepository();
		INDOOR_QUEUE = DoorQueues.getIndoorQueue();
		OUTDOOR_QUEUE = DoorQueues.getOutdoorQueue();
		DOORS_MGR = LocalDoorState.get();
		QUEUES.put(INDOOR, INDOOR_QUEUE);
		QUEUES.put(OUTDOOR, OUTDOOR_QUEUE);
		ControllerBuilder.createK(
				USER_REPO, SLOT_REPO, INDOOR_QUEUE, OUTDOOR_QUEUE, DOORS_MGR);
		CONTROLLER = ControllerBuilder.get();
		
		Iterator<ParkingSlot> it = SLOT_REPO.getAll().iterator();
		SLOT1 = it.next();
		SLOT2 = it.next();		
	}
	
	@Before
	public void clean() {
		if(USER1 != null)
			USER_REPO.delete(USER1);
		if(USER2 != null)
			USER_REPO.delete(USER2);
		if(USER3 != null)
			USER_REPO.delete(USER3);
		
		USER1 = new User(1, "name1", "surname1", "mail1", UserState.CREATED, null, null);
		USER2 = new User(2, "name2", "surname2", "mail2", UserState.CREATED, null, null);
		USER3 = new User(3, "name3", "surname3", "mail3", UserState.CREATED, null, null);
		
		SLOT1.setSlotstate(ParkingSlotState.FREE);
		SLOT2.setSlotstate(ParkingSlotState.FREE);
		SLOT_REPO.update(SLOT1); SLOT_REPO.update(SLOT2);
		
		DOORS_MGR.setFreeWithNoUser(DoorType.INDOOR);
		DOORS_MGR.setFreeWithNoUser(DoorType.OUTDOOR);
		
		while(INDOOR_QUEUE.pullNextUser() != null);
		while(OUTDOOR_QUEUE.pullNextUser() != null);
	}
	
	@Test public void testCreateUser() {
		USER1 = CONTROLLER.createUser("name", "surname", "mail");
		USER2 = USER_REPO.retrieve(USER1.getId());
		assertNotNull(USER2);
		assertEquals(USER1, USER2);
	}
	
	@Test public void testCreateThreeUser() {
		USER1 = CONTROLLER.createUser("name1", "surname1", "mail1");
		USER2 = CONTROLLER.createUser("name2", "surname2", "mail2");
		USER3 = CONTROLLER.createUser("name3", "surname3", "mail3");
		User u1 = USER_REPO.retrieve(USER1.getId());
		User u2 = USER_REPO.retrieve(USER2.getId());
		User u3 = USER_REPO.retrieve(USER3.getId());
		
		assertNotNull(u1); 
		assertNotNull(u2); 
		assertNotNull(u1);
		assertEquals(USER1, u1); 
		assertEquals(USER2, u2);
		assertEquals(USER3, u3);
		assertTrue(u1.getId() != u2.getId() && u2.getId() != u3.getId() && u1.getId() != u3.getId());
	}
	
	@Test public void testDestroyUser() {
		USER1 = CONTROLLER.createUser("name", "surname", "mail");
		CONTROLLER.destroyUser(USER1);
		assertNull(USER_REPO.retrieve(USER1.getId()));
	}
	
	@Test public void testAssignSlotToUser() {
		USER_REPO.create(USER1);
		long slotnum1 = CONTROLLER.assignSlotToUser(USER1);
		assertTrue(slotnum1 > 0);
		assertEquals(ParkingSlotState.RESERVED, SLOT_REPO.retrieve(slotnum1).getSlotstate());
		
		USER_REPO.create(USER2);
		long slotnum2 = CONTROLLER.assignSlotToUser(USER2);
		assertTrue(slotnum2 > 0);
		assertEquals(ParkingSlotState.RESERVED, SLOT_REPO.retrieve(slotnum2).getSlotstate());
		assertTrue(slotnum1 != slotnum2);
		
		USER_REPO.create(USER3);
		long slotnum3 = CONTROLLER.assignSlotToUser(USER3);
		assertTrue(slotnum3 == 0);
	}
	
	@Test public void testDoorReservation() {
		testCreateThreeUser();
		for(DoorType door : DoorType.values()) {
			assertTrue(CONTROLLER.reserveDoorForUserOrEnqueue(door, USER1));
			assertEquals(USER1, DOORS_MGR.getUserAtDoor(door));
			assertEquals(DoorState.RESERVED, DOORS_MGR.getState(door));
			assertEquals(UserState.getByDoorReservationState(door), USER1.getState());
			
			assertTrue(!CONTROLLER.reserveDoorForUserOrEnqueue(door, USER2));
			assertEquals(UserState.getByDoorWanted(door), USER2.getState());
			assertEquals(1, QUEUES.get(door).remaining());
			assertEquals(USER2, QUEUES.get(door).pullNextUser());
			QUEUES.get(door).addUser(USER2);
			
			assertTrue(!CONTROLLER.reserveDoorForUserOrEnqueue(door, USER3));
			assertEquals(UserState.getByDoorWanted(door), USER3.getState());
			assertEquals(2, QUEUES.get(door).remaining());
			assertEquals(USER2, QUEUES.get(door).pullNextUser());
			assertEquals(1, QUEUES.get(door).remaining());
			assertEquals(USER3, QUEUES.get(door).pullNextUser());
			QUEUES.get(door).addUser(USER2);
			QUEUES.get(door).addUser(USER3);
			
			DOORS_MGR.setFreeWithNoUser(door);
			User serv = CONTROLLER.reserveDoorForNextUser(door);
			assertEquals(USER2, serv);
			assertEquals(USER2, DOORS_MGR.getUserAtDoor(door));
			assertEquals(DoorState.RESERVED, DOORS_MGR.getState(door));
			assertEquals(UserState.getByDoorReservationState(door), USER2.getState());
			assertEquals(1, QUEUES.get(door).remaining());
			assertEquals(USER3, QUEUES.get(door).pullNextUser());
			QUEUES.get(door).addUser(USER3);
			
			DOORS_MGR.setFreeWithNoUser(door);
			serv = CONTROLLER.reserveDoorForNextUser(door);
			assertEquals(USER3, serv);
			assertEquals(USER3, DOORS_MGR.getUserAtDoor(door));
			assertEquals(DoorState.RESERVED, DOORS_MGR.getState(door));
			assertEquals(UserState.getByDoorReservationState(door), USER3.getState());
			assertEquals(0, QUEUES.get(door).remaining());
			
			DOORS_MGR.setFreeWithNoUser(door);
			assertEquals(DoorState.FREE, DOORS_MGR.getState(door));
			assertNull(CONTROLLER.reserveDoorForNextUser(door));
			assertEquals(DoorState.FREE, DOORS_MGR.getState(door));
			
		}
	}
	
	@Test public void testSetSomeoneOnDoor() {
		USER_REPO.create(USER1);
		for(DoorType door : DoorType.values()) {
			CONTROLLER.reserveDoorForUserOrEnqueue(door, USER1);
			CONTROLLER.setSomeoneOnDoor(door);
			
			assertEquals(USER1, DOORS_MGR.getUserAtDoor(door));
			assertEquals(DoorState.OCCUPIED, DOORS_MGR.getState(door));
		}
	}
	
	@Test public void testCarParked() {
		USER_REPO.create(USER1);
		long slotnum = CONTROLLER.assignSlotToUser(USER1);
		CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER1);
		CONTROLLER.setSomeoneOnDoor(INDOOR);
		User u = CONTROLLER.setCarOfUserAtIndoorParked();
		
		assertEquals(DoorState.FREE, DOORS_MGR.getState(INDOOR));
		assertNull(DOORS_MGR.getUserAtDoor(INDOOR));
		assertEquals(USER1, u);
		assertEquals(UserState.PARKED, USER1.getState());
		assertEquals(ParkingSlotState.OCCUPIED, SLOT_REPO.retrieve(slotnum).getSlotstate());
		assertEquals(USER1, SLOT_REPO.retrieve(slotnum).getUser());
	}
	
	@Test public void testAssignToken() {
		Pair<User, ParkManagerError> usererr = CONTROLLER.assignTokenToUserAtIndoor("1", "");
		assertNotNull(usererr.getSecond());
		assertEquals(ErrorType.DOOR_NOT_RESERVED, usererr.getSecond().getType());
		
		
		USER_REPO.create(USER1);
		long slotnum1 = CONTROLLER.assignSlotToUser(USER1);
		CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER1);
		usererr = CONTROLLER.assignTokenToUserAtIndoor(Long.toString(slotnum1), USER1.getMail());
		assertNotNull(usererr.getSecond());
		assertEquals(ErrorType.NO_USER_AT_DOOR, usererr.getSecond().getType());
		
		CONTROLLER.setSomeoneOnDoor(INDOOR);
		usererr = CONTROLLER.assignTokenToUserAtIndoor(Long.toString(slotnum1), USER1.getMail() + "wrong");
		assertNotNull(usererr.getSecond());
		assertEquals(ErrorType.INVALID_MAIL, usererr.getSecond().getType());
		
		usererr = CONTROLLER.assignTokenToUserAtIndoor(Long.toString(slotnum1), USER1.getMail());
		assertNull(usererr.getSecond());
		assertEquals(USER1, usererr.getFirst());
		CONTROLLER.setCarOfUserAtIndoorParked();
		String token1 = usererr.getFirst().getToken();
		assertNotNull(token1);
		
		
		USER_REPO.create(USER2);
		long slotnum2 = CONTROLLER.assignSlotToUser(USER2);
		CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER2);
		CONTROLLER.setSomeoneOnDoor(INDOOR);
		usererr = CONTROLLER.assignTokenToUserAtIndoor(Long.toString(slotnum2), USER2.getMail());
		assertTrue(!usererr.getFirst().getToken().equals(token1));
	}
	
	@Test public void testAnalyzeToken() {
		Pair<ParkingSlot, ParkManagerError> sloterr = CONTROLLER.analyzeToken("This is a fake token", "This is a fake mail");
		assertNotNull(sloterr.getSecond());
		assertEquals(ErrorType.INVALID_TOKEN, sloterr.getSecond().getType());
		
		USER_REPO.create(USER1);
		long slotnum1 = CONTROLLER.assignSlotToUser(USER1);
		CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER1);
		CONTROLLER.setSomeoneOnDoor(INDOOR);
		CONTROLLER.assignTokenToUserAtIndoor(Long.toString(slotnum1), USER1.getMail());
		
		sloterr = CONTROLLER.analyzeToken(USER1.getToken(), USER1.getMail() + "wrong");
		assertNotNull(sloterr.getSecond());
		assertEquals(ErrorType.INVALID_TOKEN, sloterr.getSecond().getType());
		
		sloterr = CONTROLLER.analyzeToken(USER1.getToken(), USER1.getMail());
		assertNull(sloterr.getSecond());
		assertEquals(SLOT_REPO.retrieve(slotnum1), sloterr.getFirst());
		assertEquals(USER1, sloterr.getFirst().getUser());
		
	}
	
	@Test public void testFreSlot() {
		USER1.setState(UserState.OUTDOOR_RESERVED);
		USER_REPO.update(USER1);
		SLOT1.setSlotstate(ParkingSlotState.ALMOST_FREE);
		SLOT1.setUser(USER1);
		SLOT_REPO.update(SLOT1);
		
		Pair<User, ParkingSlot> userslot = CONTROLLER.freeSlotUsedByUserAtOutdoor();
		assertNull(userslot.getFirst());
		assertNull(userslot.getSecond());
		
		DOORS_MGR.reserveForUser(OUTDOOR, USER1);
		userslot = CONTROLLER.freeSlotUsedByUserAtOutdoor();
		assertNotNull(userslot.getFirst());
		assertNotNull(userslot.getSecond());
		assertEquals(USER1, userslot.getFirst());
		assertEquals(SLOT1, userslot.getSecond());
		assertEquals(ParkingSlotState.FREE, userslot.getSecond().getSlotstate());
		
	}
	
	@Test public void testCarLeaved() {
		User u = CONTROLLER.setCarOfUserAtOutdoorLeaved();
		assertNull(u);
		
		USER1.setState(UserState.OUTDOOR_RESERVED);
		USER_REPO.create(USER1);
		DOORS_MGR.setUserAtDoor(OUTDOOR, USER1);
		DOORS_MGR.setState(OUTDOOR, DoorState.OCCUPIED);
		
		u = CONTROLLER.setCarOfUserAtOutdoorLeaved();
		assertNotNull(u);
		assertEquals(USER1, u);
		assertEquals(UserState.PICKEDUP, u.getState());
		assertEquals(DoorState.FREE, DOORS_MGR.getState(OUTDOOR));
	}
	
	
	@Test public void testCreateTwoUserWithSameMail() {
		USER1 = CONTROLLER.createUser("name", "surname", "mail");
		try {
			USER2 = CONTROLLER.createUser("name2", "surname2", "mail");
			fail();
		} catch (Exception e) {
			assertEquals(SQLException.class, e.getClass());
		}
	}

	@Test
	public void testOnlyOneUserCompleteCicle() {
		
		//User creation
		USER1 = CONTROLLER.createUser("name", "surname", "mail");
		assertEquals(USER1, USER_REPO.retrieve(USER1.getId()));
		
		//Retrieving slotnum
		ParkingSlot assigned = SLOT_REPO.retrieve(CONTROLLER.assignSlotToUser(USER1));
		assertTrue(assigned.equals(SLOT1) || assigned.equals(SLOT2));
		assertEquals(UserState.INTERESTED, USER1.getState());
		assertEquals(ParkingSlotState.RESERVED, assigned.getSlotstate());
		assertEquals(USER1, assigned.getUser());
		
		//Indoor reservation
		assertTrue(CONTROLLER.reserveDoorForUserOrEnqueue(INDOOR, USER1));
		assertEquals(UserState.INDOOR_RESERVED, USER1.getState());
		assertEquals(DoorState.RESERVED, DOORS_MGR.getState(INDOOR));
		assertEquals(USER1, DOORS_MGR.getUserAtDoor(INDOOR));
		
		//Indoor occupation
		User occupant = CONTROLLER.setSomeoneOnDoor(INDOOR);
		assertNotNull(occupant);
		assertEquals(USER1, occupant);
		assertEquals(USER1, DOORS_MGR.getUserAtDoor(INDOOR));
		assertEquals(DoorState.OCCUPIED, DOORS_MGR.getState(INDOOR));
		
		//Token assignment
		assertNull(USER1.getToken());
		Pair<User, ParkManagerError> usererr = CONTROLLER.assignTokenToUserAtIndoor(
				Long.toString(assigned.getSlotnum()), USER1.getMail());
		assertNull(usererr.getSecond());
		assertEquals(USER1, usererr.getFirst());
		assertNotNull(USER1.getToken());
		
		//Car parked
		User parked = CONTROLLER.setCarOfUserAtIndoorParked();
		assertNotNull(parked);
		assertEquals(USER1, parked);
		assertEquals(UserState.PARKED, USER1.getState());
		assertEquals(DoorState.FREE, DOORS_MGR.getState(INDOOR));
		assertNull(DOORS_MGR.getUserAtDoor(INDOOR));
		assertEquals(ParkingSlotState.OCCUPIED, assigned.getSlotstate());
		assertEquals(USER1, assigned.getUser());
		
		//Pickup
		Pair<ParkingSlot, ParkManagerError> sloterr = CONTROLLER.analyzeToken(USER1.getToken(), USER1.getMail());
		assertNotNull(sloterr.getFirst());
		assertNull(sloterr.getSecond());
		assertEquals(assigned, sloterr.getFirst());
		assertEquals(ParkingSlotState.ALMOST_FREE, sloterr.getFirst().getSlotstate());
		
		//Outdoor reservation
		assertTrue(CONTROLLER.reserveDoorForUserOrEnqueue(OUTDOOR, USER1));
		assertEquals(UserState.OUTDOOR_RESERVED, USER1.getState());
		assertEquals(DoorState.RESERVED, DOORS_MGR.getState(OUTDOOR));
		assertNotNull(DOORS_MGR.getUserAtDoor(OUTDOOR));
		assertEquals(USER1, DOORS_MGR.getUserAtDoor(OUTDOOR));
		
		//Free slot
		Pair<User, ParkingSlot> userslot = CONTROLLER.freeSlotUsedByUserAtOutdoor();
		assertEquals(assigned.getSlotnum(), userslot.getSecond().getSlotnum());
		assertEquals(ParkingSlotState.FREE, userslot.getSecond().getSlotstate());
		assertNull(userslot.getSecond().getUser());
		
		//Outdoor occupation
		occupant = CONTROLLER.setSomeoneOnDoor(OUTDOOR);
		assertNotNull(occupant);
		assertEquals(USER1, occupant);
		assertEquals(USER1, DOORS_MGR.getUserAtDoor(OUTDOOR));
		assertEquals(DoorState.OCCUPIED, DOORS_MGR.getState(OUTDOOR));
		
		//User leaved
		User leaved = CONTROLLER.setCarOfUserAtOutdoorLeaved();
		assertEquals(DoorState.FREE, DOORS_MGR.getState(OUTDOOR));
		assertNull(DOORS_MGR.getUserAtDoor(OUTDOOR));
		assertEquals(leaved, USER1);
		assertEquals(UserState.PICKEDUP, leaved.getState());
		
		//User destroy
		CONTROLLER.destroyUser(USER1);
		assertNull(USER_REPO.retrieve(USER1.getId()));
		
	}

}
