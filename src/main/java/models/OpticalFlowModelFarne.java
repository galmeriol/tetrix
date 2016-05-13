package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.Videoio;

import classification.CSVHelper;
import capture.Direction;

public class OpticalFlowModelFarne extends CommonModel {


    int binSize = 30;
    double NOISE = 0.7;
    List<MatOfKeyPoint> keypoints = new ArrayList<MatOfKeyPoint>();
    int action_counter = 1;
    
    String prefix = "/home/zafer/Desktop/workspace/motion_data/";

    public OpticalFlowModelFarne() {
	try {
	    csvHelper = new CSVHelper(prefix + "out.csv");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public
    List<Mat> list() {
	return processedIms;
    }

    @Override
    public
    Direction get() {
	Mat curr = new Mat();

	Imgproc.cvtColor(ctx.getMAINFrame(), curr, Imgproc.COLOR_BGR2GRAY);

	OpticalFlowFarne(curr, 100, 0.05, 15);
	drawOptFlowMapFarne(blue, 12);

	return new Direction("");
    }

    @Override
    Direction decide() {

	return null;
    }

    public void drawOptFlowMapFarne(Scalar color, int step)	
    {
	processedIms.clear();
	ctx.setHISTFrame(new Mat(new Size(640, 480), CvType.CV_8UC3));

	ctx.getHISTFrame().setTo(new Scalar(255, 255, 255));
	//ctx.setHISTFrame(new Mat(new org.opencv.core.Size(640, 480), org.opencv.core.CvType.CV_8UC3));

	Mat white = Mat
		.ones(new Size(640, 480), CvType.CV_8UC3)
		.setTo(new Scalar(255, 255, 255));


	double bins[] = new double[binSize];

	for(int y = 0; y < ctx.getFLOWFarneFrame().rows(); y+=step){
	    for(int x = 0; x < ctx.getFLOWFarneFrame().cols(); x+=step)
	    { 
		double[] fxy = ctx.getFLOWFarneFrame().get(y, x); 

		if(
			(fxy[0] <= NOISE && fxy[0] >= -NOISE) || 
			(fxy[1] <= NOISE && fxy[1] >= -NOISE)
			)
		    continue;

		Point startP = new Point(x, y);
		Point endP = new Point((int)x+fxy[0], (int)y+fxy[1]);

		double distance = Math.sqrt(
		                            Math.pow(fxy[0], 2)
		                            +
		                            Math.pow(fxy[1], 2)
			);


		if(distance < 6 || distance > 20)
		    continue;

		double theta = (Math.atan2(fxy[1], fxy[0]));
		float angle = (float) Math.toDegrees(theta);

		if(angle < 0)
		    angle +=360;

		/*if(theta < 0)
		    theta = (2*Math.PI) - theta;*/


		for (int i = 0; i < binSize; i++) {
		    double val = distance*angle;
		    if(i == 0){
			if(angle < (360)*(i/binSize))
			    bins[i] += val;
		    }
		    else{
			double min = (360)*(double)(i-1)/binSize;
			double max = (360)*(double)i/binSize;

			if(
				(angle >= min)
				&&
				(angle < max)
				){
			    bins[i] += val;
			}
		    }
		}

		Imgproc.arrowedLine(white, startP, endP, color, 1, Imgproc.LINE_8, 0, 0.2);

	    }
	}
	double sum = 0.0;

	for (int i = 0; i < bins.length; i++) 
	    sum += bins[i];

	bins = normalize(bins, sum);
	histIm(bins);
	
	if(sum > 0){
	    if(ctx.getAction_features().size() < ctx.vars.helperConstants.getSAMPLE_SIZE()){
		ctx.getAction_features().add(bins);
		Mat resizedMain = ctx.getMAINFrame().clone();
		Imgproc.resize(resizedMain, resizedMain, new Size(48, 36));
		ctx.getAction().add(resizedMain);
	    }
	    else{
		try {
		    String actionDirName = prefix + "action_" + action_counter + "/";
		    File actionDir = new File(actionDirName);
		    
		    if(!actionDir.exists())
			actionDir.mkdir();
		    else
			deleteFiles(actionDir);
		    
		    for(int j = 0; j < ctx.getAction().size(); j++)
			Imgcodecs.imwrite(actionDirName + "frame_" + (j+1) + ".jpg", ctx.getAction().get(j));
		    
		    Imgcodecs.imwrite(actionDirName + "histogram.jpg", ctx.getHISTFrame().clone());
		    action_counter++;
		    csvHelper.setWriter(actionDirName + "out.csv");
		    writeToCSV(ctx.getAction_features());
		    
		    ctx.getActions().add(ctx.getAction());
		    ctx.getActions_Features().add(ctx.getAction_features());
		    ctx.getActionDirNames().add(actionDirName);
		} catch (IOException e) {
		    e.printStackTrace();
		} finally{
		    ctx.getAction_features().clear();
		    ctx.getAction().clear();
		}
	    }
	}
	white.copyTo(ctx.getFARNEFrame());

	processedIms.add(ctx.getHISTFrame());


	processedIms.add(ctx.getFARNEFrame());
	//processedIms.add(ctx.getMAINFrame());
    }
    
    void deleteFiles(File file){
	String[] entries = file.list();
	for(String s: entries){
	    File currentFile = new File(file.getPath(),s);
	    currentFile.delete();
	}
    }
    
    public void OpticalFlowFarne(Mat curr, int maxDetectionCount, double qualityLevel,
                                 double minDistance) {


	Mat prevGray = ctx.getGRAYFarneFrame();

	double pyr_scale = 0.5;
	int levels = 5;
	int winsize = 15;
	int iterations = 12;
	int poly_n = 7;
	double poly_sigma = 1.5;
	int flags = 0;

	if(!ctx.getGRAYFarneFrame().empty())
	    Video.calcOpticalFlowFarneback(prevGray, curr, ctx.getFLOWFarneFrame(), pyr_scale, levels, winsize, iterations, 
	                                   poly_n, poly_sigma, flags);

	curr.copyTo(ctx.getGRAYFarneFrame());
    }


}
