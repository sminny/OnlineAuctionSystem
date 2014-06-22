import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class ClientMain {

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ClientFrame clientFrame = new ClientFrame();
		try {
			Client client = new Client(clientFrame);
			client.startClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
