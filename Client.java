package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class Client implements Runnable {
	private int port;
	private int numClients;

	public Client(int port, int numClients) {
		this.port = port;
		this.numClients = numClients;
	}

	public void run() {
		for (int i = 0; i < numClients; i++) {
			new Thread(new WorkerClient(i + 1, port)).start();
		}
	}
}
