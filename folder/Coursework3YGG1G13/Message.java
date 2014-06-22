
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;



public abstract class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int TEXT =1;
	public static final int AUTHENTICATION =2;
	public static final int REGISTRATION =3;
	public static final int VIEW_AUCTION =4;
	public static final int BID_AUCTION =5;
	public static final int CANCEL_AUCTION =6;
	public static final int CREATE_AUCTION =7;
	public static final int DISPLAY_AUCTIONS =8;
	public static final int ERROR_MESSAGE =9;
	public static final int DISCONNECT =10;
	public static final int REQUEST_MAIL =11;
	
	
	protected int ID;
	protected String userID;
	public int getID(){
		return ID;
	}
	public String getUserID(){
		return userID;
	}
	protected String getMD5(String passwordToHash)
	{
		String hash = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(passwordToHash.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}
}
class Text extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title,text,destinationID;
	public Text(String userID, String destinationID, String title, String text){
		this.title=title;
		this.text=text;
		this.destinationID=destinationID;
		this.userID=userID;
		ID = TEXT;
	}
	public String getTitle() {
		return title;
	}
	public String getText() {
		return text;
	}
	public String getUserID() {
		return userID;
	}
	public String getDestinationID(){
		return destinationID;
	}
}
/*
 * Authentication will be required every time when
 * the user is logging in.
 */
class Authentication extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hash;
	private boolean success=false;
	public Authentication(String userID,char[] password){
		this.userID = userID;
		hash =getMD5(new String(password)) ;
		ID=AUTHENTICATION;
	}
	public String getPassword() {
		return hash;
	}
	public boolean isSuccessful(){
		return success;
	}
	public void approve(){
		success=true;
	}
}
/*
 * Registration will be sent out to the server
 * after that the user is returned to login so
 * he can try enter his new account details
 * and proceed to the MainClientPanel
 */
class Registration extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName,lastName;
	private String hash;
	public Registration(String firstName,String lastName,String userID, char[] pass){
		ID=REGISTRATION;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		hash = getMD5(new String(pass));
	}
	
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getPass(){
		return hash;
	}
}
/*
 * ViewAuction will be called from MainClientPanel on the
 * presented ITEM in the list of available items
 */
class ViewAuction extends Message{
	/**
	 * Requests latest info from server
	 */
	private static final long serialVersionUID = 1L;
	private Item item;
	
	public ViewAuction(String userID,Item item){
		ID=VIEW_AUCTION;
		this.item = item;
		this.userID= userID;
	}
	public Item getItem(){
		return item;
	}
	public void setItem(Item item){
		this.item= item;
	}
}
/*
 * BidAuction messages are invoked when
 * the user is in ItemViewPanel ,which
 * displays the most recent info about
 * the item in details.
 */
class BidAuction extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Item item;
	private double bid;
	public BidAuction(String userID,Item item, double bid){
		ID=BID_AUCTION;
		this.item = item;
		this.bid = bid;
		this.userID=userID;
	}
	public Item getItem(){
		return item;
	}
	public double getBid(){
		return bid;
	}
}
/*
 * CancelAuction messages are invoked in MainClientPanel
 * server responds with a DisplayAuctions message to
 * REFRESH the list of the GUI of every connected user
 * after the user receives this, it orders it's 
 * clientFrame to REPAINT()
 */
class CancelAuction extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Item item;
	public CancelAuction(String userID,Item item){
		ID=CANCEL_AUCTION;
		this.item = item;
		this.userID = userID;
	}
	public Item getItem(){
		return item;
	}
}
/*
 * CreateAuction is used when the user
 * is in AuctionItem panel and selects
 * submit for the current properties,
 * for the given item.
 */
class CreateAuction extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Item item;
	public CreateAuction(String userID,Item item){
		ID=CREATE_AUCTION;
		this.item = item;
		this.userID = userID;
	}
	public Item getItem(){
		return item;
	}
}
/*
 * DisplayAuctions is invoked as a message
 * on different occasions:
 * -change appears on server side and notification is required for users
 * -Refresh is called from MainClientPanel
 * -others
 */
class DisplayAuctions extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;
	private String criteria= "";
	private String searchOption= "All";
	public DisplayAuctions(String userID){
		this.userID = userID;
		ID=DISPLAY_AUCTIONS;
	}
	public DisplayAuctions(String userID,String criteria){
		this(userID);
		this.criteria = criteria;
	}
	public void setItems(ArrayList<Item> items){
		this.items= items;
	}
	public ArrayList<Item> getItems(){
		return items;
	}
	public String getCriteria(){
		return criteria;
	}
	public void setCriteria(String criteria){
		this.criteria = criteria;
	}
	public void setSearchOption(String category){
		this.searchOption= category;
	}
	public String getSearchOption(){
		return searchOption;
	}
}
/*
 * This message is a generic message from the server
 * which is sent out to the user and appears on a
 * JOptionPane as a popping out message.
 */
class ErrorMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	public ErrorMessage(String message){
		ID=ERROR_MESSAGE;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
class DisconnectMessage extends Message{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DisconnectMessage(String userID){
		ID=DISCONNECT;
		this.userID = userID;
	}
}
class RequestMail extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4141412812695781663L;
	private ArrayList<Text> mail;
	public RequestMail(String userID){
		ID=11;
		this.userID = userID;
	}
	public void setMailbox(ArrayList<Text> mail){
		this.mail = mail;
	}
	public ArrayList<Text> getMailbox(){
		return mail;
	}
}