package br.com.cab.model;

import java.util.Collection;

/**
 * Representa um mapa e suas restrições.
 *
 */
public interface SimulatorMap {

	boolean containsPosition(Position position);

	Collection<? extends Position> getBloquedPositions();

	/**
	 * Determina se a {@link Position} especificada é válida, isto é, está
	 * contida no {@link SimulatorMap} e não está bloqueada.
	 * 
	 * @param position
	 * @return
	 */
	boolean isValid(Position position);

}
