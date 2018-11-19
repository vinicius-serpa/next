package br.com.cab.to;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PassengerRequestTO implements Serializable {

	private static final long serialVersionUID = -7501781515031980151L;
	
	private String resourceName = "map.properties";
	private int maxX = 300;
	private int maxY = 300;
	
	private String id;
	private int originX;
	private int originY;
	private int destinationX;
	private int destinationY;

	public PassengerRequestTO(String id, int originX, int originY, int destinationX, int destinationY) throws Exception {
		this.id = id;
		
		if (originX < 0 || originX > maxX)
			throw new Exception("wrong X origin");
		
		if (originY < 0 || originY > maxY)
			throw new Exception("wrong Y origin");
		
		if (destinationX < 0 || destinationX > maxX)
			throw new Exception("wrong X destination");
		
		if (destinationY < 0 || destinationY > maxY)
			throw new Exception("wrong Y destination");
		
		this.originX = originX;
		this.originY = originY;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
						
		Properties props = new Properties();
		try(InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
		    props.load(resourceStream);
		    
		    this.maxX = Integer.parseInt(props.getProperty("max_x"));
		    this.maxY = Integer.parseInt(props.getProperty("max_y"));
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	public String getId() {
		return id;
	}
	
	public int getOriginX() {
		return originX;
	}

	public int getOriginY() {
		return originY;
	}

	public int getDestinationX() {
		return destinationX;
	}

	public int getDestinationY() {
		return destinationY;
	}
	
	public byte[] getBytes() {
		byte[] bytes;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.flush();
			oos.reset();
			bytes = baos.toByteArray();
			oos.close();
			baos.close();
		} catch (IOException e) {
			bytes = new byte[] {};
			Logger.getLogger("bsdlog").log(Level.ALL, "unable to write to output stream" + e);
		}
		return bytes;
	}

	public static PassengerRequestTO fromBytes(byte[] body) {
		PassengerRequestTO obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = (PassengerRequestTO) ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

}
