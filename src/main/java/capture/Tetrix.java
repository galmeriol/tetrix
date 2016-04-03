package capture;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;




import javax.swing.JPanel;
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

import com.atul.JavaOpenCV.Imshow;

/**
 * @author zafer erdoğan
 *
 */
public class Tetrix{

    public Tetrix() {

    }
    void begin() throws InterruptedException{
	GrabThread t1 = new GrabThread();
	DetectionThread t2 = new DetectionThread();

	t1.start();
	t2.start();

    }


    Mat grabbedIm = new Mat();
    List<Mat> processedIms = new ArrayList<Mat>();
    List<Frame> frames = new ArrayList<Frame>();
    FrameHelper helper = new FrameHelper();

    class GrabThread extends Thread{
	@Override
	public void run() {

	    VideoCapture grabber = new VideoCapture();
	    grabber.open(0);

	    if(!grabber.isOpened())
		System.exit(-1);

	    for (;;){
		if(grabber.grab()){
		    grabber.read(grabbedIm);
		}
	    }

	} 
    }

    Context ctx = new Context();
    class DetectionThread extends Thread{
	@Override
	public void run() {
	    ctx.setMODE(Mode.DETECT);
	    frames.add(new Frame("Default", ctx, true));
	    for(;;){
		if(!grabbedIm.empty()){
		    ctx.setMainFrame(grabbedIm);
		    ctx.setMybackground(grabbedIm);
		    ctx.setFrameForHandModeling(ctx.getMainFrame().clone());
		    helper.setCtx(ctx);
		    if(ctx.getMODE() == Mode.DETECT){
			for (Frame f : frames) {
			    if(f.isNotified())
				processedIms = helper.notify(grabbedIm, f.getParams());
			    else
				processedIms = helper.mygesturedetect();
			}

			if(frames.size() < processedIms.size()){
			    int frameSize = frames.size();
			    for(int i = 0;i < processedIms.size() - frameSize;i++)
				frames.add(new Frame(String.valueOf(i), ctx, false));
			}

			for(int i = 0;i <processedIms.size();i++){
			    frames.get(i).render(processedIms.get(i));
			}
		    }
		    else if(ctx.getMODE() == Mode.SAMPLE){
			ctx.setFrameForHandModeling(grabbedIm);
			helper.handModeling();
			frames.get(0).render(grabbedIm);
		    }
		    else if(ctx.getMODE() == Mode.BACKGROUND){
			ctx.setFrameForBackModeling(grabbedIm);
			helper.backgroundModeling();
			frames.get(0).render(grabbedIm);
		    }
		}
	    }

	}  
    } 

    public static void main(String[] args) throws InterruptedException {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	Tetrix tetrix = new Tetrix();
	tetrix.begin();
    }
}
