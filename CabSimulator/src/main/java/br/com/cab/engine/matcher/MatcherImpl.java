package br.com.cab.engine.matcher;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import br.com.cab.engine.restriction.Restriction;
import br.com.cab.engine.restriction.Restrictions;
import br.com.cab.engine.selector.Selector;
import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;
import br.com.cab.utils.Pair;

/**
 * Implementação de {@link Matcher} baseada em uma {@link Restriction} e
 * {@link Selector}.
 *
 */
public class MatcherImpl extends AbstractMatcher {

	private final Restriction restriction;
	private final Selector selector;

	public MatcherImpl(Restriction restriction, Selector selector) {
		this.restriction = Objects.requireNonNull(restriction);
		this.selector = Objects.requireNonNull(selector);
	}

	@Override
	protected Set<Pair<Cab, Passenger>> doMatch(Set<Cab> cabs, Set<Passenger> passengers) {
		// Determina os pares possíveis
		final Map<Cab, Set<Passenger>> possiblePassengerByCab = Restrictions.getPossiblesMatches(cabs, passengers,
				restriction);

		// Seleciona os melhores pares
		return selector.select(possiblePassengerByCab);
	}

}
