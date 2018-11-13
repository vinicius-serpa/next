package br.com.cab.engine.selector;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;
import br.com.cab.utils.Pair;

/**
 * Implementações dessa interface utilizam algum critério para determinar quais
 * são os melhores pares de {@link Cab} e {@link Passenger} a ser efetivados.
 * 
 */
public interface Selector {

	/**
	 * Dado o conjunto de matches possíveis, seleciona os melhores.
	 * 
	 * @param possiblePassengerByCab
	 */
	Set<Pair<Cab, Passenger>> select(Map<Cab, Set<Passenger>> possiblePassengerByCab);

	/**
	 * Implementações padrões de {@link Selector}
	 *
	 */
	public static enum StandardSelector implements Selector {

		/**
		 * Seleciona os pares com as menores distância
		 */
		MINIMIZE_DISTANCE {

			@Override
			public Set<Pair<Cab, Passenger>> select(final Map<Cab, Set<Passenger>> possiblePassengerByCab) {

				final Map<Long, Set<Pair<Cab, Passenger>>> pairsByDistance = new TreeMap<>();
				for (final Entry<Cab, Set<Passenger>> entry : possiblePassengerByCab.entrySet()) {
					final Cab cab = entry.getKey();
					for (final Passenger passenger : entry.getValue()) {
						final Pair<Cab, Passenger> pair = Pair.newPair(cab, passenger);
						final Long distance = (long) cab.getPosition().distanceTo(passenger.getPosition()) * 100;
						Set<Pair<Cab, Passenger>> pairsWithSameDistance = pairsByDistance.get(distance);
						if (pairsWithSameDistance == null) {
							pairsWithSameDistance = new HashSet<>();
							pairsByDistance.put(distance, pairsWithSameDistance);
						}
						pairsWithSameDistance.add(pair);

					}
				}

				final Set<Cab> selectedCabs = new HashSet<>();
				final Set<Passenger> selectedPassengers = new HashSet<>();

				final Set<Pair<Cab, Passenger>> result = new HashSet<>();
				for (final Long distance : pairsByDistance.keySet()) {
					final Set<Pair<Cab, Passenger>> pairsWithSameDistance = pairsByDistance.get(distance);
					for (final Pair<Cab, Passenger> pair : pairsWithSameDistance) {
						final Cab cab = pair.getFirst();
						final Passenger passenger = pair.getSecond();
						if (!selectedCabs.contains(cab) && !selectedPassengers.contains(passenger)) {
							result.add(pair);
							selectedCabs.add(cab);
							selectedPassengers.add(passenger);
						}
					}
				}

				return result;
			}

		}

	}

}
