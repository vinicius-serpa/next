package br.com.cab.model;

import java.util.Objects;

import br.com.cab.engine.calculators.PositionCalculator;

abstract class AbstractDiscoverable implements Discoverable {

	protected final PositionCalculator positionCalculator;

	private Position position;

	public AbstractDiscoverable(final PositionCalculator positionCalculator, final Position position) {
		this.positionCalculator = Objects.requireNonNull(positionCalculator);
		setPosition(position);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void setPosition(final Position position) {
		this.position = Objects.requireNonNull(position);
	}

	@Override
	public String toString() {
		return "position: " + position;
	}

}
