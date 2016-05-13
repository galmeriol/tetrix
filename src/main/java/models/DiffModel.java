package models;

import java.util.ArrayList;
import java.util.List;

import org.opencv.ml.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import capture.Direction;

public class DiffModel extends CommonModel{

    int[] theObject = {0, 0};
    Rect objectBoundingRectangle = new Rect(0,0,0,0);

    Direction searchForMovement(Mat cameraFeed){

	//notice how we use the '&' operator for objectDetected and cameraFeed. This is because we wish
	//to take the values passed into the function and manipulate them, rather than just working with a copy.
	//eg. we draw to the cameraFeed to be displayed in the main() function.
	boolean objectDetected = false;
	Mat temp = new Mat();
	Direction out = new Direction("");
	ctx.getBITWISEFrame().copyTo(temp);
	//these two vectors needed for output of findContours
	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	Mat hierarchy = new Mat();
	//find contours of filtered image using openCV findContours function
	//findContours(temp,contours,hierarchy,CV_RETR_CCOMP,CV_CHAIN_APPROX_SIMPLE );// retrieves all contours
	Imgproc.findContours(temp, contours,hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);// retrieves external contours

	//if contours vector is not empty, we have found some objects
	if(contours.size()>0)objectDetected=true;
	else objectDetected = false;

	if(objectDetected){
	    int max_idx = findBiggestContour(contours);
	    MatOfPoint largestContourVec = new MatOfPoint();
	    largestContourVec = contours.get(max_idx);

	    double max_area = findBiggestArea(largestContourVec);
	    //System.out.println(max_area);
	    if(max_area < 200.0) return out;


	    objectBoundingRectangle = Imgproc.boundingRect(largestContourVec);
	    int xpos = objectBoundingRectangle.x+objectBoundingRectangle.width/2;
	    int ypos = objectBoundingRectangle.y+objectBoundingRectangle.height/2;

	    //update the objects positions by changing the 'theObject' array values
	    theObject[0] = xpos; theObject[1] = ypos;

	    Imgproc.drawContours(cameraFeed, contours, max_idx, new Scalar(0, 0, 0));
	}
	//make some temp x and y variables so we dont have to type out so much
	int x = theObject[0];
	int y = theObject[1];

	//draw some crosshairs around the object
	Imgproc.circle(cameraFeed,new Point(x,y),20,new Scalar(0,255,0),2);
	Imgproc.line(cameraFeed,new Point(x,y),new Point(x,y-25),new Scalar(0,255,0),2);
	Imgproc.line(cameraFeed,new Point(x,y),new Point(x,y+25),new Scalar(0,255,0),2);
	Imgproc.line(cameraFeed,new Point(x,y),new Point(x-25,y),new Scalar(0,255,0),2);
	Imgproc.line(cameraFeed,new Point(x,y),new Point(x+25,y),new Scalar(0,255,0),2);

	//write the position of the object to the screen
	Imgproc.putText(cameraFeed,"Tracking object at (" + x+","+ y +")", new Point(x,y),1,1,new Scalar(255,0,0),2);

	if(objectDetected){
	    out = decide();
	    //System.out.println("obj detected: " + out.name + " -> x: " + x + " - y: " + y);
	}

	Imgproc.putText(cameraFeed, 
	                out.name, 
	                new Point(40, 40), 1, 1, new Scalar(255,0,0), 2);

	return out;
    }

    public Direction get(){
	
	ctx.getFRAMEDiffSequence().clear();
	Mat gray = null;

	ctx.getFRAMESequence().add(0, ctx.getMAINFrame().clone());

	gray = new Mat();
	Imgproc.cvtColor(ctx.getMAINFrame().clone(), gray, Imgproc.COLOR_BGR2GRAY);
	Imgproc.threshold(gray, gray, ctx.vars.diffModelConstants.getTHRESHOLD() , 255, Imgproc.THRESH_BINARY);

	ctx.getFRAMEGraySequence().add(0, gray);
	
	
	if(ctx.getFRAMESequence().size() < ctx.vars.diffModelConstants.getSEQ_SIZE())
	    return new Direction("");
	
	
	processedIms.clear();

	
	for(int i = 0;i < ctx.vars.diffModelConstants.getSEQ_SIZE() - 1; i++){
	    Mat diff = new Mat();
	    Core.absdiff(ctx.getFRAMEGraySequence().get(i), ctx.getFRAMEGraySequence().get(i+1), diff);
	    
	    ctx.getFRAMEDiffSequence().add(0, diff);
	}
	Mat bitwise = new Mat();
	
	//Core.bitwise_or(ctx.getFRAMEDiffSequence().get(0), ctx.getFRAMEDiffSequence().get(1), bitwise);
	
	for (int i = 0; i < ctx.getFRAMEDiffSequence().size() - 1; i++) {
	    Core.bitwise_or(ctx.getFRAMEDiffSequence().get(i), ctx.getFRAMEDiffSequence().get(i+1), bitwise);
	}
	
	bitwise.copyTo(ctx.getBITWISEFrame());

	
	Imgproc.putText(ctx.getBITWISEFrame(), 
	                "Thresholded at " + ctx.vars.diffModelConstants.getTHRESHOLD(), 
	                new Point(20, 20), 1, 1, new Scalar(255,255,255), 2);


	//Imgproc.threshold(differenceImage, thresholdImage, SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);


	//blur the image to get rid of the noise. This will output an intensity image
	//Imgproc.blur(thresholdImage,thresholdImage,new Size(BLUR_SIZE, BLUR_SIZE));
	//threshold again to obtain binary image from blur output
	//Imgproc.threshold(thresholdImage,thresholdImage,SENSITIVITY_VALUE,255, Imgproc.THRESH_BINARY);


	//if tracking enabled, search for contours in our thresholded image
	//processedIms.add(thresholdImage);
	processedIms.add(ctx.getMAINFrame());
	//Imgproc.cvtColor(ctx.getMAINFrame(), ctx.getHSVFrame(), Imgproc.COLOR_RGB2HSV);
	//processedIms.add(ctx.getHSVFrame());
	processedIms.add(ctx.getBITWISEFrame());
	
	ctx.getFRAMESequence().removeLast();
	ctx.getFRAMEGraySequence().removeLast();
	
	return searchForMovement(ctx.getMAINFrame().clone());
    }

    Direction decide(){

	Rect rect = objectBoundingRectangle;

	int centerX = rect.x + rect.width / 2;
	int centerY = rect.y + rect.height / 2;

	/*if (centerX > FRAME_WIDTH - PADDING) {
	    return new Direction("Right");
	} else if (centerX < PADDING) {
	    return new Direction("Left");
	}*/


	// up-down move
	//System.out.println("not detected:" + centerX + " : " + centerY);
	if (centerX >= 100 && centerX <= 320)
	    // up
	    if (centerY >= 0 && centerY <= 400)
		return new Direction("Up");
	// down
	if (centerY >= 400 && centerY <=600)
	    return new Direction("Down");

	// left-right move
	if (centerY >= 0 && centerY <= 500)
	    // left
	    if (centerX >= 0 && centerX <= 200)
		return new Direction("Left");
	// right
	if (centerX >= 250)
	    return new Direction("Right");


	/*int centerX = rect.x + rect.width / 2;
	if (centerX > FRAME_WIDTH - PADDING) {
	    return new Direction("Right");
	} else if (centerX < PADDING) {
	    return new Direction("Left");
	}*/

	return new Direction("");

    }


    public List<Mat> list(){

	return processedIms;
    }



}
