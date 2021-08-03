package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	
	
	private static UserRepository userRepo;
	private static ParkingSlotRepository slotRepo;
	private static DoorsManager doorsManager;
	
	@BeforeClass
	public static void setup() {
		ParkingRepositories.createBasics(0);
		userRepo = ParkingRepositories.getUserRepository();
		slotRepo = ParkingRepositories.getParkingSlotRepository();
		doorsManager = LocalDoorState.get();
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
		userRepo.delete(USER1);
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
		
		userRepo.delete(USER1);
	}
	
	@Test
	public void testDoorManager() {
		
	}

}
