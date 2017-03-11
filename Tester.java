package ClientServer;

public class Tester {
	public static void main(String[] args) {
		ServerMultiThreaded newServer = new ServerMultiThreaded(4445);
		new Thread(newServer).start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Client client1 = new Client(4445, 1);
		Client client2 = new Client(4445, 2);
		Client client3 = new Client(4445, 3);
		
		new Thread(client1).start();
		new Thread(client2).start();
		new Thread(client3).start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Stopping server.");
		newServer.stop();
	}
}
