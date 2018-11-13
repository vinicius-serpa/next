package br.com.cab.model;

/**
 * Representa entidades que possuem {@link Position}s.
 */
public interface Discoverable {

	/**
	 * Indica a {@link Position} corrente da entidade. Nunca ser� nulo.
	 * 
	 */
	Position getPosition();

	/**
	 * Modifica a {@link Position} corrente da entidade.
	 * 
	 * @param position
	 *            Nova {@link Position} da entidade.
	 */
	void setPosition(Position position);
	

}
