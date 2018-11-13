package br.com.cab.model;

import java.util.Collection;

/**
 * Representa um mapa e suas restri��es.
 *
 */
public interface SimulatorMap {

	boolean containsPosition(Position position);

	Collection<? extends Position> getBloquedPositions();

	/**
	 * Determina se a {@link Position} especificada � v�lida, isto �, est�
	 * contida no {@link SimulatorMap} e n�o est� bloqueada.
	 * 
	 * @param position
	 * @return
	 */
	boolean isValid(Position position);

}
