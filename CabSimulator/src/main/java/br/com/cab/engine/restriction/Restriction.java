package br.com.cab.engine.restriction;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

/**
 * Implementa��es dessa interface imp�em restri��es ao transporte de um
 * {@link Passenger} por um {@link Cab}.
 *
 */
public interface Restriction {

	/**
	 * Dertermina se � fact�vel o {@link Cab} especificado transportar o
	 * {@link Passenger} especificado.
	 * 
	 */
	boolean isFeasible(Cab cab, Passenger passenger);

	/**
	 * Implementa��es padr�es de {@link Restriction}s
	 *
	 */
	public static enum StandardRestriction implements Restriction {

		/**
		 * Apenas {@link Cab}s {@link Cab.Status#EMPTY} s�o fact�veis
		 */
		CAB_STATUS {
			@Override
			public boolean isFeasible(final Cab cab, final Passenger passenger) {

				return cab.getStatus() == Cab.Status.EMPTY;
			}

		},

		/**
		 * Apenas {@link Passenger}s {@link Passenger.Status#NO_CAB} s�o
		 * fact�veis
		 */
		PASSENGER_STATUS {
			@Override
			public boolean isFeasible(final Cab cab, final Passenger passenger) {

				return passenger.getStatus() == Passenger.Status.NO_CAB;
			}

		},

	}

}
