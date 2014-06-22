import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
public class ServerMain {
	public static void main(String[] args){
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
		ServerFrame serverFrame = new ServerFrame();
		Server server = new Server(serverFrame);
		try {
			server.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
