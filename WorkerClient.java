package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class WorkerClient implements Runnable {
	protected int port;
	protected Socket clientSocket = null;
	protected int clientID = 0;
	private byte[] messageBytes = null;
	private static final int metaSize = 4;
	private static String opFileName = "/Users/Saurabh/Documents/DataTempTransfer/client/newFile0";

	public WorkerClient(int clientID, int port) {
		this.clientID = clientID;
		this.port = port;
	}

	public void run() {
		try {
			try {
				this.clientSocket = new Socket("localhost", this.port);
			} catch (IOException e) {
				System.err.println("Server can not be reached.");
				return;
			}

			InputStream is = clientSocket.getInputStream();
			RandomAccessFile raf = new RandomAccessFile(opFileName + ".txt", "rw");

			byte[] offArr = new byte[metaSize];
			byte[] partSize = new byte[metaSize];
			is.read(offArr, 0, metaSize);
			is.read(partSize, 0, metaSize);

			int offset = Integer.parseInt((new String(offArr)).trim());
			int part = Integer.parseInt((new String(partSize)).trim());

			messageBytes = new byte[part];
			is.read(messageBytes);

			raf.seek(offset);
			System.out.println("Client " + this.clientID + " seeked to " + offset);
			raf.write(messageBytes, 0, part);
			raf.close();
			is.close();

			System.out.println("Client " + clientID + ": Finished Reading and Writing.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}