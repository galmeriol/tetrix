/*
 * 
 */
package capture;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import models.CommonModel;
import models.OpticalFlowModelFarne;

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
import org.opencv.features2d.*;



public class FrameHelper extends Observable {


    List<Mat> processedIms = new ArrayList<Mat>();

    public FrameHelper() {	
	super();
    }

    void changeData(Object data) {
	setChanged();
	notifyObservers(data);
    }

    private Context ctx = new Context();

    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }
    BackgroundSubtractorMOG2 subtractor = Video.createBackgroundSubtractorMOG2(3000, 10, true);
    BackgroundSubtractorKNN subtractor2 = Video.createBackgroundSubtractorKNN(3000, 80, false);

    long last = 0;

    void subtractBackground(){
	processedIms.clear();

	long curr = System.currentTimeMillis();

	System.out.println(curr-last);
	if(curr - last > 50){
	    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9), new Point(4, 4));  


	    //Imgproc.blur(ctx.getMainFrame().submat(ctx.getBackgroundSample()), ctx.getFrameSubtracted(), new Size(3, 3));


	    //subtractor.apply(ctx.getMAINFrame(), ctx.getFRAMEBack(), 0.1);
	    
	    Mat main_ = new Mat();
	    ctx.getMAINFrame().clone().copyTo(main_);
	    
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    Imgproc.morphologyEx(main_, main_, Imgproc.MORPH_DILATE | Imgproc.MORPH_ERODE, element);
	    
	    processedIms.add(main_.clone());
	    subtractor2.apply(main_, ctx.getFRAMEBack(), 0.05);


	    //Imgproc.threshold(ctx.getFRAMEBack(), ctx.getFRAMEBack(), 128, 255, Imgproc.THRESH_BINARY);
	    last = curr;
	}


	processedIms.add(ctx.getFRAMEBack());

    }

    List<Mat> motion_templates(){
	return processedIms;
    }

    CommonModel act = new OpticalFlowModelFarne();
    Direction mygesturedetect(){
	act.setCtx(ctx);

	return act.get();
	//return new Direction("");

    }

    List<Mat> list() {

	//return processedIms;
	return act.list();

    }
    List<Mat> mygesturedetect2() {

	List<Mat> processedIms = new ArrayList<Mat>();

	return processedIms;
	
	/*
	 * 
	 * Skin color based detection deneme
	 * 
	 * */
	//rgbaMat = ctx.getMainFrame().clone();



	//Core.flip(rgbaMat, rgbaMat, 1);


	/*
	initCLowerUpper(15, 15, 50, 50, 50, 50);
	initCBackLowerUpper(5, 5, 80, 80, 100, 100);
	 * 
	 * Imgproc.GaussianBlur(rgbaMat, rgbaMat, new Size(9, 9), 5, 5);

	Imgproc.cvtColor(rgbaMat, rgbaMat, Imgproc.COLOR_BGR2RGB);
	Imgproc.cvtColor(rgbaMat, interMat, COLOR_SPACE);
	Imgproc.cvtColor(rgbaMat, ctx.getFrameBackgroundBin(), COLOR_SPACE);
	Imgproc.cvtColor(rgbaMat, ctx.getFrameHandBin(), COLOR_SPACE);



	Mode mode = ctx.getMODE();


	if (mode == Mode.SAMPLE) {  
	    handModeling();
	    processedIms.add(ctx.getFrameForHandModeling());
	    return processedIms;

	} else if (mode == Mode.DETECT) {
	    if(ctx.getAvgColorBack() == null || ctx.getAvgColorHand() == null){
		Imgproc.putText(ctx.getMainFrame(), "Sampling calistirilmadan detection calismaz.",
		                new Point(30, 30), Core.FONT_HERSHEY_COMPLEX_SMALL, 0.8, new Scalar(200, 200, 250));

		processedIms.add(ctx.getMainFrame());

		return processedIms;
	    }
	    produceBinImg(interMat);

	    processedIms.add(ctx.getFrameHandBin());
	    return processedIms;

	} else if (mode == Mode.TEST){

	    produceBinImg(interMat);

	    //makeContours();

	    processedIms.add(ctx.getFrameHandBin());
	    processedIms.add(ctx.getFrameBackgroundBin());
	    return processedIms;
	} else if(mode == Mode.BACKGROUND){
	    backgroundModeling();
	    //produceBinBackImg(ctx.getMainFrame().clone(), ctx.getFrameForBackModeling());
	    processedIms.add(ctx.getFrameForBackModeling());
	    return processedIms;
	}

	processedIms.add(tmpMat);

	return processedIms;

	 */

	/*
	 * 
	 * Skin color based deneme son
	 * 
	 * */
    }
}
