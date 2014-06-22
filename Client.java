import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
/*
 * Client is a class which connects a user to the Server,
 * it controls the user interaction with the Auction System
 * Client keeps a connection with the server, handles the messages
 * updates the GUI, and sends a response, if there is one
 */
import javax.swing.JOptionPane;


public class Client {
	private ClientFrame clientFrame;
	private String userID;
	private Comms communication;
	public Client(ClientFrame clientFrame) throws UnknownHostException, IOException{
		this.clientFrame = clientFrame;
		communication = new Comms(this);
		addListenersToClient();
		clientFrame.init();
		communication.acceptResponses();
	}
	public String getUserID(){
		return userID;
	}

	public void startClient(){
		while(true){
			try {
				/*
				 * Not all of the messages will be received by the client
				 * e.g.: Client doesn't receive Register requests
				 * only server does.
				 */
				Message msg =communication.receiveMessage();
				switch(msg.getID()){
				case Message.TEXT:
					AuctionMessageClientProtocol.handleTextMessage((Text) msg,this);
					break;
				case Message.AUTHENTICATION:
					AuctionMessageClientProtocol.handleAuthenticationMessage((Authentication) msg,this);
					break;
				case Message.VIEW_AUCTION:
					AuctionMessageClientProtocol.handleViewAuctionMessage((ViewAuction) msg,this);
					break;
				case Message.DISPLAY_AUCTIONS:
					AuctionMessageClientProtocol.handleDisplayAuctionsMessage((DisplayAuctions) msg,this);
					break;
				case Message.ERROR_MESSAGE:
					AuctionMessageClientProtocol.handleErrorMessage((ErrorMessage) msg,this);
					break;
				case Message.REQUEST_MAIL:
					AuctionMessageClientProtocol.handleRequestMailMessage((RequestMail) msg,this);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void addMail(Text text){
		clientFrame.addToMailbox(text);
	}
	public void setMail(ArrayList<Text> mail){
		clientFrame.setMail(mail);
	}
	public Comms getCommunication() {
		// TODO Auto-generated method stub
		return communication;
	}
	public ClientFrame getClientFrame(){
		return clientFrame;
	}
	public void setUserID(String userID){
		this.userID = userID;
	}
	public void addListenersToClient(){
		final LoginPanel lp = clientFrame.getLoginPanel();
		lp.getLogin().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				communication.sendMessage(new Authentication(lp.getUsername().getText(),lp.getPass().getPassword()));
			}

		});

		lp.getRegister().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				clientFrame.changeContentPane(clientFrame.getRegisterPanel());
			}

		});

		final RegisterPanel rp = clientFrame.getRegisterPanel();
		rp.getSubmit().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(rp.getUsername().getText().length()<5 || rp.getUsername().getText().length()>16){
					JOptionPane.showMessageDialog(clientFrame.getParent(),
							"Username must be between 5 and 16 charecters long");
					return;
				}else if(rp.getPassword().getPassword().length <8 ){
					JOptionPane.showMessageDialog(clientFrame.getParent(),
							"Your password needs to be atleast 8 charecters long,");
					return;
				}else if(!Arrays.equals(rp.getPassword().getPassword(), rp.getConfirmPassword().getPassword()) ){
					JOptionPane.showMessageDialog(clientFrame.getParent(), 
							"Error encountered, check your password/confirmation");
					return;
				}else if(rp.getFirstName().getText().length() < 4 || 
						rp.getLastName().getText().length()<4){
					JOptionPane.showMessageDialog(clientFrame.getParent(),
							"You need to provide a first/second name");
					return;
				}
				communication.sendMessage(new Registration(rp.getFirstName().getText(), rp.getLastName().getText(),
						rp.getUsername().getText(), rp.getPassword().getPassword()) );
				clientFrame.changeContentPane(clientFrame.getLoginPanel());
			}
		});
		rp.getCancel().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clientFrame.changeContentPane(clientFrame.getLoginPanel());	
			}
		});

		final MainClientPanel mcp = clientFrame.getMainClientPanel();
		mcp.getRefresh().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)  {
				DisplayAuctions da = new DisplayAuctions(getUserID());
				String selected = clientFrame.getCategory();
				if(!selected.contains("---")){
					da.setSearchOption(selected);
					da.setCriteria(mcp.getSearchField().getText());
					communication.sendMessage(da);
				}

			}
		});
		mcp.getViewItem().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)  {
				if(mcp.getList().getSelectedIndex() < 1)
					return;
				Item i = mcp.getItems().get(mcp.getList().getSelectedIndex()-1);
				communication.sendMessage(new ViewAuction
						(getUserID(),i));
			}
		});
		mcp.getMailBox().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				communication.sendMessage(new RequestMail(getUserID()));


			}

		});
		mcp.getAuctionItem().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				clientFrame.changeContentPane(clientFrame.getAuctionPanel());

			}

		});
		mcp.getSearch().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				DisplayAuctions da =new DisplayAuctions(getUserID());
				String selected = clientFrame.getCategory();
				if(!selected.contains("---")){
					da.setSearchOption(selected);
					da.setCriteria(mcp.getSearchField().getText());
					communication.sendMessage(da);
				}
			}
		});
		mcp.getClose().addActionListener(new ActionListener(){
			/*
			 * message sent to server for disconnecting
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				communication.sendMessage(new DisconnectMessage(getUserID()));
				System.exit(0);

			}
		});
		ActionListener cancel = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				clientFrame.changeContentPane(clientFrame.getMainClientPanel());

			}

		};

		final AuctionPanel ap = clientFrame.getAuctionPanel();
		ap.getBrowseForImage().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				int value =ap.getFileChooser().showOpenDialog(clientFrame.getParent());
				if(value == JFileChooser.APPROVE_OPTION){
					try {
						BufferedImage bi = ap.getBufferedImage();
						bi = ImageIO.read(ap.getFileChooser().getSelectedFile());
						ap.setBufferedImage(bi);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		ap.getSubmit().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					String[] tmp = ap.getKeyword().getText().split(",");
					if(ap.getTitle().getText().length() > 25){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"Title should contain less than 25 charecters");
						return;
					}else if(ap.getDescription().getText().length()>100){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"Description should contain less than 200 charecters");
						return;
					}else if(ap.getKeyword().getText().split(",").length >8){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"You are allowed to have up to 8 keywords");
						return;
					} else if(Double.parseDouble(ap.getReservePrice().getText())>1000000){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"You are not allowed to have a reserve price over 1 million");
						return;
					}
					ArrayList<String> ar = new ArrayList<String>(Arrays.asList(tmp));
					double reservePrice = Double.parseDouble(ap.getReservePrice().getText());
					Item item = new Item(ap.getTitle().getText(), ap.getDescription().getText().replace("\n", "")
							,userID, Integer.parseInt(ap.getCloseTime().getText()),
							reservePrice, ar,ap.getBufferedImage());
					communication.sendMessage(new CreateAuction(getUserID(),item));
				}catch(NumberFormatException e1){
					JOptionPane.showMessageDialog(new JFrame(), "Incorrect input, " +
							"reserve price and duration must be a numbers");
				}
			}
		});
		ap.getCancel().addActionListener(cancel);

		final ItemViewPanel ivp = clientFrame.getItemViewPanel();
		ivp.getBid().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				double bidPrice = Double.parseDouble(ivp.getBidPrice().getText());
				try {
					if(userID.equals(ivp.getItem().getVendorID())){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"You can not bid on your own item");
						return;
					}else if(bidPrice>10000000){
						JOptionPane.showMessageDialog(clientFrame.getParent(),
								"You can not bid more than 10 million");
						return;
					}
					communication.sendMessage(new BidAuction(getUserID(),ivp.getItem(),
							bidPrice));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		ivp.getBack().addActionListener(cancel);
		ivp.getCancelAuction().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!userID.equals(ivp.getItem().getVendorID())){
					JOptionPane.showMessageDialog(clientFrame.getParent(), "You can not cancel items" +
							" which you have not created");
					return;
				}
				communication.sendMessage(new CancelAuction(userID, ivp.getItem()));
			}

		});

		final MailPanel mp = clientFrame.getMailPanel();
		mp.getCancel().addActionListener(cancel);
		mp.getSend().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				mp.getRight().removeAll();
				clientFrame.changeContentPane(clientFrame.getSendMailPanel());
				clientFrame.revalidate();
				clientFrame.repaint();
			}
		});
		mp.getList().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				Text txt;
				try{
					txt = clientFrame.getMailAt(mp.getList().getSelectedIndex());
				}catch(ArrayIndexOutOfBoundsException e1){
					return;
				}
				mp.getRight().removeAll();
				mp.getRight().add(new ViewMailPanel(txt));
				clientFrame.revalidate();
				clientFrame.repaint();
			}
		});


		final SendMailPanel smp = clientFrame.getSendMailPanel();
		smp.getSend().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String src = getUserID();
				communication.sendMessage(new Text(src, smp.getDestination().getText(),
						smp.getTitle().getText(), smp.getDescription().getText().replace("\n", " ")));
			}
		});
		smp.getCancel().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clientFrame.changeContentPane(clientFrame.getMailPanel());
			}

		});
	}
}
