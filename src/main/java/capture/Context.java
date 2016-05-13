package capture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.objdetect.CascadeClassifier;

public class Context {
    public GestureVars vars = new GestureVars();

    private Mode MODE = null;
    private Mat MAINFrame = null;
    private Mat HSVFrame = null;
    private Mat FRAMEBack = null;
    private Mat HISTFrame = null;
    
    private List<String> actionDirNames = null;
    
    
    private List<List<Mat>> actions = null;
    private List<Mat> action = null;
    
    public List<Mat> getAction() {
        return action;
    }


    public void setAction(List<Mat> action) {
        this.action = action;
    }

    private List<double[]> action_features = null;
    private List<List<double[]>> actions_features = null;
    
    public List<List<double[]>> getActions_Features() {
        return actions_features;
    }


    public void setActions_Features(List<List<double[]>> actions_features) {
        this.actions_features = actions_features;
    }




    public List<List<Mat>> getActions() {
        return actions;
    }




    public void setActions(List<List<Mat>> actions) {
        this.actions = actions;
    }




    public List<double[]> getAction_features() {
        return action_features;
    }




    public void setAction_features(List<double[]> action_features) {
        this.action_features = action_features;
    }




    public Mode getMODE() {
	return MODE;
    }




    public void setMODE(Mode mode) {
	MODE = mode;
    }




    public Mat getMAINFrame() {
	return MAINFrame;
    }




    public void setMAINFrame(Mat mAINFrame) {
	MAINFrame = mAINFrame;
    }

    /*
     * 
     * Optical flow modeli için frame tanımlamaları
     * 
     * */

    public Mat getFRAMEBack() {
	return FRAMEBack;
    }




    public void setFRAMEBack(Mat fRAMEBack) {
	FRAMEBack = fRAMEBack;
    }

    public Mat getHISTFrame() {
	return HISTFrame;
    }




    public void setHISTFrame(Mat hISTFrame) {
	HISTFrame = hISTFrame;
    }

    private Mat GRAYFrame = null;
    private Mat FLOWFrame = null;
    
    private Mat GRAYFarneFrame = null;
    private Mat FLOWFarneFrame = null;

    private Mat FARNEFrame = null;

    public Mat getFLOWFrame() {
        return FLOWFrame;
    }




    public void setFLOWFrame(Mat fLOWFrame) {
        FLOWFrame = fLOWFrame;
    }




    public Mat getGRAYFrame() {
	return GRAYFrame;
    }




    public void setGRAYFrame(Mat gRAYFrame) {
	GRAYFrame = gRAYFrame;
    }

    public Mat getGRAYFarneFrame() {
	return GRAYFarneFrame;
    }




    public void setGRAYFarneFrame(Mat gRAYFarneFrame) {
	GRAYFarneFrame = gRAYFarneFrame;
    }




    public Mat getFLOWFarneFrame() {
	return FLOWFarneFrame;
    }




    public void setFLOWFarneFrame(Mat fLOWFarneFrame) {
	FLOWFarneFrame = fLOWFarneFrame;
    }




    public Mat getFARNEFrame() {
	return FARNEFrame;
    }




    public void setFARNEFrame(Mat fARNEFrame) {
	FARNEFrame = fARNEFrame;
    }




    public void initFramesForOpticalFlowModel(){

	actionDirNames = new ArrayList<String>();
	action = new ArrayList<Mat>();
	actions = new ArrayList<List<Mat>>();
	action_features = new ArrayList<double[]>();
	actions_features = new ArrayList<List<double[]>>();
	
	MAINFrame = new Mat();
	FRAMEBack = new Mat();
	HISTFrame = new Mat();
	
	HSVFrame = new Mat();

	THRESHOLDFrame = new Mat();

	GRAYFrame = new Mat();
	FLOWFrame = new Mat();
	
	GRAYFarneFrame = new Mat();
	FLOWFarneFrame = new Mat();
	
	setFARNEFrame(new Mat());

    }

    /*
     * 
     * Optical flow modeli için frame tanımlamaları sonu
     * 
     * 
     * */

    

    /*
     * 
     * Diff model için Frame tanımlamaları 
     * 
     * */

    private Mat YUVFrame = null;

    private LinkedList<Mat> FRAMESequence = null;
    private LinkedList<Mat> FRAMEGraySequence = null;
    private LinkedList<Mat> FRAMEDiffSequence = null;
    
    private Mat FIRSTFrame = null;
    private Mat SECONDFrame = null;
    private Mat THIRDFrame = null;

    private Mat GRAYFrame1 = null;
    private Mat GRAYFrame2 = null;
    private Mat GRAYFrame3 = null;

    private Mat DIFFFrame1 = null;
    private Mat DIFFFrame2 = null;

    private Mat BITWISEFrame = null;
    private Mat THRESHOLDFrame = null;




    @SuppressWarnings("serial")
    public void initFramesForDiffModel(){

	MAINFrame = new Mat();
	FRAMEBack = new Mat();
	HISTFrame = new Mat();
	
	HSVFrame = new Mat();
	YUVFrame = new Mat();

	FIRSTFrame = new Mat();
	SECONDFrame = new Mat();
	THIRDFrame = new Mat();

	DIFFFrame1 = new Mat();
	DIFFFrame2 = new Mat();

	BITWISEFrame = new Mat();
	THRESHOLDFrame = new Mat();

	GRAYFrame1 = new Mat();
	GRAYFrame2 = new Mat();
	GRAYFrame3 = new Mat();
	
	FRAMESequence = new LinkedList<Mat>();
	FRAMEGraySequence = new LinkedList<Mat>();
	FRAMEDiffSequence = new LinkedList<Mat>();
    }

    public Mat getHSVFrame() {
	return HSVFrame;
    }




    public void setHSVFrame(Mat hSVFrame) {
	HSVFrame = hSVFrame;
    }




    public Mat getYUVFrame() {
	return YUVFrame;
    }




    public void setYUVFrame(Mat yUVFrame) {
	YUVFrame = yUVFrame;
    }




    public Mat getFIRSTFrame() {
	return FIRSTFrame;
    }




    public void setFIRSTFrame(Mat fIRSTFrame) {
	FIRSTFrame = fIRSTFrame;
    }




    public Mat getSECONDFrame() {
	return SECONDFrame;
    }




    public void setSECONDFrame(Mat sECONDFrame) {
	SECONDFrame = sECONDFrame;
    }




    public Mat getTHIRDFrame() {
	return THIRDFrame;
    }




    public void setTHIRDFrame(Mat tHIRDFrame) {
	THIRDFrame = tHIRDFrame;
    }




    public Mat getGRAYFrame1() {
	return GRAYFrame1;
    }




    public void setGRAYFrame1(Mat gRAYFrame1) {
	GRAYFrame1 = gRAYFrame1;
    }




    public Mat getGRAYFrame2() {
	return GRAYFrame2;
    }




    public void setGRAYFrame2(Mat gRAYFrame2) {
	GRAYFrame2 = gRAYFrame2;
    }




    public Mat getGRAYFrame3() {
	return GRAYFrame3;
    }




    public void setGRAYFrame3(Mat gRAYFrame3) {
	GRAYFrame3 = gRAYFrame3;
    }




    public Mat getDIFFFrame1() {
	return DIFFFrame1;
    }




    public void setDIFFFrame1(Mat dIFFFrame1) {
	DIFFFrame1 = dIFFFrame1;
    }




    public Mat getDIFFFrame2() {
	return DIFFFrame2;
    }




    public void setDIFFFrame2(Mat dIFFFrame2) {
	DIFFFrame2 = dIFFFrame2;
    }




    public Mat getBITWISEFrame() {
	return BITWISEFrame;
    }




    public void setBITWISEFrame(Mat bITWISEFrame) {
	BITWISEFrame = bITWISEFrame;
    }




    public Mat getTHRESHOLDFrame() {
	return THRESHOLDFrame;
    }




    public void setTHRESHOLDFrame(Mat dIFFFrame3) {
	THRESHOLDFrame = dIFFFrame3;
    }




    public LinkedList<Mat> getFRAMESequence() {
	return FRAMESequence;
    }




    public void setFRAMESequence(LinkedList<Mat> fRAMESequence) {
	FRAMESequence = fRAMESequence;
    }




    public LinkedList<Mat> getFRAMEGraySequence() {
	return FRAMEGraySequence;
    }




    public void setFRAMEGraySequence(LinkedList<Mat> fRAMEGraySequence) {
	FRAMEGraySequence = fRAMEGraySequence;
    }




    public LinkedList<Mat> getFRAMEDiffSequence() {
	return FRAMEDiffSequence;
    }




    public void setFRAMEDiffSequence(LinkedList<Mat> fRAMEDiffSequence) {
	FRAMEDiffSequence = fRAMEDiffSequence;
    }


    public List<String> getActionDirNames() {
	return actionDirNames;
    }


    public void setActionDirNames(List<String> actionDirNames) {
	this.actionDirNames = actionDirNames;
    }


    /*
     * 
     * Diff model için frame tanımlamaları sonu
     * 
     * */

}
