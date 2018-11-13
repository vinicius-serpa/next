package br.com.cab.engine.restriction;

import br.com.cab.engine.calculators.PositionCalculator;
import br.com.cab.engine.calculators.PositionCalculator.UnfeasiblePathException;
import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

/**
 * Deve existir um caminho válido entre {@link Passenger#getPosition()} e
 * {@link Passenger#getDestination()} e entre o {@link Cab#getPosition()} e
 * {@link Passenger#getPosition()}.
 *
 */
public class PathRestriction implements Restriction {

	private final PositionCalculator positionCalculator;

	public PathRestriction(final PositionCalculator positionCalculator) {
		this.positionCalculator = positionCalculator;
	}

	@Override
	public boolean isFeasible(final Cab cab, final Passenger passenger) {
		try {
			return passenger.pathToDestination() != null
					&& positionCalculator.path(cab.getPosition(), passenger.getPosition()) != null;
		} catch (final UnfeasiblePathException e) {
			return false;
		}
	}

}
