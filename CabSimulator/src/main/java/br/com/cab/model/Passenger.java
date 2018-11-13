package br.com.cab.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.cab.engine.calculators.PositionCalculator.UnfeasiblePathException;
import br.com.cab.utils.Pair;

/**
 * 
 * Representa um passageiro
 *
 */
public interface Passenger extends Discoverable {

	public String getIdentifier();

	/**
	 * Indica a {@link Position} de destino deste {@link Passenger}. Nunca será
	 * nulo.
	 * 
	 */
	Position getDestination();

	/**
	 * Indica o caminho entre {@link Passenger#getPosition()} e
	 * {@link Passenger#getDestination()}
	 * 
	 * @throws UnfeasiblePathException
	 */
	List<? extends Position> pathToDestination() throws UnfeasiblePathException;

	/**
	 * Representa os estados possíveis de um {@link Passenger}
	 *
	 */
	public static enum Status {

		/**
		 * Nenhum {@link Cab} está a caminho deste {@link Passenger}. Estado
		 * inicial da máquina de estado.
		 */
		NO_CAB(true),

		/**
		 * Um {@link Cab} já está a caminho deste {@link Passenger}.
		 */
		WAITING_CAB(false),

		/**
		 * Este {@link Passenger} está dentro de um {@link Cab} a caminho de seu
		 * destino.
		 */
		IN_CAB(false),

		/**
		 * Este {@link Passenger} chegou a seu destino. Estado final da máquina
		 * de estado.
		 */
		ARRIVE_DESTINATION(false);

		private static final Set<Pair<Status, Status>> validTransitions;

		/**
		 * Inicializa as transições válidas da máquina de estado de Status.
		 */
		static {
			final Set<Pair<Status, Status>> validTransitionsTemp = new HashSet<>();
			validTransitionsTemp.add(Pair.newPair(NO_CAB, WAITING_CAB));
			validTransitionsTemp.add(Pair.newPair(WAITING_CAB, IN_CAB));
			validTransitionsTemp.add(Pair.newPair(IN_CAB, ARRIVE_DESTINATION));

			validTransitions = Collections.unmodifiableSet(validTransitionsTemp);
		}

		public static void validateTransition(final Status currentStatus, final Status nextStatus) {
			final Pair<Status, Status> transition = Pair.newPair(currentStatus, nextStatus);
			if (!validTransitions.contains(transition)) {
				throw new IllegalArgumentException("Invalid Transition: " + currentStatus + " to " + nextStatus);
			}
		}

		private final boolean initialState;

		Status(final boolean initialState) {
			this.initialState = initialState;
		}

		public boolean isInitialState() {
			return initialState;
		}
	}

	/**
	 * Retorna o {@link Status} corrente do {@link Passenger}. Nunca será nulo.
	 */
	Status getStatus();

	/**
	 * Modifica o {@link Status} deste {@link Passenger}.
	 * 
	 * @param newStatus
	 * 
	 * @throws new
	 *             {@link IllegalArgumentException} caso não seja uma transição
	 *             válida.
	 */
	void changeStatus(Status newStatus);

}
