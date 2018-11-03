package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Polygon extends Shape {

	private final Point firstPoint;
	private Point secondPoint;
	private Point thirdPoint;
	private Point fourthPoint;

	public Polygon(Color color, Point firstPoint) {
		super(color);
		this.firstPoint = firstPoint;
	}

	public void addPoint(Point point) {
		if (secondPoint == null) {
			secondPoint = point;
		} else if (thirdPoint == null) {
			thirdPoint = point;
		} else {
			fourthPoint = point;
		}
	}

	public boolean isComplete() {
		return fourthPoint != null;
	}

	@Override
	public void draw(Graphics2D g) {
		if (secondPoint != null && thirdPoint != null && fourthPoint != null) {
			g.setColor(getColor());
			g.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
			g.drawLine(secondPoint.x, secondPoint.y, thirdPoint.x, thirdPoint.y);
			g.drawLine(thirdPoint.x, thirdPoint.y, fourthPoint.x, fourthPoint.y);
			g.drawLine(fourthPoint.x, fourthPoint.y, firstPoint.x, firstPoint.y);
		}

	}
	
}
