package model.factory;

import java.awt.Color;
import java.awt.event.MouseEvent;

import model.Polygon;

public class PolygonFactory extends ShapeFactory {
	
	private Polygon currentPoligon;

	public PolygonFactory(Color color) {
		super(color);
	}

	@Override
	public Polygon getShape() {
		return currentPoligon;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (currentPoligon == null || currentPoligon.isComplete()) {
			currentPoligon = new Polygon(getColor(), e.getPoint());
		} else {
			currentPoligon.addPoint(e.getPoint());
		}

	}

	@Override
	public String getName() {
		return "Polygon";
	}

}
