package br.com.cab.engine.calculators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.cab.model.Position;
import br.com.cab.model.SimulatorMap;

public final class ChainedPositionCalculator implements PositionCalculator {

	private final List<PositionCalculator> positionCalculators;
	private final SimulatorMap simulatorMap;

	public ChainedPositionCalculator(final PositionCalculator... positionCalculators) {
		if (positionCalculators == null || positionCalculators.length == 0) {
			throw new IllegalArgumentException("PositionCalculators cannot be null or empty.");
		}

		this.positionCalculators = new ArrayList<>(Arrays.asList(positionCalculators));
		SimulatorMap simulatorMapLocal = null;
		for (final PositionCalculator positionCalculator : positionCalculators) {
			if (simulatorMapLocal == null) {
				simulatorMapLocal = positionCalculator.getSimulatorMap();
			} else if (simulatorMapLocal != positionCalculator.getSimulatorMap()) {
				throw new IllegalArgumentException("SimulatorMap must be the same for all positionCalculators.");
			}
		}

		this.simulatorMap = simulatorMapLocal;
	}

	@Override
	public SimulatorMap getSimulatorMap() {
		return simulatorMap;
	}

	@Override
	public List<? extends Position> path(final Position origin, final Position destination)
			throws UnfeasiblePathException {
		UnfeasiblePathException lastException = null;
		for (final PositionCalculator positionCalculator : positionCalculators) {
			try {
				return positionCalculator.path(origin, destination);
			} catch (final UnfeasiblePathException ex) {
				lastException = ex;
			}

		}
		assert lastException != null;
		throw lastException;
	}

	@Override
	public Position randomPosition(final Position currentPosition, final List<Position> lastPositions) {
		return positionCalculators.get(0).randomPosition(currentPosition, lastPositions);
	}

}
