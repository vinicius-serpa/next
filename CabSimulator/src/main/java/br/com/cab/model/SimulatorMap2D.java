package br.com.cab.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * {@link SimulatorMap} que representa um plano. {@link SimulatorMap2D} são
 * imutáveis.
 * 
 *
 */
public final class SimulatorMap2D implements SimulatorMap {

	private final Position2D origin;
	private final Position2D limit;

	private final Set<Position2D> bloquedPositions;

	public SimulatorMap2D(final int maxX, final int maxY, final Collection<Position2D> bloquedPositions) {
		this(Position2D.ORIGIN, Position2D.getInstance(maxX, maxY), bloquedPositions);
	}

	public SimulatorMap2D(final Position2D origin, final Position2D limit,
			final Collection<Position2D> bloquedPositions) {
		this.origin = Objects.requireNonNull(origin);
		this.limit = Objects.requireNonNull(limit);
		this.bloquedPositions = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(bloquedPositions)));
	}

	@Override
	public boolean containsPosition(final Position position) {
		final Position2D position2D = (Position2D) position;
		return position2D.getX() >= origin.getX() && position2D.getY() >= origin.getY()
				&& position2D.getX() <= limit.getX() && position2D.getY() <= limit.getY();
	}

	@Override
	public Set<Position2D> getBloquedPositions() {
		return bloquedPositions;
	}

	@Override
	public boolean isValid(final Position position) {
		final Position2D position2D = (Position2D) position;
		return containsPosition(position2D) && !bloquedPositions.contains(position2D);
	}

	public Position2D getOrigin() {
		return origin;
	}

	public Position2D getLimit() {
		return limit;
	}

}
