package capture;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class FramePanel extends JPanel {

	private Image img;

	public FramePanel() {
	    
	}
	
	public void setImage(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}