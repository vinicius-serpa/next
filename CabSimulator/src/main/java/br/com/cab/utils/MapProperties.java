package br.com.cab.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class MapProperties {
	
	private String resourceName = "map.properties";
	private int maxX = 0;
	private int maxY = 0;
	
	public MapProperties() {
		
		Properties props = new Properties();
		
		try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
		    
			if (resourceStream != null) {
				props.load(resourceStream);
			} else {
				throw new FileNotFoundException("property file '" + this.resourceName + "' not found in the classpath");
			}
		    
		    this.maxX = Integer.parseInt(props.getProperty("max_x"));
		    this.maxY = Integer.parseInt(props.getProperty("max_y"));
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

}
