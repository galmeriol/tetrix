package classification;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;

public class KMeans {
    //Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 7;    
    //Number of Points
    private int NUM_POINTS = 15;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;

    private List flows = new ArrayList();
    public List getFlows() {
        return flows;
    }
    public void setFlows(List flows) {
        this.flows = flows;
    }

    private List points = new ArrayList();
    private List clusters = new ArrayList();

    public List getClusters() {
        return clusters;
    }
    public void setClusters(List clusters) {
        this.clusters = clusters;
    }
    public KMeans() {
	this.flows = new ArrayList();
	this.clusters = new ArrayList();    	
    }
    //Initializes the process
    public void init() {
	//Create Points
	points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
	
	//Create Clusters
	//Set Random Centroids
	for (int i = 0; i < NUM_CLUSTERS; i++) {
	    Cluster cluster = new Cluster(i);
	    Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
	    cluster.setCentroid(centroid);
	    clusters.add(cluster);
	}
    }

    public void calculate() {
	double precision = 7;
	boolean finish = false;
	int iteration = 0;
	while(!finish) {
	    clearClusters();

	    List lastCentroids = getCentroids();

	    assignCluster();

	    calculateCentroids();

	    iteration++;

	    List currentCentroids = getCentroids();

	    double distance = 0;
	    for(int i = 0; i < lastCentroids.size(); i++) {
		double dst = Point.distance((Point)lastCentroids.get(i),(Point)currentCentroids.get(i));
		System.out.println("dst:" + dst);
		distance += dst;
	    }
	    
	    if(distance > 7) {
		finish = true;
	    }
	}
    }

    private void clearClusters() {
	for(Cluster cluster : (List<Cluster>)clusters) {
	    cluster.clear();
	}
    }

    private List getCentroids() {
	List centroids = new ArrayList(NUM_CLUSTERS);
	for(Cluster cluster : (List<Cluster>)clusters) {
	    Point aux = cluster.getCentroid();
	    Point point = new Point(aux.getX(),aux.getY());
	    centroids.add(point);
	}
	return centroids;
    }

    private void assignCluster() {
	double max = Double.MAX_VALUE;
	double min = max; 
	int cluster = 0;                 
	double distance = 0.0; 

	for(FlowVector flow : (List<FlowVector>)flows) {
	    min = max;
	    for(int i = 0; i < NUM_CLUSTERS; i++) {
		Cluster c = (Cluster) clusters.get(i);
		distance = FlowVector.distance(flow, c.getCentroid());
		if(distance < min){
		    min = distance;
		    cluster = i;
		}
	    }
	    flow.setCluster(cluster);
	    ((Cluster) clusters.get(cluster)).addFlow(flow);
	}
    }

    private void calculateCentroids() {
	for(Cluster cluster : (List<Cluster>)clusters) {
	    double sumX = 0;
	    double sumY = 0;
	    List list = cluster.getFlows();
	    int n_points = list.size();

	    for(FlowVector flow : (List<FlowVector>)list) {
		sumX += (flow.getS().x + flow.getE().x)/2;
		sumY += (flow.getS().y + flow.getE().y)/2;;
	    }

	    Point centroid = cluster.getCentroid();
	    if(n_points > 0) {
		double newX = sumX / n_points;
		double newY = sumY / n_points;
		centroid.setX(newX);
		centroid.setY(newY);
	    }
	}
    }
}
