package br.com.cab.engine.restriction;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

public class MaxDistanceRestriction implements Restriction {

	private final int maxDistance;

	public MaxDistanceRestriction(final int maxDistance) {
		if (maxDistance <= 0) {
			throw new IllegalArgumentException("Max distance cannot be negative or zero.");
		}
		this.maxDistance = maxDistance;
	}

	@Override
	public boolean isFeasible(final Cab cab, final Passenger passenger) {
		final double distance = cab.getPosition().distanceTo(passenger.getPosition());
		return distance < maxDistance;
	}

}
