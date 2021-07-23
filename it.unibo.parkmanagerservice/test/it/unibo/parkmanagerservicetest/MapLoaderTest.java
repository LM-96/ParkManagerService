package it.unibo.parkmanagerservicetest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Test;

import it.unibo.trolley.MapLoaderKt;
import itunibo.planner.plannerUtil;
import mapRoomKotlin.mapUtil;

public class MapLoaderTest {
	
	private static String MAP_FILE = "resources/parking_map.txt";
	
	@Test
	public void testLoader() throws IOException {
		Path file = Paths.get(MAP_FILE);
		assertTrue(Files.exists(file));
		
		String content = Files.lines(file).collect(Collectors.joining("\n"));
		MapLoaderKt.loadMapFromTxt(MAP_FILE);
		
		assertEquals(mapUtil.INSTANCE.getMap().toString().trim(), content.trim());
	}

}
