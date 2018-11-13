package br.com.cab.engine.calculators;

import java.util.Collections;
import java.util.List;

import br.com.cab.model.Position;
import br.com.cab.model.SimulatorMap;

public interface PositionCalculator {

	/**
	 * Indica a impossibilidade de encontrar um caminho. Entre as razões dessa
	 * impossibilidade podemos citar:<br>
	 * <li>A inexistência de um caminho possível em função das
	 * {@link SimulatorMap#getBloquedPositions()}
	 * <li>A incapacidade do algoritmo de busca encontrar um caminho possível em
	 * um tempo aceitável.
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
		 * Indica o caminho parcial encontrado antes do lançamento da exceção.
		 * 
		 */
		public List<? extends Position> getPartialPositions() {
			return partialPositions;
		}

	}

	/**
	 * Retorna o {@link SimulatorMap} ao qual este {@link PositionCalculator}
	 * está sujeito.
	 *
	 */
	SimulatorMap getSimulatorMap();

	/**
	 * Indica a sequência de {@link Position}s para sair da {@link Position} de
	 * origem e alcançar a {@link Position} de destino. O resultado deve conter
	 * como primeiro elemento origin e como último elemento destination.
	 * 
	 * @param origin
	 * @param destination
	 * 
	 * @throws UnfeasiblePathException
	 *             caso seja impossível traçar um caminho entre as
	 *             {@link Position}s indicadas
	 */
	List<? extends Position> path(Position origin, Position destination) throws UnfeasiblePathException;

	/**
	 * Indica uma {@link Position} aleatória adjacente a {@link Position}
	 * especificada.
	 * 
	 * @param currentPosition
	 * @param lastPositions
	 *            Indica as últimas {@link Position}s percorridas. Sendo que os
	 *            primeiros elementos são as mais antigas.
	 */
	Position randomPosition(Position currentPosition, List<Position> lastPositions);

}
