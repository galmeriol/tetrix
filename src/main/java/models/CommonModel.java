package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.utils.Converters;

import classification.CSVHelper;
import capture.Context;
import capture.Direction;

public abstract class CommonModel {

    Scalar blue = new Scalar(255, 100, 5);
    List<org.opencv.core.Mat> processedIms = new ArrayList<org.opencv.core.Mat>();
    CSVHelper csvHelper = null;
    Context ctx = null;

    public Context getCtx() {
	return ctx;
    }

    public void setCtx(Context ctx) {
	this.ctx = ctx;
    }

    public abstract List<org.opencv.core.Mat> list();
    public abstract Direction get();
    abstract Direction decide();

    /*
     * 
     * Algoritma modelleri için ortak metodlar
     * 
     * */

    int findBiggestContour(List<org.opencv.core.MatOfPoint> contours){
	int max_contour_idx = 0;
	double max_area = 0;

	for (int i = 0; i < contours.size();i++){
	    org.opencv.core.MatOfPoint tmp = contours.get(i);

	    double area = Math.abs(org.opencv.imgproc.Imgproc.contourArea(tmp, false));
	    if (area > max_area) {
		max_area = area;
		max_contour_idx = i;
	    }
	}
	return max_contour_idx;
    }

    double findBiggestArea(org.opencv.core.MatOfPoint contour){
	double area = Math.abs(org.opencv.imgproc.Imgproc.contourArea(contour, false));

	return area;
    }

    double[] normalize(double[] bins, double sum){
	

	if(sum > 0){
	    for (int i = 0; i < bins.length; i++) {
		if(bins[i] == sum)
		    bins[i] = 0;
		
		bins[i] /= sum;
		//bins[i] *= 480;
	    }
	}
	return bins;
    }

    void histIm(double[] bins){

	int binWidth = 640/(bins.length);
	
	for( int i = 0; i < bins.length; i++ )
	{
	    double binH = bins[i]*480;
	    org.opencv.imgproc.Imgproc.rectangle(
	                                         ctx.getHISTFrame(),
	                                         new org.opencv.core.Point(i*binWidth + 1 , ctx.getHISTFrame().rows() - binH),
	                                         new org.opencv.core.Point(i*binWidth + binWidth - 1, ctx.getHISTFrame().rows()) ,
	                                         blue,
	                                         -1);
	}
    }
    
    void writeToCSV(List<double[]> binsList) throws IOException{
	csvHelper.writeToCSV(binsList);
    }
    
    List<double[]> readFromCSV() throws IOException{
	return csvHelper.readFromCSV();
    }
    
    
}
