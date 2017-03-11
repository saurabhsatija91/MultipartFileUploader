package ClientServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMultiThreaded implements Runnable {
	protected int port = 4445;
	protected ServerSocket serverSocket = null;
	protected boolean hasStopped = false;
	protected Thread movingThread = null;
	
	private static String filename = "/Users/Saurabh/Documents/DataTempTransfer/server/filebasic.txt";
	private File file = new File(filename);
	private static int partSize = 30;
	private static int offset = 0;
	
	public ServerMultiThreaded(int port) {
		this.port = port;
	}
	
	public synchronized boolean hasStopped() {
		return hasStopped;
	}
	
	public synchronized void stop() {
		this.hasStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("server can't be closed");
		}
	}
	
	@Override
	public void run() {
		synchronized(this) {
			this.movingThread = Thread.currentThread();
		}
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is listening at: " + port);
			Socket clientSocket;
			while (!hasStopped() && (offset < (int)file.length())) {
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					if (hasStopped()) {
						System.out.println("Server is leaving.");
						return;
					}
					throw new RuntimeException("Client can't be connected");
				}
				new Thread (
						new WorkerThread(clientSocket, file, offset, Math.min(partSize, (int)file.length() - offset))
						).start();
				offset += partSize;
			}
		} catch (IOException e) {
			System.err.println("Trouble starting server.");
		}
	}
}
