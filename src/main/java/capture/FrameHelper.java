package capture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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

    int ismyframeuse = 0;
    int palm_radius;

    CascadeClassifier face_cascade = new CascadeClassifier( 
                                                           System.getProperty("user.dir") + "/src/main/java/haarcascade_frontalface_default.xml");
    Mat mybackground = new Mat();
    float radius_palm_center = 0.0f;
    /*
    CvSeq fingerseq = new CvSeq();

    CvBox2D contour_center = new CvBox2D();*/
    List<Point> fingerseq = new ArrayList<Point>();
    List<Point> finger_dft = new ArrayList<Point>();
    List<Point> palm = new ArrayList<Point>();
    Point palm_center = new Point();
    Point p = new Point();
    MatOfInt4[] defects;
    MatOfInt[] hull;
    Point armcenter = new Point();
    RotatedRect contour_center;
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Rect[] faces;
    
    
    Rect facedetect(Mat frame, CascadeClassifier facecad)
    {
	//System.out.println("info: face detection started");
	Mat frameGray = new Mat();
	
	Rect p = new Rect();
	
	p.height = 0;
	p.width = 0;
	p.x = 0;
	int maxarea = -1;
	int maxareai = -1;
	p.y = 0;

	//Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);

	//Imgproc.equalizeHist(frameGray, frameGray); 

	MatOfRect faces_ = new MatOfRect();
	facecad.detectMultiScale(frameGray, 
	                         faces_/*, 
	                         1.1, 
	                         2, 
	                         0 | Objdetect.CASCADE_FIND_BIGGEST_OBJECT, 
	                         new Size(30, 30), 
	                         new Size(frame.width(), frame.height())*/);
	faces = faces_.toArray();
//	System.out.println(faces.length + " adet yüz bulundu.");
	for (Rect face: faces) //consider the first face
	{
	    int x = (int)face.x;
	    int halfWidth = (int)(face.width*0.5);

	    int y = (int)face.y;
	    int halfHeight = (int)(face.height*0.5);
	    Point center = new Point(x + halfWidth, y + halfHeight);


	    if(face.area()>maxarea){
		p = face;
		maxarea = (int)face.area();
	    }
	    Imgproc.ellipse(frame, 
	                    center, 
	                    new Size((int)(face.width*0.5), (int)(face.height*0.5)), 
	                    0.0, 
	                    0.0, 
	                    360.0, 
	                    new Scalar(0, 255, 255, 0),
	                    4, 
	                    8, 
	                    0);
	   // System.out.println("elips çizildi");
	}
	//System.out.println("info: face detection succeeded");
	return p;
    }


    void SkinColorModel(Mat frame, Rect faceregion, int ymax, int ymin, int crmax, int crmin, int cbmax, int cbmin)
    {
	//System.out.println("info: skin color modeling started");
	int y, cb, cr,r,b,g,gray;
	Mat p = new Mat();
	Imgproc.cvtColor(frame, p, Imgproc.COLOR_BGR2YCrCb);
	crmax = -1;
	crmin = 295;
	cbmax = -1;
	cbmin = 295;
	ymax = 295;
	ymin = -1;
	if (faceregion.area() > 5)
	{

	    for (int i = faceregion.x; i < faceregion.x + faceregion.width && i < frame.width(); i++)
	    {
		for (int j = faceregion.y; j < faceregion.y + faceregion.height && j<frame.height(); j++)
		{
		    double[] frameScalar = new double[3];
		    double[] pScalar = new double[3];

		    frameScalar = frame.get(j, i);
		    pScalar = p.get(j, i);

		    b = (int)frameScalar[0];
		    g = (int)frameScalar[1];
		    r = (int)frameScalar[2];

		    y = (int)pScalar[0];
		    cr = (int)pScalar[1];
		    cb = (int)pScalar[2];

		    gray = (int)(0.2989 * r + 0.5870 * g + 0.1140 * b);
		    if (gray<200 && gray>40 && r>g && r>b)
		    {
			ymax = (y > ymax) ? y : ymax;
			ymin = (y < ymin) ? y : ymin;
			crmax = (cr > crmax) ? cr : crmax;
			crmin = (cr < crmin) ? cr : crmin;
			cbmax = (cb > cbmax) ? cb : cbmax;
			cbmin = (cb < cbmin) ? cb : cbmin;
		    }
		}
	    }
	    /**ymin = *ymin - 10;
	     *ymax = *ymax + 10;
	     *crmin = *crmin - 10;
	     *crmax = *crmax + 10;
	     *cbmin = *cbmin - 10;
	     *cbmax = *cbmax + 10;*/
	}
	else
	{
	    ymax = 255;//(*ymax>163) ? 163 : *ymax;
	    ymin = 0;// (*ymin < 54) ? 54 : *ymin;
	    crmax = 173;// (*crmax > 173) ? 173 : *crmax;
	    crmin = 133;// (*crmin < 133) ? 133 : *crmin;
	    cbmax = 127;// (*cbmax > 127) ? 127 : *cbmax;
	    cbmin = 77;// (*cbmin < 77) ? 77 : *cbmin;
	}
	/**crmax = (*crmax > 173) ? 173 : *crmax;
	 *crmin = (*crmin < 133) ? 133 : *crmin;
	 *cbmax = (*cbmax > 127) ? 127 : *cbmax;
	 *cbmin = (*cbmin < 77) ? 77 : *cbmin;*/

	//System.out.println("info: skin color modeling succeeded");
    }

    void Get_hull(Mat frame)
    {
	//System.out.println("info: hull fitting started");
	defects = new MatOfInt4[contours.size()];
	hull = new MatOfInt[contours.size()];
	fingerseq.clear();
	for (int i = 0; i < contours.size(); i++) {
	    defects[i] = new MatOfInt4();
	    hull[i] = new MatOfInt();

	    MatOfPoint contour = contours.get(i);
	    
	    Imgproc.convexHull(contour, hull[i], false);
	    Imgproc.convexityDefects(contour, hull[i], defects[i]); 
	    fingerseq.clear();
	    int[] intlist = hull[i].toArray();
	    for(int j=0;j < intlist.length; j++)
		fingerseq.add(contour.toList().get(hull[i].toList().get(j)));

	}
	palm.clear();
	for (int i = 0; i< defects.length; i+=4)
	{
	    Point contour[] = contours.get(i).toArray();
	    List<Integer> df = defects[i].toList();

	    for (int j = 0; j < df.size(); j = j+4) {

		Point dept_p = contour[df.get(j+2)];
		float depth_f = df.get(j+3)/256.0f;

		if (depth_f > 10)
		{
		    p.x = dept_p.x;
		    p.y = dept_p.y;
		    Imgproc.circle(frame, p, 15, new Scalar(255, 0, 0), -1, 8, 0);
		    palm.add(dept_p);
		}
	    }
	    
	}

	//System.out.println("info: hull fitting succeeded");

    }
    int Get_Palm_Center(Mat frame)
    {
	//System.out.println("info: getting palm center point");
	Point distemp = new Point();
	int lengthtemp;
	int mydft = 0;
	palm_center.x = armcenter.x;
	palm_center.y = armcenter.y;
	if (palm.size() > 0)
	{

	    palm_center.x = 0;
	    palm_center.y = 0;
	    for (int i = 0; i < palm.size(); i++)
	    {
		Point temp = palm.get(i);
		palm_center.x =palm_center.x + temp.x;
		palm_center.y =palm_center.y + temp.y;
	    }

	    palm_center.x = (int)(palm_center.x / palm.size());
	    palm_center.y = (int)(palm_center.y / palm.size());
	    palm_radius = 0;

	    for (int i = 0; i < palm.size(); i++)
	    {
		Point temp = palm.get(i);
		distemp.x = temp.x - palm_center.x;
		distemp.y = temp.y - palm_center.y;
		lengthtemp = (int)Math.sqrt((distemp.x*distemp.x) + (distemp.y*distemp.y));
		palm_radius += lengthtemp;
	    }

	    palm_radius = (int)(palm_radius / palm.size());
	    if (palm_center.y > armcenter.y) {
		palm_center.x = armcenter.x;
		palm_center.y = armcenter.y;
	    }
	    if (palm.size() < 3) palm_radius = 0;
	    Imgproc.circle(frame, palm_center, 5, new Scalar(0, 255, 0), -1, 8, 0);
	    Imgproc.ellipse(frame, palm_center, new Size(palm_radius, palm_radius), 0, 0, 360, new Scalar(0.0, 255.0, 0.0, 0.0), 4, 8, 0);

	    finger_dft.clear();

	    for (int i = 0; i < palm.size(); i++)
	    {
		Point temp = palm.get(i);
		p.x = temp.x;
		p.y = temp.y;
		if (palm_center.x - palm_radius*1.9<p.x && palm_center.x + palm_radius*1.9>p.x &&palm_center.y - palm_radius*1.5<p.y &&palm_center.y + palm_radius*0.8>p.y)
		{
		    mydft++;
		    finger_dft.add(p);
		    Imgproc.circle(frame, p, 5, new Scalar(0, 255, 255), -1, 8, 0);
		}
	    }

	}
	//System.out.println("info: palm center point calculated");
	return mydft;

    }

    Mat HandDetection(Mat frame, Rect faceregion, int ymax, int ymin, int crmax, int crmin, int cbmax, int cbmin)
    {
	//System.out.println("info: hand detection started");
	Size sz = frame.size();


	MatOfPoint maxrecord = null;
	long max_contour_size=-1;

	//System.out.println(sz.width + "  " + sz.height);
	Mat mask = new Mat(new Size(sz.width, sz.height), CvType.CV_8UC1);


	if (faceregion.area() > 5)
	{
	    if (faceregion.y > faceregion.height / 4)
	    {
		faceregion.y = faceregion.y - (faceregion.height / 4);
		faceregion.height = faceregion.height + (faceregion.height / 4);
	    }
	    else
	    {
		faceregion.height = faceregion.height + faceregion.y;
		faceregion.y = 0;

	    }
	    //avoid noise for T-shirt
	    faceregion.height = faceregion.height + (faceregion.height / 2);
	}

	int y,cr,cb;
	//Turn to YCrCb
	Mat p = new Mat(); Mat b = new Mat();

	Imgproc.cvtColor(frame, p, Imgproc.COLOR_BGR2YCrCb);

	//Turn to Gray
	//cvtColor(frame, gray, CV_BGR2GRAY);
	//Imgproc.threshold(p, mask, 50, 150, 0);

	for(int i = 0;i < frame.cols(); i++)
	    for (int j = 0;j < frame.rows(); j++)
	    {
		double[] vals = p.get(j, i);
		y = (int)vals[0];
		cr = (int)vals[1];
		cb = (int)vals[2];

		if (y>ymin && y<ymax && cr<crmax && cr>crmin && cb<cbmax && cb>cbmin) {
		    mask = fill(mask, j, i, 255);
		}
		else {
		    mask = fill(mask, j, i, 0);
		}

		if (mybackground != null)
		{
		    //mybackground = cvCreateImage(sz, image.depth(), image.arrayChannels());
		    //b = new Mat(mybackground);
		    double[] valims = frame.get(j, i);
		    double[] valbacks = mybackground.get(j, i);
		    if (
			    Math.abs(
			             (int)valims[0] - 
			             (int)valbacks[0])<10
			             && 
			             Math.abs(
			                      (int)valims[1] - 
			                      (int)valbacks[1])<10 
			                      && 
			                      Math.abs(
			                               (int)valims[2] - 
			                               (int)valbacks[2])<10
			    )

			mask = fill(mask, j, i, 0);
		}

	    }

	for (int i = 0; i < faces.length; i++){
	    for (int j = faces[i].x; j < faces[i].x + faces[i].width - 1; j++){
		for (int k = faces[i].y; k < faces[i].y + faces[i].height - 1; k++){
		    try{
			mask = fill(mask, k, j, 0);
		    }
		    catch(RuntimeException ex){
			System.out.println(j + "  " + k);
		    }
		}
	    }
	}
	//mask = new Mat(maskmat);
	Imgproc.erode(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2))); //ERODE first then DILATE to eliminate the noises.
	Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2))); //ERODE first then DILATE to eliminate the noises.
	//Imgproc.morphologyEx(mask, mask, null, null, Imgproc.MORPH_OPEN, 1);
	//mask.convertTo(mask, CvType.CV_8UC1);
	Imgproc.findContours(mask, contours, new Mat(new Size(150, 100), 0),Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

	for(int z = 1; z < contours.size(); z++)
	{
	    Imgproc.drawContours(mask, contours, z, new Scalar(100.0, 50.0, 0.0, 0.0));
	}

	if (contours != null)
	{
	    for(MatOfPoint point : contours){
		contour_center = Imgproc.minAreaRect(new MatOfPoint2f(point.toArray()));
		armcenter.x = contour_center.center.x;
		armcenter.y = contour_center.center.y;
		Imgproc.circle(frame, armcenter,10, new Scalar(255,255,255),-1, 8, 0);
		Get_hull(frame);
	    }


	}
	//System.out.println("info: hand detection succeeded");
	return frame;
    }

    Mat fill(Mat mat, int i, int j, double val){
	double[] vals = new double[mat.channels()];
	for(int z = 0; z < mat.channels();z++)
	    vals[z] = val;
	mat.put(i, j, vals);
	return mat;
    }

    double get_cos_value(Point b, Point c)//palm_center - base point
    {
	Point a = new Point();
	a.x = palm_center.x;
	a.y = palm_center.y;
	double vec1x = b.x - a.x;
	double vec1y = b.y - a.y;
	double vec2x = c.x - a.x;
	double vec2y = c.y - a.y;
	return ((vec1x*vec2x + vec1y*vec2y)) / (Math.sqrt(((vec1x*vec1x) + (vec1y*vec1y)))*Math.sqrt(((vec2x*vec2x) + (vec2y*vec2y))));
    }
    
    Comparator<Point> point_compare(){
	return new Comparator<Point>() {
	    
	    public int compare(Point o1, Point o2) {
		if(o1 != null && o2 != null)
		    return (int)(o1.x - o2.x);
		else
		    return Integer.MAX_VALUE;
	    }
	}; 
    }
    
    int Get_fingertip(Mat frame) //number of fingertips
    {
	Point[] gaps = new Point[150];
	Point[] possible_tips = new Point[200];

	Point mypoint_temp = new Point(0.0, 0.0);
	
	Point tmp_cvpnt = new Point();
	int cnt_finger = 0;

	if (palm_radius == 0) return 0;
	
	for (int i = 0; i < finger_dft.size(); i++)
	{
	    gaps[i] = new Point();
	    Point temp = finger_dft.get(i);
	    gaps[i].x = temp.x;
	    gaps[i].y = temp.y;
	}
	gaps[finger_dft.size()] = new Point();
	gaps[finger_dft.size() + 1] = new Point();
	gaps[finger_dft.size() + 2] = new Point();
	
	
	gaps[finger_dft.size()].x = -1;
	gaps[finger_dft.size()].y = 0;//lower bound
	gaps[finger_dft.size() + 1].x = 30000;
	gaps[finger_dft.size() + 1].y = 0;
	gaps[finger_dft.size()+2].x = 30001;
	gaps[finger_dft.size() + 2].y = 30001;//higher bound
	Arrays.sort(gaps, point_compare());
	//std::qsort(gaps, finger_dft.total() + 3, sizeof(struct mypoint), qcompare1);
	for (int i = 0; i < fingerseq.size(); i++)
	{
	    possible_tips[i] = new Point();
	    Point temp = fingerseq.get(i);
	    possible_tips[i].x = temp.x;
	    possible_tips[i].y = temp.y;
	}
	Arrays.sort(possible_tips, point_compare());
	//std::qsort(possible_tips, fingerseq->total, sizeof(struct mypoint), qcompare1);
	mypoint_temp.x = -1;
	mypoint_temp.y = -1;
	tmp_cvpnt.x = palm_center.x;
	tmp_cvpnt.y = 999;
	for (int i = 0; i < fingerseq.size(); i++)
	{
	    //p.x = possible_tips[i].x;
	    //p.y = possible_tips[i].y;
	    //cvCircle(&frame, p, 5, CV_RGB(100, 0, 200), -1, CV_AA, 0);
	    /*System.out.println(get_cos_value(tmp_cvpnt, possible_tips[i]) < 0.98);
	    System.out.println(possible_tips[i].y<palm_center.y + 0.8*palm_radius);
	    System.out.println(((palm_center.x - possible_tips[i].x)*(palm_center.x - possible_tips[i].x)) + 
	                       ((palm_center.y - possible_tips[i].y)*(palm_center.y - possible_tips[i].y))>palm_radius*palm_radius*3.5);
	    */
	    if (/*(possible_tips[i].x>gaps[pnt].x || gaps[pnt].x==30000) &&*/get_cos_value(tmp_cvpnt, possible_tips[i])
		    <0.98 &&possible_tips[i].y<palm_center.y + 0.8*palm_radius /*&& 
		    ((palm_center.x - possible_tips[i].x)*(palm_center.x - possible_tips[i].x)) + 
		    ((palm_center.y - possible_tips[i].y)*(palm_center.y - possible_tips[i].y))>palm_radius*palm_radius*3.5*/)
	    {
		cnt_finger++;
		tmp_cvpnt.x = possible_tips[i].x;
		tmp_cvpnt.y = possible_tips[i].y;
		p.x = possible_tips[i].x;
		p.y = possible_tips[i].y;
		//checked_tips.push_back(mypoint_temp);
		Imgproc.circle(frame, p, 5, new Scalar(0,0,0), -1, 8, 0);
		//while (gaps[++pnt].x < possible_tips[i].x);
	    }
	}

	/*for (int i = 0; i < checked_tips.size(); i++)
        	{
        		if (get_cos_value(mypoint_temp, checked_tips[i]) < 0.8)
        		{
        			mypoint_temp.x = checked_tips[i].x;
        			mypoint_temp.y = checked_tips[i].y;
        			p.x = mypoint_temp.x;
        			p.y = mypoint_temp.y;
        			cvCircle(&frame, p, 5, CV_RGB(0, 0, 0), -1, CV_AA, 0);
        			cnt_finger++;
        		}
        	}*/
	//int i=pnt;
	return cnt_finger;
    }

    Mat mygesturedetect(Mat frame) //-1 undetected, 0-scissor, 1-rock, 2-paper
    {
	String gesture = "";
	mybackground = frame;
	Rect faceregion;
	int rmax = 0, rmin = 0, gmax = 0, gmin = 0, bmax = 0, bmin = 0;
	faceregion = facedetect(frame, face_cascade);
	//faceregion = handdetect_haar(frame, fist_cascade);
	SkinColorModel(frame, faceregion, rmax, rmin, gmax, gmin, bmax, bmin);
	Mat p = HandDetection(frame, faceregion, rmax, rmin, gmax, gmin, bmax, bmin);
	int dfts = Get_Palm_Center(frame);
	int tips=Get_fingertip(frame);

	int flag = (contours == null) ? 1 : 0;
	if (flag == 1) gesture = "ANLAMSIZ";
	System.out.println("Tip sayısı: " + tips + " - " + "dfts: " + dfts);
	if (tips >= 4 && dfts >= 3 && tips<=6&&dfts<=5) gesture = "KAĞIT"; //paper
	if (tips ==0 && dfts>=2 && dfts<=5) gesture = "KAĞIT";//paper, special case 1 (open palm with all fingers together)
	if (tips == 0) gesture = "TAŞ";//rock
	if (tips >= 1 && tips <= 2 && dfts >= 2 && dfts <= 4) gesture = "MAKAS";//scissors
	if (tips == 3 && dfts >= 2 && dfts <= 3) gesture = "MAKAS";//scissors
	if (tips == 3 && dfts >= 4 && dfts <= 5) gesture = "KAĞIT";//paper
	System.out.println("Gesture: " + gesture);
	return p;
    }


}
