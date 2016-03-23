package capture;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    void begin(){
	new GrabThread().start();
    }

    VideoCapture grabber = new VideoCapture();
    Mat videoIm = new Mat();
    Frame frame = new Frame();
    FrameHelper helper = new FrameHelper();

    class GrabThread extends Thread{
	@Override
	public void run() {
	    grabber.open(0);
	    if(!grabber.isOpened())
		System.exit(-1);
	    
	    frame.setVisible(true);
	    for (;;){
		DetectionThread detect = new DetectionThread();
		if(grabber.read(videoIm)){
		    frame.render(videoIm);
		    detect.start();
		}
		else{
		    //System.out.println("Görüntü yakalanırken hata oluştu.");
		    break;
		}
	    }  
	} 
    }

    class DetectionThread extends Thread{
	@Override
	public void run() {
	    synchronized (helper) {
		videoIm = helper.mygesturedetect(videoIm);
	    }
	    
	}  
    } 

public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
	public void run() {
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    Tetrix tetrix = new Tetrix();
	    tetrix.begin();
	}
    });
}}
