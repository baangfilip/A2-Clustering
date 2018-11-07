package se.kb222vt.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Cluster {
	private ArrayList<Centroid> centroids = new ArrayList<>();
	private int numberOfBlogs;
	private int iterations;
	
	public Cluster(ArrayList<Centroid> centroids, int iterations) {
		this.centroids = centroids;
		for(Centroid c : centroids) {
			this.numberOfBlogs += c.getBlogs().size();
		}
		this.iterations = iterations;
	}
	
	public int getIterations() {
		return iterations;
	}
}