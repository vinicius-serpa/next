package br.com.cab.simulator.listener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.JPanel;

import br.com.cab.model.Cab;
import br.com.cab.model.Passenger;
import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;
import br.com.cab.simulator.SimulatorListener;

public class JPanelSimulatorListener extends JPanel implements SimulatorListener {

	private static final long serialVersionUID = -2602902438192375955L;

	private Set<Cab> cabs;
	private Set<Passenger> passengers;

	private final SimulatorMap2D simulatorMap2D;

	public JPanelSimulatorListener(final SimulatorMap2D simulatorMap2D) {
		this.simulatorMap2D = Objects.requireNonNull(simulatorMap2D);
	}

	@Override
	public void paint(final Graphics g) {
		final Graphics2D graphics2d = (Graphics2D) g;

		graphics2d.setColor(Color.WHITE);
		final Position2D limit = simulatorMap2D.getLimit();
		g.fillRect(0, 0, limit.getX() + 50, limit.getY() + 50);

		if (cabs != null) {
			for (final Cab cab : cabs) {
				switch (cab.getStatus()) {
				case EMPTY:
					graphics2d.setColor(Color.GRAY);
					break;
				case ON_THE_WAY:
					graphics2d.setColor(Color.DARK_GRAY);
					break;
				case BUSY:
					graphics2d.setColor(Color.BLACK);
				}
				final Position2D p = (Position2D) cab.getPosition();
				graphics2d.drawString(cab.getIdentifier(), p.getX(), p.getY());
			}
		}

		if (passengers != null) {
			for (final Passenger passenger : passengers) {
				switch (passenger.getStatus()) {
				case NO_CAB:
					graphics2d.setColor(Color.BLUE);
					break;
				case WAITING_CAB:
					graphics2d.setColor(Color.GREEN);
					break;
				case IN_CAB:
					graphics2d.setColor(Color.BLACK);
					break;
				case ARRIVE_DESTINATION:
					graphics2d.setColor(Color.RED);
				}
				final Position2D origin = (Position2D) passenger.getPosition();
				graphics2d.fillRect(origin.getX(), origin.getY(), 2, 2);

				final Position2D destination = (Position2D) passenger.getDestination();
				graphics2d.setColor(Color.LIGHT_GRAY);
				graphics2d.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY());
			}
		}

		graphics2d.setColor(Color.BLACK);
		simulatorMap2D.getBloquedPositions().stream().forEach(p -> graphics2d.fillRect(p.getX(), p.getY(), 1, 1));
	}

	@Override
	public void turnCompleted(final Set<Cab> cabs, final Set<Passenger> passengers) {
		this.cabs = new HashSet<>(cabs);
		this.passengers = new HashSet<>(passengers);
		repaint();
	}
}
