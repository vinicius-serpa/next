package br.com.cab.to;

public class PassengerRequestTO {

	private String id;
	private int originX;
	private int originY;
	private int destinationX;
	private int destinationY;
	
	PassengerRequestTO (String id, int originX, int originY, int destinationX, int destinationY) {
		this.id = id;
		this.originX = originX;
		this.originY = originY;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
	}
	
}
