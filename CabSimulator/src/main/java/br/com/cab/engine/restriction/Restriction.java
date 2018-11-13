package br.com.cab.engine.restriction;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

/**
 * Implementações dessa interface impõem restrições ao transporte de um
 * {@link Passenger} por um {@link Cab}.
 *
 */
public interface Restriction {

	/**
	 * Dertermina se é factível o {@link Cab} especificado transportar o
	 * {@link Passenger} especificado.
	 * 
	 */
	boolean isFeasible(Cab cab, Passenger passenger);

	/**
	 * Implementações padrões de {@link Restriction}s
	 *
	 */
	public static enum StandardRestriction implements Restriction {

		/**
		 * Apenas {@link Cab}s {@link Cab.Status#EMPTY} são factíveis
		 */
		CAB_STATUS {
			@Override
			public boolean isFeasible(final Cab cab, final Passenger passenger) {

				return cab.getStatus() == Cab.Status.EMPTY;
			}

		},

		/**
		 * Apenas {@link Passenger}s {@link Passenger.Status#NO_CAB} são
		 * factíveis
		 */
		PASSENGER_STATUS {
			@Override
			public boolean isFeasible(final Cab cab, final Passenger passenger) {

				return passenger.getStatus() == Passenger.Status.NO_CAB;
			}

		},

	}

}
