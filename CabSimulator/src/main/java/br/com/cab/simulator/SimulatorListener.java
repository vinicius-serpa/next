package br.com.cab.simulator;

import java.util.Set;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

public interface SimulatorListener {

	/**
	 * Invocado após a conclusão de um turno de simulação.
	 * 
	 * @param cabs
	 * @param passengers
	 */
	void turnCompleted(Set<Cab> cabs, Set<Passenger> passengers);

}
