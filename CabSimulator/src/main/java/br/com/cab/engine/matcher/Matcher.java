package br.com.cab.engine.matcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;
import br.com.cab.utils.Pair;

/**
 * Implementações dessa interface são responsáveis por realizar o casamento de
 * {@link Cab}s e {@link Passenger}.
 * 
 */
public interface Matcher {

	public static class MatcherResult {

		public static MatcherResult EMPTY = new MatcherResult(Collections.emptySet(), Collections.emptySet(),
				Collections.emptySet());

		/**
		 * Método utilitário para validar a corretude do resultado. As
		 * invariantes são:<br>
		 * <li>Todos os {@link Cab}s iniciais devem estar presentes no resultado
		 * e somente estes.
		 * <li>Todos os {@link Passenger}s iniciais devem estar presentes no
		 * resultado e somente estes.
		 * <li>Um {@link Cab} não pode ser ao mesmo tempo matched e unmatched
		 * <li>Um {@link Passenger} não pode ser ao mesmo tempo matched e
		 * unmatched
		 * 
		 * @param initialCabs
		 *            {@link Cab}s iniciais.
		 * @param initialPassengers
		 *            {@link Passenger}s iniciais
		 * @param matches
		 *            Casamento realizados
		 * @param unmatchedPassengers
		 *            {@link Cab}s sem {@link Passenger}
		 * @param unmatchedPassengers
		 *            {@link Passenger} sem {@link Cab}s
		 */
		public static void validate(Set<Cab> initialCabs, Set<Passenger> initialPassengers,
				Set<Pair<Cab, Passenger>> matches, Set<Cab> unmatchedCabs, Set<Passenger> unmatchedPassengers) {
			final Set<Cab> matchesCabs = matches.stream().map(Pair::getFirst).collect(Collectors.toSet());
			final Set<Passenger> matchesPassengers = matches.stream().map(Pair::getSecond).collect(Collectors.toSet());

			if (!Collections.disjoint(matchesCabs, unmatchedPassengers)) {
				throw new IllegalStateException("Cabs cannot be match and unmatched in same time.");
			}

			if (!Collections.disjoint(matchesPassengers, unmatchedPassengers)) {
				throw new IllegalStateException("Passengers cannot be match and unmatched in same time.");
			}

			final Set<Cab> allCabs = new HashSet<>(matchesCabs);
			allCabs.addAll(unmatchedCabs);
			if (!initialCabs.equals(allCabs)) {
				throw new IllegalStateException("All cabs must be returned.");
			}

			final Set<Passenger> allPassengers = new HashSet<>(matchesPassengers);
			allPassengers.addAll(unmatchedPassengers);
			if (!initialPassengers.equals(allPassengers)) {
				throw new IllegalStateException("All cabs must be returned.");
			}

		}

		private final Set<Pair<Cab, Passenger>> matches;
		private final Set<Cab> unmatchedCabs;
		private final Set<Passenger> unmatchedPassengers;

		public MatcherResult(Set<Pair<Cab, Passenger>> matches, Set<Cab> unmatchedCabs,
				Set<Passenger> unmatchedPassengers) {
			this.matches = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(matches)));
			this.unmatchedCabs = Collections.unmodifiableSet(Objects.requireNonNull(unmatchedCabs));
			this.unmatchedPassengers = Collections.unmodifiableSet(Objects.requireNonNull(unmatchedPassengers));
		}

		public Set<Pair<Cab, Passenger>> getMatches() {
			return matches;
		}

		public Set<Cab> getUnmatchedCabs() {
			return unmatchedCabs;
		}

		public Set<Passenger> getUnmatchedPassengers() {
			return unmatchedPassengers;
		}

	}

	/**
	 * Realiza o casamento entre {@link Cab}s e {@link Passenger}s
	 * 
	 * @param cabs
	 * @param passengers
	 * 
	 * @throws IllegalArgumentException
	 *             Caso os {@link Cab}s ou {@link Passenger}s especificados não
	 *             estejam em seu estado inicial
	 */
	MatcherResult match(Set<Cab> cabs, Set<Passenger> passengers);

}
