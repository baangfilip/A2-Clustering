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
	
	/**
	 * Cleans the cluster object and including objects from irrelevant data for response
	 */
	public void cleanForResponse() {
		for(Centroid c : this.centroids) {
			c.clearPrevAssignments(); //saves 7-800kb for 99 blogs and 10 centroids (50%)
			c.getWords().clear();//saves 100kb for 99 blogs and 10 centroids (12%)
			for(Blog blog: c.getBlogs()) {
				//TODO: create a copy of the blogs first 
				//blog.getWords().clear();//saves 700kb for 99 blogs with 706 words (97%)
			}
		}
	}
}