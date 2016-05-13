package models;

import java.util.Comparator;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.opencv.core.*;
import org.opencv.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.*;
import org.opencv.utils.*;
import org.opencv.photo.*;
import org.opencv.videoio.*;
import org.opencv.objdetect.*;
import org.opencv.core.Mat;
import org.opencv.ml.*;
import org.opencv.features2d.*;

public class SkinColorBasedModel {

   /* public SkinColorBasedModel(Context context) {
	setCtx(context);
    }
    
    Context ctx = null;
    
    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }
    
    void thresholdFrame(){
	Mat frame = ctx.getMainFrame().clone();
	Mat thr = ctx.getFrameThr();

	Imgproc.cvtColor(frame, ctx.getFrameThr(), Imgproc.COLOR_BGR2GRAY);

	int threshold = 0;
	if(ctx.getTetrixBag().containsKey("threshold"))
	    threshold = Integer.parseInt(ctx.getTetrixBag().get("threshold").toString());
	else{
	    threshold = 80;
	}
	System.out.println("threshold : " + threshold);
	Imgproc.threshold(thr, thr, threshold, 255, Imgproc.THRESH_BINARY);

	ctx.setFrameThr(thr);

    }

    void facedetect()
    {
	//System.out.println("info: face detection started");
	Mat frame = ctx.getMainFrame().clone();
	Mat thr = ctx.getFrameThr();
	Rect p = ctx.getFaceregion();
	p.height = 0;
	p.width = 0;
	p.x = 0;
	int maxarea = -1;
	p.y = 0;

	//double morph_size = 0.5;

	//Mat element = Imgproc.getStructuringElement( Imgproc.MORPH_RECT, new Size( 2*morph_size + 1, 2*morph_size+1 ), new Point( morph_size, morph_size ) );

	//Imgproc.blur(ctx.getMainFrame(), ctx.getFrameHSV(), new Size(3, 3));

	Imgproc.GaussianBlur(ctx.getMainFrame(), ctx.getFrameHSV(), new Size(11,11), 0, 0);
	Imgproc.GaussianBlur(ctx.getMainFrame(), ctx.getFrameHSV(), new Size(11,11), 0, 0);
	Imgproc.medianBlur(ctx.getFrameHSV(), ctx.getFrameHSV(), 3);
	Imgproc.medianBlur(ctx.getFrameHSV(), ctx.getFrameHSV(), 3);

	Imgproc.cvtColor(ctx.getMainFrame(), ctx.getFrameHSV(), Imgproc.COLOR_BGR2HSV);

	thresholdFrame();

	Core.inRange(ctx.getFrameHSV(), 
	             new Scalar(threshold - 50 >= 0 ? threshold - 50 : 0, 
	                                            threshold, 
	                                            threshold + 30 > 255 ? 255 : threshold + 50, 255), 
	                                            new Scalar(threshold + 100, 
	                                                       threshold + 150 > 255 ? 255 : threshold + 100 , 
	                                                                             threshold + 180 > 255 ? 255 : threshold + 150, 255), 
	                                                                             ctx.getFrameThr());
	 
	//Imgproc.equalizeHist(ctx.getFrameThr(), ctx.getFrameThr());


	//Imgproc.morphologyEx(ctx.getFrameHSV(), ctx.getFrameHSV(), Imgproc.MORPH_OPEN, element, new Point(-1, -1), 1);

	if (ctx.getFaceSize() == 0)
	{
	    int height = ctx.getFrameThr().rows();
	    if (Math.round(height * 0.2f) > 0)
	    {
		ctx.setFaceSize(Math.round(height * 0.4f));
	    }
	}

	MatOfRect faces_ = new MatOfRect();
	ctx.getFace_cascade().detectMultiScale(ctx.getFrameThr(), 
	                                       faces_, 
	                                       1.3, 
	                                       2, 
	                                       0 | Objdetect.CASCADE_SCALE_IMAGE, 
	                                       new Size(ctx.getFaceSize(), ctx.getFaceSize()), 
	                                       new Size());
	ctx.setFaces(faces_.toArray());
	//System.out.println(faces.length + " adet yüz bulundu.");
	for (Rect face: ctx.getFaces()) {
	    if(face.area()>maxarea){
		p = face;
		maxarea = (int)face.area();
	    }
	}
	//Mat mask = Mat.zeros(frame.size(), CvType.CV_8UC3);

	Mat ROI = frame.submat(p);

	if(ROI.width() > 0){
	    frame.submat(p).setTo(new Scalar(0, 0, 0));
	    thr.submat(p).setTo(new Scalar(0, 0, 0));
	}

	Imgproc.rectangle(frame,
	                  p.tl(),
	                  p.br(),
	                  new Scalar(0, 0, 0),
	                  3);

	ctx.setFrameThr(thr);
	ctx.setMainFrame(frame);
    }

    void Get_hull()
    {
	//System.out.println("info: hull fitting started");



	ctx.setDefects(new MatOfInt4[ctx.getContours().size()]);
	ctx.setHull(new MatOfInt[ctx.getContours().size()]);
	ctx.getFingerseq().clear();


	for (int i = 0; i < ctx.getContours().size(); i++) {

	    ctx.getDefects()[i] = new MatOfInt4();
	    ctx.getHull()[i] = new MatOfInt();

	    MatOfPoint contour = ctx.getContours().get(i);

	    Imgproc.convexHull(contour, ctx.getHull()[i], false);
	    Imgproc.convexityDefects(contour, ctx.getHull()[i], ctx.getDefects()[i]);

	    ctx.getFingerseq().clear();
	    int[] intlist = ctx.getHull()[i].toArray();
	    for(int j=0;j < intlist.length; j++)
		ctx.getFingerseq().add(contour.toList().get(ctx.getHull()[i].toList().get(j)));

	}
	ctx.getPalm().clear();
	for (int i = 0; i< ctx.getDefects().length; i+=4)
	{
	    Point contour[] = ctx.getContours().get(i).toArray();
	    List<Integer> df = ctx.getDefects()[i].toList();

	    for (int j = 0; j < df.size(); j = j+4) {

		Point dept_p = contour[df.get(j+2)];
		float depth_f = df.get(j+3)/256.0f;

		if (depth_f > 10)
		{
		    ctx.getP().x = dept_p.x;
		    ctx.getP().y = dept_p.y;
		    Imgproc.circle(ctx.getMainFrame(), ctx.getP(), 15, new Scalar(255, 0, 0), -1, 8, 0);
		    ctx.getPalm().add(dept_p);
		}
	    }

	}

	//System.out.println("info: hull fitting succeeded");

    }

    void findBiggestContour(){
	int max_contour_idx = 0;
	double max_area = 0;

	for (int i = 0; i < ctx.getContours().size();i++){
	    MatOfPoint tmp = ctx.getContours().get(i);

	    double area = Math.abs(Imgproc.contourArea(tmp, false));
	    if (area > max_area) {
		max_area = area;
		max_contour_idx = i;
	    }
	}
	ctx.setBiggestContour(max_contour_idx);
    }

    void HandDetection()
    {
	ctx.getContours().clear();

	thresholdFrame();

	Mat thr = ctx.getFrameThr().clone();
	Mat frame = ctx.getMainFrame().clone();

	Mat hierarchy = new Mat();
	Imgproc.findContours(thr, ctx.getContours(), hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


	findBiggestContour();

	Imgproc.drawContours(frame, ctx.getContours(), ctx.getBiggestContour(), new Scalar(250, 250, 250), 3);

	ctx.setMainFrame(frame);
	ctx.setFrameThr(thr);
	

	Runnable drawContours = new Runnable() {
	    Mat frame = new Mat();
	    Mat hierarchy = new Mat();
	    public void run() {
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
		{
		    for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
		    {

		    }
		}
	    }

	    public Runnable init(Mat frame, Mat hierarchy){
		this.frame = frame;
		this.hierarchy = hierarchy;
		return this;
	    }
	}.init(ctx.getMainFrame(), hierarchy);
	 
    }

    Comparator<Point> point_compare(){
	return new Comparator<Point>() {

	    public int compare(Point o1, Point o2) {
		if(o1 != null && o2 != null){
		    if(o1.x > o2.x) 
			return 1;
		    else if(o1.x < o2.x) 
			return -1;
		    else
			return 0;
		}
		else
		    return 0;
	    }
	}; 
    }*/
}
