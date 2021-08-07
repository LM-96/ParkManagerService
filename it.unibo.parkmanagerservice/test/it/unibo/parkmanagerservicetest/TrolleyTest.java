package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.ctxcarparking.MainCtxcarparkingKt;
import it.unibo.parkimanagerservice.test.utils.CoapObserver;
import it.unibo.parkimanagerservice.test.utils.JCoapChangeObserver;
import it.unibo.parkmanagerservice.actorchat.ActorChatter;
import it.unibo.parkmanagerservice.trolley.SlotMap;
import kotlin.Pair;

public class TrolleyTest {
	
	private static Path TMPPL;
	private static ActorChatter chatter;
	private static CoapObserver coap;
	
	private static String HOME = "0,0";
	private static SlotMap SLOT_MAP = SlotMap.INSTANCE;
	
	@BeforeClass public static void setUp() throws IOException, JSONException, InterruptedException {
		setOnlyTrolley();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				MainCtxcarparkingKt.main();				
			}
			
		}).start();
		/*int attempts = 10;
		for(int i=0; i<attempts && chatter == null; i++) {
			try {
				System.out.println("[" + (i+1) + "\\10] Connecting chatter...");
				chatter = ActorChatter.Companion.newChatterFor("localhost", 8010, "trolley");
				Thread.sleep(1000);
			} catch (Exception e) {
				chatter = null;
			}
		}*/
		
		/*if(chatter == null) {
			System.out.println("Unable to start chatter");
			restoreOriginalPl();
			fail();
			System.exit(-1);
		}*/
		
		coap = new JCoapChangeObserver("localhost:8010", "ctxcarparking", "trolley");
		coap.startObserve();
		
		while(!(new JSONObject(coap.nextChange()).get("state").equals("IDLE")));
		chatter = ActorChatter.Companion.newChatterFor("localhost", 8010, "trolley");
		
		System.out.println("Chatter started.");		
	}
	
	@AfterClass public static void finalizing() throws IOException {
		restoreOriginalPl();
		Files.deleteIfExists(TMPPL);
	}
	
	private static void setOnlyTrolley() throws IOException {
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
		writer.write("context(ctxbasicrobot, \"127.0.0.1\",  \"TCP\", \"8020\").\n");
		writer.write("  qactor( trolley, ctxcarparking, \"it.unibo.trolley.Trolley\").\n");
		writer.write("  qactor( basicrobot, ctxbasicrobot, \"external\").\n");
		writer.flush();
		writer.close();
		lines.close();
	}
	
	private static void restoreOriginalPl() throws IOException {
		Path carparkingpl = Paths.get("carparking.pl");
		Files.copy(TMPPL, carparkingpl, StandardCopyOption.REPLACE_EXISTING);
		Files.delete(TMPPL);
	}
	
	@Test
	public void testParkOneCar() throws IOException, JSONException, InterruptedException {
		List<String> history = new ArrayList<String>();
		String pos;
		int slotnum = 1;
		Pair<Integer, Integer> slotpos = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum));
		List<String> historyExpected = Arrays.asList(
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_IN\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_HOME\",\"position\":{\"x\":\"0\",\"y\":\"0\"}}"
				
		);
		
		chatter.sendDispatch("parkcar", Integer.toString(slotnum));
		System.out.println("TrolleyTest | Request to park at slotnum " + slotnum);
		while(!(new JSONObject(coap.nextChange()).get("state").equals("WORKING"))) {
			System.out.println("TrolleyTest | Waiting for trolley to be WORKING");
		}
		
		System.out.println("\t\t##TrolleyTest | Observing...");
		
		while(!((pos = coap.nextChange()).contains("IDLE"))) {
			if(historyExpected.contains(pos))
				history.add(pos);
		}
		
		System.out.println("TrolleyTest | Execution done... checking history");
		history.forEach(l -> System.out.println(l));			
		assertArrayEquals(historyExpected.toArray(), history.toArray());
		history.clear();
	}

	@Test
	public void testTwoParkAtTheSameMoment() throws IOException, JSONException, InterruptedException {
		List<String> history = new ArrayList<String>();
		String pos;
		int slotnum1 = 1;
		int slotnum2 = 2;
		Pair<Integer, Integer> slotpos1 = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum1));
		Pair<Integer, Integer> slotpos2 = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum2));
		List<String> historyExpected = Arrays.asList(
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_IN\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos1.getFirst() + "\",\"y\":\"" + slotpos1.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"" + slotpos1.getFirst() + "\",\"y\":\"" + slotpos1.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_IN\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos2.getFirst() + "\",\"y\":\"" + slotpos2.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"" + slotpos2.getFirst() + "\",\"y\":\"" + slotpos2.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_HOME\",\"position\":{\"x\":\"0\",\"y\":\"0\"}}"
				
		);
		
		chatter.sendDispatch("parkcar", Integer.toString(slotnum1));
		System.out.println("TrolleyTest | Request to park at slotnum " + slotnum1);
		chatter.sendDispatch("parkcar", Integer.toString(slotnum2));
		System.out.println("TrolleyTest | Request to park at slotnum " + slotnum2);
		while(!(new JSONObject(coap.nextChange()).get("state").equals("WORKING"))) {
			System.out.println("TrolleyTest | Waiting for trolley to be WORKING");
		}
		
		System.out.println("\t\t##TrolleyTest | Observing...");
		
		while(!((pos = coap.nextChange()).contains("IDLE"))) {
			if(historyExpected.contains(pos))
				history.add(pos);
		}
		
		System.out.println("TrolleyTest | Execution done... checking history");
		history.forEach(l -> System.out.println(l));			
		assertArrayEquals(historyExpected.toArray(), history.toArray());
		history.clear();
	}
	
	@Test
	public void testPickupOneCar() throws IOException, JSONException, InterruptedException {
		List<String> history = new ArrayList<String>();
		String pos;
		int slotnum = 1;
		Pair<Integer, Integer> slotpos = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum));
		List<String> historyExpected = Arrays.asList(
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_OUT\",\"position\":{\"x\":\"6\",\"y\":\"3\"}}",
				"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"3\"}}",
				"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_HOME\",\"position\":{\"x\":\"0\",\"y\":\"0\"}}"
				
		);
		
		chatter.sendDispatch("pickupcar", Integer.toString(slotnum));
		System.out.println("TrolleyTest | Request to pick up at slotnum " + slotnum);
		while(!(new JSONObject(coap.nextChange()).get("state").equals("WORKING"))) {
			System.out.println("TrolleyTest | Waiting for trolley to be WORKING");
		}
		
		System.out.println("\t\t##TrolleyTest | Observing...");
		
		while(!((pos = coap.nextChange()).contains("IDLE"))) {
			if(historyExpected.contains(pos))
				history.add(pos);
		}
		
		System.out.println("TrolleyTest | Execution done... checking history");
		history.forEach(l -> System.out.println(l));			
		assertArrayEquals(historyExpected.toArray(), history.toArray());
		history.clear();
	}
	
	@Test
	public void testParkToAllSlot() throws IOException, JSONException, InterruptedException {
		List<String> history = new ArrayList<String>();
		String pos;
		Pair<Integer, Integer> slotpos;
		
		for(int slotnum : SLOT_MAP.getAllSlotnum()) {
			slotpos = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum));
			List<String> historyExpected = Arrays.asList(
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_IN\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
					"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"1\"}}",
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
					"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_HOME\",\"position\":{\"x\":\"0\",\"y\":\"0\"}}"
					
			);
			
			chatter.sendDispatch("parkcar", Integer.toString(slotnum));
			System.out.println("TrolleyTest | Request to park at slotnum " + slotnum);
			while(!(new JSONObject(coap.nextChange()).get("state").equals("WORKING"))) {
				System.out.println("TrolleyTest | Waiting for trolley to be WORKING");
			}
			
			System.out.println("\t\t##TrolleyTest | Observing...");
			
			while(!((pos = coap.nextChange()).contains("IDLE"))) {
				if(historyExpected.contains(pos))
					history.add(pos);
			}
			
			System.out.println("TrolleyTest | Execution done... checking history");
			history.forEach(l -> System.out.println(l));			
			assertArrayEquals(historyExpected.toArray(), history.toArray());
			history.clear();
		}
	}
	
	@Test
	public void testPickFromAllSlot() throws IOException, JSONException, InterruptedException {
		List<String> history = new ArrayList<String>();
		String pos;
		Pair<Integer, Integer> slotpos;
		
		for(int slotnum : SLOT_MAP.getAllSlotnum()) {
			slotpos = SLOT_MAP.getAdiacentAllowedPositionFromSlot(Integer.toString(slotnum));
			List<String> historyExpected = Arrays.asList(
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_SLOT\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
					"{\"state\":\"WORKING\",\"action\":\"LOAD_CAR\",\"position\":{\"x\":\"" + slotpos.getFirst() + "\",\"y\":\"" + slotpos.getSecond() + "\"}}",
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_OUT\",\"position\":{\"x\":\"6\",\"y\":\"3\"}}",
					"{\"state\":\"WORKING\",\"action\":\"UNLOAD_CAR\",\"position\":{\"x\":\"6\",\"y\":\"3\"}}",
					"{\"state\":\"WORKING\",\"action\":\"MOVING_TO_HOME\",\"position\":{\"x\":\"0\",\"y\":\"0\"}}"
					
			);
			
			chatter.sendDispatch("pickupcar", Integer.toString(slotnum));
			System.out.println("TrolleyTest | Request to pick up at slotnum " + slotnum);
			while(!(new JSONObject(coap.nextChange()).get("state").equals("WORKING"))) {
				System.out.println("TrolleyTest | Waiting for trolley to be WORKING");
			}
			
			System.out.println("\t\t##TrolleyTest | Observing...");
			
			while(!((pos = coap.nextChange()).contains("IDLE"))) {
				if(historyExpected.contains(pos))
					history.add(pos);
			}
			
			System.out.println("TrolleyTest | Execution done... checking history");
			history.forEach(l -> System.out.println(l));			
			assertArrayEquals(historyExpected.toArray(), history.toArray());
			history.clear();
		}
	}*/

}
