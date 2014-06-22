import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	@SuppressWarnings("unused")
	private ImageIcon imgIcon;
	public ImagePanel(ImageIcon imgIcon){
		try{
			this.imgIcon=imgIcon;
			setPreferredSize(new Dimension(imgIcon.getIconWidth(),imgIcon.getIconHeight()));
		}catch(NullPointerException e){
			try {
				imgIcon = new ImageIcon(ImageIO.read(new File("happiness.jpg")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		label = new JLabel(imgIcon);
		add(label);
	}
	public void setIcon(ImageIcon image) {
		label.setIcon(image);
		repaint();
	}
	public void setIcon(){
		try {
			setIcon(new ImageIcon(ImageIO.read(new File("happiness.jpg"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
