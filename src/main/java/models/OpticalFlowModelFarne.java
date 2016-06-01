package models;

import java.awt.Rectangle;
import java.awt.image.SampleModel;
import java.beans.FeatureDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.Videoio;

import classification.CSVHelper;
import classification.Cluster;
import classification.FlowVector;
import classification.KMeans;
import classification.SVMClassifier;
import capture.Direction;
import capture.Mode;

public class OpticalFlowModelFarne extends CommonModel {


    List<Cluster> clusters = new ArrayList<Cluster>();
    List<FlowVector> vectors = new ArrayList<FlowVector>();
    List flows = new ArrayList();
    KMeans kmeans = new KMeans();
    int binSize = 30;
    double NOISE = 0.5;
    List<MatOfKeyPoint> keypoints = new ArrayList<MatOfKeyPoint>();

    double bins[] = new double[binSize];
    double binsSum[] = new double[binSize];

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

	Rect sample = new Rect(new Point(20, 20), new Point(290, 290));

	if(ctx.getMODE().equals(Mode.DETECT)){
	    Mat curr = new Mat();
	    processedIms.clear();

	    Imgproc.cvtColor(ctx.getMAINFrame(), curr, Imgproc.COLOR_BGR2GRAY);

	    if(ctx.isHandResampled()){
		Mat hndGray = ctx.getMAINFrame().submat(sample);
		Imgproc.cvtColor(hndGray, hndGray, Imgproc.COLOR_BGR2GRAY);
		Imgcodecs.imwrite(System.getProperty("user.dir") + "/src/main/java/hand.jpg", hndGray);
		ctx.setHand(Imgcodecs.imread(System.getProperty("user.dir") + "/src/main/java/hand.jpg"));
		ctx.setHandResampled(false);
	    }

	    //ctx.setHSVFrame(MatchHand(curr, ctx.getHand(), ctx.getMatch_method()));
	    //processedIms.add(ctx.getHSVFrame().clone());

	    OpticalFlowFarne(curr);
	    drawOptFlowMapFarne(blue, 12);

	    return new Direction("");
	}
	else if(ctx.getMODE().equals(Mode.SAMPLE)){
	    processedIms.clear();
	    Mat main = ctx.getMAINFrame().clone();
	    Imgproc.rectangle(main, new Point(20, 20), new Point(270, 270), new Scalar(0, 0, 0), 2);

	    processedIms.add(main);

	    return new Direction("");
	}
	return new Direction("");
    }

    @Override
    Direction decide() {

	return null;
    }

    Double maxH = 0.0;
    Double minH = 0.0;
    Double meanH = 0.0;
    Double meanHFirstQ = 0.0;
    Double meanHMidQ = 0.0;
    Double meanHLastQ = 0.0;
    public void drawOptFlowMapFarne(Scalar color, int step)	
    {
	boolean isFirst = true;
	flows.clear();
	Scalar clr = blue;
	ctx.setHISTFrame(new Mat(new Size(640, 480), CvType.CV_8UC3));

	ctx.getHISTFrame().setTo(new Scalar(255, 255, 255));
	//ctx.setHISTFrame(new Mat(new org.opencv.core.Size(640, 480), org.opencv.core.CvType.CV_8UC3));
	bins = new double[binSize];

	int width = 640;
	int height = 480;
	int sq = 20;
	Mat white = Mat
		.ones(new Size(width, height), CvType.CV_8UC3)
		.setTo(new Scalar(255, 255, 255));

	Mat white2 = Mat
		.ones(new Size(width, height), CvType.CV_8UC3)
		.setTo(new Scalar(255, 255, 255));

	Imgproc.line(white2, new Point(3, 0), new Point(3, white2.height()), new Scalar(100,100,100), 1, Imgproc.LINE_AA , 0);
	Imgproc.line(white2, new Point(white2.width() - 3, 0), new Point(white2.width() - 3, white2.height()), new Scalar(100,100,100), 1, Imgproc.LINE_AA , 0);

	int left = 50;
	int top = 20;
	for(int y = 0; y < ctx.getFLOWFarneFrame().rows(); y+=step){
	    for(int x = 0; x < ctx.getFLOWFarneFrame().cols(); x+=step)
	    {
		/*if(x < ctx.getHandRect().tl().x || x > ctx.getHandRect().br().x)
		    continue;

		if(y < ctx.getHandRect().tl().y || y > ctx.getHandRect().br().y)
		    continue;*/

		double[] fxy = ctx.getFLOWFarneFrame().get(y, x); 
		if(
			(fxy[0] <= NOISE && fxy[0] >= -NOISE) || 
			(fxy[1] <= NOISE && fxy[1] >= -NOISE)
			)
		    continue;

		Point startP = new Point(x, y);
		Point endP = new Point((int)x+fxy[0], (int)y+fxy[1]);

		double distance = 0.0;
		double deltaX = endP.x - startP.x;
		double deltaY = (startP.y - endP.y);
		distance = Math.sqrt(
		                     Math.pow(deltaY, 2)
		                     +
		                     Math.pow(deltaX, 2)
			);

		if(distance < 3 || distance > 35)
		    continue;

		double theta = (Math.atan2(deltaY, deltaX));
		float angle = (float) Math.toDegrees(theta);

		while(angle < 0)
		    angle += 360;

		for (int i = 1; i <= binSize; i++) {
		    double val = distance*angle;
		    if(i == 1){
			if(angle < (360)*(i/binSize))
			    bins[i-1] += val;
		    }
		    else{
			double min = (360)*(double)(i-1)/binSize;
			double max = (360)*(double)i/binSize;

			if(
				(angle >= min)
				&&
				(angle < max)
				){
			    bins[i-1] += val;
			}
		    }
		}

		if(distance < 5)
		    clr = new Scalar(9, 150, 77);
		else if(distance >= 5 && distance < 12.5)
		    clr = new Scalar(9, 60, 172);
		else if(distance >= 12.5 && distance < 20)
		    clr = new Scalar(9, 145, 172);
		else
		    clr = new Scalar(77, 11, 48);


		Imgproc.arrowedLine(white, startP, endP, clr, 1, Imgproc.LINE_8, 0, 0.2);
		if(left > 600){
		    top += 80;
		    left = 50;
		    isFirst = false;
		    Imgproc.line(white2, new Point(0, top + 8 - 60), new Point(white2.width(), top + 8 - 60), new Scalar(100,100,100), 1, Imgproc.LINE_AA , 0);
		}
		if((left-50) % 160 == 0){
		    Imgproc.arrowedLine(white2, new Point(left + 30, top + 8), new Point(left + deltaX + 30, top - deltaY + 8), clr, 1, Imgproc.LINE_AA, 0, 0.2);
		    Imgproc.putText(white2, "Uz:" + (int)distance + "-" + "Th:" + (int)angle, new Point(left - 10, top - 20), Core.FONT_HERSHEY_COMPLEX_SMALL , 0.5, clr, 1, Core.LINE_AA, false);
		    if(isFirst)
			Imgproc.line(white2, new Point(left - 50, 0), new Point(left - 50, white2.height()), new Scalar(100,100,100), 1, Imgproc.LINE_AA , 0);
		}
		left += 80;
	    }
	}
	double sum = 0.0;

	for (int i = 0; i < bins.length; i++){
	    sum += bins[i];
	    if(maxH < bins[i])
		maxH = bins[i];
	    else if(minH > bins[i])
		minH = bins[i];

	    meanH += bins[i]/bins.length;
	    
	    if(i < bins.length / 3)
		meanHFirstQ += bins[i]/ctx.vars.helperConstants.getSAMPLE_SIZE();
	    else if(i < bins.length*(2/3))
		meanHMidQ += bins[i]/ctx.vars.helperConstants.getSAMPLE_SIZE();
	    else
		meanHLastQ += bins[i]/ctx.vars.helperConstants.getSAMPLE_SIZE();
	    
	}

	bins = normalize(bins, sum);



	if(ctx.getAction_features().size() < ctx.vars.helperConstants.getSAMPLE_SIZE())
	    binsSum = merge(binsSum, bins);

	histIm(bins);

	if(sum > 10){    
	    if(ctx.getAction_features().size() < ctx.vars.helperConstants.getSAMPLE_SIZE()){
		ctx.getAction_features().add(bins);
		Mat resizedMain = ctx.getMAINFrame().clone();
		Imgproc.resize(resizedMain, resizedMain, new Size(400, 300));
		ctx.getAction().add(resizedMain);
		ctx.getActionFlow().add(white.clone());
		ctx.getActionHist().add(ctx.getHISTFrame().clone());
		ctx.getGridFlow().add(white2);
	    }
	    else{
		try {

		    String actionDirName = prefix + "action_" + action_counter + "/";
		    File actionDir = new File(actionDirName);

		    if(!actionDir.exists())
			actionDir.mkdir();
		    else
			deleteFiles(actionDir);

		    maxH = maxH/sum;
		    minH = minH/sum;
		    meanH = meanH/sum;
		    meanHFirstQ = meanHFirstQ/sum;
		    meanHMidQ = meanHMidQ/sum;
		    meanHLastQ = meanHLastQ/sum;
		    
		    for(int j = 0; j < ctx.getAction().size(); j++){
			Imgcodecs.imwrite(actionDirName + "frame_" + (j+1) + ".jpg", ctx.getAction().get(j));
			Imgcodecs.imwrite(actionDirName + "histogram" + (j+1) + ".jpg", ctx.getActionHist().get(j));
			Imgcodecs.imwrite(actionDirName + "flowIm" + (j+1) + ".jpg", ctx.getActionFlow().get(j));
			Imgcodecs.imwrite(actionDirName + "grid" + (j+1) + ".jpg", ctx.getGridFlow().get(j));
		    }

		    sum = 0;
		    for (int i = 0; i < binsSum.length; i++) {
			sum += binsSum[i];
		    }

		    binsSum = normalize(binsSum, sum);
		    histIm(binsSum);

		    
		    System.out.println("maxH:" + maxH + "-minH:" + minH + "-meanH:" + meanH);
		    double[] descriptor = new double[] {maxH, minH, meanH, meanHFirstQ, meanHMidQ, meanHLastQ};
		    csvHelper.setWriter(actionDirName + "descriptor.csv");


		    List<double[]> desc = new ArrayList<double[]>();
		    desc.add(descriptor);
		    writeToCSV(desc);
		    double prob = ctx.getClassifier().evaluate(descriptor, ctx.getSvmmodel());
		    System.out.println("BAKKK------" + prob);
		    maxH = 0.0;
		    minH = 0.0;
		    meanH = 0.0;
		    meanHFirstQ = 0.0;
		    meanHMidQ = 0.0;
		    meanHLastQ = 0.0;

		    Imgcodecs.imwrite(actionDirName + "histogramTotal.jpg", ctx.getHISTFrame().clone());

		    action_counter++;
		    csvHelper.setWriter(actionDirName + "out.csv");
		    writeToCSV(ctx.getAction_features());

		    ctx.getActions().add(ctx.getAction());
		    ctx.getActions_Features().add(ctx.getAction_features());
		    ctx.getActionDirNames().add(actionDirName);

		    ctx.getAction().clear();
		    ctx.getActionFlow().clear();
		    ctx.getActionHist().clear();
		    ctx.getGridFlow().clear();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally{
		    ctx.getAction_features().clear();
		    ctx.getAction().clear();
		}
	    }

	}



	white.copyTo(ctx.getFARNEFrame());

	processedIms.add(white2);
	processedIms.add(ctx.getFARNEFrame());
	//processedIms.add(ctx.getMAINFrame());
    }
    private double similarity(double d1, double d2){
	return (magnitude(d1,d2)+direction(d1,d2))*0.5;
    }

    private double[] merge(double[] a, double[] b){
	if(a.length != b.length)
	    return new double[binSize];
	for (int i = 0; i < b.length; i++)
	    a[i] += b[i];
	return a;
    }

    private double magnitude(double d1, double d2){
	if(d1*d2 != 0)
	    return (1 + Math.abs(d1*d2))/
		    (2*Math.abs(d1*d2));

	return 0;
    }

    private double direction(double d1, double d2){
	if(d1+d2 != 0)
	    return 1 - ((Math.abs(d1-d2))/
		    (Math.abs(d1)+Math.abs(d2)));

	return 1;
    }

    void deleteFiles(File file){
	String[] entries = file.list();
	for(String s: entries){
	    File currentFile = new File(file.getPath(),s);
	    currentFile.delete();
	}
    }

    public void OpticalFlowFarne(Mat curr) {


	Mat prevGray = ctx.getGRAYFarneFrame();

	double pyr_scale = 0.3;
	int levels = 1;
	int winsize = 7;
	int iterations = 12;
	int poly_n = 5;
	double poly_sigma = 1.1;
	int flags = 0;

	if(!ctx.getGRAYFarneFrame().empty()){
	    Video.calcOpticalFlowFarneback(prevGray, curr, ctx.getFLOWFarneFrame(), pyr_scale, levels, winsize, iterations, 
	                                   poly_n, poly_sigma, flags);

	}

	curr.copyTo(ctx.getGRAYFarneFrame());
    }



    public Mat MatchHand(Mat curr, Mat template, int match_method) {

	/*int result_cols = curr.cols() - template.cols() + 1;
	int result_rows = curr.rows() - template.rows() + 1;
	Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

	if(handKeys.empty()){
	    Mat resizedHand = new Mat();
	    Imgproc.resize(ctx.getHand(), resizedHand, new Size(ctx.getHand().width() * 0.2, ctx.getHand().height() * 0.2));

	    featureDetector.detect(ctx.getHand(), handKeys);
	    descriptorExtractor.compute(ctx.getHand(), handKeys, handDesc);
	}
	featureDetector.detect(curr, imageKeys);
	descriptorExtractor.compute(curr, imageKeys, imageDesc);

	LinkedList matches = new LinkedList();
	descriptorMatcher.knnMatch(handDesc, imageDesc, matches, 2);

	LinkedList<DMatch> goodMatchesList = new LinkedList();

	float nndrRatio = 0.5f;

	for (int i = 0; i < matches.size(); i++)
	{
	    MatOfDMatch matofDMatch = (MatOfDMatch)matches.get(i);
	    DMatch[] dmatcharray = matofDMatch.toArray();
	    DMatch m1 = dmatcharray[0];
	    DMatch m2 = dmatcharray[1];

	    if (m1.distance <= m2.distance * nndrRatio)
	    {
		goodMatchesList.addLast(m1);

	    }
	}

	MatOfDMatch goodMatches = new MatOfDMatch();
	goodMatches.fromList(goodMatchesList);

	Mat matchoutput = new Mat();
	Features2d.drawMatches(ctx.getHand(), handKeys, curr, imageKeys, goodMatches, matchoutput, blue, new Scalar(0, 0, 0), new MatOfByte(), 2);

	return matchoutput;*/
	Mat result = new Mat();
	Mat edges = new Mat();
	Mat edges2 = new Mat();

	Mat template_ = template.clone();
	Imgproc.Canny(template_, edges, 50, 200);

	Mat curr_ = curr.clone();
	Imgproc.Canny(curr_, edges2, 50, 200);

	Imgproc.matchTemplate(edges2, edges, result, match_method);
	Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

	MinMaxLocResult mmr = Core.minMaxLoc(result);

	Point matchLoc;
	if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
	    matchLoc = mmr.minLoc;
	} else {
	    matchLoc = mmr.maxLoc;
	}


	Imgproc.rectangle(curr, matchLoc, new Point(matchLoc.x + template.cols(),
	                                            matchLoc.y + template.rows()), blue, 1);

	ctx.setHandRect(new Rect(matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows())));
	return curr;

    }
}

