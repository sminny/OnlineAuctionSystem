import java.util.concurrent.LinkedBlockingQueue;


public class ServerMessageTransmitter implements Runnable{
	/*
	 * POSSIBLE - empty constructor
	 * transmitMessage(Pair<Socket,Message> pair) to be used for substitution
	 * of constructor
	 */
	private LinkedBlockingQueue<Pair<String ,Message>> transmitedMessages;
	private Server server;
	public ServerMessageTransmitter(Server server){
		this.server = server;
		transmitedMessages = new LinkedBlockingQueue<Pair<String,Message>>();
	}
	public void transmitMessage(Message msg,String ID){
		Pair<String,Message> pair = new Pair<String,Message>(ID,msg);
		transmitedMessages.add(pair);
	}
	@Override
	public void run() {	
		while(true){	
			try {
				Pair<String,Message> p = transmitedMessages.take();
				server.getCommunication().sendMessage(p.getElementOne(), p.getElementTwo());
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
