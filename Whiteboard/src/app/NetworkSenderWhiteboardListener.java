package app;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.Shape;

public class NetworkSenderWhiteboardListener implements WhiteboardListener {

	@Override
	public void addShape(Shape shape) throws IOException {
		Socket socket = new Socket("192.168.3.117", 8070);
		ObjectOutputStream oOS = new ObjectOutputStream(
				socket.getOutputStream());
		oOS.writeObject(shape);
		socket.close();
	}

}
