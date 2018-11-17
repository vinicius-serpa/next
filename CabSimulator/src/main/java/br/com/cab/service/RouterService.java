package br.com.cab.service;

import java.util.Objects;

import br.com.cab.engine.calculators.PositionCalculator;
import br.com.cab.model.Passenger;
import br.com.cab.model.PassengerImpl;
import br.com.cab.model.Position;
import br.com.cab.model.Position2D;
import br.com.cab.simulator.Simulator;
import br.com.cab.to.PassengerRequestTO;

public class RouterService {
	
	private Simulator map; 
	private PositionCalculator positionCalculator; 
	
	public RouterService(Simulator simulator, PositionCalculator positionCalculator) {
		this.map = Objects.requireNonNull(simulator);
		this.positionCalculator = Objects.requireNonNull(positionCalculator);
	}
	
	public void passengerRequest(PassengerRequestTO request) {
		
		final Position origin = Position2D.getInstance(request.getOriginX(), request.getOriginY());
		final Position destination = Position2D.getInstance(request.getDestinationX(), request.getDestinationY());			
		final Passenger passenger = new PassengerImpl(request.getId(), positionCalculator, origin, destination);
		
		map.add(passenger);
	}
}
