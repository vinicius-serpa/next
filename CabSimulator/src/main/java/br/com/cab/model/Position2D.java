package br.com.cab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import br.com.cab.utils.Pair;

/**
 * {@link Position} baseada em pontos no plano. {@link Position2D} são
 * imutáveis.
 *
 */
public final class Position2D implements Position {

	public static Position2D getInstance(final int x, final int y) {
		try {
			return cache.get(Pair.newPair(x, y));
		} catch (final Exception e) {
			return new Position2D(x, y);
		}
	}

	public static Position2D randomPosition(final SimulatorMap2D simulatorMap2D, final Random random, final int maxX,
			final int maxY) {
		Position2D result = null;
		do {
			result = Position2D.getInstance(random.nextInt(maxX), random.nextInt(maxY));
		} while (simulatorMap2D != null && !simulatorMap2D.isValid(result));
		return result;
	}

	public static final List<Position2D> createLine(final Position2D initialPosition, final int size,
			final boolean horizontal) {
		final List<Position2D> result = new ArrayList<>(size);
		Position2D previousPosition = initialPosition;
		for (int i = 0; i < size; i++) {
			result.add(previousPosition);
			if (horizontal) {
				previousPosition = previousPosition.incrementX();
			} else {
				previousPosition = previousPosition.incrementY();
			}
		}
		return result;
	}

	private static CacheLoader<Pair<Integer, Integer>, Position2D> cacheLoader = new CacheLoader<Pair<Integer, Integer>, Position2D>() {
		@Override
		public Position2D load(final Pair<Integer, Integer> key) throws Exception {
			return new Position2D(key.getFirst(), key.getSecond());
		}
	};

	private static LoadingCache<Pair<Integer, Integer>, Position2D> cache = CacheBuilder.newBuilder()
			.concurrencyLevel(1).maximumSize(1000000).recordStats().build(cacheLoader);

	public static final Position2D ORIGIN = getInstance(0, 0);

	private final int x;
	private final int y;

	// Lazy properties
	private List<Position2D> adjacents;
	private int hash;
	private String toString;

	private Position2D(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double distanceTo(final Position position) {
		final Position2D position2D = (Position2D) position;
		return  Math.sqrt(Math.pow(x - position2D.x, 2) + Math.pow(y - position2D.y, 2));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Position2D incrementX(final int increment) {
		return getInstance(x + increment, y);
	}

	public Position2D incrementY(final int increment) {
		return getInstance(x, y + increment);
	}

	public Position2D incrementX() {
		return getInstance(x + 1, y);
	}

	public Position2D decrementX() {
		return getInstance(x - 1, y);
	}

	public Position2D incrementY() {
		return getInstance(x, y + 1);
	}

	public Position2D decrementY() {
		return getInstance(x, y - 1);
	}

	public List<Position2D> getAdjacents() {
		if (adjacents == null) {
			adjacents = new ArrayList<>(4);
			adjacents.add(decrementX());
			adjacents.add(decrementY());
			adjacents.add(incrementX());
			adjacents.add(incrementY());
			adjacents = Collections.unmodifiableList(adjacents);
		}
		return adjacents;
	}

	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			h = Objects.hash(x, y);
			hash = h;
		}
		return h;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Position2D)) {
			return false;
		}

		final Position2D other = (Position2D) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		String string = toString;
		if (string == null) {
			string = "(" + x + ", " + y + ")";
			toString = string;
		}
		return toString;
	}

}
