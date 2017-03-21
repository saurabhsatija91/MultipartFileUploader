package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
	private int port;
	private int numClients;
	private int fileSize;
	private int partSize;
	private static final int metaSize = 4;
	private String opFileName = "";
	private String opFileDir = "/Users/Saurabh/Documents/DataTempTransfer/client/";

	public Client(int port) {
		this.port = port;
	}

	private void decodeMetadata(Socket clientSocket) {
		byte[] fileSizeArr = new byte[4 * metaSize];
		byte[] partSizeArr = new byte[metaSize];
		byte[] opFileNameArr = new byte[10 * metaSize];
		InputStream is;

		try {
			is = clientSocket.getInputStream();
			is.read(fileSizeArr, 0, 4 * metaSize);
			is.read(partSizeArr, 0, metaSize);
			is.read(opFileNameArr);

			is.close();
		} catch (IOException e) {
			System.err.println("Reading metadata failed from server");
			e.printStackTrace();
		}

		this.fileSize = Integer.parseInt(new String(fileSizeArr).trim());
		this.partSize = Integer.parseInt(new String(partSizeArr).trim());
		this.opFileName = this.opFileDir + new String(opFileNameArr).trim();
		this.numClients = (int) Math.ceil((double) fileSize / partSize);

		System.out.println("Client: fileSize = " + this.fileSize);
		System.out.println("Client: partSize = " + this.partSize);
		System.out.println("Client: # of thread = " + this.numClients);

	}

	public void run() {
		try {
			Socket clientSocket = new Socket("localhost", this.port);
			decodeMetadata(clientSocket);
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Coundn't receive file info.");
			e.printStackTrace();
		}
		for (int i = 0; i < numClients; i++) {
			new Thread(new WorkerClient(i + 1, port, opFileName)).start();
		}
	}
}
