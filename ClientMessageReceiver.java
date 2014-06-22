import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ClientMessageReceiver implements Runnable{
	/*
	 * LinkedBlockingQueue to be tested, take() waits
	 * 
	 * LoginMessage puts the user into the logged-in arraylist / disconnect removes him
	 * 
	 * Tracing the origin of the message:
	 * -Through the User - each message has a User src
	 */
	private LinkedBlockingQueue<Message> tasks;
	//private Socket clientSocket;
	private Pair<ObjectInputStream,ObjectOutputStream> streams;
	public ClientMessageReceiver(LinkedBlockingQueue<Message> tasks,Socket clientSocket,
			Pair<ObjectInputStream,ObjectOutputStream> streams){
		//this.clientSocket= clientSocket;
		this.tasks= tasks;
		this.streams = streams;
	}
	
	public void run() {
		while(true){
			
				Object msg;
				try {
					msg = streams.getElementOne().readObject();
					tasks.add((Message)msg);
				} catch(NullPointerException e){
					break;
				}catch (ClassNotFoundException e) {
				
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (EOFException e){
					try {
						streams.getElementOne().close();
						streams.getElementTwo().flush();
						streams.getElementTwo().close();
						break;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
