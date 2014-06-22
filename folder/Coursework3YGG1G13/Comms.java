
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*
 * Communication layer for both Server/Client
 * 
 */
public class Comms {
	private HashMap<String ,Pair<ObjectInputStream,ObjectOutputStream> > loggedIn;
	private LinkedBlockingQueue<Message> tasks = new LinkedBlockingQueue<Message>();
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private Pair<ObjectInputStream,ObjectOutputStream> streams;

	public final static int SERVER_PORT = 8001;
	public Comms(Client client) {
		try {
			clientSocket = new Socket("localhost",SERVER_PORT);
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
			streams = new Pair<ObjectInputStream,ObjectOutputStream>(in,out);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 	catch (IOException e) {
			e.printStackTrace();
		}

	}
	public Comms(Server server) {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch(BindException e){
			JOptionPane.showMessageDialog(new JFrame(), "The socket is already in use");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loggedIn= new HashMap<String,Pair<ObjectInputStream,ObjectOutputStream>>();

	}
	/*
	 * sendMessage transmits different Messages - Text,Authentication,Info,Submit
	 */
	public void sendMessage(Message msg) {
		ObjectOutputStream oos = streams.getElementTwo();
		try {
			oos.writeObject(msg);
			oos.flush();
		}catch(SocketException e){
			JOptionPane.showMessageDialog(new JFrame(), "Server is currently unreachable, please close the application\n and try again");

			try {
				clientSocket.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}

		}catch (IOException e) {
			e.printStackTrace();
		} 

	}
	public synchronized boolean sendMessage(String userID,Message msg) {
		if(!loggedIn.containsKey(userID)){
			loggedIn.remove(userID);
			if(msg.getID() == Message.TEXT)
				return true;
			else
				return false;
		}
		ObjectOutputStream oos = loggedIn.get(userID).getElementTwo();
		try {
			oos.reset();
			oos.writeObject(msg);
			oos.flush();
		}catch(SocketException e){
			disconnect(msg.getUserID());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Message receiveMessage() throws InterruptedException {
		return tasks.take();
	}
	public void acceptRequests(){
		new Thread(new ServerMessageReceiver(tasks, serverSocket, loggedIn)).start();
	}
	public void acceptResponses(){
		new Thread(new ClientMessageReceiver(tasks, clientSocket, streams)).start();
	}
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void disconnect(String userID) {
		Pair<ObjectInputStream,ObjectOutputStream> streams =loggedIn.get(userID);
		try {
			streams.getElementOne().close();
			streams.getElementTwo().close();
		}catch (SocketException e){

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		loggedIn.remove(userID);

	}
	public HashMap<String ,Pair<ObjectInputStream,ObjectOutputStream> > getLoggedIn() {
		return loggedIn;
	}
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
