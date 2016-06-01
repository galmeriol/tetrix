package capture;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;

import javax.swing.JFrame;

import org.opencv.core.Mat;

public class Frame extends JFrame{

    //private final JFrame frame;

    private final FramePanel videoPanel;
    private ConfigPanel configPanel = null;
    boolean notify = false;
    
    @SuppressWarnings("rawtypes")
    HashMap initVals = new HashMap();
    
    Context ctx = null;
    
    public Frame(String title, Context ctx, boolean isConfigActive) {
	
	super(title);
	getContentPane().setLayout(new FlowLayout());
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
	setCtx(ctx);
	videoPanel = new FramePanel();
	
	if(isConfigActive){
	    configPanel = new ConfigPanel();
	    configPanel.setCtx(ctx);
	    getContentPane().add(configPanel);
	}
	getContentPane().add(videoPanel);
    }  
    
    public void setMODE(Mode mode){
	ctx.setMODE(mode);
    }
    
    public Mode getMODE(){
	return ctx.getMODE();
    }
   
    public void render(Mat image) {
	Image i = toBufferedImage(image);
	videoPanel.setImage(i);
	videoPanel.repaint();
	pack();
    }

    public static Image toBufferedImage(Mat m){
	int type = BufferedImage.TYPE_BYTE_GRAY;
	if ( m.channels() > 1 ) {
	    type = BufferedImage.TYPE_3BYTE_BGR;
	}
	int bufferSize = m.channels()*m.cols()*m.rows();
	byte [] b = new byte[bufferSize];
	m.get(0,0,b); // get all the pixels
	BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
	final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	System.arraycopy(b, 0, targetPixels, 0, b.length);
	return image;

    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

}