package ClientServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ServerMultiThreaded implements Runnable {
	protected int port = 4445;
	protected ServerSocket serverSocket = null;
	protected boolean hasStopped = false;
	protected Thread movingThread = null;

	private static String filename = "/Users/Saurabh/Documents/DataTempTransfer/server/data.txt";
	private static File file = new File(filename);
	private static int partSize = 4096;
	private static int offset = 0;
	private static final int metaSize = 4;
	private static int fileSize = (int) file.length();

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

	public byte[] createMetadata(int fileSize, int partSize, byte[] filename) {
		byte[] packet = new byte[5 * metaSize + filename.length];
		byte[] fileSizeArr = new byte[4 * metaSize];
		byte[] partSizeArr = new byte[metaSize];

		fileSizeArr = Integer.toString(fileSize).getBytes();
		fileSizeArr = Arrays.copyOf(fileSizeArr, 4 * metaSize);
		partSizeArr = Integer.toString(partSize).getBytes();
		partSizeArr = Arrays.copyOf(partSizeArr, metaSize);

		System.arraycopy(fileSizeArr, 0, packet, 0, fileSizeArr.length);
		System.arraycopy(partSizeArr, 0, packet, fileSizeArr.length, partSizeArr.length);
		System.arraycopy(filename, 0, packet, fileSizeArr.length + partSizeArr.length, filename.length);

		return packet;
	}

	private void sendMetadata(Socket clientSocket) {
		String file = filename.substring(filename.lastIndexOf('/') + 1);
		byte[] metadata = createMetadata(fileSize, partSize, file.getBytes());
		try {
			OutputStream os = clientSocket.getOutputStream();
			os.write(metadata);
			os.close();
		} catch (IOException e) {
			System.err.println("Coundn't send metadata to client. ABORTING...");
			System.exit(-1);
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Server: filesize = " + fileSize + " bytes.");
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is listening at: " + port);
			Socket clientSocket = serverSocket.accept();
			sendMetadata(clientSocket);

			while (!hasStopped() && (offset < fileSize)) {
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					if (hasStopped()) {
						System.out.println("Server is leaving.");
						return;
					}
					throw new RuntimeException("Client can't be connected");
				}
				new Thread(
						new WorkerServer(clientSocket, file, offset, Math.min(partSize, (int) file.length() - offset)))
								.start();
				offset += partSize;
			}
		} catch (IOException e) {
			System.err.println("Trouble starting server.");
		}
	}
}
