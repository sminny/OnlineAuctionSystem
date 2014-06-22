
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.naming.NamingException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * This class assumes that the Server will be placed in an environment where
 * the access of the files that the machine operates with will be only to
 * the machine and certain authorities
 * 

 */
public class DataPersistance {
	private DateFormat dateFormat;
	private Date date;
	private List<User> registered;
	private Map<String,ArrayList<Text>> mailbox;
	private ArrayList<Item> items; // openAuctions e items
	private ArrayList<Item> openAuctions;
	private Map<String,ArrayList<Item>> owned ;
	private Map<String,String> authentication;
	private Map<String,ArrayList<Item>> bids;
	public File data,userData,itemData,activityLogData,completeActivityLog,itemActivityLog;
	private ServerFrame serverFrame;
	public DataPersistance(ServerFrame serverFrame){
		this.serverFrame = serverFrame;
		bids = new HashMap<String,ArrayList<Item>>();
		mailbox = new HashMap<String,ArrayList<Text>>();
		dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
		data = new File("DataPersistance");
		data.mkdir();
		userData = new File("DataPersistance/UserData");
		userData.mkdir();
		itemData = new File("DataPersistance/ItemData");
		itemData.mkdir();
		activityLogData = new File("DataPersistance/ActivityLog");
		activityLogData.mkdir();
		completeActivityLog = new File("DataPersistance/ActivityLog/CompleteLog.txt");
		registered= new ArrayList<User>();
		owned = new HashMap<String,ArrayList<Item>>();
		items = new ArrayList<Item>();
		authentication = new HashMap<String,String>();
		openAuctions = new ArrayList<Item>();
		start();
	}
	private void start(){
		try {
			completeActivityLog.createNewFile();
			populateDataStructures();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void populateDataStructures() throws IOException {

		File[] userSpace = userData.listFiles();
		try {
			loadUserData(userSpace);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		loadItemData();
		logActivity("Items registered: "+items.size()+" Open Auctions: "
				+ openAuctions.size()+ "\n"+"Users registered: "+registered.size()
				+" Authentications logged: "+authentication.size()+" Owner-Item relationships: \n" 
				+owned.size()+" bids:"+bids.size());
		logActivity("Data structures ready for use");
	}
	private void loadUserData(File[]userSpace) throws IOException, NamingException{
		BufferedReader br ;
		String buffer = null;
		String[] ar;
		logActivity("Loading user data into datastructures");
		for(int i =0;i< userSpace.length;i++){

			File[] personalFiles = userSpace[i].listFiles();

			String hash;
			br = new BufferedReader(new FileReader(personalFiles[0]));
			hash = br.readLine();
			br.close();
			br = new BufferedReader(new FileReader(personalFiles[1]));
			User newUser = null;

			buffer =br.readLine();
			ar = buffer.split(" ");
			buffer = userSpace[i].getName();
			newUser = new User(ar[1], ar[2], buffer);
			newUser.setPenaltyPoints(Integer.parseInt(br.readLine().split(" ")[1]));
			br.close();
			owned.put(buffer, new ArrayList<Item>());
			registered.add(newUser);
			authentication.put(buffer, hash);
			File mail =new File(userSpace[i].getPath()+"/mailbox");
			mailbox.put(buffer, new ArrayList<Text>());
			File[] texts = mail.listFiles();
			if (texts==null){

				continue;
			}
			loadUserMails(texts, buffer);
		}
	}
	public boolean cancelAuction(Item item) throws IOException{
		File path = new File(userData.getPath()+"/"+item.getVendorID()+"/userData.txt");
		BufferedReader br = null;
		BufferedWriter bw = null;
		boolean result;
		logActivity("USER "+item.getVendorID()+" CANCELLED the auction for "+item.getTitle());
		logActivity("checking if USER "+item.getVendorID()+" is subject to penalty");
		try{
			if(item.getHighestBid().getElementTwo()> item.getReservePrice()){
				logActivity("USER "+item.getVendorID()+" has received a PENALTY POINT for breaching regularities");
				br = new BufferedReader(new FileReader(path));
				String buffer =br.readLine()+"\n";
				String penaltyLine= br.readLine();

				for(User u: registered){
					if (u.getUsername().equals(item.getVendorID())){
						u.penalize();
						penaltyLine = penaltyLine.substring(0, penaltyLine.length()-1)+u.getPenaltyPoints();		
						break;
					}
				}
				bw = new BufferedWriter(new FileWriter(path));
				buffer +=penaltyLine;
				bw.write(buffer);
				bw.flush();
				br.close();
				bw.close();
				result = true;
			}else{
				logActivity("USER "+item.getVendorID()+" CANCELLED the auction WITHOUT PENALTY");
				result = false;
			}
		}catch(NoSuchElementException e){
			logActivity("USER "+item.getVendorID()+" CANCELLED the auction " +
					"WITHOUT PENALTY, no bids registered");
			result = false;
		}
		closeAuction(item);
		return result;
	}
	private void loadUserMails(File[] texts,String userName) throws FileNotFoundException{
		BufferedReader br;
		for(int j=0;j<texts.length;j++){
			String userID, destinationID,title,text;
			br = new BufferedReader(new FileReader(texts[j]));
			try {
				title = br.readLine();
				title = title.substring(7);
				userID= br.readLine();
				userID= userID.substring(6);
				destinationID = br.readLine();
				destinationID = destinationID.substring(4);
				text = br.readLine();
				text = text.substring(6);
				mailbox.get(userName).add(new Text(userID, destinationID, title, text));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logActivity("All MAILS for "+userName+" have been stored in data structures");
	}

	private void processItem(Pair<File,File> item) throws NumberFormatException, IOException{
		String buffer;
		BufferedReader br;
		String[] ar;
		String title = "",description,vendorID,reservePrice,status,itemID;
		Date start= null,close=null;

		br= new BufferedReader(new FileReader(item.getElementOne()));
		buffer = br.readLine();
		ar = buffer.split(" ");
		//title & status
		for(int i=1;i<ar.length-2;i++){
			title = ar[i]+" ";
		}
		status = ar[ar.length-2];
		itemID= ar[ar.length-1];
		//keywords
		buffer = br.readLine();
		ArrayList<String> keyword = new ArrayList<String>();
		ar = buffer.split(" ");
		ar = ar[1].split(",");
		for(int i=0;i<ar.length;i++){
			keyword.add(ar[i]);
		}
		buffer= br.readLine();
		//start/close time
		try {
			ar= buffer.split(" ");
			start = dateFormat.parse(ar[2]+" "+ar[3]);
			buffer= br.readLine();
			ar= buffer.split(" ");
			close = dateFormat.parse(ar[2]+" "+ar[3]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		buffer = br.readLine();
		vendorID = buffer.split(" ")[1];
		buffer = br.readLine();
		description = buffer.substring(13);
		buffer = br.readLine();
		reservePrice = buffer.split(" ")[2];

		Item i;

		if(item.getElementTwo() == null){
			i = new Item(title, description, vendorID, Double.parseDouble(reservePrice), 
					keyword);
		}else{

			i = new Item(title, description, vendorID, Double.parseDouble(reservePrice), 
					keyword, item.getElementTwo().getPath());
		}
		//bids
		buffer = br.readLine(); 	//remove the BIDS: line
		while(br.ready()){
			buffer = br.readLine();
			String[] bid= buffer.split(" ");
			try{
				bids.get(bid[0]).add(i);
			}catch(NullPointerException e){
				bids.put(bid[0], new ArrayList<Item>());
				bids.get(bid[0]).add(i);
			}
			Pair<String,Double> nextBid = new Pair<String,Double>(bid[0],Double.parseDouble(bid[1]));
			i.addBid(nextBid);
		}

		i.setItemID(Integer.parseInt(itemID));

		i.setStartTime(start);
		i.setCloseTime(close);
		if(status.equals("OPEN")){
			i.setOpen(true);
			openAuctions.add(i);
		}else
			i.setOpen(false);
		items.add(i);					//add item
		owned.get(vendorID).add(i);		//add mapping from user to collection of items he owns
		br.close();
	}
	private void loadItemData() throws IOException{
		HashMap<File,File> storage = new HashMap<>();
		logActivity("Loading item data into datastructures");
		/*
		 * Every file system might return .listFiles() in a different way
		 * parsing all of these is not a good idea, much better to map them
		 * and check which one has/hasn't got a pair
		 */
		for(File file  : itemData.listFiles()){
			if(file.getName().matches(".*(.png)$")){
				String name = new String();
				name = file.getName().substring(0, file.getName().length()-4);
				// length should be -4 (".png")
				storage.put(new File(itemData.getPath()+"/"+name), file);
			}
			else {
				if(!storage.containsKey(file)){
					storage.put(file, new File("happiness.jpg"));
				}
			}
		}
		Set<File> keySet= storage.keySet();
		Iterator<File> i = keySet.iterator();
		while(i.hasNext()){
			File next = i.next();
			processItem(new Pair<File,File>(next,storage.get(next)));
		}
		logActivity("Loading successful ");
	}
	/*
	 * All of the below methods that log
	 * user/item/activity data need to add
	 * or create the needed data in the 
	 * appropriate file
	 * e.g. registerUser(User user) - add into HashMap
	 * 				  				- create new file in userData
	 * 								- add to activityLog
	 */
	@SuppressWarnings("resource")
	public void registerUser(User user,String hash) throws IOException, NamingException{
		registered.add(user);

		File newUser = new File("DataPersistance/UserData/"+user.getUsername());
		//name already exists
		if(!newUser.mkdir()){
			throw new NamingException();
		}	
		logActivity("REGISTER "+user.getFirstName()
				+" "+user.getFamilyName()+" with ID:"+user.getUsername());
		File userData = new File(newUser.getAbsolutePath()+"/userData.txt");
		File shadow = new File(newUser.getAbsolutePath()+"/shadow.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(userData));
		bw.write("NAME: "+user.getFirstName()+" "+user.getFamilyName()+"\n");
		bw.write("PENALTY_POINTS: "+user.getPenaltyPoints());
		bw.flush();

		bw= new BufferedWriter(new FileWriter(shadow));
		bw.write(hash);
		bw.flush();
		addAuthentication(user.getUsername(), hash);
		bw.close();
		createMailBox(user);
	}
	private void createMailBox(User user){
		File mail = new File(userData.getPath()+"/"+user.getUsername()+"/mailbox");
		mail.mkdir();
		mailbox.put(user.getUsername(), new ArrayList<Text>());

	}

	/*
	 * Adding mail to mailbox of the destination User,
	 * if there is no such user, returns FileNotFoundException
	 */
	public void addMail(Text text)throws FileNotFoundException{
		String destination = text.getDestinationID();
		logActivity("MAIL ADDED src:"+text.getUserID()+" dest:"+text.getDestinationID());
		mailbox.get(destination).add(text);
		String boxPath = userData.getPath()+"/"+destination+"/mailbox";
		try {
			File dest = new File(boxPath);
			if(!dest.exists())
				throw new FileNotFoundException();
			File mail = new File(dest.getPath()+"/"+text.getUserID()+
					new Date(System.currentTimeMillis()).toString());
			if(mail.exists()){
				mail= new File(mail.getPath()+"1");
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(mail));
			bw.write("TITLE: "+text.getTitle()+"\n");
			bw.write("FROM: "+text.getUserID()+"\n");
			bw.write("TO: "+text.getDestinationID()+"\n");
			bw.write("TEXT: "+text.getText()+"\n");

			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logActivity(String activity) {

		date = new Date();
		String feed = dateFormat.format(date)+": " +activity;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(completeActivityLog, true));
			bw.append(feed+"\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverFrame.addToLivefeed(feed+"\n");
	}
	/*
	 * EXCEPTION THROWN :
	 * - catch in protocol and send an Error message back
	 * - check if item exists
	 * - check if item is open/closed
	 * - check highest bid
	 */
	public synchronized void addBid(Item item,Pair<String,Double> bid) throws IOException,FileNotFoundException,
	NullPointerException, NumberFormatException {
		File file = new File(itemData.getAbsolutePath()+"/"+item.getVendorID()+item.getItemID());
		BufferedReader br = new BufferedReader(new FileReader(file));
		String lastLine="";
		lastLine = br.readLine();
		
		if(lastLine.contains("CLOSED")){
			br.close();
			logActivity("ITEM "+item.getTitle()+" has been closed, bid not authroized");
			throw new NullPointerException();
		}
		while(br.ready()){
			lastLine = br.readLine();
		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));

		item.addBid(bid);
		for(Item i: owned.get(item.getVendorID())){
			if(i.getItemID() == item.getItemID()){
				i.addBid(bid);
				break;
			}
		}
		if(!lastLine.contains("BIDS") ){
			if(Double.parseDouble(lastLine.split(" ")[1])<bid.getElementTwo()){

				bw.append(bid.getElementOne()+" "+bid.getElementTwo()+"\n");
				bw.flush();
				bw.close();
				logActivity("ADDING BID from "+bid.getElementOne()+" to "+file.getName()+"- "+bid.getElementTwo());
				bids.get(bid.getElementOne()).add(item);
				return;
			}else{
				bw.close();
				throw new NumberFormatException();
			}
		}
		try{
			bids.get(bid.getElementOne()).add(item);
		}catch(NullPointerException e){
			bids.put(bid.getElementOne(),new ArrayList<Item>());
			bids.get(bid.getElementOne()).add(item);
		}
		logActivity("ADDING BID from "+bid.getElementOne()+" to "+file.getName()+"- "+bid.getElementTwo());
		bw.append(bid.getElementOne()+" "+bid.getElementTwo()+"\n");
		bw.flush();
		bw.close();
	}
	/*
	 * Problems with receiving references from different machines
	 * .remove() compares references, which will not be found inside 
	 * the data structures
	 */
	public void closeAuction(Item item){
		item.setOpen(false);
		if(!openAuctions.contains(item)){
			for(int i=0;i<openAuctions.size();i++){
				if(openAuctions.get(i).getItemID() == item.getItemID()){
					openAuctions.get(i).setOpen(false);
					openAuctions.remove(i);
				}
			}
		}else{
			openAuctions.remove(item);
		}
		logActivity("CLOSING AUCTION "+item.getVendorID()+item.getItemID()+" ");
		try {
			String buffer = "";
			File itemFile = new File(itemData.getPath()+"/"+item.getVendorID()+item.getItemID());
			BufferedReader br = new BufferedReader(new FileReader(itemFile));
			buffer = br.readLine();
			String[] words = buffer.split(" ");
			String ID = words[words.length-1];
			String status = words[words.length-2];
			buffer = (buffer.substring(0, buffer.length()-(ID.length()+status.length()+1))
					+"CLOSED "+item.getItemID()+"\n");
			while(br.ready()){
				buffer += (br.readLine()+"\n");
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(itemFile));
			bw.write(buffer);
			bw.flush();
			bw.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logActivity("CLOSING COMPLETE , item has been updated in file system");
	}
	/*
	 * EXCEPTIONS:
	 * -file exists
	 */
	public void addItem(final Item item){
		item.setItemID(items.size());
		try{
			owned.get(item.getVendorID()).add(item);	//add to ownership list
		}catch(NullPointerException e){

			owned.put(item.getVendorID(), new ArrayList<Item>());
			owned.get(item.getVendorID()).add(item);
		}
		final File newItem = new File(itemData.getAbsolutePath()+"/"+item.getVendorID()+item.getItemID());
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			String st = sdf.format(item.getStartTime());
			String ct = sdf.format(item.getCloseTime());
			newItem.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(newItem));
			if(item.isOpen()){
				bw.write("TITLE: "+item.getTitle()+" OPEN"+" "+item.getItemID()+"\n");
			}else
				bw.write("TITLE: "+item.getTitle()+" CLOSED"+" "+item.getItemID()+"\n");
			bw.write("KEYWORDS: ");
			for(String i : item.getKeywords()){
				bw.write(i+", ");
			}
			bw.write("\nSTART TIME: "+st+"\n");
			bw.write("CLOSE TIME: "+ct+"\n");
			bw.write("VENDOR: "+item.getVendorID()+"\n");
			bw.write("DESCRIPTION: "+item.getDescription().replace("\n", "")+"\n");
			bw.write("RESERVE PRICE: "+item.getReservePrice()+"\n");
			bw.write("BIDS: \n");

			@SuppressWarnings("serial")
			class DrawPanel extends JPanel{
				private BufferedImage bi;
				public BufferedImage getBufferedImage(){
					return bi;
				}
				public void drawBI(){
					ImageIcon i = item.getImage();
					String name = item.getVendorID()+item.getItemID();	
					try {
						if(item.getImage()==null){
							bi=ImageIO.read(new File("happiness.jpg"));
							ImageIO.write(bi,"png" ,new File(itemData.getAbsolutePath()+"/"+name+".png"));
						}else{
							bi=convertToBufferedImage(i);
							ImageIO.write(bi,"png",new File(itemData.getAbsolutePath()+"/"+name+".png"));
						}
						this.paint(bi.getGraphics());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				private BufferedImage convertToBufferedImage(ImageIcon i){
					BufferedImage bi = new BufferedImage(
							Item.IMAGE_WIDTH,
							Item.IMAGE_HEIGHT,
							BufferedImage.TYPE_INT_RGB);
					Graphics g = bi.createGraphics();
					i.paintIcon(null, g, 0,0);
					g.dispose();
					return bi;
				}
			};
			DrawPanel dp = new DrawPanel();
			dp.drawBI();
			item.setImage(dp.getBufferedImage());
			items.add(item);								//add to list of items
			openAuctions.add(item);							//add as an item which has an OPEN status
			bw.flush();
			bw.close();
			logActivity("NEW ITEM auctioned - "+item.getTitle()+" FROM "+item.getVendorID()
					+" SAVED AS "+item.getVendorID()+item.getItemID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void addAuthentication(String username,String hash){
		authentication.put(username, hash);
	}
	public List<User> getRegistered() {
		return registered;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public Map<String, ArrayList<Item>> getOwned() {
		return owned;
	}
	public Map<String, String> getAuthentication() {
		return authentication;
	}
	public File getData() {
		return data;
	}
	public File getUserData() {
		return userData;
	}
	public boolean authenticate(String userID, String password){
		return authentication.get(userID).equals(password);
	}
	public Item getItem(int itemID) {

		for(Item i : items){
			if(i.getItemID() == itemID){
				return i;
			}
		}
		return null;
	}
	public ArrayList<Text> getMail(String userID) {

		return mailbox.get(userID);
	}
	public ArrayList<Item> getOpen() {
		return openAuctions;
	}
	public boolean isValidMail(String src) {
		return mailbox.containsKey(src);
	}
	public ArrayList<Item> getBids(String src) {
		return bids.get(src);
	}
	public void produceItemLog(String text) {
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList((text.split("\n"))));
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(activityLogData.getPath()
					+"/"+"itemLog"+new Date(System.currentTimeMillis())));
			for(String line: lines){
				if(line.contains("CLOSED by reaching")){
					bw.write(line);
				}
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
