package models;

import java.util.ArrayList;
import java.util.List;

import org.opencv.bioinspired.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.ANN_MLP;
import org.opencv.video.Video;

import capture.Direction;

public class OpticalFlowModel extends CommonModel{
    
    MatOfPoint2f initial2f = new MatOfPoint2f(); //başlangıç frame' i
    MatOfPoint2f flowed = new MatOfPoint2f();  //hareketten sonraki frame

    static int numberOfCorners = 400;

    MatOfByte status = new MatOfByte();
    MatOfFloat error = new MatOfFloat();

    
    @Override
    public 
    Direction get() {
	opticalflow();
	return new Direction("");
    }

    @Override
    Direction decide() {
	return null;
    }

    @Override
    public
    List<Mat> list() {
	return processedIms;
    }
    
    public void OpticalFlow(Mat curr, int maxDetectionCount, double qualityLevel,
                            double minDistance) {
	
	MatOfPoint initial = new MatOfPoint();

	Imgproc.goodFeaturesToTrack(curr, initial, maxDetectionCount, qualityLevel, minDistance);

	initial.convertTo(initial2f, CvType.CV_32FC2);

	// for first image of the sequence
	Mat prevGray = ctx.getGRAYFrame();

	if(!prevGray.empty()){
	    Video.calcOpticalFlowPyrLK(prevGray, curr, // 2 consecutive images
	                               initial2f, // input point position in first image
	                               flowed, // output point postion in the second image
	                               status,    // tracking success
	                               error);      // tracking error

	    //Video.calcOpticalFlowFarneback(prevGray, f, flow, 0.5, 3, 15, 3, 5, 1.2, 0);
	}


	curr.copyTo(ctx.getGRAYFrame());
    }


    public void drawOptFlowMap(Mat flow, int step, Scalar color) {
	
	if(flowed.empty()) return;
	
	byte status_[] = status.toArray();
	
	for(int i = 0; i < Math.min(initial2f.size().height,  flowed.size().height); i++)
	{
	    Point feature1  = new Point();
	    Point feature2  = new Point();

	    feature1 = initial2f.toArray()[i];
	    feature2 = flowed.toArray()[i];

	    byte statu = status_[i];

	    if (statu == 0)	
		continue;

	    int line_thickness;
	    line_thickness = 1;

	    Scalar line_color = new Scalar(255,0,0);

	    Point p = new Point(),q = new Point();
	    p.x = (int) feature1.x;
	    p.y = (int) feature1.y;
	    q.x = (int) feature2.x;
	    q.y = (int) feature2.y;

	    double angle = Math.atan2( (double) p.y - q.y, (double) p.x - q.x );
	    double hypotenuse = Math.sqrt( Math.pow(p.y - q.y, 2) + Math.pow(p.x - q.x, 2) );

	    q.x = (int) (p.x - 3 * hypotenuse * Math.cos(angle));
	    q.y = (int) (p.y - 3 * hypotenuse * Math.sin(angle));

	    Imgproc.line(ctx.getFLOWFrame(), p, q, line_color, line_thickness);
	}
    }

    public List<Mat> opticalflow(){
	
	Mat gray = new Mat();
	Imgproc.cvtColor(ctx.getMAINFrame().clone(), gray, Imgproc.COLOR_BGR2GRAY);
	
	Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 1);

	//OpticalFlowFarne(gray);
	OpticalFlow(gray, numberOfCorners, 0.01, 10);

	//Video.calcOpticalFlowFarneback(prevGray, gray, uflow, 0.5, 3, 15, 3, 5, 1.2, 0);

	drawOptFlowMap(gray, 32, new Scalar(255 ,0, 0));
	
	if(!flowed.empty()){
	    processedIms.add(ctx.getFLOWFrame());
	}
	else
	    processedIms.add(ctx.getMAINFrame());
	
	ctx.setFLOWFrame(ctx.getMAINFrame().clone());

	return processedIms;

    }

}
