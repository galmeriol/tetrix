/*
 * 
 */
package capture;

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
import org.opencv.video.*;
import org.opencv.videoio.*;
import org.opencv.objdetect.*;
import org.opencv.core.Mat;
import org.opencv.ml.*;
import org.opencv.features2d.*;


public class FrameHelper {

    private Context ctx = new Context();


    //TODO bunlar context içine gidecek.

    int SAMPLE_NUM = 7;

    Mat tmpMat = new Mat();
    Mat binMat = new Mat();
    Mat rgbaMat = new Mat();
    Mat rgbMat = new Mat();

    Mat interMat = new Mat();

    Rect boundingRect = new Rect();

    Mat[] sampleMats = new Mat[SAMPLE_NUM];

    private double[][] cLower = new double[SAMPLE_NUM][3];
    private double[][] cUpper = new double[SAMPLE_NUM][3];
    private double[][] cBackLower = new double[SAMPLE_NUM][3];
    private double[][] cBackUpper = new double[SAMPLE_NUM][3];

    private Scalar lowerBound = new Scalar(0, 0, 0);
    private Scalar upperBound = new Scalar(0, 0, 0);


    public List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    public int cMaxId = -1;
    public Mat hie = new Mat();
    public List<MatOfPoint> hullP = new ArrayList<MatOfPoint>();
    public MatOfInt hullI = new MatOfInt();
    public MatOfInt4 defects = new MatOfInt4();

    public ArrayList<Integer> defectIdAfter = new ArrayList<Integer>();


    public List<Point> fingerTips = new ArrayList<Point>();
    public List<Point> fingerTipsOrder = new ArrayList<Point>();
    //public Map<Double, Point> fingerTipsOrdered = new TreeMap<Double, Point>();

    public MatOfPoint2f defectMat = new MatOfPoint2f();
    public List<Point> defectPoints = new ArrayList<Point>();
    //public Map<Double, Integer> defectPointsOrdered = new TreeMap<Double, Integer>();

    public Point palmCenter = new Point();
    public MatOfPoint2f hullCurP = new MatOfPoint2f();
    public MatOfPoint2f approxHull = new MatOfPoint2f();

    public MatOfPoint2f approxContour = new MatOfPoint2f();

    public MatOfPoint palmDefects = new MatOfPoint();

    public Point momentCenter = new Point();
    public double momentTiltAngle;

    public Point inCircle = new Point();

    public double inCircleRadius;

    public List<Double> features = new ArrayList<Double>();

    private boolean isHand = false;

    private float[] palmCircleRadius = {0};


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

	/*Imgproc.GaussianBlur(ctx.getMainFrame(), ctx.getFrameHSV(), new Size(11,11), 0, 0);
	Imgproc.GaussianBlur(ctx.getMainFrame(), ctx.getFrameHSV(), new Size(11,11), 0, 0);
	Imgproc.medianBlur(ctx.getFrameHSV(), ctx.getFrameHSV(), 3);
	Imgproc.medianBlur(ctx.getFrameHSV(), ctx.getFrameHSV(), 3);*/

	Imgproc.cvtColor(ctx.getMainFrame(), ctx.getFrameHSV(), Imgproc.COLOR_BGR2HSV);

	thresholdFrame();

	/*Core.inRange(ctx.getFrameHSV(), 
	             new Scalar(threshold - 50 >= 0 ? threshold - 50 : 0, 
	                                            threshold, 
	                                            threshold + 30 > 255 ? 255 : threshold + 50, 255), 
	                                            new Scalar(threshold + 100, 
	                                                       threshold + 150 > 255 ? 255 : threshold + 100 , 
	                                                                             threshold + 180 > 255 ? 255 : threshold + 150, 255), 
	                                                                             ctx.getFrameThr());
	 */
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

    void HandDetection()
    {
	ctx.getContours().clear();

	thresholdFrame();

	Mat thr = ctx.getFrameThr().clone();
	Mat frame = ctx.getMainFrame().clone();

	Mat hierarchy = new Mat();
	int max_contour_idx = 0;
	Imgproc.findContours(thr, ctx.getContours(), hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
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

	Imgproc.drawContours(frame, ctx.getContours(), ctx.getBiggestContour(), new Scalar(250, 250, 250), 3);

	ctx.setMainFrame(frame);
	ctx.setFrameThr(thr);
	/*

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
	 */
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
    }


    public List<Mat> notify(Mat frame, HashMap params){
	System.out.println("notified");
	this.ctx.getTetrixBag().clear();
	this.ctx.getTetrixBag().putAll(params);
	return mygesturedetect();
    }

    void subtractBackground(){
	BackgroundSubtractorMOG2 subtractor = Video.createBackgroundSubtractorMOG2(3000, 10, true);


	Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9), new Point(4, 4));  


	Imgproc.blur(ctx.getMainFrame().submat(ctx.getBackgroundSample()), ctx.getFrameSubtracted(), new Size(3, 3));


	subtractor.apply(ctx.getMainFrame(), ctx.getFrameSubtracted());



	subtractor.getBackgroundImage(ctx.getFrameBackground());


	Imgproc.morphologyEx(ctx.getFrameSubtracted(), ctx.getFrameSubtracted(), Imgproc.MORPH_DILATE, element);  

	Imgproc.threshold(ctx.getFrameSubtracted(), ctx.getFrameSubtracted(), 128, 255, Imgproc.THRESH_BINARY);

    }

    void createHandGuide(Mat im){

	int cols = im.cols();
	int rows = im.rows();

	double scale = 0.7;

	ctx.setSquareLen(scale*(rows/20));


	Point P_0 = new Point(scale*(cols/2), scale*(rows/4));
	Point P_1 = new Point(scale*(cols*5/12), scale*(rows*5/12));
	Point P_2 = new Point(scale*(cols*7/12), scale*(rows*5/12));
	Point P_3 = new Point(scale*(cols/2), scale*(rows*7/12));
	Point P_4 = new Point(scale*(cols/1.5), scale*(rows*7/12));
	Point P_5 = new Point(scale*(cols*4/9), scale*(rows*3/4));
	Point P_6 = new Point(scale*(cols*5/9), scale*(rows*3/4));

	List<Point> listOfPoints = new ArrayList<Point>();
	listOfPoints.add(P_0);listOfPoints.add(P_1);listOfPoints.add(P_2);listOfPoints.add(P_3);
	listOfPoints.add(P_4);listOfPoints.add(P_5);listOfPoints.add(P_6);

	for (int i = 0; i < 7; i++)
	{
	    Point P_INIT = listOfPoints.get(i);

	    ctx.getGuideRects().add(new Rect(P_INIT,
	                                     new Point(P_INIT.x + ctx.getSquareLen() , P_INIT.y + ctx.getSquareLen())));
	}
    }

    void handModeling() {

	if(ctx.getGuideRects().size() < 1)
	    createHandGuide(ctx.getFrameForHandModeling());

	for (Rect rect : ctx.getGuideRects()) {
	    Imgproc.rectangle(ctx.getFrameForHandModeling(), rect.tl(), rect.br(), new Scalar(0, 200, 0), 2);
	}

	Imgproc.putText(ctx.getFrameForHandModeling(), "Elinizi noktalari kapatacak sekilde yerlestirin...",
	                new Point(30, 30), Core.FONT_HERSHEY_COMPLEX_SMALL, 0.8, new Scalar(200, 200, 250));


	int COLOR_SPACE = Imgproc.COLOR_RGB2Lab;

	Mat frameForColorModeling = new Mat();

	Imgproc.cvtColor(ctx.getFrameForHandModeling(), frameForColorModeling , COLOR_SPACE);

	ctx.setAvgColor(new double[7][3]);
	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		try{
		    ctx.getAvgColor()[i][j] = (
			    frameForColorModeling.get(
			                              (int)(ctx.getGuideRects().get(i).y+(ctx.getSquareLen()/2)), 
			                              (int)(ctx.getGuideRects().get(i).x+(ctx.getSquareLen()/2))))[j];
		}
		catch(Exception ex){
		    System.out.println(i + ". rect, " + j + ". color");
		}
	    }
	}

	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		System.out.println(ctx.getAvgColor()[i][j]);
	    }
	}

    }

    void createBackgroundGuide(Mat im){
	int cols = im.cols();
	int rows = im.rows();

	double scale = 0.7;

	ctx.setSquareLen(scale*(rows/20));


	Point P_0 = new Point(scale*(cols/6), scale*(rows/3));
	Point P_1 = new Point(scale*(cols*6), scale*(rows*2/3));
	Point P_2 = new Point(scale*(cols/2), scale*(rows/6));
	Point P_3 = new Point(scale*(cols/2), scale*(rows/2));
	Point P_4 = new Point(scale*(cols/2), scale*(rows*5/6));
	Point P_5 = new Point(scale*(cols*5/6), scale*(rows/3));
	Point P_6 = new Point(scale*(cols*5/6), scale*(rows*2/3));

	List<Point> listOfPoints = new ArrayList<Point>();
	listOfPoints.add(P_0);listOfPoints.add(P_1);listOfPoints.add(P_2);listOfPoints.add(P_3);
	listOfPoints.add(P_4);listOfPoints.add(P_5);listOfPoints.add(P_6);

	for (int i = 0; i < 7; i++)
	{
	    Point P_INIT = listOfPoints.get(i);

	    ctx.getBackGuideRect().add(new Rect(P_INIT,
	                                        new Point(P_INIT.x + ctx.getSquareLen() , P_INIT.y + ctx.getSquareLen())));
	}
    }

    void backgroundModeling(){

	if(ctx.getBackGuideRect().size() < 1)
	    createBackgroundGuide(ctx.getFrameForBackModeling());

	for (Rect rect : ctx.getBackGuideRect()) {
	    Imgproc.rectangle(ctx.getFrameForBackModeling(), rect.tl(), rect.br(), new Scalar(0, 200, 0), 2);
	}

	Imgproc.putText(ctx.getFrameForBackModeling(), "Elinizi noktalari kapatacak sekilde yerlestirin...",
	                new Point(30, 30), Core.FONT_HERSHEY_COMPLEX_SMALL, 0.8, new Scalar(200, 200, 250));


	int COLOR_SPACE = Imgproc.COLOR_RGB2Lab;

	Mat frameForColorModeling = new Mat();

	Imgproc.cvtColor(ctx.getFrameForBackModeling(), frameForColorModeling , COLOR_SPACE);

	ctx.setAvgColor(new double[7][3]);
	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		try{
		    ctx.getAvgColor()[i][j] = (
			    frameForColorModeling.get(
			                              (int)(ctx.getBackGuideRect().get(i).y+(ctx.getSquareLen()/2)), 
			                              (int)(ctx.getBackGuideRect().get(i).x+(ctx.getSquareLen()/2))))[j];
		}
		catch(Exception ex){
		    System.out.println(i + ". rect, " + j + ". color");
		}
	    }
	}

	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		System.out.println(ctx.getAvgColor()[i][j]);
	    }
	}
    }


    Rect makeBoundingBox(Mat img)
    {
	ctx.getContours().clear();
	Imgproc.findContours(img, ctx.getContours(), new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

	if (ctx.getBiggestContour() > -1) {

	    boundingRect = Imgproc.boundingRect(ctx.getContours().get(ctx.getBiggestContour()));
	}

	return boundingRect;
    }


    void cropBinImg(Mat imgIn, Mat imgOut)
    {
	Mat binIm = new Mat();
	imgIn.copyTo(binIm);

	Rect boxRect = makeBoundingBox(binIm);
	Rect finalRect = null;

	if (boxRect!=null) {
	    Mat roi = new Mat(imgIn, boxRect);
	    int armMargin = 2;

	    Point tl = boxRect.tl();
	    Point br = boxRect.br();

	    int colNum = imgIn.cols();
	    int rowNum = imgIn.rows();

	    int wristThresh = 10;

	    List<Integer> countOnes = new ArrayList<Integer>();

	    if (tl.x < armMargin) {
		double rowLimit = br.y;
		int localMinId = 0;
		for (int x = (int)tl.x; x < br.x; x++)
		{
		    int curOnes = Core.countNonZero(roi.col(x));
		    int lstTail = countOnes.size()-1;
		    if (lstTail >= 0) {
			if (curOnes < countOnes.get(lstTail)) {
			    localMinId = x;
			}
		    }

		    if (curOnes > (countOnes.get(localMinId) + wristThresh))
			break;

		    countOnes.add(curOnes);
		}

		Rect newBoxRect = new Rect(new Point(localMinId, tl.y), br);
		roi = new Mat(imgIn, newBoxRect);

		Point newtl = newBoxRect.tl();
		Point newbr = newBoxRect.br();

		int y1 = (int)newBoxRect.tl().y;
		while (Core.countNonZero(roi.row(y1)) < 2) {
		    y1++;
		}

		int y2 = (int)newBoxRect.br().y;
		while (Core.countNonZero(roi.row(y2)) < 2) {
		    y2--;
		}
		finalRect = new Rect(new Point(newtl.x, y1), new Point(newbr.x, y2));
	    } else if (br.y > rowNum - armMargin) {
		double rowLimit = br.y;



		int scanCount = 0;
		int scanLength = 8;
		int scanDelta = 8;
		int y;
		for (y = (int)br.y - 1; y > tl.y; y--)
		{
		    int curOnes = Core.countNonZero((roi.row(y - (int)tl.y)));
		    int lstTail = countOnes.size()-1;
		    if (lstTail >= 0) {
			countOnes.add(curOnes);

			if (scanCount % scanLength == 0) {
			    int curDelta = curOnes - countOnes.get(scanCount-5);
			    if (curDelta > scanDelta)
				break;
			}


		    } else
			countOnes.add(curOnes);

		    scanCount++;
		}
		finalRect = new Rect(tl, new Point(br.x, y+scanLength));
	    }

	    if (finalRect!=null) {
		roi = new Mat(imgIn, finalRect);
		roi.copyTo(tmpMat);
		imgIn.copyTo(imgOut);
		imgOut.setTo(Scalar.all(0));
		roi = new Mat(imgOut, finalRect);
		tmpMat.copyTo(roi);
	    }
	}

    }

    void produceBinHandImg(Mat imgIn, Mat imgOut)
    {
	for (int i = 0; i < 7; i++)
	{
	    lowerBound.set(new double[]{ctx.getAvgColor()[i][0]-cLower[i][0], ctx.getAvgColor()[i][1]-cLower[i][1],
	                                ctx.getAvgColor()[i][2]-cLower[i][2]});
	    upperBound.set(new double[]{ctx.getAvgColor()[i][0]+cUpper[i][0], ctx.getAvgColor()[i][1]+cUpper[i][1],
	                                ctx.getAvgColor()[i][2]+cUpper[i][2]});




	    Core.inRange(imgIn, lowerBound, upperBound, sampleMats[i]);


	}

	imgOut.release();
	sampleMats[0].copyTo(imgOut);


	for (int i = 1; i < SAMPLE_NUM; i++)
	{
	    Core.add(imgOut, sampleMats[i], imgOut);
	}



	Imgproc.medianBlur(imgOut, imgOut, 3);
    }

    void boundariesCorrection()
    {
	for (int i = 1; i < SAMPLE_NUM; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		cLower[i][j] = cLower[0][j];
		cUpper[i][j] = cUpper[0][j];

		cBackLower[i][j] = cBackLower[0][j];
		cBackUpper[i][j] = cBackUpper[0][j];
	    }
	}

	for (int i = 0; i < SAMPLE_NUM; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		if (ctx.getAvgColor()[i][j] - cLower[i][j] < 0)
		    cLower[i][j] = ctx.getAvgColor()[i][j];

		if (ctx.getAvgColor()[i][j] + cUpper[i][j] > 255)
		    cUpper[i][j] = 255 - ctx.getAvgColor()[i][j];

		if (ctx.getAvgColor()[i][j] - cBackLower[i][j] < 0)
		    cBackLower[i][j] = ctx.getAvgColor()[i][j];

		if (ctx.getAvgColor()[i][j] + cBackUpper[i][j] > 255)
		    cBackUpper[i][j] = 255 - ctx.getAvgColor()[i][j];
	    }
	}
    }

    void produceBinImg(Mat imgIn, Mat imgOut)
    {
	int colNum = imgIn.cols();
	int rowNum = imgIn.rows();
	int boxExtension = 0;

	boundariesCorrection();

	Mat binTmpMat = new Mat();
	Mat binTmpMat2 = new Mat();

	produceBinHandImg(imgIn, binTmpMat);


	produceBinBackImg(imgIn, binTmpMat2);


	Core.bitwise_and(binTmpMat, binTmpMat2, binTmpMat);
	binTmpMat.copyTo(tmpMat);
	binTmpMat.copyTo(imgOut);

	Rect roiRect = makeBoundingBox(tmpMat);
	//adjustBoundingBox(roiRect, binTmpMat);

	if (roiRect!=null) {
	    roiRect.x = Math.max(0, roiRect.x - boxExtension);
	    roiRect.y = Math.max(0, roiRect.y - boxExtension);
	    roiRect.width = Math.min(roiRect.width+boxExtension, colNum);
	    roiRect.height = Math.min(roiRect.height+boxExtension, rowNum);


	    Mat roi1 = new Mat(binTmpMat, roiRect);
	    Mat roi3 = new Mat(imgOut, roiRect);
	    imgOut.setTo(Scalar.all(0));

	    roi1.copyTo(roi3);

	    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));	
	    Imgproc.dilate(roi3, roi3, element, new Point(-1, -1), 2);

	    Imgproc.erode(roi3, roi3, element, new Point(-1, -1), 2);


	}

	cropBinImg(imgOut, imgOut);

    }


    void produceBinBackImg(Mat imgIn, Mat imgOut)
    {
	for (int i = 0; i < SAMPLE_NUM; i++)
	{


	    lowerBound.set(new double[]{ctx.getAvgColor()[i][0]-cBackLower[i][0], ctx.getAvgColor()[i][1]-cBackLower[i][1],
	                                ctx.getAvgColor()[i][2]-cBackLower[i][2]});
	    upperBound.set(new double[]{ctx.getAvgColor()[i][0]+cBackUpper[i][0], ctx.getAvgColor()[i][1]+cBackUpper[i][1],
	                                ctx.getAvgColor()[i][2]+cBackUpper[i][2]});


	    Core.inRange(imgIn, lowerBound, upperBound, sampleMats[i]);


	}

	imgOut.release();
	sampleMats[0].copyTo(imgOut);


	for (int i = 1; i < SAMPLE_NUM; i++)
	{
	    Core.add(imgOut, sampleMats[i], imgOut);
	}

	Core.bitwise_not(imgOut, imgOut);


	Imgproc.medianBlur(imgOut, imgOut, 7);


    }

    void initCLowerUpper(double cl1, double cu1, double cl2, double cu2, double cl3,
                         double cu3)
    {
	cLower[0][0] = cl1;
	cUpper[0][0] = cu1;
	cLower[0][1] = cl2;
	cUpper[0][1] = cu2;
	cLower[0][2] = cl3;
	cUpper[0][2] = cu3;
    }

    void initCBackLowerUpper(double cl1, double cu1, double cl2, double cu2, double cl3,
                             double cu3)
    {
	cBackLower[0][0] = cl1;
	cBackUpper[0][0] = cu1;
	cBackLower[0][1] = cl2;
	cBackUpper[0][1] = cu2;
	cBackLower[0][2] = cl3;
	cBackUpper[0][2] = cu3;
    }


    boolean isClosedToBoundary(Point pt, Mat img)
    {
	int margin = 5;
	if ((pt.x > margin) && (pt.y > margin) && 
		(pt.x < img.cols()-margin) &&
		(pt.y < img.rows()-margin)) {
	    return false;
	}

	return true;
    }


    void makeContours(){
	ctx.getContours().clear();
	Imgproc.findContours(binMat, ctx.getContours(), new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

	//Find biggest contour and return the index of the contour, which is hg.cMaxId

	int biggestContour = ctx.getBiggestContour();

	if (biggestContour > -1) {

	    MatOfPoint bigg = ctx.getContours().get(biggestContour);

	    MatOfPoint2f curve = new MatOfPoint2f();
	    curve.fromList(bigg.toList());

	    Imgproc.approxPolyDP(curve, curve, 2, true);

	    bigg.fromList(curve.toList());

	    Imgproc.drawContours(ctx.getMainFrame(), ctx.getContours(), ctx.getBiggestContour(), new Scalar(0, 255, 230), 1);


	    //Palm center is stored in hg.inCircle, radius of the inscribed circle is stored in hg.inCircleRadius
	    //hg.findInscribedCircle(rgbaMat);


	    boundingRect = Imgproc.boundingRect(bigg);

	    Imgproc.convexHull(bigg, ctx.getHull()[0], false);

	    List<MatOfPoint> hullP = new ArrayList<MatOfPoint>();
	    for (int i = 0; i < ctx.getContours().size(); i++)
		hullP.add(new MatOfPoint());


	    int[] cId = ctx.getHull()[0].toArray();
	    List<Point> lp = new ArrayList<Point>();
	    Point[] contourPts = bigg.toArray();

	    for (int i = 0; i < cId.length; i++)
	    {
		lp.add(contourPts[cId[i]]);
		//Core.circle(rgbaMat, contourPts[cId[i]], 2, new Scalar(241, 247, 45), -3);
	    }

	    //hg.hullP.get(hg.cMaxId) returns the locations of the points in the convex hull of the hand
	    hullP.get(ctx.getBiggestContour()).fromList(lp);
	    lp.clear();


	    fingerTips.clear();
	    defectPoints.clear();
	    //defectPointsOrdered.clear();

	    //fingerTipsOrdered.clear();
	    defectIdAfter.clear();


	    if ((contourPts.length >= 5) 
		    && /*hg.detectIsHand(rgbaMat) &&*/ (cId.length >=5)){
		Imgproc.convexityDefects(bigg, ctx.getHull()[0], defects);
		List<Integer> dList = defects.toList();


		Point prevPoint = null;

		for (int i = 0; i < dList.size(); i++)
		{
		    int id = i % 4;
		    Point curPoint;

		    if (id == 2) { //Defect point
			double depth = (double)dList.get(i+1)/256.0;
			curPoint = contourPts[dList.get(i)];

			Point curPoint0 = contourPts[dList.get(i-2)];
			Point curPoint1 = contourPts[dList.get(i-1)];
			Point vec0 = new Point(curPoint0.x - curPoint.x, curPoint0.y - curPoint.y);
			Point vec1 = new Point(curPoint1.x - curPoint.x, curPoint1.y - curPoint.y);
			double dot = vec0.x*vec1.x + vec0.y*vec1.y;
			double lenth0 = Math.sqrt(vec0.x*vec0.x + vec0.y*vec0.y);
			double lenth1 = Math.sqrt(vec1.x*vec1.x + vec1.y*vec1.y);
			double cosTheta = dot/(lenth0*lenth1);

			if ((depth > inCircleRadius*0.7)&&(cosTheta>=-0.7)
				&& (!isClosedToBoundary(curPoint0, rgbaMat))
				&&(!isClosedToBoundary(curPoint1, rgbaMat))
				){


			    defectIdAfter.add((i));


			    Point finVec0 = new Point(curPoint0.x-inCircle.x,
			                              curPoint0.y-inCircle.y);
			    double finAngle0 = Math.atan2(finVec0.y, finVec0.x);
			    Point finVec1 = new Point(curPoint1.x-inCircle.x,
			                              curPoint1.y - inCircle.y);
			    double finAngle1 = Math.atan2(finVec1.y, finVec1.x);


			}



		    }
		}


	    }

	}

	Imgproc.rectangle(rgbaMat, boundingRect.tl(), boundingRect.br(), new Scalar(0, 255, 0), 2);
	Imgproc.drawContours(rgbaMat, hullP, ctx.getBiggestContour(), new Scalar(0, 255, 255));

    }


    List<Mat> mygesturedetect() {

	List<Mat> processedIms = new ArrayList<Mat>();
	//subtractBackground();

	//facedetect();

	initCLowerUpper(50, 50, 10, 10, 10, 10);
	initCBackLowerUpper(50, 50, 3, 3, 3, 3);

	rgbaMat = ctx.getMainFrame().clone();

	Core.flip(rgbaMat, rgbaMat, 1);


	Imgproc.GaussianBlur(rgbaMat, rgbaMat, new Size(5,5), 5, 5);

	Imgproc.cvtColor(rgbaMat, interMat, Imgproc.COLOR_BGR2Lab);

	Mode mode = ctx.getMODE();


	if(ctx.getAvgColor() == null){
	    Imgproc.putText(ctx.getMainFrame(), "Sampling calistirilmadan detection calismaz.",
	                    new Point(30, 30), Core.FONT_HERSHEY_COMPLEX_SMALL, 0.8, new Scalar(200, 200, 250));

	    processedIms.add(ctx.getMainFrame());

	    return processedIms;
	}


	if (mode == Mode.SAMPLE) { //Second mode which presamples the colors of the hand  

	    handModeling();

	} else if (mode == Mode.DETECT) {
	    produceBinImg(interMat, binMat);

	    processedIms.add(binMat);
	    return processedIms;

	} else if (mode == Mode.TEST){

	    produceBinImg(interMat, binMat);

	    makeContours();
	} else if(mode == Mode.BACKGROUND){
	    backgroundModeling();
	}

	processedIms.add(ctx.getMainFrame());

	return processedIms;
    }
}
