

public class MessageHandler implements Runnable{
	private Message msg;
	private Server server;
	public MessageHandler(Message msg,Server server){
		this.msg = msg;
		this.server = server;
	}
	@Override
	public void run() {
		/*
		 * Analysing the message and taking the appropriate action
		 * according to the AuctionMessageProtocol
		 */
		
		switch(msg.getID()){
			case Message.TEXT:
				AuctionMessageServerProtocol.handleTextMessage((Text) msg,server);
				break;
			case Message.AUTHENTICATION:
				AuctionMessageServerProtocol.handleAuthenticationMessage((Authentication) msg,server);
				break;
			case Message.REGISTRATION:
				AuctionMessageServerProtocol.handleRegistrationMessage((Registration) msg,server);
				break;
			case Message.VIEW_AUCTION:
				AuctionMessageServerProtocol.handleViewAuctionMessage((ViewAuction) msg,server);
				break;
			case Message.BID_AUCTION:
				AuctionMessageServerProtocol.handleBidAuctionMessage((BidAuction) msg,server);
				break;
			case Message.CANCEL_AUCTION:
				AuctionMessageServerProtocol.handleCancelAuctionMessage((CancelAuction) msg,server);
				break;
			case Message.CREATE_AUCTION:
				AuctionMessageServerProtocol.handleCreateAuctionMessage((CreateAuction) msg,server);
				break;
			case Message.DISPLAY_AUCTIONS:
				AuctionMessageServerProtocol.handleDisplayAuctionsMessage((DisplayAuctions) msg,server);
				break;
			case Message.DISCONNECT:
				AuctionMessageServerProtocol.handleDisconnectMessage((DisconnectMessage) msg,server);
				break;
			case Message.REQUEST_MAIL:
				AuctionMessageServerProtocol.handleRequstMailMessage((RequestMail)msg,server);
				break;
		}
			
	}
	
}
