package pad.ijvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class NetworkResources {
	Socket socket;
	ServerSocket serverSocket;
	BufferedReader srvIn;
	PrintWriter srvOut;

	public boolean connectToServer(byte[] ipAddress, int port) {
		boolean success = true;
		String ipAddressString = String.valueOf((int) ipAddress[0]);
		for(int i = 1; i<ipAddress.length; i++) {
			ipAddressString += "." + String.valueOf((int) ipAddress[i]);
		}

		try {
			socket = new Socket(ipAddressString, port);
			srvIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		try {
			srvOut = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}

	public boolean createServer(int port) {
		boolean success = true;
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			srvIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		try {
			srvOut = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}

	void print(byte[] dataByteArray) {
		char data = (char) ByteBuffer.wrap(dataByteArray).getInt();
		srvOut.print(data);
		srvOut.flush();
	}

	byte[] read() {
		try {
			return ByteBuffer.allocate(4).putInt(srvIn.read()).array();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	void closeConnection() {
		try {
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
