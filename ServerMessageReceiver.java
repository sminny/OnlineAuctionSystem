import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ServerMessageReceiver implements Runnable{


	private LinkedBlockingQueue<Message> tasks;
	private ServerSocket serverSocket;
	private HashMap<String,Pair<ObjectInputStream,ObjectOutputStream>> loggedIn;
	private String guestUser=null;
	private boolean active = true;
	public ServerMessageReceiver(LinkedBlockingQueue<Message> tasks,ServerSocket serverSocket,
			HashMap<String,Pair<ObjectInputStream,ObjectOutputStream>> loggedIn){
		this.serverSocket= serverSocket;
		this.tasks= tasks;
		this.loggedIn = loggedIn;
	}

	public void run() {

		while(true){
			final Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				Thread session = new Thread(){
					public void run(){
						try {
							ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
							ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
							Message msg = (Message) in.readObject();
							guestUser = msg.getUserID();
							Pair<ObjectInputStream, ObjectOutputStream> streams = new Pair<ObjectInputStream, ObjectOutputStream>(in,out);
							loggedIn.put(guestUser,streams);
							while(active){
								tasks.add(msg);
								msg = (Message) in.readObject();
								if(msg.getID()== Message.AUTHENTICATION){
									loggedIn.remove(guestUser);
									guestUser=msg.getUserID();
									loggedIn.put(guestUser, streams);

								}
							}	
						}catch(SocketException e){
							this.interrupt();
						}catch(EOFException e){
							loggedIn.remove(guestUser);
						}catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				};
				session.start();
			}catch(BindException e){ 
				JOptionPane.showMessageDialog(new JFrame(), "There is already a socket running on the socket, please change it");
			}catch(SocketException e){
				break;
			}catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
}