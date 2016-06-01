package classification;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import libsvm.*;

public class SVMClassifier extends CommonClassifier{

    int TRAIN_DATA_SIZE = 1000;
    int TEST_DATA_SIZE = 1000;
    
    double[][] train = new double[TRAIN_DATA_SIZE][]; 
    
    public double[][] getTrain() {
        return train;
    }

    public void setTrain(double[][] train) {
        this.train = train;
    }

    public double[][] getTest() {
        return test;
    }

    public void setTest(double[][] test) {
        this.test = test;
    }

    double[][] test = new double[TEST_DATA_SIZE][];
    
    public svm_model train() {
	svm_problem prob = new svm_problem();
	int dataCount = train.length;
	prob.y = new double[dataCount];
	prob.l = dataCount;
	prob.x = new svm_node[dataCount][];     

	for (int i = 0; i < dataCount; i++){            
	    double[] features = train[i];
	    prob.x[i] = new svm_node[features.length-1];
	    for (int j = 1; j < features.length; j++){
		svm_node node = new svm_node();
		node.index = j;
		node.value = features[j];
		prob.x[i][j-1] = node;
	    }           
	    prob.y[i] = features[0];
	}               

	svm_parameter param = new svm_parameter();
	param.probability = 1;
	param.gamma = 0.5;
	param.nu = 0.5;
	param.C = 1;
	param.svm_type = svm_parameter.C_SVC;
	param.kernel_type = svm_parameter.LINEAR;       
	param.cache_size = 20000;
	param.eps = 0.001;      

	svm_model model = svm.svm_train(prob, param);

	return model;
    }

    public double evaluate(double[] features, svm_model model) 
    {
        svm_node[] nodes = new svm_node[features.length-1];
        for (int i = 1; i < features.length; i++)
        {
            svm_node node = new svm_node();
            node.index = i;
            node.value = features[i];

            nodes[i-1] = node;
        }

        int totalClasses = 2;       
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model,labels);

        double[] prob_estimates = new double[totalClasses];
        double v = svm.svm_predict_probability(model, nodes, prob_estimates);

        for (int i = 0; i < totalClasses; i++){
            System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
        }
        System.out.println("(Actual:" + features[0] + " Prediction:" + v + ")");            

        return v;
    }
    
    public void saveSvmModel(svm_model model, String filename) throws IOException{
	svm.svm_save_model(filename, model);
    }
    
    public svm_model loadSvmModel(String filename) throws IOException{
	return svm.svm_load_model(filename);
    }
    
    public svm_model loadRecentlyCreatedModel() throws IOException{
	String prefix = "/home/zafer/Desktop/workspace/motion_data/";
	
	File svmmodel = new File(prefix + "models");
	String[] model_files = svmmodel.list();
	Arrays.sort(model_files, new Comparator<String>() {

		public int compare(String o1, String o2) {
		    int motion_indis = Integer.valueOf(o1.replace("model", ""));
		    int motion_indis2 = Integer.valueOf(o2.replace("model", ""));

		    if(motion_indis < motion_indis2)
			return 1;
		    else if(motion_indis > motion_indis2)
			return -1;

		    return 0;
		}
	    });
	
	return svm.svm_load_model(prefix + "models/" + model_files[0]);
    }
}
