package classification;

import java.util.*;

import org.opencv.core.*;

public class Cluster {

    public List flows;
    public Point centroid;
    public int id;

    //Creates a new Cluster
    public Cluster(int id) {
	this.id = id;
	this.flows = new ArrayList();
	this.centroid = null;
    }

    public List getFlows() {
	return flows;
    }

    public void addFlow(FlowVector flow) {
	flows.add(flow);
    }

    public void setPoints(List flows) {
	this.flows = flows;
    }

    public Point getCentroid() {
	return centroid;
    }

    public void setCentroid(Point centroid) {
	this.centroid = centroid;
    }

    public int getId() {
	return id;
    }

    public void clear() {
	flows.clear();
    }


}