package br.com.cab.engine.calculators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import br.com.cab.model.Position;
import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;
import br.com.cab.utils.Pair;

abstract class AbstractPositionCalculator2D implements PositionCalculator {

	private final Random random = new Random();

	private final CacheLoader<Pair<Position2D, Position2D>, Object> cacheLoader = new CacheLoader<Pair<Position2D, Position2D>, Object>() {
		@Override
		public Object load(final Pair<Position2D, Position2D> key) throws Exception {
			try {
				return Collections.unmodifiableList(doPath(key.getFirst(), key.getSecond()));
			} catch (final UnfeasiblePathException e) {
				return e;
			}
		}
	};

	private final LoadingCache<Pair<Position2D, Position2D>, Object> cache = CacheBuilder.newBuilder().recordStats()
			.concurrencyLevel(100).maximumSize(100000).build(cacheLoader);

	private final SimulatorMap2D simulatorMap2D;

	public AbstractPositionCalculator2D(final SimulatorMap2D simulatorMap2D) {
		this.simulatorMap2D = Objects.requireNonNull(simulatorMap2D);
	}

	abstract List<Position2D> doPath(final Position2D origin, final Position2D destination)
			throws UnfeasiblePathException;

	@Override
	public final SimulatorMap2D getSimulatorMap() {
		return simulatorMap2D;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Position2D> path(final Position origin, final Position destination)
			throws UnfeasiblePathException {
		try {
			final Object result = cache.get(Pair.newPair((Position2D) origin, (Position2D) destination));
			if (result instanceof UnfeasiblePathException) {
				throw (UnfeasiblePathException) result;
			}
			return (List<Position2D>) result;
		} catch (final ExecutionException e) {
			throw new UnfeasiblePathException("Cannot calculate path", e);
		}
	}

	@Override
	public final Position randomPosition(final Position currentPosition, final List<Position> lastPositions) {
		final Position2D currentPosition2D = (Position2D) currentPosition;
		final List<Position2D> validAdjacents = getValidAdjacents(currentPosition2D);
		if (validAdjacents.isEmpty()) {
			return currentPosition;
		}
		final List<Position2D> result = evictLastPositions(validAdjacents, lastPositions);
		return result.get(random.nextInt(result.size()));
	}

	private List<Position2D> getValidAdjacents(final Position2D position2D) {
		final List<Position2D> result = new ArrayList<>(4);
		for (final Position2D adjacent : position2D.getAdjacents()) {
			if (simulatorMap2D.isValid(adjacent)) {
				result.add(adjacent);
			}
		}
		return result;
	}

	/**
	 * Tenta evitar as últimas {@link Position}s percorridas. Se não for
	 * possível evitar todas as N {@link Position}s, evita N-1, se não for
	 * possível, evita N-2 e assim sucessivamente.
	 * 
	 * @param validAdjacents
	 * @param lastPositions
	 * @return
	 */
	private List<Position2D> evictLastPositions(final List<Position2D> validAdjacents,
			final List<Position> lastPositions) {
		if (lastPositions.isEmpty()) {
			return validAdjacents;
		}

		List<Position2D> result;
		int i = 0;
		do {
			result = new ArrayList<>(validAdjacents);
			result.removeAll(lastPositions.subList(i++, lastPositions.size()));
		} while (result.isEmpty());
		return result;
	}

}
