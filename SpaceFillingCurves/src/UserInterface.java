import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class UserInterface extends JFrame {
	private static final long serialVersionUID = 5051321847886284731L;
	private final static String title = "Space Filling Curves";
	private final int width = 1000;
	private final int height = 1000;
	private BufferedImage img;
	
	public UserInterface() {
		// initialize the frame
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(width, height));
		setVisible(true);
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
}
