package br.com.cab.engine.restriction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;

/**
 * Oferece utilitários relacionados a {@link Restriction}
 *
 */
public class Restrictions {

	private static final ThreadPoolExecutor executor;
	private static int threadCount;

	static {
		executor = new ThreadPoolExecutor(50, 100, 1, TimeUnit.HOURS, new ArrayBlockingQueue<>(50000),
				new ThreadFactory() {
					@Override
					public Thread newThread(final Runnable target) {
						final Thread result = new Thread(target, "Restrictions " + threadCount++);
						result.setDaemon(true);
						return result;
					}
				});
	}

	/**
	 * Essa classe não tem a finalidade de criar objetos.
	 */
	private Restrictions() {
		throw new AssertionError();
	}

	/**
	 * Define quais são os pares {@link Cab} e {@link Passenger}s possíveis,
	 * isto é, aqueles pares que não desrespeitam nenhuma {@link Restriction}
	 * imposta.
	 * 
	 * @param cabs
	 * @param passengers
	 * @param restriction
	 * @return
	 */
	public static Map<Cab, Set<Passenger>> getPossiblesMatches(final Set<Cab> cabs, final Set<Passenger> passengers,
			final Restriction restriction) {
		final Map<Cab, Set<Passenger>> result = new HashMap<>();
		final List<Future<?>> futures = new ArrayList<>();
		for (final Cab cab : cabs) {
			final Set<Passenger> feasiblePassengers = Collections.synchronizedSet(new HashSet<>());
			for (final Passenger passenger : passengers) {
				futures.add(executor.submit(new Runnable() {
					@Override
					public void run() {
						if (restriction.isFeasible(cab, passenger)) {
							feasiblePassengers.add(passenger);
						}
					}
				}));
			}
			result.put(cab, Collections.unmodifiableSet(feasiblePassengers));
		}
		futures.forEach(f -> {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});

		return Collections.unmodifiableMap(result);
	}

}
