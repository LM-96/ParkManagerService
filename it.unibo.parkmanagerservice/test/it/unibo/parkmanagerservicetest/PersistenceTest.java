package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

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
import it.unibo.parkmanagerservice.persistence.ParkingRepositories;
import it.unibo.parkmanagerservice.persistence.ParkingSlotRepository;
import it.unibo.parkmanagerservice.persistence.UserRepository;

public class PersistenceTest {
	
	private static User USER1 = new User(0, "name1", "surname1", "mail1", UserState.CREATED, null, null);
	private static User USER2 = new User(1, "name2", "surname2", "mail2", UserState.CREATED, null, null);
	
	private static ParkingSlot SLOT1 = new ParkingSlot(1, ParkingSlotState.FREE, null);
	private static ParkingSlot SLOT2 = new ParkingSlot(2, ParkingSlotState.FREE, null);
	
	private static DoorState[] DOOR_STATES = DoorState.values();
	private static DoorType[] DOORS_TYPE = DoorType.values();
	private static UserState[] USER_STATES = UserState.values();
	private static ParkingSlotState[] SLOT_STATES = ParkingSlotState.values();
	
	
	private static UserRepository userRepo;
	private static ParkingSlotRepository slotRepo;
	private static DoorsManager doorsManager;
	
	@BeforeClass
	public static void setup() {
		ParkingRepositories.createBasics(0);
		userRepo = ParkingRepositories.getUserRepository();
		slotRepo = ParkingRepositories.getParkingSlotRepository();
	}
	
	@Before
	public void clean() {
		userRepo.delete(USER1);
		userRepo.delete(USER2);
		slotRepo.delete(SLOT1);
		slotRepo.delete(SLOT2);
		
		USER1 = new User(0, "name1", "surname1", "mail1", UserState.CREATED, null, null);
		USER2 = new User(1, "name2", "surname2", "mail2", UserState.CREATED, null, null);
		SLOT1 = new ParkingSlot(1, ParkingSlotState.FREE, null);
		SLOT2 = new ParkingSlot(2, ParkingSlotState.FREE, null);
		
		doorsManager = LocalDoorState.get();
		for(DoorType door : DOORS_TYPE) {
			doorsManager.setState(door, DoorState.FREE);
		}
	}

	@Test
	public void testCrudForUser() {
		userRepo.create(USER1);
		User retrieved = userRepo.retrieve(USER1.getId());
		assertEquals(USER1, retrieved);
		
		USER1.setName("new name 1");
		userRepo.update(USER1);
		assertEquals(USER1, userRepo.retrieve(USER1.getId()));
		
		userRepo.delete(USER1);
		assertNull(userRepo.retrieve(USER1.getId()));
		
		USER1.setName("name1");
	}
	
	@Test
	public void testGetterByStateForUser() {
		userRepo.create(USER1);
		Collection<User> retrieved;
		for(UserState state : USER_STATES) {			
			USER1.setState(state);
			userRepo.update(USER1);
			
			for(UserState state2 : USER_STATES) {
				retrieved = userRepo.getByState(state2);
				if(state != state2)
					assertEquals(0, retrieved.size());
			}
			
			retrieved = userRepo.getByState(state);
			assertEquals(1, retrieved.size());
			assertTrue(retrieved.contains(USER1));
		}
		
		userRepo.create(USER2);
		for(UserState state : USER_STATES) {
			USER1.setState(state);
			userRepo.update(USER1);
			
			for(UserState state2 : USER_STATES) {
				USER2.setState(state2);
				userRepo.update(USER2);
				retrieved = userRepo.getByState(state2);
				
				if(state != state2) {
					assertEquals(1, retrieved.size());
					assertEquals(USER2, retrieved.iterator().next());
				}
				else {
					assertEquals(2, retrieved.size());
					assertTrue(retrieved.containsAll(Arrays.asList(USER1, USER2)));
				}
				
			}
		}
	}
	
	@Test
	public void testGetterByFirstInTheStateUser() {
		userRepo.create(USER1);
		userRepo.create(USER2);
		LocalDateTime time;
		
		for(UserState state : USER_STATES) {
			time = LocalDateTime.now();
			USER1.setState(state); USER1.setTime(Timestamp.valueOf(time));
			USER2.setState(state); USER2.setTime(Timestamp.valueOf(LocalDateTime.now()));
			userRepo.update(USER1);
			userRepo.update(USER2);
			assertEquals(USER1, userRepo.getFirstInState(state));
			
			USER2.setTime(Timestamp.valueOf(time.minusSeconds(1)));
			userRepo.update(USER2);
			assertEquals(USER2, userRepo.getFirstInState(state));
		}
	}
	
	@Test
	public void testGetterByMailUser() {
		userRepo.create(USER1);
		userRepo.create(USER2);
		
		assertEquals(USER1, userRepo.getByMail(USER1.getMail()));
		assertEquals(USER2, userRepo.getByMail(USER2.getMail()));
		assertNull(userRepo.getByMail(USER1.getMail() + USER2.getMail()));
	}
	
	@Test
	public void testGetterByTokenUser() {
		String token1 = "token1";
		String token2 = "token2";
		USER1.setToken(token1);
		USER2.setToken(token2);
		userRepo.create(USER1);
		userRepo.create(USER2);
		
		assertTrue(userRepo.getByToken(token1).isPresent());
		assertTrue(userRepo.getByToken(token2).isPresent());
		assertEquals(USER1, userRepo.getByToken(token1).get());
		assertEquals(USER2, userRepo.getByToken(token2).get());
		assertTrue(!userRepo.getByToken(token1 + token2).isPresent());
	}
	
	@Test
	public void testCrudForSlot() {
		slotRepo.create(SLOT1);
		ParkingSlot retrieved = slotRepo.retrieve(SLOT1.getSlotnum());
		assertEquals(SLOT1, retrieved);
		
		SLOT1.setSlotstate(ParkingSlotState.OCCUPIED);
		userRepo.create(USER1);
		userRepo.update(USER1);
		SLOT1.setUser(USER1);
		slotRepo.update(SLOT1);
		assertEquals(SLOT1, slotRepo.retrieve(SLOT1.getSlotnum()));
		
		slotRepo.delete(SLOT1);
		assertNull(slotRepo.retrieve(SLOT1.getSlotnum()));
	}
	
	@Test
	public void testGetterByStateForSlot() {
		slotRepo.create(SLOT1);
		Collection<ParkingSlot> retrieved;
		for(ParkingSlotState state : SLOT_STATES) {			
			SLOT1.setSlotstate(state);
			slotRepo.update(SLOT1);
			
			for(ParkingSlotState state2 : SLOT_STATES) {
				retrieved = slotRepo.getByState(state2);
				if(state != state2)
					assertEquals(0, retrieved.size());
			}
			
			retrieved = slotRepo.getByState(state);
			assertEquals(1, retrieved.size());
			assertTrue(retrieved.contains(SLOT1));
		}
		
		slotRepo.create(SLOT2);
		for(ParkingSlotState state : SLOT_STATES) {
			SLOT1.setSlotstate(state);
			slotRepo.update(SLOT1);
			
			for(ParkingSlotState state2 : SLOT_STATES) {
				SLOT2.setSlotstate(state2);
				slotRepo.update(SLOT2);
				retrieved = slotRepo.getByState(state2);
				
				if(state != state2) {
					assertEquals(1, retrieved.size());
					assertEquals(SLOT2, retrieved.iterator().next());
				}
				else {
					assertEquals(2, retrieved.size());
					assertTrue(retrieved.containsAll(Arrays.asList(SLOT1, SLOT2)));
				}
				
			}
		}
	}
	
	@Test
	public void testGetterByTokenSlot() {
		String token1 = "token1";
		String token2 = "token2";
		USER1.setToken(token1);
		USER2.setToken(token2);
		userRepo.create(USER1);
		userRepo.create(USER2);
		
		SLOT1.setSlotstate(ParkingSlotState.OCCUPIED);
		SLOT1.setUser(USER1);
		SLOT2.setSlotstate(ParkingSlotState.OCCUPIED);
		SLOT2.setUser(USER2);
		slotRepo.create(SLOT1);
		slotRepo.create(SLOT2);
		
		Optional<ParkingSlot> retrieved = slotRepo.getByToken(token1);
		assertTrue(retrieved.isPresent());
		assertEquals(USER1, retrieved.get().getUser());
		retrieved = slotRepo.getByToken(token2);
		assertTrue(retrieved.isPresent());
		assertEquals(USER2, retrieved.get().getUser());		
	}
	
	@Test
	public void testGetterFirstFreeSlot() {
		Optional<ParkingSlot> retrieved;
		slotRepo.create(SLOT1);
		slotRepo.create(SLOT2);
		
		for(ParkingSlotState s1 : SLOT_STATES) {
			SLOT1.setSlotstate(s1);
			slotRepo.update(SLOT1);
			
			for(ParkingSlotState s2 : SLOT_STATES) {
				SLOT2.setSlotstate(s2);
				slotRepo.update(SLOT2);
				
				retrieved = slotRepo.getFirstFree();
				
				if(s1.equals(ParkingSlotState.FREE) && s2.equals(ParkingSlotState.FREE)) {
					assertTrue(retrieved.isPresent());
					assertTrue(retrieved.get().equals(SLOT1) || retrieved.get().equals(SLOT2));
				} else if(s1.equals(ParkingSlotState.FREE)) {
					assertTrue(retrieved.isPresent());
					assertTrue(retrieved.get().equals(SLOT1));
				} else if(s2.equals(ParkingSlotState.FREE)) {
					assertTrue(retrieved.isPresent());
					assertTrue(retrieved.get().equals(SLOT2));
				} else
					assertTrue(!retrieved.isPresent());
			}
		}
	}
	
	@Test
	public void testGetterByUserSlot() {
		userRepo.create(USER1);
		userRepo.create(USER2);
		SLOT1.setSlotstate(ParkingSlotState.OCCUPIED);
		SLOT1.setUser(USER1);
		SLOT2.setSlotstate(ParkingSlotState.OCCUPIED);
		SLOT2.setUser(USER2);
		slotRepo.create(SLOT1);
		slotRepo.create(SLOT2);
		
		Optional<ParkingSlot> retrieved = slotRepo.getReservedForUser(USER1.getId());
		assertTrue(retrieved.isPresent());
		assertEquals(SLOT1, retrieved.get());
		assertEquals(USER1, retrieved.get().getUser());
		
		retrieved = slotRepo.getReservedForUser(USER2.getId());
		assertTrue(retrieved.isPresent());
		assertEquals(SLOT2, retrieved.get());
		assertEquals(USER2, retrieved.get().getUser());	
	}
	
	
	@Test
	public void testAddUserWithSameMail() {
		userRepo.create(USER1);
		USER2.setMail(USER1.getMail());
		try {
			userRepo.create(USER2);
			fail();
		} catch(Exception e) {
			assertEquals(SQLException.class, e.getClass());
		}
	}
	
	@Test
	public void testDoorManager() {
		for(DoorState state : DOOR_STATES) {
			for(DoorType door : DOORS_TYPE) {
				doorsManager.setState(door, state);
				assertEquals(state, doorsManager.getState(door));
			}
		}
	}
	
	@Test
	public void testDoorManagerUtilityOperation() {
		userRepo.create(USER1);
		for(DoorType door : DOORS_TYPE) {
			doorsManager.setFreeWithNoUser(door);
			assertEquals(DoorState.FREE, doorsManager.getState(door));
			assertNull(doorsManager.getUserAtDoor(door));
			
			doorsManager.reserveForUser(door, USER1);
			assertEquals(DoorState.RESERVED, doorsManager.getState(door));
			assertEquals(USER1, doorsManager.getUserAtDoor(door));
			
			doorsManager.setState(door, DoorState.OCCUPIED);
			assertEquals(DoorState.OCCUPIED, doorsManager.getState(door));
			assertEquals(USER1, doorsManager.getUserAtDoor(door));
			
			doorsManager.setFreeWithNoUser(door);
			assertEquals(DoorState.FREE, doorsManager.getState(door));
			assertNull(doorsManager.getUserAtDoor(door));
		}
	}

}
