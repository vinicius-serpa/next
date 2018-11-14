package br.com.cab.service;

import br.com.cab.engine.calculators.PositionCalculator;
import br.com.cab.model.Passenger;
import br.com.cab.model.PassengerImpl;
import br.com.cab.model.Position;
import br.com.cab.model.Position2D;

public class RouterService {
	
	private PositionCalculator positionCalculator;
	
	public RouterService(PositionCalculator positionCalculator) {
		this.positionCalculator = positionCalculator;
	}
	
	public Passenger passengerRequest(String id, int originX, int originY, int destinationX, int destinationY) {
		
		final Position origin = Position2D.getInstance(originX, originY);
		final Position destination = Position2D.getInstance(destinationX, destinationY);			
		final Passenger passenger = new PassengerImpl(id, positionCalculator, origin, destination);
		
		return passenger;		
	}
}
