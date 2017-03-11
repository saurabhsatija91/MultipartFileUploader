package ClientServer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class WorkerThread implements Runnable {
	protected Socket clientSocket = null;
	private final int metaSize = 4;
	private byte[] fileArr = null;
	private byte[] packet = null;
	
	private byte[] createPacket (int offset, int size, byte[] data) {
		byte[] packet = new byte[2 * metaSize + data.length];
		byte[] offArr = new byte[metaSize];
		byte[] partSize = new byte[metaSize];
		
		offArr = Integer.toString(offset).getBytes();
		offArr = Arrays.copyOf(offArr, metaSize);
		partSize = Integer.toString(size).getBytes();
		partSize = Arrays.copyOf(partSize, metaSize);
		
		System.arraycopy(offArr, 0, packet, 0, offArr.length);
		System.arraycopy(partSize, 0, packet, offArr.length, partSize.length);
		System.arraycopy(data, 0, packet, offArr.length + partSize.length, data.length);
		
		return packet;
	}
	
	public WorkerThread(Socket clientSocket, File file, int offset, int partSize) {
		this.clientSocket = clientSocket;
		fileArr = new byte[partSize];
		
		System.out.println("WT: File/part size: " + partSize);
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
			InputStream is = clientSocket.getInputStream();
			OutputStream os = clientSocket.getOutputStream();
			long timeStamp = System.currentTimeMillis();
			
			os.write(packet);
			os.close();
			is.close();
			System.out.println("Your request is processed in: " +
					(System.currentTimeMillis() - timeStamp) + "s");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
