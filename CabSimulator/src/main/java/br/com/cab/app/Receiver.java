package br.com.cab.app;

import com.rabbitmq.client.*;

import br.com.cab.to.PassengerRequestTO;

import java.io.IOException;

public class Receiver {

	private final static String QUEUE_NAME = "routing";

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				PassengerRequestTO message = PassengerRequestTO.fromBytes(body);
				System.out.println(" [x] Received '" + message.getId() + "'");
			}
		};

		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

}