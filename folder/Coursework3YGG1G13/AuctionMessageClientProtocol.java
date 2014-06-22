
import javax.swing.JOptionPane;


/*
 * Class for handling all the types of
 * messages which you can receive on the
 * Client side
 */

public class AuctionMessageClientProtocol {
	/*
	 * Add the mail to the Mailbox
	 */
	public static void handleTextMessage(Text text,Client client){
		client.addMail(text);
	}
	/*
	 * Log in if authentication was successful
	 */
	public static void handleAuthenticationMessage(Authentication auth,Client client){
		if(auth.isSuccessful()){
			ClientFrame clientFrame = client.getClientFrame();
			clientFrame.changeContentPane(clientFrame.getMainClientPanel());
			client.setUserID(auth.getUserID());
		}
		else{
			JOptionPane.showMessageDialog(client.getClientFrame().getParent(), "Unable to log in, try again");
		}
	}
	/*
	 * Displays the ViewAuction panel
	 */
	public static void handleViewAuctionMessage(ViewAuction va,Client client) {
		try{
			ItemViewPanel ivp = client.getClientFrame().getItemViewPanel();
			ivp.setItem(va.getItem());
			client.getClientFrame().changeContentPane(ivp);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	/*
	 * Display the auction that the server responded to
	 */
	public static void handleDisplayAuctionsMessage(DisplayAuctions da,Client client) {
		client.getClientFrame().updateItemList(da.getItems());	
	}
	/*
	 * Displays the error message in the system
	 */
	public static void handleErrorMessage(ErrorMessage em, Client client) {
		JOptionPane.showMessageDialog(client.getClientFrame().getParent(),"Notification: "+em.getMessage() );
	}
	/*
	 * Updates the mailbox and shows the updated content pane
	 */
	public static void handleRequestMailMessage(RequestMail rm, Client client) {
		client.setMail(rm.getMailbox());
		client.getClientFrame().changeContentPane(client.getClientFrame().getMailPanel());
	}

}
