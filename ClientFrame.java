
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.NoSuchElementException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
/**
 * ClientFrame is the main GUI for the 
 * Client to interact with, all the listeners
 * are attached in the Client class.
 */
public class ClientFrame extends JFrame{
	
	private static final long serialVersionUID = -2397974408211479121L;
	private ArrayList<Text> mail;
	private MainClientPanel mainClientPanel;
	private LoginPanel loginPanel;
	private AuctionPanel auctionPanel;
	private ItemViewPanel itemViewPanel;
	private MailPanel mailPanel;
	private RegisterPanel registerPanel;
	private SendMailPanel sendMailPanel;
	private ViewMailPanel viewMailPanel;
	public static SimpleDateFormat ddt;
	public static final Font FONT = new Font("Helvetica", Font.BOLD, 18);

	public ClientFrame() {
		super("Online Auction Client");
		ddt = new SimpleDateFormat("dd/MM/yy HH:mm");
		viewMailPanel = new ViewMailPanel(new Text("", "", "", ""));
		loginPanel = new LoginPanel();
		auctionPanel = new AuctionPanel();
		itemViewPanel = new ItemViewPanel(new Item("", "", "", 1, 1, new ArrayList<String>()));
		mailPanel = new MailPanel();
		registerPanel = new RegisterPanel();
		sendMailPanel = new SendMailPanel();
		//viewMailPanel = new ViewMailPanel(null);
		mainClientPanel = new MainClientPanel();
		mail = new ArrayList<Text>();
		JPanel contentPane = new JPanel();
		contentPane.add(loginPanel);
		setContentPane(contentPane);
		pack();
	}

	public void addRegisterListener(ActionListener r){
		loginPanel.getRegister().addActionListener(r);
	}
	public ArrayList<Text> getMail() {
		return mail;
	}
	public MainClientPanel getMainClientPanel() {
		return mainClientPanel;
	}
	public LoginPanel getLoginPanel() {
		return loginPanel;
	}
	public AuctionPanel getAuctionPanel() {
		return auctionPanel;
	}
	public ItemViewPanel getItemViewPanel() {
		return itemViewPanel;
	}
	public MailPanel getMailPanel() {
		return mailPanel;
	}
	public RegisterPanel getRegisterPanel() {
		return registerPanel;
	}
	public SendMailPanel getSendMailPanel() {
		return sendMailPanel;
	}
	public ViewMailPanel getViewMailPanel(Text txt) {
		viewMailPanel.setText(txt);
		return viewMailPanel;
	}
	public Font getFont() {
		return FONT;
	}
	public void setMail(ArrayList<Text> mail) {
		this.mail = mail;
		mailPanel.updateMail(mail);
	}
	public void setMainClientPanel(MainClientPanel mainClientPanel) {
		this.mainClientPanel = mainClientPanel;
	}
	public void setLoginPanel(LoginPanel loginPanel) {
		this.loginPanel = loginPanel;
	}
	public void setAuctionPanel(AuctionPanel auctionPanel) {
		this.auctionPanel = auctionPanel;
	}
	public void setItemViewPanel(ItemViewPanel itemViewPanel) {
		this.itemViewPanel = itemViewPanel;
	}
	public void setMailPanel(MailPanel mailPanel) {
		this.mailPanel = mailPanel;
	}
	public void setRegisterPanel(RegisterPanel registerPanel) {
		this.registerPanel = registerPanel;
	}
	public void setSendMailPanel(SendMailPanel sendMailPanel) {
		this.sendMailPanel = sendMailPanel;
	}
	public void setViewMailPanel(ViewMailPanel viewMailPanel) {
		this.viewMailPanel = viewMailPanel;
	}
	public void addToMailbox(Text msg){
		mail.add(msg);
		mailPanel.addMail(msg);
	}
	public void setMailBox(ArrayList<Text> mail){
		this.mail=mail;
	}

	public void init(){

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void changeContentPane(JPanel pan){
		JPanel contentPane = (JPanel) this.getContentPane();
		this.setSize(pan.getPreferredSize() );
		contentPane.removeAll();
		contentPane.add(pan);
		contentPane.revalidate(); 
		contentPane.repaint();
	}
	/*
	 * Invoked in Client by passing an ArrayList of items
	 */
	public void updateItemList(final ArrayList<Item> items){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	mainClientPanel.populateItemPanel(items);
		    }
		});
		
	}
	public Text getMailAt(int selectedIndex) {
		return mail.get(selectedIndex);
	}
	public void removeMailAt(int selectedIndex){
		mail.remove(selectedIndex);
	}

	public String getCategory() {
		// TODO Auto-generated method stub
		return (String)mainClientPanel.getSearchCriteria().getSelectedItem();
	}

	public ViewMailPanel getViewMailPanel() {
		return viewMailPanel;
	}
}
/*
 * Logging in the Client
 */
class LoginPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7679450110761918205L;
	private JTextField username;
	private JPasswordField pass;
	private JLabel usernameLabel,password,well,come;
	private JButton login, register;
	private GridBagConstraints c;
	public LoginPanel(){
		c= new GridBagConstraints();
		well = new JLabel("Welcome to the");
		come = new JLabel("Online Auction System");
		usernameLabel = new JLabel("Username:");
		password= new JLabel("Password:");
		username = new JTextField(10);
		pass = new JPasswordField(10);
		login = new JButton("Login");
		register = new JButton("Register");
		setLayout(new GridBagLayout());
		init();
	}
	public void init(){
		c.weighty=0.0;
		c.gridwidth=1;
		c.gridheight=1;
		c.anchor = GridBagConstraints.CENTER;
		setPreferredSize(new Dimension(400,600));

		well.setFont(ClientFrame.FONT);
		add(well,c);
		c.gridy=1;
		come.setFont(ClientFrame.FONT);
		add(come,c);
		c.gridy=2;
		add(username,c);
		c.gridy=3;
		add(username,c);
		c.gridy=4;
		add(password,c);
		c.gridy=5;
		add(pass,c);
		c.gridy=6;
		JPanel dummy = new JPanel();
		dummy.setLayout(new FlowLayout());
		dummy.add(login);
		dummy.add(register);
		add(dummy,c);

	}
	public JTextField getUsername() {
		return username;
	}
	public JPasswordField getPass() {
		return pass;
	}
	public JLabel getUsernameLabel() {
		return usernameLabel;
	}
	public JLabel getPassword() {
		return password;
	}
	public JLabel getWell() {
		return well;
	}
	public JLabel getCome() {
		return come;
	}
	public JButton getLogin() {
		return login;
	}
	public JButton getRegister() {
		return register;
	}

}
/*
 * Registering the client
 */
class RegisterPanel extends JPanel{
	private static final long serialVersionUID = -1281794693992186729L;
	private JLabel fName,lName,pass,confirmPass,registration,user;
	private JTextField firstName,lastName,username;
	private JPasswordField password,confirmPassword;
	private JButton submit,cancel;
	private GridBagConstraints c;
	public RegisterPanel(){
		c= new GridBagConstraints();
		registration= new JLabel("Registration:");
		fName = new JLabel("First Name:");
		lName = new JLabel("Last Name:");
		pass = new JLabel("Password:");
		user = new JLabel("Username:");
		confirmPass = new JLabel("Confirm Password:");
		username= new JTextField(10);
		firstName = new JTextField(10);
		lastName = new JTextField(10);
		password = new JPasswordField(10);
		confirmPassword = new JPasswordField(10);
		submit = new JButton("Submit");
		cancel = new JButton("Cancel");
		init();
	}
	public void init(){
		setPreferredSize(new Dimension(400,600));
		setLayout(new GridBagLayout());
		registration.setFont(ClientFrame.FONT);
		c.anchor= GridBagConstraints.CENTER;
		c.weighty=0.0;
		c.weightx=0.0;
		add(registration,c);
		c.gridy=1;

		add(fName,c);
		c.gridy=2;

		add(firstName,c);
		c.gridy=3;

		add(lName,c);
		c.gridy=4;

		add(lastName,c);
		c.gridy=5;

		add(user,c);
		c.gridy=6;

		add(username,c);
		c.gridy=7;

		add(pass,c);
		c.gridy=8;

		add(password,c);
		c.gridy=9;

		add(confirmPass,c);
		c.gridy=10;

		add(confirmPassword,c);
		c.gridy=11;

		JPanel dummy = new JPanel();
		dummy.setLayout(new FlowLayout());
		dummy.add(submit);
		dummy.add(cancel);
		add(dummy,c);
	}
	public JLabel getfName() {
		return fName;
	}
	public JLabel getlName() {
		return lName;
	}
	public JLabel getPass() {
		return pass;
	}
	public JLabel getConfirmPass() {
		return confirmPass;
	}
	public JLabel getRegistration() {
		return registration;
	}
	public JLabel getUser() {
		return user;
	}
	public JTextField getFirstName() {
		return firstName;
	}
	public JTextField getLastName() {
		return lastName;
	}
	public JTextField getUsername() {
		return username;
	}
	public JPasswordField getPassword() {
		return password;
	}
	public JPasswordField getConfirmPassword() {
		return confirmPassword;
	}
	public JButton getSubmit() {
		return submit;
	}
	public JButton getCancel() {
		return cancel;
	}

}
/*
 * Main operational panel for 
 * the user
 */
class MainClientPanel extends JPanel{

	private static final long serialVersionUID = -8422022409815078496L;
	private JButton auctionItem,search,close,mailBox,viewItem,refresh;
	private JComboBox<String> searchCriteria;
	private JTextField searchField;
	private String[] category = {"--- Select Criteria for Search ---","All","Seller",
			"Category","ItemID","Start Date","Description","My Auctions","My Bids"};
	private GridBagConstraints c;
	private JPanel itemPanel;
	private JList<String> list;
	private DefaultListModel<String> dlm;
	private ArrayList<Item> items;
	public MainClientPanel(){
		c= new GridBagConstraints();
		dlm = new DefaultListModel<String>();
		list = new JList<String>(dlm);
		mailBox= new JButton("Check Mail");
		viewItem = new JButton("View Item");
		search = new JButton("Search");
		refresh = new JButton("Refresh");
		auctionItem = new JButton("Auction Item");
		close = new JButton("Close");
		searchField = new JTextField(30);
		searchCriteria = new JComboBox<String>(category);
		itemPanel = new JPanel();
		this.setPreferredSize(new Dimension(700,300));
		itemPanel.setPreferredSize(new Dimension(800,600));
		init();
	}
	/*
	 * click to be made to select and further get info
	 * for the particular item
	 * 
	 * clicking on an item switches the panel
	 */
	public void init(){
		list.setAutoscrolls(true);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(10);
		list.setPreferredSize(new Dimension(300,200));

		itemPanel.setLayout(new GridBagLayout());
		setLayout(new GridBagLayout());
		JPanel dummy = new JPanel();
		dummy.setLayout(new GridLayout(6,1));
		dummy.add(new JLabel("Options"));
		dummy.add(searchField);
		dummy.add(search);
		dummy.add(searchCriteria);
		JPanel twoButtons = new JPanel();
		twoButtons.add(mailBox);
		twoButtons.add(refresh);
		dummy.add(twoButtons);

		JPanel trippleButton = new JPanel();
		trippleButton.add(auctionItem);
		trippleButton.add(viewItem);
		trippleButton.add(close);

		dummy.add(trippleButton);
		c.weightx=0.0;
		c.weighty=0.0;

		itemPanel.add(list,c);
		c.gridy= 1;
		add(dummy,c);
		c.gridx= 2;
		dlm.addElement("Status     Title     owner     expires     ");

		JScrollPane jsp = new JScrollPane(list);
		add(jsp,c);

	}
	public JButton getAuctionItem() {
		return auctionItem;
	}
	public JButton getSearch() {
		return search;
	}
	public JButton getClose() {
		return close;
	}
	public JButton getMailBox() {
		return mailBox;
	}
	public JButton getViewItem() {
		return viewItem;
	}
	public JButton getRefresh() {
		return refresh;
	}
	public JComboBox<String> getSearchCriteria() {
		return searchCriteria;
	}
	public JTextField getSearchField() {
		return searchField;
	}
	public String[] getCategory() {
		return category;
	}
	public GridBagConstraints getC() {
		return c;
	}
	public JPanel getItemPanel() {
		return itemPanel;
	}
	public JList<String> getList() {
		return list;
	}
	public DefaultListModel<String> getDlm() {
		return dlm;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	
	//TODO Check this method and how items will be introduced
	public synchronized void populateItemPanel(ArrayList<Item> items){
		if(items==null){
			dlm.clear();
			dlm.addElement("Status  |   Title   |   owner   |   expires     ");
			return;
		}
		this.items = items;
		dlm.clear();
		dlm.addElement("Status  |   Title   |   owner   |   expires     ");
		for(Item i:items){
			String str;
			if(i.isOpen())
				str ="Open |"+i.getTitle()+" |"+i.getVendorID()+"|"+ClientFrame.ddt.format(i.getCloseTime());
			else
				str ="Closed |"+i.getTitle()+" |"+i.getVendorID()+"|"+ClientFrame.ddt.format(i.getCloseTime());
			dlm.addElement(str);
		}

	}	
}
/*
 * AuctionPanel used for submitting items
 */
class AuctionPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5320787608488890956L;
	private JTextField title,closeTime,reservePrice,keyword;
	private JLabel labelTitle,labelCloseTime,labelReservePrice,labelKeyword,labelDescription;
	private JTextArea description;
	//private int vendorID,itemID; will be chosen from Client
	private JButton browseForImage,submit,cancel;
	private GridBagConstraints c;
	private JPanel leftPanel,rightPanel;
	private JScrollPane jsp;
	private JFileChooser fileChooser;
	private BufferedImage bufferedImage=null;
	public AuctionPanel(){
		fileChooser = new JFileChooser();
		leftPanel= new JPanel();
		rightPanel = new JPanel();
		c = new GridBagConstraints();
		title = new JTextField(12);
		closeTime = new JTextField(12);
		reservePrice = new JTextField(12);
		keyword = new JTextField(12);
		description = new JTextArea(5, 12);
		labelCloseTime = new JLabel("Duration of Auction (hours)");
		labelDescription = new JLabel("Item Description");
		labelKeyword = new JLabel("category(e.g.\"home,garden\")");
		labelReservePrice = new JLabel("Reserved Price");
		labelTitle = new JLabel("Title");
		browseForImage = new JButton("Choose Image");
		submit = new JButton("Submit");
		cancel = new JButton("Cancel");
		jsp = new JScrollPane(description);
		init();
	}
	public void init(){
		this.setPreferredSize(new Dimension(500,500));
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"JPG & PNG Images", "jpg", "png");
		fileChooser.setFileFilter(filter);
		setLayout(new GridBagLayout());
		leftPanel.setLayout(new GridBagLayout());
		rightPanel.setLayout(new GridBagLayout());
		c.gridx=1;
		leftPanel.add(labelTitle,c);
		rightPanel.add(labelReservePrice,c);
		c.gridy=2;
		leftPanel.add(title,c);
		rightPanel.add(reservePrice,c);
		c.gridy=3;
		leftPanel.add(labelKeyword,c);
		rightPanel.add(labelCloseTime,c);
		c.gridy=4;
		leftPanel.add(keyword,c);
		rightPanel.add(closeTime,c);
		c.gridy=5;
		JPanel dummy = new JPanel();
		leftPanel.add(labelDescription,c);
		rightPanel.add(browseForImage,c);
		c.gridy=6;
		leftPanel.add(jsp,c);
		dummy.add(submit);
		dummy.add(cancel);
		rightPanel.add(dummy,c);

		add(leftPanel);
		add(rightPanel);
	}
	public JTextField getTitle() {
		return title;
	}
	public JTextField getCloseTime() {
		return closeTime;
	}
	public JTextField getReservePrice() {
		return reservePrice;
	}
	public JTextField getKeyword() {
		return keyword;
	}
	public JLabel getLabelTitle() {
		return labelTitle;
	}
	public JLabel getLabelCloseTime() {
		return labelCloseTime;
	}
	public JLabel getLabelReservePrice() {
		return labelReservePrice;
	}
	public JLabel getLabelKeyword() {
		return labelKeyword;
	}
	public JLabel getLabelDescription() {
		return labelDescription;
	}
	public JTextArea getDescription() {
		return description;
	}
	public JButton getBrowseForImage() {
		return browseForImage;
	}
	public JButton getSubmit() {
		return submit;
	}
	public JButton getCancel() {
		return cancel;
	}
	public GridBagConstraints getC() {
		return c;
	}
	public JPanel getLeftPanel() {
		return leftPanel;
	}
	public JPanel getRightPanel() {
		return rightPanel;
	}
	public JScrollPane getJsp() {
		return jsp;
	}
	public JFileChooser getFileChooser() {
		return fileChooser;
	}
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
	public void setBufferedImage(BufferedImage bi) {
		this.bufferedImage = bi;
	}
}

class ItemViewPanel extends JPanel{

	private static final long serialVersionUID = 7580126616452082595L;

	private Item item;
	private ImageIcon imgIcon;
	private JPanel central,right;
	private JLabel title,vendorID,highestBid,startTime,closeTime,keywords;
	private JTextArea description;
	private JButton bid,back,cancelAuction;
	private JScrollPane jsp;
	private JTextField bidPrice;
	private ImagePanel imagePanel;
	private GridBagConstraints c;

	public ItemViewPanel(Item item) {
		this.item = item;
		c= new GridBagConstraints();
		
		imgIcon= item.getImage();
		imagePanel = new ImagePanel(imgIcon);
		back = new JButton("Back");
		cancelAuction= new JButton ("Cancel Auction");
		right = new JPanel();
		central = new JPanel();
		bid = new JButton("Bid");
		bidPrice=  new JTextField(8);
		setPanelStatistics();
		jsp = new JScrollPane(description);
		setLayout(new GridBagLayout());
		init();
	}
	public void init(){
		GridBagLayout gbl = new GridBagLayout();
		central.setLayout(gbl);
		right.setLayout(gbl);

		central.add(title,c);
		right.add(new JLabel(" "),c);
		c.gridy=1;
		
		central.add(vendorID,c);
		right.add(startTime,c);
		c.gridy=2;

		central.add(jsp,c);
		right.add(highestBid,c);
		c.gridy=3;

		right.add(keywords,c);
		c.gridy=4;

		JPanel dummy = new JPanel();
		dummy.add(bid);
		dummy.add(bidPrice);
		right.add(dummy,c);
		c.gridy=5;
		JPanel twoButtons = new JPanel();
		twoButtons.setLayout(gbl);
		c.gridy=0;
		c.gridx=0;
		twoButtons.add(back,c);
		c.gridx=1;
		twoButtons.add(cancelAuction,c);
		c.gridx=0;
		c.gridy=6;
		right.add(twoButtons,c);

		add(imagePanel);
		add(central);
		add(right);
	}
	public Item getItem() {
		return item;
	}
	public ImageIcon getImageIcon() {
		return imgIcon;
	}
	public JPanel getCentral() {
		return central;
	}
	public JPanel getRight() {
		return right;
	}
	public JLabel getTitle() {
		return title;
	}
	public JTextArea getDescription() {
		return description;
	}
	public JLabel getVendorID() {
		return vendorID;
	}
	public JLabel getHighestBid() {
		return highestBid;
	}
	public JLabel getStartTime() {
		return startTime;
	}
	public JLabel getCloseTime() {
		return closeTime;
	}
	public JLabel getKeywords() {
		return keywords;
	}
	public JButton getBid() {
		return bid;
	}
	public JButton getBack() {
		return back;
	}
	public JButton getCancelAuction(){
		return cancelAuction;
	}
	public JTextField getBidPrice() {
		return bidPrice;
	}
	public ImagePanel getImagePanel() {
		return imagePanel;
	}
	public GridBagConstraints getC() {
		return c;
	}
	public void setPanelStatistics(){
		Font f = new Font("Helvetica",Font.BOLD,18);
		title= new JLabel(item.getTitle());
		title.setFont(f);
		description= new JTextArea(6,10);
		description.setEditable(false);
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		vendorID= new JLabel("Vendor ID: "+item.getVendorID());
		startTime= new JLabel("Start time:"+item.getStartTime());
		closeTime= new JLabel("Close time:"+item.getCloseTime());
		highestBid = new JLabel("Highest Bid: ");
		String s = "";
		for(int i=0;i<item.getKeywords().size();i++){
			s=s+item.getKeywords().get(i)+",";
		}
		keywords= new JLabel("Keywords: "+s);
	}
	public void setItem(Item item) {
		
		this.item = item;
		description.setText("Description: \n"+item.getDescription());
		title.setText(item.getTitle());
		vendorID.setText("Vendor ID: " +item.getVendorID() );
		startTime.setText("Start time:"+item.getStartTime());
		closeTime.setText("Close time:"+item.getCloseTime());
		try{
			imagePanel.setIcon(item.getImage());
			imgIcon=item.getImage();
		}catch(NullPointerException e){
			imagePanel.setIcon();
			this.validate();
		}
		String s = "";
		for(int i=0;i<item.getKeywords().size();i++){
			if(i==item.getKeywords().size()-1)
				s=s+item.getKeywords().get(i);
			else
				s=s+item.getKeywords().get(i)+",";
		}
		keywords.setText("Keywords: "+s);
		try{
			highestBid .setText("Highest Bid: "+item.getHighestBid().getElementTwo());
		}catch(NoSuchElementException e){
			highestBid .setText("Highest Bid: 0");
			return;
		}
	}


}


class MailPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6354556473384057606L;
	private JButton send, cancel;
	private JLabel mail;
	private JList<String> list;
	private DefaultListModel<String> dlm;
	private JScrollPane jsp;
	private JPanel right;
	private GridBagConstraints c;
	public MailPanel(){
		c= new GridBagConstraints();
		right = new JPanel();
		send = new JButton("Send Message");

		cancel = new JButton("   Cancel   ");
		dlm = new DefaultListModel<String>();
		list = new JList<String>(dlm);
		jsp = new JScrollPane(list);
		mail = new JLabel("Mail");
		init();
	}
	public void init(){
		jsp.setPreferredSize(new Dimension(200,200));
		list.setFixedCellWidth(20);
		right.setPreferredSize(new Dimension(300,300));
		JPanel left = new JPanel();
		left.setLayout(new GridLayout(3,1));
		left.add(send);
		left.add(cancel);
		list.setAutoscrolls(true);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(10);
		
		add(left);
		add(jsp);
		add(right);
	}
	public void addMail(Text msg) {
		dlm.addElement(msg.getUserID()+"   -   "+msg.getTitle());
	}
	public void updateMail(ArrayList<Text> mail) {
		dlm.clear();
		for(int i =0; i<mail.size();i++){
			Text txt = mail.get(i);
			dlm.addElement(txt.getUserID()+"  -  "+txt.getTitle());
		}

	}
	public JButton getSend() {
		return send;
	}
	public JButton getCancel() {
		return cancel;
	}
	public JLabel getMail() {
		return mail;
	}
	public JList<String> getList() {
		return list;
	}
	public DefaultListModel<String> getDlm() {
		return dlm;
	}
	public JScrollPane getJsp() {
		return jsp;
	}
	public JPanel getRight() {
		return right;
	}
	public GridBagConstraints getC() {
		return c;
	}
}
class ViewMailPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5893282394872021403L;
	private JLabel fromLabel,titleLabel,descriptionLabel;
	private JTextField from,title;
	private JTextArea description;
	private JScrollPane jsp;
	private Text text;
	private GridBagConstraints c;
	public ViewMailPanel(Text text){
		c= new GridBagConstraints();
		this.text = text;
		fromLabel = new JLabel("From:");
		titleLabel = new JLabel("Title:");
		descriptionLabel = new JLabel("Description:");
		from = new JTextField(text.getUserID());
		title = new JTextField(text.getTitle());
		description = new JTextArea(text.getText(), 10, 25);
		jsp = new JScrollPane(description);
		init();
	}
	public void init(){		
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		from.setEditable(false);
		title.setEditable(false);
		description.setEditable(false);
		setLayout(new GridBagLayout());
		add(titleLabel,c);
		c.gridy=1;
		add(title,c);
		c.gridy=2;
		add(fromLabel,c);
		c.gridy=3;
		add(from,c);
		c.gridy=4;
		add(descriptionLabel,c);
		c.gridy=5;
		add(jsp ,c);

	}
	public JLabel getFromLabel() {
		return fromLabel;
	}
	public JLabel getTitleLabel() {
		return titleLabel;
	}
	public JLabel getDescriptionLabel() {
		return descriptionLabel;
	}
	public JTextField getFrom() {
		return from;
	}
	public JTextField getTitle() {
		return title;
	}
	public JTextArea getDescription() {
		return description;
	}
	public JScrollPane getJsp() {
		return jsp;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text txt){
		this.text = txt;
	}
	public GridBagConstraints getC() {
		return c;
	}

}
class SendMailPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4396120822549591151L;
	private JLabel toLabel,titleLabel,descriptionLabel;
	private JTextField destination,title;
	private JTextArea description;
	private JScrollPane jsp;
	private JButton send,cancel;
	private GridBagConstraints c;
	public SendMailPanel(){
		c= new GridBagConstraints();
		toLabel = new JLabel("To:");
		titleLabel = new JLabel("Title:");
		cancel = new JButton("Cancel");
		descriptionLabel = new JLabel("Description");
		destination = new JTextField(10);
		title = new JTextField(10);
		description = new JTextArea(10,25);
		jsp = new JScrollPane(description);
		send = new JButton("Send");
		setPreferredSize(new Dimension(500,500));
		init();
	}
	public void init(){
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		setLayout(new GridBagLayout());
		add(titleLabel,c);
		c.gridy=1;
		add(title,c);
		c.gridy=2;
		add(toLabel,c);
		c.gridy=3;
		add(destination,c);
		c.gridy=4;
		add(descriptionLabel,c);
		c.gridy=5;
		add(jsp ,c);
		c.gridy=6;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		add(send,c);
		c.gridx = 2;
		add(cancel,c);
	}

	public JLabel getToLabel() {
		return toLabel;
	}
	public JLabel getTitleLabel() {
		return titleLabel;
	}
	public JLabel getDescriptionLabel() {
		return descriptionLabel;
	}
	public JTextField getDestination() {
		return destination;
	}
	public JTextField getTitle() {
		return title;
	}
	public JTextArea getDescription() {
		return description;
	}
	public JScrollPane getJsp() {
		return jsp;
	}
	public JButton getSend() {
		return send;
	}
	public GridBagConstraints getC() {
		return c;
	}
	public JButton getCancel() {
		return cancel;
	}

}