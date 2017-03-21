package ClientServer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class WorkerServer implements Runnable {
	protected Socket clientSocket = null;
	private byte[] fileArr = null;
	private byte[] packet = null;
	private static final int metaSize = 4;
	
	private byte[] createPacket (int offset, int size, byte[] data) {
		byte[] packet = new byte[5 * metaSize + data.length];
		byte[] offArr = new byte[4 * metaSize];
		byte[] partSize = new byte[metaSize];
		
		offArr = Integer.toString(offset).getBytes();
		offArr = Arrays.copyOf(offArr, 4 * metaSize);
		partSize = Integer.toString(size).getBytes();
		partSize = Arrays.copyOf(partSize, metaSize);
		
		System.arraycopy(offArr, 0, packet, 0, offArr.length);
		System.arraycopy(partSize, 0, packet, offArr.length, partSize.length);
		System.arraycopy(data, 0, packet, offArr.length + partSize.length, data.length);
		
		return packet;
	}
	
	public WorkerServer(Socket clientSocket, File file, int offset, int partSize) {
		this.clientSocket = clientSocket;
		fileArr = new byte[partSize];
		
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.getChannel().position((long) offset);
			fis.read(fileArr, 0, partSize);
			fis.close();
			packet = createPacket (offset, partSize, fileArr);
		} catch (FileNotFoundException e) {
			System.err.println("File input stream error");
		} catch (IOException e) {
			System.err.println("Error reading file in buffer");
		}
	}
	@Override
	public void run() {
		try {
			OutputStream os = clientSocket.getOutputStream();
			os.write(packet);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
