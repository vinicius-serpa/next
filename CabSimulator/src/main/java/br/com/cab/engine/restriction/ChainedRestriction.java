package br.com.cab.engine.restriction;

import java.util.List;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

/**
 * Permite encadear várias {@link Restriction}s
 *
 */
public class ChainedRestriction implements Restriction {

	// A ordem é relevante devido a performance (restrições menos complexas
	// devem vir antes)
	private final List<Restriction> restricions;

	public ChainedRestriction(final List<Restriction> restricions) {
		// Programação defensiva
		// Fast fail
		if (restricions == null || restricions.isEmpty()) {
			throw new IllegalArgumentException("Restriction cannot be null or empty.");
		}
		this.restricions = restricions;
	}

	@Override
	public boolean isFeasible(final Cab cab, final Passenger passenger) {
		for (final Restriction restriction : restricions) {
			if (!restriction.isFeasible(cab, passenger)) {
				return false;
			}
		}
		return true;
	}

}
