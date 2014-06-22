import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Item implements Serializable , Comparable<Item>{
	/**
	 * 
	 */
	public static final int IMAGE_WIDTH = 300;
	public static final int IMAGE_HEIGHT = 300;
	private static final long serialVersionUID = -8002756972799855732L;
	private String title,description;
	private String vendorID,criteria;
	private Date startTime,closeTime;
	private double reservePrice;
	private int itemID;
	private boolean open = true;
	private ArrayList<String> keyword;
	private Stack<Pair<String,Double>> bids;
	private ImageIcon image=null;
	public Item(String title,String description, String vendorID,
			long end,double reservePrice,ArrayList<String> keyword){
		this(title,description,vendorID,reservePrice,keyword);
		long creationTime = System.currentTimeMillis();
		startTime = new Date(creationTime);
		this.closeTime = new Date(end*3600000+creationTime);
	}
	public Item(String title,String description, String vendorID,double reservePrice,ArrayList<String> keyword){
		this.title = title;
		this.description = description;
		this.vendorID = vendorID;
		this.reservePrice = reservePrice;
		this.keyword=keyword;
		bids = new Stack<Pair<String,Double>>();
	}
	public Item(String title,String description, String vendorID,double reservePrice,
			ArrayList<String> keyword,String path) throws IOException{
		this(title,description,vendorID,reservePrice,keyword);

		this.image =resize( ImageIO.read(new File(path)));
	}
	public Item(String title,String description, String vendorID,long num,
			double reservePrice,ArrayList<String> keyword,String path) throws IOException{
		this(title,description,vendorID,num,reservePrice,keyword);
		this.image =resize( ImageIO.read(new File(path)));
		
	}
	public Item(String title, String description, String userID, long end,
			double reservePrice, ArrayList<String> keyword,
			BufferedImage bufferedImage) {
		this(title,description,userID,end,reservePrice,keyword);
		if(bufferedImage == null)
			this.image=null;
		else
			this.image = resize(bufferedImage);
	}
	public void setItemID(int ID){
		this.itemID = ID;
	}
	public int getItemID(){
		return itemID;
	}
	public Pair<String,Double> getHighestBid(){
		return bids.lastElement();
	}
	public String getTitle() {
		return title;
	}
	public ArrayList<String> getKeywords(){
		return keyword;
	}
	public void addKeyword(String item){
		keyword.add(item);
	}
	public void addBid(Pair<String,Double> bid){
		bids.add(bid);
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public String getDescription() {
		return description;
	}
	public String getVendorID() {
		return vendorID;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getCloseTime() {
		return closeTime;
	}
	public double getReservePrice() {
		return reservePrice;
	}
	public ImageIcon getImage() {
		return image;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	public void setReservePrice(double reservePrice) {
		this.reservePrice = reservePrice;
	}
	public void setImage(BufferedImage image) {
		this.image = resize(image);
	}
	public void setImageIcon(ImageIcon image){
		this.image = image;
	}
	public Stack<Pair<String,Double>> getBids() {
		return bids;
	}
	public String getCriteria(){
		return criteria;
	}
	public void setCriteria(String criteria){
		this.criteria = criteria;
	}
	private ImageIcon  resize(BufferedImage  original) {


		BufferedImage resizedImage = new BufferedImage(IMAGE_HEIGHT, IMAGE_WIDTH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(original, 0, 0, IMAGE_HEIGHT, IMAGE_WIDTH, null);
		g.dispose();
		ImageIcon resizedImages=new ImageIcon(resizedImage);

		return resizedImages;
	}
	@Override
	public int compareTo(Item o) {
		return this.getCloseTime().compareTo(o.getCloseTime());
	}
}
