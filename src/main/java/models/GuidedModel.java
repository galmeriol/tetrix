package models;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class GuidedModel {
/*
    
    public GuidedModel(Context context) {
	setCtx(context);
    }
    

    private Context ctx = new Context();


    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }

    int COLOR_SPACE = Imgproc.COLOR_RGB2HSV;

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




	Mat frameForColorModeling = new Mat();

	Imgproc.cvtColor(ctx.getFrameForHandModeling(), frameForColorModeling , COLOR_SPACE);

	ctx.setAvgColorHand(new double[7][3]);
	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		try{
		    ctx.getAvgColorHand()[i][j] = (
			    frameForColorModeling.get(
			                              (int)(ctx.getGuideRects().get(i).y+(ctx.getSquareLen()/2)), 
			                              (int)(ctx.getGuideRects().get(i).x+(ctx.getSquareLen()/2))))[j];
		}
		catch(Exception ex){
		    System.out.println("row:" + ctx.getGuideRects().get(i).y+(ctx.getSquareLen()/2));
		    System.out.println("col:" + ctx.getGuideRects().get(i).x+(ctx.getSquareLen()/2));
		    System.out.println(i + ". rect, " + j + ". color");
		}
	    }
	}

	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		System.out.println(ctx.getAvgColorHand()[i][j]);
	    }
	}

    }

    void createBackgroundGuide(Mat im){
	int cols = im.cols();
	int rows = im.rows();

	double scale = 1;

	ctx.setSquareLen(scale*(rows/20));


	Point P_0 = new Point(scale*(cols/6), scale*(rows/3));
	Point P_1 = new Point(scale*(cols/6), scale*(rows*2/3));
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

	Mat back = ctx.getFrameForBackModeling();
	System.out.println("background modeling...");
	if(ctx.getBackGuideRect().size() < 1)
	    createBackgroundGuide(ctx.getMainFrame());

	for (Rect rect : ctx.getBackGuideRect()) {
	    Imgproc.rectangle(back, rect.tl(), rect.br(), new Scalar(0, 200, 0), 2);
	}

	Imgproc.putText(back, "Elinizi noktalari kapatacak sekilde yerlestirin...",
	                new Point(30, 30), Core.FONT_HERSHEY_COMPLEX_SMALL, 0.8, new Scalar(200, 200, 250));



	Mat frameForColorModeling = new Mat();

	Imgproc.cvtColor(ctx.getMainFrame(), frameForColorModeling , COLOR_SPACE);

	ctx.setAvgColorBack(new double[7][3]);

	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		try{
		    ctx.getAvgColorBack()[i][j] = (
			    frameForColorModeling.get(
			                              (int)(ctx.getBackGuideRect().get(i).y+(ctx.getSquareLen()/2)), 
			                              (int)(ctx.getBackGuideRect().get(i).x+(ctx.getSquareLen()/2))))[j];
		}
		catch(Exception ex){
		    ex.printStackTrace();
		}
	    }
	}

	for (int i = 0; i < 7; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		System.out.println(i + ". rect, " + j + ". color a");
		System.out.println(ctx.getAvgColorBack()[i][j]);
	    }
	}
	ctx.setFrameForBackModeling(back);
    }


    Rect makeBoundingBox(Mat img)
    {
	ctx.getContours().clear();
	Imgproc.findContours(img, ctx.getContours(), new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);	
	findBiggestContour();
	if (ctx.getContours().size() > 0 && ctx.getBiggestContour() > -1) {

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
	sampleMats = new Mat[SAMPLE_NUM];
	for (int i = 0; i < 7; i++)
	{
	    if(sampleMats[i] != null)
		sampleMats[i].release();
	    else
		sampleMats[i] = new Mat();

	    lowerBound.set(new double[]{ctx.getAvgColorHand()[i][0]-cLower[i][0], ctx.getAvgColorHand()[i][1]-cLower[i][1],
	                                ctx.getAvgColorHand()[i][2]-cLower[i][2]});
	    upperBound.set(new double[]{ctx.getAvgColorHand()[i][0]+cUpper[i][0], ctx.getAvgColorHand()[i][1]+cUpper[i][1],
	                                ctx.getAvgColorHand()[i][2]+cUpper[i][2]});


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

    double[][] boundariesCorrection(double[][] avgColor)
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
		if (avgColor[i][j] - cLower[i][j] < 0)
		    cLower[i][j] = avgColor[i][j];

		if (avgColor[i][j] + cUpper[i][j] > 255)
		    cUpper[i][j] = 255 - avgColor[i][j];

		if (avgColor[i][j] - cBackLower[i][j] < 0)
		    cBackLower[i][j] = avgColor[i][j];

		if (avgColor[i][j] + cBackUpper[i][j] > 255)
		    cBackUpper[i][j] = 255 - avgColor[i][j];
	    }
	}

	return avgColor;
    }

    void produceBinImg(Mat imgIn)
    {
	Mat imOut = new Mat();

	int colNum = imgIn.cols();
	int rowNum = imgIn.rows();
	int boxExtension = 0;

	ctx.setAvgColorHand(boundariesCorrection(ctx.getAvgColorHand()));
	ctx.setAvgColorBack(boundariesCorrection(ctx.getAvgColorBack()));

	produceBinHandImg(imgIn.clone(), ctx.getFrameHandBin());


	produceBinBackImg(imgIn.clone(), ctx.getFrameBackgroundBin());


	Mat binTMP = new Mat(); Mat binTMP2 = new Mat();


	Core.bitwise_and(ctx.getFrameHandBin(), ctx.getFrameBackgroundBin(), binTMP);

	binTMP.copyTo(imOut);
	binTMP.copyTo(binTMP2);

	Rect roiRect = makeBoundingBox(binTMP2);
	//adjustBoundingBox(roiRect, binTmpMat);

	if (roiRect!=null) {
	    roiRect.x = Math.max(0, roiRect.x - boxExtension);
	    roiRect.y = Math.max(0, roiRect.y - boxExtension);
	    roiRect.width = Math.min(roiRect.width+boxExtension, colNum);
	    roiRect.height = Math.min(roiRect.height+boxExtension, rowNum);


	    Mat roi1 = new Mat(binTMP, roiRect);
	    Mat roi3 = new Mat(imOut, roiRect);
	    imOut.setTo(Scalar.all(0));

	    roi1.copyTo(roi3);
	    //
	    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));	
	    Imgproc.dilate(roi3, roi3, element, new Point(-1, -1), 2);

	    Imgproc.erode(roi3, roi3, element, new Point(-1, -1), 2);

	}
	//subtractBackground();
	ctx.setFrameHandBin(imOut);

	//cropBinImg(ctx.getFrameHandBin(), ctx.getFrameHandBin());

    }


    void produceBinBackImg(Mat imgIn, Mat imgOut)
    {
	sampleMats = new Mat[SAMPLE_NUM];
	for (int i = 0; i < SAMPLE_NUM; i++)
	{
	    if(sampleMats[i] != null)
		sampleMats[i].release();
	    else
		sampleMats[i] = new Mat();

	    lowerBound.set(new double[]{ctx.getAvgColorBack()[i][0]-cBackLower[i][0], ctx.getAvgColorBack()[i][1]-cBackLower[i][1],
	                                ctx.getAvgColorBack()[i][2]-cBackLower[i][2]});
	    upperBound.set(new double[]{ctx.getAvgColorBack()[i][0]+cBackUpper[i][0], ctx.getAvgColorBack()[i][1]+cBackUpper[i][1],
	                                ctx.getAvgColorBack()[i][2]+cBackUpper[i][2]});

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
    
    void makeContours(){
	ctx.getContours().clear();
	Imgproc.findContours(ctx.getFrameHandBin(), ctx.getContours(), new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

	findBiggestContour();

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


	    ctx.setHull(new MatOfInt[] { new MatOfInt() });
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
		    && hg.detectIsHand(rgbaMat) && (cId.length >=5)){
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
*/
}
