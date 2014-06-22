import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
/*
 * Server frame for the live feed
 * of the running server. 
 */
public class ServerFrame extends JFrame{
	private static final long serialVersionUID = -6266337283781983654L;
	private ObserverPanel op;
	public ServerFrame(){
		super("Server Application");
		op = new ObserverPanel();
		init();
	}
	public void init(){
		setSize(600, 500);
		setContentPane(op);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void addToLivefeed(String feed){
		op.addToLivefeed( feed);
	}
	public ObserverPanel getObserverPanel() {
		return op;
	}

}
class ObserverPanel extends JPanel{
	private static final long serialVersionUID = 7497676621273544161L;
	private JScrollPane jsp;
	private JTextArea textArea;
	private GridBagConstraints c;
	private JButton log,shutdown;
	public ObserverPanel(){
		setPreferredSize(new Dimension(600,500));
		c= new GridBagConstraints();
		log = new JButton("Produce Log");
		shutdown = new JButton("Shutdown");
		textArea = new JTextArea(25, 50);
		jsp= new JScrollPane(textArea);
		init();
	}
	public void init(){
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		DefaultCaret dc = (DefaultCaret) textArea.getCaret();
		dc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setLayout(new GridBagLayout());
		add(jsp,c);
		JPanel dummy = new JPanel();
		dummy.add(log);
		dummy.add(shutdown);
		c.gridy=2;
		c.anchor= GridBagConstraints.LAST_LINE_END;
		add(dummy,c);

	}
	public void addToLivefeed(String feed){
		textArea.append(feed);
	}
	public JButton getShutdown(){
		return shutdown;
	}
	public JScrollPane getJsp() {
		return jsp;
	}
	public JTextArea getTextArea() {
		return textArea;
	}
	public GridBagConstraints getC() {
		return c;
	}
	public JButton getLog() {
		return log;
	}
}
