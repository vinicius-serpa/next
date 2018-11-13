package br.com.cab.engine.calculators;

import java.util.ArrayList;
import java.util.List;

import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;

public final class PositionCalculator2DImpl extends AbstractPositionCalculator2D {

	public PositionCalculator2DImpl(final SimulatorMap2D simulatorMap2D) {
		super(simulatorMap2D);
	}

	@Override
	List<Position2D> doPath(final Position2D origin, final Position2D destination) throws UnfeasiblePathException {
		final List<Position2D> result = new ArrayList<>();
		result.add(origin);
		Position2D current2D = origin;
		while (!current2D.equals(destination)) {
			current2D = nextPosition(current2D, destination);
			if (current2D == null) {
				throw new UnfeasiblePathException("Cannot calculate path.", result);
			}
			result.add(current2D);
		}
		result.add(destination);
		return result;
	}

	private Position2D nextPosition(final Position2D currentPosition, final Position2D destinationPosition)
			throws UnfeasiblePathException {
		final int deltaX = Math.abs(currentPosition.getX() - destinationPosition.getX());
		final int deltaY = Math.abs(currentPosition.getY() - destinationPosition.getY());

		final Position2D resultX;
		final boolean resultXIsValid;
		if (deltaX == 0) {
			resultX = null;
			resultXIsValid = false;
		} else {
			resultX = Position2D.getInstance(increment(currentPosition.getX(), destinationPosition.getX()),
					currentPosition.getY());
			resultXIsValid = getSimulatorMap().isValid(resultX);
		}

		final Position2D resultY;
		final boolean resultYIsValid;
		if (deltaY == 0) {
			resultY = null;
			resultYIsValid = false;
		} else {
			resultY = Position2D.getInstance(currentPosition.getX(),
					increment(currentPosition.getY(), destinationPosition.getY()));
			resultYIsValid = getSimulatorMap().isValid(resultY);
		}

		if (resultXIsValid && resultYIsValid) {
			if (deltaX >= deltaY) {
				return resultX;
			}
			return resultY;
		}

		if (resultXIsValid) {
			return resultX;
		}

		if (resultYIsValid) {
			return resultY;
		}

		return null;
	}

	private int increment(final int current, final int destination) {
		if (current == destination) {
			return current;
		}

		if (current > destination) {
			return current - 1;
		}

		return current + 1;
	}

}
