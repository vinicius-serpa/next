package br.com.cab.utils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.cab.model.Passenger;
import br.com.cab.model.Passenger.Status;

/**
 * Oferece utilitários relacionados a {@link Passenger}
 *
 */
public class Passengers {

	/**
	 * Essa classe não tem a finalidade de criar objetos.
	 */
	private Passengers() {
		throw new AssertionError();
	}

	public static <P extends Passenger> Set<P> filter(final Collection<P> passengers, final Status status) {
		return passengers.stream().filter(p -> p.getStatus() == status).collect(Collectors.toSet());
	}

}
