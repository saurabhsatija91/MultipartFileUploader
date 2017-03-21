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

		Client newClient = new Client(4445);

		new Thread(newClient).start();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping server.");
		newServer.stop();
	}
}
