package classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class FlowVector {
    private Point s = new Point(0, 0);
    private Point e = new Point(0, 0);
    private int cluster_number = 0;

    public FlowVector(Point s, Point e)
    {
	this.setS(s);
	this.setE(e);
    }

    public void setS(Point s) {
	this.s = s;
    }

    public Point getS()  {
	return this.s;
    }

    public void setE(Point e) {
	this.e = e;
    }

    public Point getE() {
	return this.e;
    }

    public void setCluster(int n) {
	this.cluster_number = n;
    }

    public int getCluster() {
	return this.cluster_number;
    }


    protected static double distance(FlowVector f, Point centroid) {
	return VectorDistance(f.s, f.e, centroid);
    }

    protected static Point createRandomPoint(int min, int max) {
	Random r = new Random();
	double x = min + (max - min) * r.nextDouble();
	double y = min + (max - min) * r.nextDouble();
	return new Point(x,y);
    }

    protected static List createRandomPoints(int min, int max, int number) {
	List points = new ArrayList(number);
	for(int i = 0; i < number; i++) {
	    points.add(createRandomPoint(min,max));
	}
	return points;
    }

    public static double VectorDistance(Point A, Point B, Point P) {
	double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
	return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
    }
}
