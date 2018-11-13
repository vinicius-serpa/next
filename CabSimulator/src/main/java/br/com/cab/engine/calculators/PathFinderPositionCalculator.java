package br.com.cab.engine.calculators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.cab.engine.calculators.PathFinderPositionCalculator.PathFinderListener.StandardPathFinderListener;
import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;

public final class PathFinderPositionCalculator extends AbstractPositionCalculator2D {

	private static int MAX_ITERATIONS = 30000;

	public static interface PathFinderListener {
		void step(PathFinder pathFinder);

		public static enum StandardPathFinderListener implements PathFinderListener {
			EMPTY {
				@Override
				public void step(final PathFinder pathFinder) {
					// do nothing
				}
			}
		}
	}

	private static final class Position2DWithCount {

		private final Position2D position2D;
		private final Position2D origin;
		private final int count;

		// Lazy properties
		private Double distanceToOrigin;
		private String toString;

		public Position2DWithCount(final Position2D position2D, final Position2D origin, final int count) {
			this.position2D = position2D;
			this.origin = origin;
			this.count = count;
		}

		double distanceToOrigin() {
			if (distanceToOrigin == null) {
				distanceToOrigin = position2D.distanceTo(origin);
			}
			return distanceToOrigin.doubleValue();
		}

		@Override
		public int hashCode() {
			return position2D.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof Position2DWithCount)) {
				return false;
			}

			final Position2DWithCount other = (Position2DWithCount) obj;

			return position2D.equals(other.position2D);
		}

		@Override
		public String toString() {
			if (toString == null) {
				toString = "(" + position2D.getX() + ", " + position2D.getY() + ", " + count + ")";
			}
			return toString;
		}

	}

	public class PathFinder {

		private final PathFinderListener listener;
		private final Position2D origin;
		private final Position2D destination;

		private final Map<Position2D, Integer> mainMap = new HashMap<>();
		private final Set<Position2DWithCount> processedPositions = new HashSet<>();
		private final Set<Position2DWithCount> notProcessedPositions = new HashSet<>();

		public PathFinder(final PathFinderListener listener, final Position2D origin, final Position2D destination) {
			this.listener = Objects.requireNonNull(listener);
			this.origin = origin;
			this.destination = destination;
		}

		public Position2D getOrigin() {
			return origin;
		}

		public Position2D getDestination() {
			return destination;
		}

		Map<Position2D, Integer> getMainMap() {
			return mainMap;
		}

		List<Position2D> process() throws UnfeasiblePathException {
			long iterations = 0;
			// Adiciona o ponto de destino
			mainMap.put(destination, 0);
			notProcessedPositions.add(new Position2DWithCount(destination, origin, 0));

			boolean mustContinue = true;
			while (mustContinue) {
				final Position2DWithCount notProcessed = nextNotProcessedPosition();
				if (notProcessed != null) {
					doProcess(notProcessed);
					processedPositions.add(notProcessed);
				}

				// Condições de parada
				if (mainMap.containsKey(origin) || notProcessedPositions.isEmpty()) {
					mustContinue = false;
					break;
				}

				// Condição de desistência
				if (iterations++ > MAX_ITERATIONS) {
					throw new UnfeasiblePathException("time out", Collections.emptyList());
				}

			}

			final List<Position2D> result = new ArrayList<>();

			Position2D nextPosition = origin;

			while (nextPosition != null && !nextPosition.equals(destination)) {
				result.add(nextPosition);
				nextPosition = getLowestAdjacent(nextPosition);
			}

			if (nextPosition == null) {
				throw new UnfeasiblePathException("Cannot find a path", result);
			}

			result.add(nextPosition);

			return result;
		}

		private Position2DWithCount nextNotProcessedPosition() {
			Position2DWithCount result = null;
			double lowerDistance = Long.MAX_VALUE;
			for (final Position2DWithCount notProcessedPosition : notProcessedPositions) {
				final double distanceToOrigin = notProcessedPosition.distanceToOrigin();
				if (result == null || lowerDistance > distanceToOrigin) {
					result = notProcessedPosition;
					lowerDistance = distanceToOrigin;
				}
			}
			if (result != null) {
				notProcessedPositions.remove(result);
			}
			return result;
		}

		Position2D getLowestAdjacent(final Position2D nextPosition) {
			Position2D lowestPosition = null;
			int lowestCount = Integer.MAX_VALUE;
			for (final Position2D adjacent : nextPosition.getAdjacents()) {
				final Integer count = mainMap.get(adjacent);
				if (count != null) {
					if (lowestPosition == null || count.intValue() < lowestCount) {
						lowestPosition = adjacent;
						lowestCount = count;
					}
				}
			}
			return lowestPosition;
		}

		void doProcess(final Position2DWithCount position2DWithCount) {
			final List<Position2DWithCount> validAdjacents = createAdjacents(position2DWithCount);
			removePositionsByMap(validAdjacents);
			removePositions(validAdjacents);
			for (final Position2DWithCount a : validAdjacents) {
				mainMap.put(a.position2D, a.count);
				if (!processedPositions.contains(a)) {
					notProcessedPositions.add(a);
				}
			}
			listener.step(this);
		}

		private List<Position2DWithCount> createAdjacents(final Position2DWithCount position2DWithCount) {
			final int newCount = position2DWithCount.count + 1;
			return position2DWithCount.position2D.getAdjacents().stream()
					.map(p -> new Position2DWithCount(p, origin, newCount)).collect(Collectors.toList());
		}

		/**
		 * If the cell is a wall, remove it from the list
		 * 
		 * @param adjacents
		 */
		private void removePositionsByMap(final List<Position2DWithCount> adjacents) {
			final Iterator<Position2DWithCount> iterator = adjacents.iterator();
			while (iterator.hasNext()) {
				final Position2D p = iterator.next().position2D;
				if (!getSimulatorMap().isValid(p)) {
					iterator.remove();
				}
			}
		}

		/**
		 * If there is an element in the main list with the same coordinate and
		 * an equal or higher counter, remove it from the list
		 * 
		 */
		private void removePositions(final List<Position2DWithCount> positions2DWithCount) {

			final Iterator<Position2DWithCount> iterator = positions2DWithCount.iterator();
			while (iterator.hasNext()) {
				final Position2DWithCount p = iterator.next();
				final Integer count = mainMap.get(p.position2D);

				if (count != null) {
					if (count.intValue() > p.count) {
						mainMap.remove(p.position2D);
					} else {
						iterator.remove();
					}
				}

			}
		}

	}

	public PathFinderPositionCalculator(final SimulatorMap2D simulatorMap2D) {
		super(simulatorMap2D);
	}

	@Override
	List<Position2D> doPath(final Position2D origin, final Position2D destination) throws UnfeasiblePathException {
		try {
			return new PathFinder(StandardPathFinderListener.EMPTY, origin, destination).process();
		} catch (final RuntimeException ex) {
			throw new UnfeasiblePathException("Cannot calculate path", ex);
		}

	}

	// 1 2 3 4 5 6 7 8
	// X X X X X X X X X X
	// X _ _ _ X X _ X _ X 1
	// X _ X _ _ X _ _ _ X 2
	// X S X X _ _ _ X _ X 3
	// X _ X _ _ X _ _ _ X 4
	// X _ _ _ X X _ X _ X 5
	// X _ X _ _ X _ X _ X 6
	// X _ X X _ _ _ X _ X 7
	// X _ _ O _ X _ _ _ X 8
	// X X X X X X X X X X
	public static void main(final String[] args) throws UnfeasiblePathException {
		final List<Position2D> bloquedPositions = new ArrayList<>();

		for (int x = 0; x <= 9; x++) {
			// Primeira linha
			bloquedPositions.add(Position2D.getInstance(x, 0));
			// Última linha
			bloquedPositions.add(Position2D.getInstance(x, 9));
			// Primeira coluna
			bloquedPositions.add(Position2D.getInstance(0, x));
			// Última coluna
			bloquedPositions.add(Position2D.getInstance(9, x));
		}
		bloquedPositions.add(Position2D.getInstance(4, 1));
		bloquedPositions.add(Position2D.getInstance(5, 1));
		bloquedPositions.add(Position2D.getInstance(7, 1));
		bloquedPositions.add(Position2D.getInstance(2, 2));
		bloquedPositions.add(Position2D.getInstance(5, 2));
		bloquedPositions.add(Position2D.getInstance(2, 3));
		bloquedPositions.add(Position2D.getInstance(3, 3));
		bloquedPositions.add(Position2D.getInstance(7, 3));
		bloquedPositions.add(Position2D.getInstance(2, 4));
		bloquedPositions.add(Position2D.getInstance(5, 4));
		bloquedPositions.add(Position2D.getInstance(4, 5));
		bloquedPositions.add(Position2D.getInstance(5, 5));
		bloquedPositions.add(Position2D.getInstance(7, 5));
		bloquedPositions.add(Position2D.getInstance(2, 6));
		bloquedPositions.add(Position2D.getInstance(5, 6));
		bloquedPositions.add(Position2D.getInstance(7, 6));
		bloquedPositions.add(Position2D.getInstance(2, 7));
		bloquedPositions.add(Position2D.getInstance(3, 7));
		bloquedPositions.add(Position2D.getInstance(7, 7));
		bloquedPositions.add(Position2D.getInstance(5, 8));

		final SimulatorMap2D simulatorMap2D = new SimulatorMap2D(Position2D.ORIGIN, Position2D.getInstance(10, 10),
				bloquedPositions);

		final Position2D origin = Position2D.getInstance(1, 3);
		final Position2D destination = Position2D.getInstance(3, 8);

		final PathFinderPositionCalculator positionCalculator = new PathFinderPositionCalculator(simulatorMap2D);

		final PathFinder pathFinder = positionCalculator.new PathFinder(StandardPathFinderListener.EMPTY, origin,
				destination);
		System.out.println(pathFinder.process());
	}

}
