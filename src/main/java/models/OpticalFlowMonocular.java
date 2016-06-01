package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import capture.Direction;
import classification.CSVHelper;
import classification.Cluster;
import classification.FlowVector;
import classification.KMeans;

public class OpticalFlowMonocular extends CommonModel{

    private final int tileSize = 12;
    private final int st = 16;
    private final int dt = 8;
    private final double emax = 0.99;
    private final double emin = 0.01;
    
    List<Cluster> clusters = new ArrayList<Cluster>();
    List<FlowVector> vectors = new ArrayList<FlowVector>();
    List flows = new ArrayList();
    KMeans kmeans = new KMeans();
    int binSize = 30;
    double NOISE = 0.5;
    List<MatOfKeyPoint> keypoints = new ArrayList<MatOfKeyPoint>();
    int action_counter = 1;

    String prefix = "/home/zafer/Desktop/workspace/motion_data/";

    public OpticalFlowMonocular() {
	try {
	    csvHelper = new CSVHelper(prefix + "out.csv");
	    kmeans.init();
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

	OpticalFlowMonocular(curr);
	//drawOptFlowMapFarne(blue, 12);

	return new Direction("");
    }

    @Override
    Direction decide() {

	return null;
    }

    public void drawOptFlowMapFarne(Scalar color, int step)	
    {

    }

    public void OpticalFlowMonocular(Mat curr) {

	Mat prevGray = ctx.getGRAYFrame();

	int tileScale = 1;
	int winsize = tileScale*tileSize;
	int col = curr.width()/winsize;
	int row = curr.height()/winsize;

	double err = 0.0;
	double sum = 0.0;
	double dif = 0.0;
	Mat prev = new Mat();
	Mat crr = new Mat();
	if(!ctx.getGRAYFrame().empty())
	    for(int i = 0; i < row; i++){
		for(int j=0;j < col; j++){
		    prev = prevGray.submat(i*winsize, (i+1)*winsize, j*winsize, (j+1)*winsize);
		    crr = curr.submat(i*winsize, (i+1)*winsize, j*winsize, (j+1)*winsize);
		    double err_ = 0.0;
		    for(int k=0;k<crr.width();k++){
			for(int l=0;l<crr.height();l++){
			    sum += crr.get(k, l)[0] + prev.get(k, l)[0];
			    dif += crr.get(k, l)[0] - prev.get(k, l)[0];
			}
		    }
		    err_ = Math.abs(dif)/sum;
		    
		    sum = 0;
		    dif = 0;
		    err+=err_;
		}
	    }
	
	System.out.println("errorasdasdasdsd:" + err);

	curr.copyTo(ctx.getGRAYFrame());
    }
}
