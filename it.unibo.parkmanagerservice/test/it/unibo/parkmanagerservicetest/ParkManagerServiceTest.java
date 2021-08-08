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
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.ctxcarparking.MainCtxcarparkingKt;
import it.unibo.parkimanagerservice.test.utils.CoapObserver;
import it.unibo.parkimanagerservice.test.utils.JCoapChangeObserver;
import it.unibo.parkmanagerservice.actorchat.ActorChatter;

public class ParkManagerServiceTest {
	
	private static ActorChatter pmschat;
	private static CoapObserver pmscoap;
	private static Path TMPPL;
	
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
	

	@Test
	public void test() throws IOException {
		pmschat.sendEvent("weighton", "X");
		System.out.println("\t\t##event sended");
		System.in.read();
	}

}
