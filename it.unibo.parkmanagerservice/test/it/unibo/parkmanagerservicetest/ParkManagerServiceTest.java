package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.ctxcarparking.MainCtxcarparkingKt;
import it.unibo.parkimanagerservice.test.utils.CoapObserver;
import it.unibo.parkimanagerservice.test.utils.JCoapChangeObserver;
import it.unibo.parkmanagerservice.actorchat.ActorChatter;
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

public class ParkManagerServiceTest {
	
	private static ActorChatter pmschat;
	private static CoapObserver pmscoap;
	private static Path TMPPL;
	
	private static UserRepository userRepo;
	private static ParkingSlotRepository slotRepository;
	private static DoorsManager doors = LocalDoorState.get();
	
	@BeforeClass
	public static void setup() throws IOException, InterruptedException {
		setOnlyCarParking();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				MainCtxcarparkingKt.main();				
			}
			
		}).start();
		pmscoap = new JCoapChangeObserver("localhost:8010", "ctxcarparking", "parkingmanagerservice");
		pmscoap.startObserve();
		pmscoap.nextChange();
		
		pmschat = ActorChatter.Companion.newChatterFor("localhost", 8010, "parkingmanagerservice");
		
		while(slotRepository == null) {
			slotRepository = ParkingRepositories.getParkingSlotRepository();
		}
		userRepo = ParkingRepositories.getUserRepository();
	}
	
	@AfterClass
	public static void finalizing() throws IOException {
		restoreOriginalPl();
		Files.deleteIfExists(TMPPL);
	}
	
	
	private static void setOnlyCarParking() throws IOException {
		Path carparkingpl = Paths.get("carparking.pl");
		Stream<String> lines = Files.lines(carparkingpl);
		
		TMPPL = Files.createTempFile("carparking.pl", null);
		Files.copy(carparkingpl, TMPPL, StandardCopyOption.REPLACE_EXISTING);
		BufferedWriter writer = Files.newBufferedWriter(carparkingpl,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		
		Iterator<String> it = lines.filter(l -> (l.contains("ctxcarparking") && l.contains("context")) 
				|| l.contains("trolley"))
			.iterator();
		writer.write("%====================================================================================\r\n"
				+ "% carparking description   \r\n"
				+ "%====================================================================================\r\n");
		writer.write("context(ctxcarparking, \"localhost\",  \"TCP\", \"8010\").\n");
		writer.write("  qactor( parkingmanagerservice, ctxcarparking, \"it.unibo.parkingmanagerservice.Parkingmanagerservice\").\n");
		writer.write("  qactor( itocccounter, ctxcarparking, \"it.unibo.itocccounter.Itocccounter\").\n");
		writer.write("  qactor( parkingmanagerservice, ctxcarparking, \"it.unibo.parkingmanagerservice.Parkingmanagerservice\").\n");
		writer.write("  qactor( dtfreecounter, ctxcarparking, \"it.unibo.dtfreecounter.Dtfreecounter\").\n");
		writer.write("  qactor( notificationactor, ctxcarparking, \"it.unibo.notificationactor.Notificationactor\").\n");
		writer.write("  qactor( antifireactor, ctxcarparking, \"it.unibo.antifireactor.Antifireactor\").\n");
		writer.flush();
		writer.close();
		lines.close();
	}
	
	private static void restoreOriginalPl() throws IOException {
		Path carparkingpl = Paths.get("carparking.pm");
		Files.copy(TMPPL, carparkingpl, StandardCopyOption.REPLACE_EXISTING);
		Files.delete(TMPPL);
	}
	
	@Before
	public void before() {
		pmscoap.clear();		
	}
	

	@Test
	public void test() throws IOException, InterruptedException {
		pmschat.sendRequest("enter", "n1,s1,m1");
		String slot1 = pmschat.readLastResponse().msgContent();
		System.out.println("*******" + slot1);
		assertEquals(doors.getUserAtDoor(DoorType.INDOOR).getMail(), "m1");
		
	}

}
