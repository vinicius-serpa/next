package br.com.cab.engine.matcher;

import java.util.HashSet;
import java.util.Set;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;
import br.com.cab.utils.Pair;

/**
 * Classe esqueleto de {@link Matcher}.
 *
 */
public abstract class AbstractMatcher implements Matcher {

	@Override
	public final MatcherResult match(final Set<Cab> cabs, final Set<Passenger> passengers) {

		if (cabs.isEmpty() || passengers.isEmpty()) {
			return MatcherResult.EMPTY;
		}

		final Set<Pair<Cab, Passenger>> matches = doMatch(cabs, passengers);

		final Set<Cab> matchedCabs = new HashSet<>();
		final Set<Passenger> matchedPassengers = new HashSet<>();

		for (final Pair<Cab, Passenger> selected : matches) {
			matchedCabs.add(selected.getFirst());
			matchedPassengers.add(selected.getSecond());
		}

		final Set<Cab> unmatchedCabs = new HashSet<>(cabs);
		unmatchedCabs.removeAll(matchedCabs);

		final Set<Passenger> unmatchedPassengers = new HashSet<>(passengers);
		unmatchedPassengers.removeAll(matchedPassengers);

		MatcherResult.validate(cabs, passengers, matches, unmatchedCabs, unmatchedPassengers);

		return new MatcherResult(matches, unmatchedCabs, unmatchedPassengers);
	}

	protected abstract Set<Pair<Cab, Passenger>> doMatch(Set<Cab> cabs, Set<Passenger> passengers);

}
