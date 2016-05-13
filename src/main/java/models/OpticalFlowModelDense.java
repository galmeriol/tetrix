package models;

import java.awt.image.BufferedImage;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.DualTVL1OpticalFlow;
import org.opencv.video.Video;
import capture.Direction;

public class OpticalFlowModelDense extends CommonModel{

    @Override
    public
    List<Mat> list() {
	return processedIms;
    }

    @Override
    public
    Direction get() {
	
	/*DualTVL1OpticalFlow dense = Video.createOptFlow_DualTVL1();
	
	Mat gray = new Mat();
	Imgproc.cvtColor(ctx.getMAINFrame(), gray, Imgproc.COLOR_BGR2GRAY);
	
	if(ctx.getGRAYFrame().empty()){
	    ctx.setGRAYFrame(gray);
	    return new Direction("");
	}
	
	if(ctx.getGRAYFrame().empty())
	    return new Direction("");
	
	Mat flow = new Mat();
	dense.calc(gray, ctx.getGRAYFrame(), flow);
	
	//drawOptFlowDenseMap(new Scalar(255, 100, 5), 12);
	processedIms.add(flow);

	ctx.setGRAYFrame(gray);
	
	*/
	
	return new Direction("");
    }

    @Override
    Direction decide() {
	return null;
    }

    
    public void drawOptFlowDenseMap(Scalar color, int step)	
    {
	

    }
}
