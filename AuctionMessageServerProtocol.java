import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;

/*
 * Class which identifies all the different types
 * of actions taken against the messages received
 * from the server
 */
public class AuctionMessageServerProtocol {
	/*
	 * Logs and redirects the message if possible, and
	 * saves it in the local file system
	 */
	public static void handleTextMessage(Text msg,Server server){
		try {
			String src = msg.getUserID();
			server.logActivity("Handling TEXT from "+src);
			if(!server.isValidMail(msg.getDestinationID())){
				server.logActivity("User "+src+" tried to SEND MAIL to a NON-EXISTING USER on  the system");
				server.sendMessage(src, new ErrorMessage("This user does not exist on the system, " +
						"please check the ID you provided"));
				return;
			}
			server.addMail(msg);
			if(server.sendMessage(msg.getDestinationID(),(Message)msg)){
				server.logActivity("TEXT sent successfully to destination");
				server.sendMessage(src, new ErrorMessage("Message sent successfully"));
				server.sendMessage(msg.getDestinationID(), new ErrorMessage("You received a message"));
			}else{
				server.logActivity("TEXT sending failed, saved in file system");
				server.sendMessage(src, new ErrorMessage("The user is currently offline, but will receive notification " +
						"\n about your message"));
			}
		} catch (FileNotFoundException e) {
			server.sendMessage(msg.getUserID(), new ErrorMessage("This user does not exist"));
		}
	}
	/*
	 * Authenticates the User hash and replies
	 * wheather it was successful 
	 */
	public static void handleAuthenticationMessage(Authentication msg,Server server){
		String src = msg.getUserID();
		server.logActivity("Handling AUTHENTICATION from "+src);
		try{
			if(server.authenticate(src,msg.getPassword())){
				msg.approve();
				if (server.sendMessage(src, msg)){
					server.logActivity("AUTHENTICATION RESPONSE sent to "+msg.getUserID()+" - success");
					DisplayAuctions da = new DisplayAuctions(src);
					da.setItems(server.getItems());
					server.sendMessage(src, da);
				}
			}else{
				if(server.sendMessage(msg.getUserID(), new ErrorMessage("Unable to login, please check your credentials")))
					server.logActivity("AUTHENTICATION RESPONSE sent to "+msg.getUserID()+" - failure");
			}
		}catch(NullPointerException e){
			server.sendMessage(msg.getUserID(), new ErrorMessage("Unable to login, please check your credentials"));
			server.logActivity("AUTHENTICATION RESPONSE sent to "+msg.getUserID()+" - failure");
		}
	}
	/*
	 * Checks if the information is available
	 */
	public static void handleRegistrationMessage(Registration msg,Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling REGISTRATION from "+src);
		User usr = new User(msg.getFirstName(), msg.getLastName(), src);
		try {
			server.registerUser(usr,msg.getPass());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			server.logActivity("FAILURE - writing files during REGISTRATION handling failed" +
					". Failure Trace:"+ e.getMessage());
		} catch (NamingException e) {
			server.sendMessage(src, new ErrorMessage("Name Already Taken, please pick a different one"));
			server.logActivity("ERROR MESSAGE sent to "+src+" REASON: registration \n username already exists");
		}
	}
	/*
	 * Replies with the latest updated version of the Auction
	 */
	public static void handleViewAuctionMessage(ViewAuction msg,Server server){
		String src = msg.getUserID();
		server.logActivity("Handling VIEW_AUCTION from "+src);
		try{

			Item item = server.getItem(msg.getItem().getItemID());
			server.logActivity("Item "+item.getVendorID()+item.getItemID());
			msg.setItem(item);
			server.sendMessage(src, msg);
			server.logActivity("VIEW AUCTION RESPONSE sent to "+src);
		}catch(NullPointerException e){
			server.logActivity("ERROR MESSAGE sent to "+src+" REASON: item not found in items");
			server.sendMessage(src, new ErrorMessage("Item not found on system"));
		}

	}
	/*
	 * Checks if the bid is within system requirements
	 * different outcomes based on the bid of the user
	 * -successful
	 * -bid not high enough
	 * -auction closed
	 */
	public static void handleBidAuctionMessage(BidAuction msg,Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling BID AUCTION from "+src);
		try {
			server.addBid(msg.getItem(),new Pair<String,Double>(src,msg.getBid()));
			Item i = server.getItem(msg.getItem().getItemID());
			server.sendMessage(src, new ViewAuction(src,i ));
			server.logActivity("Sending RESPONSE to "+src+" for BID AUCTION with latest details for the item");
			server.sendMessage(src, new ErrorMessage("Bid was successfull"));
		} catch (NumberFormatException e) {
			// bid lower than highest bid
			server.sendMessage(src, new ErrorMessage("Your bid is lower than the highest bid"));
			server.logActivity("ERROR MESSAGE sent to "+src+" REASON: bid LOWER than HIGHEST bid");
		} catch (FileNotFoundException e) {
			// item file not found
			server.logActivity("FAILURE, item file not found - TRACE: "+e.getMessage());
		} catch (NullPointerException e) {
			e.printStackTrace();
			server.sendMessage(src, new ErrorMessage("The auction you are trying to bid on has been closed"));
			server.logActivity("ERROR MESSAGE sent to "+src+" REASON: auction was CLOSED");
		} catch (IOException e) {
			// reading writing problems
			server.logActivity("FAILURE, reading writing problems - TRACE: "+e.getMessage());
		}
	}
	/*
	 * Cancels auction and penalises the user if he cancelled the bid 
	 * after the reserve price was met
	 */
	public static void handleCancelAuctionMessage(CancelAuction msg,Server server){
		server.logActivity("Handling CANCEL AUCTION from "+msg.getUserID());
		if(msg.getItem().isOpen()==false){
			server.logActivity("USER "+msg.getUserID()+" tried to CLOSE an Auction which is already closed");
			server.sendMessage(msg.getUserID(), new ErrorMessage("You can not close an item which" +
					" is already closed"));
			return;
		}
		try {
			if(msg.getItem().getVendorID().equals(msg.getUserID())){
				
				if(server.cancelAuction(msg.getItem()))
					server.sendMessage(msg.getUserID(), new ErrorMessage("Successfully canceled" +
							" the item, penalized for canceling"));
				else
					server.sendMessage(msg.getUserID(), new ErrorMessage("Successfully canceled " +
							"the item,no penalty"));
			}else
				server.sendMessage(msg.getUserID(), new ErrorMessage("Cancel failed, you are not " +
						"the owner of this item"));
		} catch (IOException e) {
			server.logActivity("FAILURE, reading writing problems - TRACE: "+e.getMessage());
		}
	}
	/*
	 * creates an auction saves it into the data system
	 */
	public static void handleCreateAuctionMessage(CreateAuction msg,Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling CREATE AUCTION from "+src);
		
		if(server.getUser(src).getPenaltyPoints()>2){
			server.sendMessage(src, new ErrorMessage("Creation was not successfull," +
					"you have over 2 penalty points"));
			server.logActivity("NOTIFICATION sent to "+src+" REASON: user exceeds PENALTY POINTS");
			return;
		}
		
		
		server.createAuction(msg.getItem());
		server.sendMessage(src, new ErrorMessage("Creation was successfull"));
		server.logActivity("NOTIFICATION sent to "+src+" REASON: auction CREATED");
	}
	/*
	 * Sends a Disconnect message to the Server when the user leaves
	 */
	public static void handleDisconnectMessage(DisconnectMessage msg,Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling DISCONNECT from "+src);
		server.disconnect(msg.getUserID());
		server.logActivity("User "+src+" has disconnected");
	}
	/* Refreshes the list of items in the MainClientPanel
	 * with the following items based on the search option:
	 * All
	 * Seller
	 * Category
	 * ItemID
	 * Date
	 * Description 	
	 * My Auctions
	 * My Bids
	 */
	public static void handleDisplayAuctionsMessage(DisplayAuctions msg,Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling DISPLAY AUCTIONS from "+src);
		switch(msg.getSearchOption()){
		case "All":
			DisplayAuctions da = new DisplayAuctions(src);
			da.setItems(server.getItems());
			server.sendMessage(src, da);
			server.logActivity("RESPONSE sent to User "+src+ " with ALL items");
			break;
		case "Seller":
			msg.setItems(server.getItemsSeller(msg.getCriteria()));
			server.logActivity("RESPONSE sent to User "+src+ " with items by SELLER "+msg.getCriteria());
			server.sendMessage(src, msg);
			break;
		case "Category":
			msg.setItems(server.getItemsCategory(msg.getCriteria()));
			server.logActivity("RESPONSE sent to User "+src+ " with items by Category");
			server.sendMessage(src, msg);
			break;
		case "ItemID":
			try{
				double criteria =Double.parseDouble(msg.getCriteria());
				msg.setItems(server.getItemsItemID(criteria));
				server.logActivity("RESPONSE sent to User "+src+ " with items by ItemID");
				server.sendMessage(src, msg);
			}catch(NumberFormatException e){
				server.logActivity("Incorrect criteria for user "+src+" expected number, received string");
				server.sendMessage(src, new ErrorMessage("Wrong input or criteria"));
			}
			break;
		case "Start Date":
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
			try {
				Date date = sdf.parse(msg.getCriteria());
				msg.setItems(server.getItemsDate(date));
				server.logActivity("RESPONSE sent to User "+src+ " with items by DATE");
				server.sendMessage(src, msg);
			} catch (ParseException e) {
				server.logActivity("Wrong DATE from "+src+" ERROR MESSAGE sent back");
				server.sendMessage(src,new ErrorMessage( "The format you gave for the date is wrong, format is : "+ sdf.toPattern()));
			}
			break;
		case "Description":
			msg.setItems(server.getItemsDescription(msg.getCriteria()));
			server.logActivity("RESPONSE sent to User "+src+ " with items by DESCRIPTION");
			server.sendMessage(src, msg);
			break;
		case "My Auctions":
			msg.setItems(server.getMyAuctions(src));
			server.logActivity("RESPONSE sent to User "+src+ " with items by MY AUCTIONS");
			server.sendMessage(src, msg);
			break;
		case "My Bids":
			msg.setItems(server.getMyBids(src));
			server.logActivity("RESPONSE sent to User "+src+ " with items by MY BIDS");
			server.sendMessage(src, msg);
			break;
		}

	}
	/*
	 * Retreives all the mails for the user in the file system
	 */
	public static void handleRequstMailMessage(RequestMail msg, Server server) {
		String src = msg.getUserID();
		server.logActivity("Handling REQUEST MAIL from "+src);
		msg.setMailbox(server.getMailbox(src));
		server.sendMessage(src,msg );

	}
}
