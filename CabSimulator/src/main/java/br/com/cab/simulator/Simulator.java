package br.com.cab.simulator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import br.com.cab.engine.matcher.Matcher;
import br.com.cab.engine.matcher.Matcher.MatcherResult;
import br.com.cab.model.Cab;
import br.com.cab.model.Cab.UnfeasibleOperationException;
import br.com.cab.model.Passenger;
import br.com.cab.utils.Cabs;
import br.com.cab.utils.Pair;
import br.com.cab.utils.Passengers;

public class Simulator {

	/**
	 * Tempo entre a execução entre turnos.
	 */
	private static long TIME_BETWEEN_TURN = 10;

	/**
	 * Número de ciclos para remover {@link Passenger}s no estado final
	 */
	private static long CYCLES_TO_REMOVE_PASSENGERS = 5000;

	private final Object lock = new Object();

	/**
	 * Tarefa responsável por realizar o casamento entre {@link Cab}s e
	 * {@link Passenger}s
	 * 
	 */
	private class MatcherTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				final MatcherResult matcherResult;
				final Set<Cab> emptyCabs;
				final Set<Passenger> noCabPassengers;
				synchronized (lock) {
					emptyCabs = Cabs.filter(cabs, Cab.Status.EMPTY);
					noCabPassengers = Passengers.filter(passengers, Passenger.Status.NO_CAB);
				}
				matcherResult = matcher.match(emptyCabs, noCabPassengers);
				for (final Pair<Cab, Passenger> match : matcherResult.getMatches()) {
					try {
						match.getFirst().accept(match.getSecond());
					} catch (final UnfeasibleOperationException e) {
						e.printStackTrace(System.out);
					}
				}
				randomMove();
				try {
					Thread.sleep(TIME_BETWEEN_TURN / 3);
				} catch (final InterruptedException e) {
					e.printStackTrace(System.out);
				}
			}
		}

		/**
		 * Move randomicamente {@link Cab}s
		 */
		private void randomMove() {
			for (final Cab emptyCab : Cabs.filter(cabs, Cab.Status.EMPTY)) {
				emptyCab.updatePosition();
			}
		}

	}

	/**
	 * Tarefa responsável pela movimentação dos {@link Cab}s
	 * 
	 */
	private class MoveTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				processNextTurn();
				listener.turnCompleted(Collections.unmodifiableSet(cabs), Collections.unmodifiableSet(passengers));
				try {
					Thread.sleep(TIME_BETWEEN_TURN);
				} catch (final InterruptedException e) {
					e.printStackTrace(System.out);
				}
			}
		}

		/**
		 * Invocado para processar o próximo turno.
		 */
		private void processNextTurn() {
			passengerMove();
			destinationMove();
			cycles++;
			if (cycles == CYCLES_TO_REMOVE_PASSENGERS) {
				cycles = 0;
				removeFinalStatePassengers();
			}
		}

		/**
		 * Move {@link Cab}s em direção ao {@link Passenger#getPosition()}
		 */
		private void passengerMove() {
			final Set<Cab> onTheWayCabs;
			synchronized (lock) {
				onTheWayCabs = Cabs.filter(cabs, Cab.Status.ON_THE_WAY);
			}

			for (final Cab onTheWayCab : onTheWayCabs) {
				onTheWayCab.updatePosition();
				// Determina se atingiu o alvo
				if (onTheWayCab.getPosition().equals(onTheWayCab.getPassenger().getPosition())) {
					try {
						onTheWayCab.findPassenger();
					} catch (final UnfeasibleOperationException e) {
						e.printStackTrace(System.out);
					}
				}
			}
		}

		/**
		 * Move {@link Cab}s para {@link Passenger#getDestination()}
		 */
		private void destinationMove() {
			final Set<Cab> busyCabs;
			synchronized (lock) {
				busyCabs = Cabs.filter(cabs, Cab.Status.BUSY);
			}

			for (final Cab busyCab : busyCabs) {
				busyCab.updatePosition();
				// Determina se atingiu o alvo
				if (busyCab.getPosition().equals(busyCab.getPassenger().getDestination())) {
					try {
						busyCab.reachesDestination();
					} catch (final UnfeasibleOperationException e) {
						e.printStackTrace(System.out);
					}
				}
			}
		}

		/**
		 * Remove {@link Passenger}s que estão no estado final.
		 */
		private void removeFinalStatePassengers() {
			synchronized (lock) {
				final Iterator<Passenger> iterator = passengers.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getStatus() == Passenger.Status.ARRIVE_DESTINATION) {
						iterator.remove();
					}
				}
			}
		}

	}

	private final Matcher matcher;

	private final SimulatorListener listener;

	private final Set<Cab> cabs = new HashSet<>();
	private final Set<Passenger> passengers = new HashSet<>();

	private long cycles;

	public Simulator(final Matcher matcher, final SimulatorListener listener) {
		this.matcher = Objects.requireNonNull(matcher);
		this.listener = Objects.requireNonNull(listener);

		// Thread matcher
		final Thread threadMatcher = new Thread(new MatcherTask(), "Matcher");
		threadMatcher.setDaemon(true);
		threadMatcher.start();

		// Thread move
		final Thread threadMove = new Thread(new MoveTask(), "Move");
		threadMove.setDaemon(true);
		threadMove.start();
	}

	/**
	 * Adiciona um novo {@link Cab} a simulação. Apenas {@link Cab}s no estado
	 * inicial são permitidos.
	 * 
	 * @param cab
	 */
	public void add(final Cab cab) {
		Objects.requireNonNull(cab);
		if (!cab.getStatus().isInitialState()) {
			throw new IllegalArgumentException("Only cab in initial state are alloweds.");
		}
		System.out.println("Cab added: " + cab);
		synchronized (lock) {
			cabs.add(cab);
		}
	}

	/**
	 * Adiciona um novo {@link Passenger} a simulação. Apenas {@link Passenger}s
	 * no estado inicial são permitidos.
	 * 
	 * @param cab
	 */
	public void add(final Passenger passenger) {
		Objects.requireNonNull(passenger);
		if (!passenger.getStatus().isInitialState()) {
			throw new IllegalArgumentException("Only cab in initial state are alloweds.");
		}
		System.out.println("Passenger added: " + passenger);
		synchronized (lock) {
			passengers.add(passenger);
		}
	}

}
