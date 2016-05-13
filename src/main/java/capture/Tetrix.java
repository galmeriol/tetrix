package capture;

import java.applet.Applet;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;




import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.opencv.core.*;
import org.opencv.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.*;
import org.opencv.utils.*;
import org.opencv.photo.*;
import org.opencv.video.*;
import org.opencv.videoio.*;
import org.opencv.objdetect.*;
import org.opencv.core.Mat;
import org.opencv.ml.*;

/**
 * @author zafer erdoğan
 *
 */

public class Tetrix implements Observer{

    List<Mat> processedIms = new ArrayList<Mat>();
    List<Frame> frames = new ArrayList<Frame>();
    FrameHelper helper = new FrameHelper();
    VideoCapture grabber = new VideoCapture(0);
    

    public Tetrix() throws InterruptedException {
	begin();
    }


    IGestureListener mHandGestureListener;
    public void setHandGestureListener(IGestureListener handGestureListener) {
	mHandGestureListener = handGestureListener;
    }

    void begin() throws InterruptedException{
	helper.addObserver(this);
	ctx.initFramesForDiffModel();
	ctx.initFramesForOpticalFlowModel();
	
	GrabThread t1 = new GrabThread();
	DetectionThread t2 = new DetectionThread();

	t1.start();	
	t2.start();


    }
    
    Context ctx = new Context();

    private void setCtx(Context c){
	ctx = c;
    }


    class GrabThread extends Thread{


	public void run() {

	    grabber.open(0);

	    if(!grabber.isOpened())
		return;

	    grabber.grab();
	    for(;;){
		grabber.read(ctx.getMAINFrame());
	    }
	}
    }


    class DetectionThread extends Thread{
	@Override
	public void run() {
	    //VideoCapture grabber = new VideoCapture(0);
	    //grabber.set(Videoio.CAP_PROP_FPS, 100);
	    frames.add(new Frame("0", ctx, true));
	    long initTime = System.currentTimeMillis();
	    long currTime = 0;
	    for(;;){
		//if(grabber.grab()){
		if(ctx.getMAINFrame().empty()) continue;
		helper.setCtx(ctx);

		/*for (Frame f : frames) {*/



		Direction d = null;
		
		d = helper.mygesturedetect();
		processedIms = helper.list();
		
		currTime = System.currentTimeMillis();
		
		if(!d.name.isEmpty() && currTime - initTime > 1000 && mHandGestureListener != null){
		    if(d.name.contains("Left"))
			mHandGestureListener.onLeftMove();
		    else if(d.name.contains("Right"))
			mHandGestureListener.onRightMove();
		    else if(d.name.contains("Up"))
			mHandGestureListener.onUpMove();
		    else if(d.name.contains("Down"))
			mHandGestureListener.onDownMove();
		    
		    initTime = currTime;
		}

		if(frames.size() < processedIms.size()){
		    int frameSize = frames.size();
		    for(int i = 1;i <= processedIms.size() - frameSize;i++)
			frames.add(new Frame(String.valueOf(i), ctx, false));
		}

		for(int i = 0;i <processedIms.size();i++){
		    frames.get(i).render(processedIms.get(i));
		}

	    }

	    //}

	}
    } 

    public static void main(String[] args) throws InterruptedException {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	Tetrix tetrix = new Tetrix();

    }

    public void update(Observable o, Object ctx) {
	setCtx((Context)ctx);
    }

}
