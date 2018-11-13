package br.com.cab.model;

/**
 * Representa uma posi��o espacial. Implementa��es devem garantir a corretude de
 * {@link Object#equals(Object)} e {@link Object#hashCode()}
 *
 */
public interface Position {

	double distanceTo(Position position);

}
