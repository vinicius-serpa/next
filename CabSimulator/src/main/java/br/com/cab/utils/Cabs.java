package br.com.cab.utils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.cab.model.Cab;
import br.com.cab.model.Cab.Status;

/**
 * Oferece utilitários relacionados a {@link Cab}
 *
 */
public class Cabs {

	/**
	 * Essa classe não tem a finalidade de criar objetos.
	 */
	private Cabs() {
		throw new AssertionError();
	}

	public static <C extends Cab> Set<C> filter(final Collection<C> cabs, final Status status) {
		return cabs.stream().filter(c -> c.getStatus() == status).collect(Collectors.toSet());
	}

}
