package app;

import java.io.IOException;

import model.Shape;

public interface WhiteboardListener {

	void addShape(Shape shape) throws IOException;

}
