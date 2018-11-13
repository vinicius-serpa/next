package br.com.cab.engine.calculators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import br.com.cab.app.Application;
import br.com.cab.engine.calculators.PathFinderPositionCalculator.PathFinder;
import br.com.cab.engine.calculators.PathFinderPositionCalculator.PathFinderListener;
import br.com.cab.engine.calculators.PositionCalculator.UnfeasiblePathException;
import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;

public class JPanelPathFinderListener extends JPanel implements PathFinderListener {

	private static final long serialVersionUID = -2602902438192375955L;

	private PathFinder pathFinder;
	private Map<Position2D, Integer> mainMap;

	private final SimulatorMap2D simulatorMap2D;

	public JPanelPathFinderListener(final SimulatorMap2D simulatorMap2D) {
		this.simulatorMap2D = Objects.requireNonNull(simulatorMap2D);
	}

	@Override
	public void paint(final Graphics g) {
		if (pathFinder != null) {
			final Graphics2D graphics2d = (Graphics2D) g;

			graphics2d.setColor(Color.WHITE);
			final Position2D limit = simulatorMap2D.getLimit();
			g.fillRect(0, 0, limit.getX() + 50, limit.getY() + 50);

			for (final Entry<Position2D, Integer> entry : mainMap.entrySet()) {
				final int value = entry.getValue();
				final float valueColor = Math.min(value / 500.0f, 1.0f);
				final Color color = new Color(0.0f + valueColor, 0, 1.0f - valueColor);
				graphics2d.setColor(color);
				final Position2D position2D = entry.getKey();
				graphics2d.drawOval(position2D.getX(), position2D.getY(), 1, 1);
			}

			final Position2D origin = pathFinder.getOrigin();
			final Position2D destination = pathFinder.getDestination();
			graphics2d.setColor(Color.LIGHT_GRAY);
			graphics2d.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY());

			graphics2d.setColor(Color.BLACK);
			simulatorMap2D.getBloquedPositions().stream().forEach(p -> graphics2d.fillRect(p.getX(), p.getY(), 1, 1));
		}

	}

	@Override
	public void step(final PathFinder pathFinder) {
		this.pathFinder = pathFinder;
		this.mainMap = new HashMap<>(pathFinder.getMainMap());
		repaint();
	}

	public static void main(final String[] args) throws InterruptedException {
		final int MAX_X = 300;
		final int MAX_Y = 300;

		final Random random = new Random();

		final SimulatorMap2D simulatorMap2D = new SimulatorMap2D(MAX_X, MAX_Y,
				Application.randomBlockedPositions(random));
		final JPanelPathFinderListener listener = new JPanelPathFinderListener(simulatorMap2D);

		final JFrame frame = new JFrame("Path Finder");
		frame.add(listener);
		frame.setSize(MAX_X*2, MAX_Y*2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		while (true) {

			final Position2D origin = Position2D.randomPosition(simulatorMap2D, random, MAX_X, MAX_Y);
			final Position2D destination = Position2D.randomPosition(simulatorMap2D, random, MAX_X, MAX_Y);

			try {
				final PathFinder pathFinder = new PathFinderPositionCalculator(simulatorMap2D).new PathFinder(listener,
						origin, destination);
				pathFinder.process();
			} catch (final UnfeasiblePathException e) {
				e.printStackTrace();
			}

			Thread.sleep(3000);
		}

	}
}
