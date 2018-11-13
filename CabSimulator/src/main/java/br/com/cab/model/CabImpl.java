package br.com.cab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.cab.engine.calculators.PositionCalculator;
import br.com.cab.engine.calculators.PositionCalculator.UnfeasiblePathException;

public final class CabImpl extends AbstractDiscoverable implements Cab {

	/**
	 * Indica o número máximo de {@link Position}s a serem armazenados
	 */
	private static final int LAST_POSITIONS_MAX_SIZE = 10;

	private static int count;

	private List<? extends Position> path;
	private int pathIndex;

	/**
	 * Armazena as últimas posições
	 */
	private final List<Position> lastPositions = new ArrayList<>(LAST_POSITIONS_MAX_SIZE);

	private final String identifier;

	private Status status = Status.EMPTY;

	private Passenger passenger;

	public CabImpl(final PositionCalculator positionCalculator, final Position initialPosition) {
		super(positionCalculator, initialPosition);
		identifier = Integer.toString(count++);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setPosition(final Position position) {
		super.setPosition(position);
		if (status == Status.BUSY) {
			passenger.setPosition(position);
		}

		if (lastPositions != null) {
			if (lastPositions.size() == LAST_POSITIONS_MAX_SIZE) {
				// Remove a Position mais antiga
				lastPositions.remove(0);
			}
			lastPositions.add(position);
		}
	}

	@Override
	public synchronized Status getStatus() {
		return status;
	}

	@Override
	public synchronized Passenger getPassenger() {
		return passenger;
	}

	private void changeStatus(final Status newStatus) {
		Status.validateTransition(status, newStatus);
		this.status = newStatus;
	}

	@Override
	public synchronized void accept(final Passenger passenger) throws UnfeasibleOperationException {
		if (!status.acceptPassenger()) {
			throw new UnfeasibleOperationException("In " + status + " cannot accept passengers.");
		}

		if (this.passenger != null) {
			throw new IllegalStateException("Cannot accept two passengers.");
		}

		if (path != null) {
			throw new IllegalStateException("Path must be null.");
		}

		try {
			path = positionCalculator.path(getPosition(), passenger.getPosition());
			pathIndex = 0;
		} catch (final UnfeasiblePathException ex) {
			throw new UnfeasibleOperationException("Cannot calculate path to passenger", ex);
		}

		passenger.changeStatus(Passenger.Status.WAITING_CAB);
		changeStatus(Status.ON_THE_WAY);

		this.passenger = passenger;
	}

	@Override
	public synchronized void findPassenger() throws UnfeasibleOperationException {
		if (passenger == null) {
			throw new UnfeasibleOperationException("Unknown passenger");
		}

		if (!getPosition().equals(passenger.getPosition())) {
			throw new UnfeasibleOperationException("Incompatible positions");
		}

		try {
			path = passenger.pathToDestination();
			pathIndex = 0;
		} catch (final UnfeasiblePathException ex) {
			throw new UnfeasibleOperationException("Cannot calculate path to destination", ex);
		}

		passenger.changeStatus(Passenger.Status.IN_CAB);
		changeStatus(Status.BUSY);
	}

	@Override
	public synchronized void reachesDestination() throws UnfeasibleOperationException {
		if (passenger == null) {
			throw new UnfeasibleOperationException("Unknown passenger");
		}
		if (!getPosition().equals(passenger.getDestination())) {
			throw new UnfeasibleOperationException("Current position is not destination position.");
		}

		passenger.changeStatus(Passenger.Status.ARRIVE_DESTINATION);
		changeStatus(Status.EMPTY);

		path = null;
		pathIndex = 0;
		passenger = null;
	}

	@Override
	public synchronized void updatePosition() {
		switch (status) {
		case EMPTY:
			setPosition(positionCalculator.randomPosition(getPosition(), Collections.unmodifiableList(lastPositions)));
			break;
		case ON_THE_WAY:
		case BUSY:
			setPosition(path.get(pathIndex++));
			break;
		default:
			throw new IllegalStateException("Cannot recognize status: " + status);
		}

	}
}
