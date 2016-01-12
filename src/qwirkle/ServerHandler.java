package qwirkle;

public class ServerHandler extends Thread {
	
	private QwirkleClient client;
	private String command;


	public ServerHandler (String command, QwirkleClient client) {
		this.command = command;
		this.client = client;
	}
	
	@Override
	public void run() {
		// TODO: process command.
		
	}
}
