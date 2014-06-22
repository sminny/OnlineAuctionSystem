import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.naming.NamingException;
import javax.swing.JOptionPane;

/*
 * LoginMessage puts the user into the logged-in arraylist / disconnect removes him
 * 
 */
public class Server {
	private DataPersistance dataPersistance;
	private ServerMessageTransmitter smt;
	private DataObserver dataObs;
	private ExecutorService executor;
	private ServerFrame serverFrame;
	private Comms communication;
	private boolean executing = true;
	//private static final int NUMBER_OF_THREADS = 14;
	public Server(ServerFrame serverFrame) {

		communication = new Comms(this);
		executor = Executors.newCachedThreadPool();
		//executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		this.serverFrame = serverFrame;
		dataPersistance = new DataPersistance(serverFrame);
		smt = new ServerMessageTransmitter(this);
		dataObs = new DataObserver(this,dataPersistance.getOpen());
		serverFrame.init();
		addActionListeners();
		//default server created on port 8001

	}
	public void addActionListeners(){
		final ObserverPanel op =serverFrame.getObserverPanel();
		op.getLog().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataPersistance.produceItemLog(serverFrame.getObserverPanel().getTextArea().getText());
				JOptionPane.showMessageDialog(serverFrame.getParent(), "Log created for latest updates on server");
			}
		});
		op.getShutdown().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communication.shutdown();
				
				dataObs.interupt();
				executing=false;
				HashMap<String ,Pair<ObjectInputStream,ObjectOutputStream> > loggedIn;
				loggedIn = communication.getLoggedIn();
				Set<String> users = loggedIn.keySet();
				ArrayList<String> ar = new ArrayList<>();
				for(String s : users){
					ar.add(s);
				}
				
				for(int i =0;i<loggedIn.size();i++){
					communication.sendMessage(ar.get(0), new ErrorMessage("Server is going down for maintanance"));
					communication.disconnect(ar.get(0));
				}
				System.exit(0);
			}
		});
	}
	public void startServer() throws IOException{
		/*
		 * Read all data stored on files
		 */
		//dp.populateDataStructures();
		communication.acceptRequests();
		executor.execute(smt);
		/*
		 * Thread for closing auctions
		 */
		executor.execute(dataObs);
		while(executing){
			try {
				executor.execute(new MessageHandler(communication.receiveMessage(),this));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Comms getCommunication() {
		// TODO Auto-generated method stub
		return null;
	}
	public ServerFrame getServerFrame(){
		return serverFrame;
	}
	public void addMail(Text msg) throws FileNotFoundException {
		dataPersistance.addMail(msg);
	}
	public boolean sendMessage(String userID, Message msg)  {
		return communication.sendMessage(userID, msg);	
	}
	//	public void sendMessage(String destinationID, Text msg) {
	//		communication.sendMessage(destinationID, msg);
	//		
	//	}
	public void logActivity(String activity) {
		dataPersistance.logActivity(activity);
	}
	public boolean authenticate(String userID, String password) {
		return dataPersistance.authenticate(userID,password);
	}
	public void registerUser(User user, String pass) throws IOException, NamingException {
		dataPersistance.registerUser(user, pass);

	}
	public Item getItem(int itemID){
		return dataPersistance.getItem(itemID);
	}
	public void addBid(Item item, Pair<String,Double> bid) throws NumberFormatException, FileNotFoundException, NullPointerException, IOException {
		dataPersistance.addBid(item, bid);

	}
	public ArrayList<Item> getItems() {
		return dataPersistance.getOpen();
	}
	public boolean cancelAuction(Item item) throws IOException {
		ArrayList<Item> openAuctions = dataPersistance.getOpen();
		Item it=null;
		for(int j=0;j<openAuctions.size();j++){
			if(openAuctions.get(j).getItemID() == item.getItemID()){
				it = openAuctions.get(j);
				it.setOpen(false);
				openAuctions.remove(j);
			}
		}
		dataObs.removeFromQueue(it);
		return dataPersistance.cancelAuction(it);

	}
	public void createAuction(Item item) {
		dataPersistance.addItem(item);
		dataObs.trackItem(item);
	}
	public void disconnect(String userID) {
		communication.disconnect(userID);

	}
	/*
	 * Different search methods according to
	 * the different search 
	 */
	public ArrayList<Text> getMailbox(String userID) {
		return dataPersistance.getMail(userID);
	}
	
	public ArrayList<Item> getItemsSeller(String criteria) {
		ArrayList<Item> owned =dataPersistance.getOwned().get(criteria);
		ArrayList<Item> search = new ArrayList<Item>();
		for(int i =0;i<owned.size();i++){
			if(owned.get(i).isOpen()){
				search.add(owned.get(i));
			}
		}
		return search;
	}
	public ArrayList<Item> getItemsCategory(String criteria) {
		ArrayList<Item> search = new ArrayList<Item>();
		ArrayList<Item> open = dataPersistance.getOpen();
		for(int i =0;i<open.size();i++){
			if(open.get(i).getKeywords().contains(criteria)){
				search.add(open.get(i));
			}
		}
		return search;
	}
	public ArrayList<Item> getItemsItemID(double criteria) {
		ArrayList<Item> search = new ArrayList<Item>();
		ArrayList<Item> open = dataPersistance.getItems();
		for(int i =0;i<open.size();i++){
			if(open.get(i).getItemID() == criteria){
				search.add(open.get(i));
				return search;
			}
		}
		return search;
	}
	public ArrayList<Item> getItemsDate(Date criteria) {
		ArrayList<Item> search = new ArrayList<Item>();
		ArrayList<Item> open = dataPersistance.getItems();
		for(int i =0;i<open.size();i++){
			if(open.get(i).getStartTime().compareTo(criteria)>=0 ){
				search.add(open.get(i));
			}
		}
		return search;
	}
	/*
	 * Returns an array list of sorted by number relevance
	 *  from the description
	 */
	public ArrayList<Item> getItemsDescription(String criteria) {
		ArrayList<String> identifiers = new ArrayList<String>(Arrays.asList(criteria.split(" ")));
		
		ArrayList<Item> search = new ArrayList<Item>();
		ArrayList<Item> open = dataPersistance.getOpen();
		PriorityQueue<PriorityPair> queue = new PriorityQueue<PriorityPair>();
		for(int i =0;i<open.size();i++){
			Item item = open.get(i);
			PriorityPair pp = new PriorityPair(item, 0);
			for(int j=0; j<identifiers.size();j++){
				if(item.getDescription().contains(identifiers.get(j))){
					pp.increment();
				}
			}
			if(pp.getInteger() >0){
				queue.add(pp);
			}
		}
		int size = queue.size();
		for(int k=0;k<size;k++){
			PriorityPair pp = queue.poll();
			search.add(pp.getItem());
		}
		return search;
	}
	public boolean isValidMail(String src) {
		return dataPersistance.isValidMail(src);
	}
	public void closeItem(Item item) throws FileNotFoundException {
		Text txt;
		dataPersistance.closeAuction(item);
		Stack<Pair<String,Double>> bids = item.getBids();
		Pair<String,Double> bid=null;
		try{
			bid = bids.pop();
		}catch(EmptyStackException e){
			txt = new Text("System", item.getVendorID(),"Auction "+item.getTitle(),
					"The following item expited, no bidders registered");
			logActivity("AUCTION "+item.getTitle()+" CLOSED , no bids registered,sending TEXT to OWNER");
			sendMessage(item.getVendorID(), txt);
			sendMessage(item.getVendorID(), new ErrorMessage("You received a Message, check your mail"));
			dataPersistance.addMail(txt);
			return;
		}
		Set<String> bidders = new HashSet<String>();
		String destination = bid.getElementOne();
		if(bid.getElementTwo()>=item.getReservePrice()){
			txt = new Text("System", destination, "Winner of "+item.getTitle(), "Congratulations," +
					"you were the highest bidder, you may collect your auction");
			sendMessage(destination,txt);
			sendMessage(destination, new ErrorMessage("You received a Message, check your mail"));
			dataPersistance.addMail(txt);
			logActivity("AUCTION "+item.getTitle()+" CLOSED by reaching reserve price," +
					"sending TEXT to HIGHEST BIDDER "+destination);
		}else if(bid.getElementTwo()<item.getReservePrice()){
			txt = new Text("System", destination, "Highest bidder of "+item.getTitle(), "We are sorry," +
					"but you are not the winner of the auction, because the reserve price was not reached");
			sendMessage(destination,txt);
			sendMessage(destination, new ErrorMessage("You received a Message, check your mail"));
			dataPersistance.addMail(txt);
			logActivity("AUCTION "+item.getTitle()+" CLOSED , DID NOT REACH reserve price," +
					"sending TEXT to HIGHEST BIDDER "+destination);
		}
		logActivity("Notifying bidders for the expired AUCTION");
		while(!bids.empty()){

			bid = bids.pop();
			destination = bid.getElementOne();
			bidders.add(destination);
		}
		Iterator<String> i = bidders.iterator();
		while(i.hasNext()){
			destination = i.next();
			txt= new Text("System", destination, "Auction Closed -"+item.getTitle(), "The following auction" +
					" has been closed by the system");
			sendMessage(destination,txt);
			sendMessage(destination, new ErrorMessage("You received a Message, check your mail"));
			dataPersistance.addMail(txt);
		}
	}
	public User getUser(String src) {
		User usr=null;
		for(int i =0; i<dataPersistance.getRegistered().size();i++){
			if(dataPersistance.getRegistered().get(i).getUsername().equals(src)){
				usr = dataPersistance.getRegistered().get(i);
				break;
			}
		}
		return usr;
	}
	public ArrayList<Item> getMyAuctions(String src) {
		ArrayList<Item> search = new ArrayList<Item>(dataPersistance.getOwned().get(src));
		return search;
	}
	public ArrayList<Item> getMyBids(String src) {
		ArrayList<Item> search = dataPersistance.getBids(src);
		return search;
	}
}


