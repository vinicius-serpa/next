package br.com.cab.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.com.cab.engine.calculators.ChainedPositionCalculator;
import br.com.cab.engine.calculators.PathFinderPositionCalculator;
import br.com.cab.engine.calculators.PositionCalculator;
import br.com.cab.engine.calculators.PositionCalculator2DImpl;
import br.com.cab.engine.matcher.Matcher;
import br.com.cab.engine.matcher.MatcherImpl;
import br.com.cab.engine.restriction.ChainedRestriction;
import br.com.cab.engine.restriction.PathRestriction;
import br.com.cab.engine.restriction.Restriction;
import br.com.cab.engine.restriction.Restriction.StandardRestriction;
import br.com.cab.engine.selector.Selector;
import br.com.cab.engine.selector.Selector.StandardSelector;
import br.com.cab.model.Cab;
import br.com.cab.model.CabImpl;
import br.com.cab.model.Passenger;
import br.com.cab.model.PassengerImpl;
import br.com.cab.model.Position2D;
import br.com.cab.model.SimulatorMap2D;
import br.com.cab.service.RouterService;
import br.com.cab.simulator.Simulator;
import br.com.cab.simulator.listener.JPanelSimulatorListener;
import br.com.cab.to.PassengerRequestTO;
import br.com.cab.utils.MapProperties;

public class Application extends JFrame {

	private static final long serialVersionUID = -5680964038598774462L;

	private static int MAX_X = 0;
	private static int MAX_Y = 0;

	private static final int NUMBER_OF_CABS = 10;
	private static final int INTERVAL_BETWEEN_PASSENGER_ADD = 1000;
	private static final int NUMBER_OF_BLOCKED_HORIZONTAL_LINES = 3;
	private static final int NUMBER_OF_BLOCKED_VERTICAL_LINES = 3;
	private static final int MAX_BLOCKED_SIZE_LINE = 500;

	public Application(final JPanelSimulatorListener listener) {
		super("Simulator");			
		setSize(MAX_X + 50, MAX_Y + 50);
		add(listener);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);				
	}

	public static void main(final String[] args) throws InterruptedException {

		MapProperties map = new MapProperties();
		
		MAX_X = map.getMaxX();
		MAX_Y = map.getMaxY();		
		
		final Random random = new Random(100);

		final SimulatorMap2D simulatorMap2D = new SimulatorMap2D(Position2D.ORIGIN,
				Position2D.getInstance(MAX_X, MAX_Y), randomBlockedPositions(random));

		final JPanelSimulatorListener listener = new JPanelSimulatorListener(simulatorMap2D);

		final PositionCalculator positionCalculator;
		{
			final PositionCalculator positionCalculator2D = new PositionCalculator2DImpl(simulatorMap2D);
			positionCalculator = new ChainedPositionCalculator(positionCalculator2D,
					new PathFinderPositionCalculator(simulatorMap2D));
		}

		final Restriction restriction = new ChainedRestriction(Arrays.asList(StandardRestriction.CAB_STATUS,
				StandardRestriction.PASSENGER_STATUS, new PathRestriction(positionCalculator)));

		final Selector selector = StandardSelector.MINIMIZE_DISTANCE;

		final Matcher matcher = new MatcherImpl(restriction, selector);

		final Simulator simulator = new Simulator(matcher, listener);

		new Application(listener);

		for (int i = 0; i < NUMBER_OF_CABS; i++) {
			final Cab cab = new CabImpl(positionCalculator,
					Position2D.randomPosition(simulatorMap2D, random, MAX_X, MAX_Y));
			simulator.add(cab);
		}

		// boolean createMyPassenger = true;
		// int count = 0;
		
		// RabbitMQ Listener
		rabbitConsumer(positionCalculator, simulator);
		
		while (true) {
			
			Thread.sleep(INTERVAL_BETWEEN_PASSENGER_ADD);
			
			/*  			
			// block to fill map with random passengers
			final Position2D origin = Position2D.randomPosition(simulatorMap2D, random, MAX_X, MAX_Y);
			final Position2D destination = randomIncrement(simulatorMap2D, random, origin);
			final Passenger passenger = new PassengerImpl(positionCalculator, origin, destination);
			simulator.add(passenger);
			
			// block to test passenger request using RouterService
			count++;			
			if (createMyPassenger && count == 9) {												
				RouterService routerService = new RouterService(simulator, positionCalculator);				
				routerService.passengerRequest(new PassengerRequestTO("ROBOT01", 120, 100, 220, 200));						
				createMyPassenger = false;
			}
			*/			
		}
	}

	private static void rabbitConsumer(final PositionCalculator positionCalculator, final Simulator simulator) {
		
		String QUEUE_NAME = "routing";
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					PassengerRequestTO message = PassengerRequestTO.fromBytes(body);
					System.out.println(" [x] Received '" + message.getId() + "'");
					
					RouterService routerService = new RouterService(simulator, positionCalculator);				
					routerService.passengerRequest(message);							
				}
			};

			channel.basicConsume(QUEUE_NAME, true, consumer);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private static Position2D randomIncrement(final SimulatorMap2D simulatorMap2D, final Random random,
			final Position2D position2D) {
		Position2D result = null;
		do {
			result = position2D.incrementX(random.nextInt(200) - 100).incrementY(random.nextInt(200) - 100);
		} while (simulatorMap2D != null && !simulatorMap2D.isValid(result));
		return result;
	}

	public static final List<Position2D> randomBlockedPositions(final Random random) {
		final List<Position2D> result = new ArrayList<>();

		for (int i = 0; i < NUMBER_OF_BLOCKED_HORIZONTAL_LINES; i++) {
			result.addAll(Position2D.createLine(Position2D.randomPosition(null, random, MAX_X, MAX_Y),
					random.nextInt(MAX_BLOCKED_SIZE_LINE), true));
		}

		for (int i = 0; i < NUMBER_OF_BLOCKED_VERTICAL_LINES; i++) {
			result.addAll(Position2D.createLine(Position2D.randomPosition(null, random, MAX_X, MAX_Y),
					random.nextInt(MAX_BLOCKED_SIZE_LINE), false));
		}

		return result;
	}

}
