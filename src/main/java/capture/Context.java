package capture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.objdetect.CascadeClassifier;

public class Context {
    
    
    private Mode MODE = null;
    
    private HashMap faceDetectionBag = new HashMap();
    private HashMap handDetectionBag = new HashMap();
    private HashMap hullBag = new HashMap();
    
    
    private Rect faceregion = new Rect();
    private Rect backgroundSample = new Rect(100, 100, 300, 300);
    
    private HashMap TetrixBag = new HashMap();
    
    private int ismyframeuse = 0;
    private int palm_radius;
    private int faceSize = 0;
    private CascadeClassifier face_cascade = new CascadeClassifier( 
                                                           System.getProperty("user.dir") + "/src/main/java/haarcascade_frontalface_default.xml");
    private Mat mybackground = new Mat();
    private float radius_palm_center = 0.0f;
    /*
    CvSeq fingerseq = new CvSeq();

    CvBox2D contour_center = new CvBox2D();*/
    private List<Point> fingerseq = new ArrayList<Point>();
    private List<Point> finger_dft = new ArrayList<Point>();
    private List<Point> palm = new ArrayList<Point>();
    private Point palm_center = new Point();
    private Point p = new Point();
    private MatOfInt4[] defects;
    private MatOfInt[] hull;
    private Point armcenter = new Point();
    private RotatedRect contour_center;
    private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    private int biggestContour = 0;
    private Rect[] faces;
    private List<Rect> guideRects = new ArrayList<Rect>();
    private List<Rect> backGuideRect = new ArrayList<Rect>();
    private double squareLen = 0;
    
    private double[][] avgColor = null;
    
    private Mat frameHSV = new Mat();
    private Mat frameThr = new Mat();
    private Mat frameSubtracted = new Mat();
    private Mat frameBackground = new  Mat();
    private Mat frameForHandModeling = new Mat();
    private Mat frameForBackModeling = new Mat();
    
    private Mat mainFrame = new Mat();
    
    public HashMap getTetrixBag() {
	return TetrixBag;
    }
    public void setTetrixBag(HashMap params) {
	this.TetrixBag = params;
    }
    public int getIsmyframeuse() {
	return ismyframeuse;
    }
    public void setIsmyframeuse(int ismyframeuse) {
	this.ismyframeuse = ismyframeuse;
    }
    public int getPalm_radius() {
	return palm_radius;
    }
    public void setPalm_radius(int palm_radius) {
	this.palm_radius = palm_radius;
    }
    public int getFaceSize() {
	return faceSize;
    }
    public void setFaceSize(int faceSize) {
	this.faceSize = faceSize;
    }
    public CascadeClassifier getFace_cascade() {
	return face_cascade;
    }
    public void setFace_cascade(CascadeClassifier face_cascade) {
	this.face_cascade = face_cascade;
    }
    public Mat getMybackground() {
	return mybackground;
    }
    public void setMybackground(Mat mybackground) {
	this.mybackground = mybackground;
    }
    public float getRadius_palm_center() {
	return radius_palm_center;
    }
    public void setRadius_palm_center(float radius_palm_center) {
	this.radius_palm_center = radius_palm_center;
    }
    public List<Point> getFingerseq() {
	return fingerseq;
    }
    public void setFingerseq(List<Point> fingerseq) {
	this.fingerseq = fingerseq;
    }
    public List<Point> getFinger_dft() {
	return finger_dft;
    }
    public void setFinger_dft(List<Point> finger_dft) {
	this.finger_dft = finger_dft;
    }
    public List<Point> getPalm() {
	return palm;
    }
    public void setPalm(List<Point> palm) {
	this.palm = palm;
    }
    public Point getPalm_center() {
	return palm_center;
    }
    public void setPalm_center(Point palm_center) {
	this.palm_center = palm_center;
    }
    public Point getP() {
	return p;
    }
    public void setP(Point p) {
	this.p = p;
    }
    public MatOfInt4[] getDefects() {
	return defects;
    }
    public void setDefects(MatOfInt4[] defects) {
	this.defects = defects;
    }
    public MatOfInt[] getHull() {
	return hull;
    }
    public void setHull(MatOfInt[] hull) {
	this.hull = hull;
    }
    public Point getArmcenter() {
	return armcenter;
    }
    public void setArmcenter(Point armcenter) {
	this.armcenter = armcenter;
    }
    public RotatedRect getContour_center() {
	return contour_center;
    }
    public void setContour_center(RotatedRect contour_center) {
	this.contour_center = contour_center;
    }
    public List<MatOfPoint> getContours() {
	return contours;
    }
    public void setContours(List<MatOfPoint> contours) {
	this.contours = contours;
    }
    public Rect[] getFaces() {
	return faces;
    }
    public void setFaces(Rect[] faces) {
	this.faces = faces;
    }
    public Mat getFrameHSV() {
	return frameHSV;
    }
    public void setFrameHSV(Mat frameHSV) {
	this.frameHSV = frameHSV;
    }
    public Mat getFrameThr() {
	return frameThr;
    }
    public void setFrameThr(Mat frameThr) {
	this.frameThr = frameThr;
    }
    public Mat getMainFrame() {
        return mainFrame;
    }
    public void setMainFrame(Mat mainFrame) {
        this.mainFrame = mainFrame;
    }
    public Rect getFaceregion() {
        return faceregion;
    }
    public void setFaceregion(Rect faceregion) {
        this.faceregion = faceregion;
    }
    public Mat getFrameSubtracted() {
        return frameSubtracted;
    }
    public void setFrameSubtracted(Mat frameSubtracted) {
        this.frameSubtracted = frameSubtracted;
    }
    public Mat getFrameBackground() {
        return frameBackground;
    }
    public void setFrameBackground(Mat frameBackground) {
        this.frameBackground = frameBackground;
    }
    public Rect getBackgroundSample() {
        return backgroundSample;
    }
    public void setBackgroundSample(Rect backgroundSample) {
        this.backgroundSample = backgroundSample;
    }
    public Mat getFrameForHandModeling() {
        return frameForHandModeling;
    }
    public void setFrameForHandModeling(Mat frameForHandModeling) {
        this.frameForHandModeling = frameForHandModeling;
    }
    public List<Rect> getGuideRects() {
        return guideRects;
    }
    public void setGuideRects(List<Rect> guideRects) {
        this.guideRects = guideRects;
    }
    public double getSquareLen() {
        return squareLen;
    }
    public void setSquareLen(double squareLen) {
        this.squareLen = squareLen;
    }
    public double[][] getAvgColor() {
        return avgColor;
    }
    public void setAvgColor(double[][] avgColor) {
        this.avgColor = avgColor;
    }
    public Mode getMODE() {
	return MODE;
    }

    public void setMODE(Mode mode) {
	MODE = mode;
    }
    public int getBiggestContour() {
        return biggestContour;
    }
    public void setBiggestContour(int biggestContour) {
        this.biggestContour = biggestContour;
    }
    public List<Rect> getBackGuideRect() {
        return backGuideRect;
    }
    public void setBackGuideRect(List<Rect> backGuideRect) {
        this.backGuideRect = backGuideRect;
    }
    public Mat getFrameForBackModeling() {
        return frameForBackModeling;
    }
    public void setFrameForBackModeling(Mat frameForBackModeling) {
        this.frameForBackModeling = frameForBackModeling;
    }
}
