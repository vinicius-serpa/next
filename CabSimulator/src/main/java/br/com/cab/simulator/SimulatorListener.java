package br.com.cab.simulator;

import java.util.Set;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

public interface SimulatorListener {

	/**
	 * Invocado ap�s a conclus�o de um turno de simula��o.
	 * 
	 * @param cabs
	 * @param passengers
	 */
	void turnCompleted(Set<Cab> cabs, Set<Passenger> passengers);

}
