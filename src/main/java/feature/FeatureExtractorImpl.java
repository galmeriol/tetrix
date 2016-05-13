package feature;

import java.beans.FeatureDescriptor;
import org.opencv.features2d.*;
public class FeatureExtractorImpl extends DescriptorExtractor{
    
    protected FeatureExtractorImpl(long addr) {
	super(addr);
    }

    FeatureDetector fd = FeatureDetector.create(FeatureDetector.DENSE);
    
    void init(){
	
    }
}
