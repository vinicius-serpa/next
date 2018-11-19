package br.com.cab.app;

import com.rabbitmq.client.ConnectionFactory;

import br.com.cab.to.PassengerRequestTO;

import com.rabbitmq.client.Connection;

import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;

public class Sender {

	private final static String QUEUE_NAME = "routing";
	
	public static void main(String[] args) throws java.io.IOException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		
		PassengerRequestTO message = new PassengerRequestTO("ROBOT01", 80, 20, 280, 200);
		
		
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		System.out.println(" [x] Sent '" + PassengerRequestTO.fromBytes(message.getBytes()).getId() + "'");
		
		channel.close();
		connection.close();

	}

}
