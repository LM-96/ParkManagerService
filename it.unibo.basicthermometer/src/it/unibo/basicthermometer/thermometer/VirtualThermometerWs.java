package it.unibo.basicthermometer.thermometer;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/thermometer")
public class VirtualThermometerWs implements Thermometer {
	
	private double temperature;
	
	

	@Override
	public double readTemperature() {
		return temperature;
	}

}
