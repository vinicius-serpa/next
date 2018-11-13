package br.com.cab.engine.calculators;

import java.util.Collections;
import java.util.List;

import br.com.cab.model.Position;
import br.com.cab.model.SimulatorMap;

public interface PositionCalculator {

	/**
	 * Indica a impossibilidade de encontrar um caminho. Entre as raz�es dessa
	 * impossibilidade podemos citar:<br>
	 * <li>A inexist�ncia de um caminho poss�vel em fun��o das
	 * {@link SimulatorMap#getBloquedPositions()}
	 * <li>A incapacidade do algoritmo de busca encontrar um caminho poss�vel em
	 * um tempo aceit�vel.
	 */
	public static class UnfeasiblePathException extends Exception {

		private static final long serialVersionUID = -8862476740899139436L;

		private final List<? extends Position> partialPositions;

		public UnfeasiblePathException(final String message, final List<? extends Position> partialPositions) {
			super(message);
			this.partialPositions = Collections.unmodifiableList(partialPositions);
		}

		public UnfeasiblePathException(final String message, final Throwable throwable) {
			this(message, throwable, Collections.emptyList());
		}

		public UnfeasiblePathException(final String message, final Throwable throwable,
				final List<? extends Position> partialPositions) {
			super(message, throwable);
			this.partialPositions = Collections.unmodifiableList(partialPositions);
		}

		/**
		 * Indica o caminho parcial encontrado antes do lan�amento da exce��o.
		 * 
		 */
		public List<? extends Position> getPartialPositions() {
			return partialPositions;
		}

	}

	/**
	 * Retorna o {@link SimulatorMap} ao qual este {@link PositionCalculator}
	 * est� sujeito.
	 *
	 */
	SimulatorMap getSimulatorMap();

	/**
	 * Indica a sequ�ncia de {@link Position}s para sair da {@link Position} de
	 * origem e alcan�ar a {@link Position} de destino. O resultado deve conter
	 * como primeiro elemento origin e como �ltimo elemento destination.
	 * 
	 * @param origin
	 * @param destination
	 * 
	 * @throws UnfeasiblePathException
	 *             caso seja imposs�vel tra�ar um caminho entre as
	 *             {@link Position}s indicadas
	 */
	List<? extends Position> path(Position origin, Position destination) throws UnfeasiblePathException;

	/**
	 * Indica uma {@link Position} aleat�ria adjacente a {@link Position}
	 * especificada.
	 * 
	 * @param currentPosition
	 * @param lastPositions
	 *            Indica as �ltimas {@link Position}s percorridas. Sendo que os
	 *            primeiros elementos s�o as mais antigas.
	 */
	Position randomPosition(Position currentPosition, List<Position> lastPositions);

}
