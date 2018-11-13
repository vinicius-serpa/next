package br.com.cab.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.com.cab.utils.Pair;

/**
 * 
 * Representa um taxista
 *
 */
public interface Cab extends Discoverable {

	public String getIdentifier();

	/**
	 * Representa os estados possíveis de um {@link Cab}
	 *
	 */
	public static enum Status {

		/**
		 * O {@link Cab} está vazio. Estado inicial da máquina de estado.
		 */
		EMPTY(true, true),

		/**
		 * O {@link Cab} está a caminho de um {@link Passenger}
		 */
		ON_THE_WAY(false, false),

		/**
		 * O {@link Cab} está ocupado transportando o {@link Passenger} a seu
		 * destino
		 */
		BUSY(false, false);

		private static final Set<Pair<Status, Status>> validTransitions;

		/**
		 * Inicializa as transições válidas da máquina de estado de Status.
		 */
		static {
			final Set<Pair<Status, Status>> validTransitionsTemp = new HashSet<>();
			validTransitionsTemp.add(Pair.newPair(EMPTY, ON_THE_WAY));
			validTransitionsTemp.add(Pair.newPair(ON_THE_WAY, BUSY));
			validTransitionsTemp.add(Pair.newPair(BUSY, EMPTY));

			validTransitions = Collections.unmodifiableSet(validTransitionsTemp);
		}

		public static void validateTransition(final Status currentStatus, final Status nextStatus) {
			final Pair<Status, Status> transition = Pair.newPair(currentStatus, nextStatus);
			if (!validTransitions.contains(transition)) {
				throw new IllegalArgumentException("Invalid Transition: " + currentStatus + " to " + nextStatus);
			}
		}

		private final boolean initialState;
		private final boolean acceptPassenger;

		Status(final boolean initialState, final boolean acceptPassenger) {
			this.initialState = initialState;
			this.acceptPassenger = acceptPassenger;
		}

		public boolean isInitialState() {
			return initialState;
		}

		public boolean acceptPassenger() {
			return acceptPassenger;
		}

	}

	/**
	 * Retorna o {@link Status} corrente do {@link Cab}. Nunca será nulo.
	 * 
	 */
	Status getStatus();

	/**
	 * Retorna o {@link Passenger} que solicitou este {@link Cab}, ou o
	 * {@link Passenger} atualmente em transporte. Nulo indica que não a
	 * {@link Passenger}s vinculados a este {@link Cab}
	 */
	Passenger getPassenger();

	/**
	 * Exceção utilizada para indicar a infactibilidade de uma operação.
	 * 
	 */
	public static class UnfeasibleOperationException extends Exception {

		private static final long serialVersionUID = -794175677552184441L;

		public UnfeasibleOperationException(final String message) {
			super(message);
		}

		public UnfeasibleOperationException(final String message, final Throwable throwable) {
			super(message, throwable);
		}
	}

	/**
	 * Invocado quando este {@link Cab} aceita um {@link Passenger}
	 * 
	 * @param passenger
	 * 
	 * @throws UnfeasibleOperationException
	 *             caso o motorista seja incapaz de aceitar o {@link Passenger}
	 *             especificado
	 */
	void accept(Passenger passenger) throws UnfeasibleOperationException;

	/**
	 * Invocado quando este {@link Cab} chega ao encontro do {@link Passenger}.
	 * 
	 * @throws UnfeasibleOperationException
	 *             Caso não exista um {@link Passenger} vinculado a este
	 *             {@link Cab}. Ou caso a {@link Position} do {@link Cab} seja
	 *             diferente da {@link Position} do {@link Passenger}
	 */
	void findPassenger() throws UnfeasibleOperationException;

	/**
	 * Invocado quando este {@link Cab} chega ao destino do {@link Passenger}.
	 * 
	 * @throws UnfeasibleOperationException
	 *             Caso não exista um {@link Passenger} vinculado a este
	 *             {@link Cab}. OU caso a {@link Position} do {@link Cab} seja
	 *             diferente de {@link Passenger#getDestination()}
	 */
	void reachesDestination() throws UnfeasibleOperationException;

	/**
	 * Atualiza a {@link Position} corrente deste {@link Cab}. Potencialmente
	 * também atualiza a {@link Position} de um {@link Passenger}.
	 */
	void updatePosition();

}
