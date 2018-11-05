package se.kb222vt.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Cluster {
	private ArrayList<Centroid> centroids = new ArrayList<>();
	private int numberOfBlogs;
	
	public Cluster(ArrayList<Centroid> centroids) {
		this.centroids = centroids;
		for(Centroid c : centroids) {
			this.numberOfBlogs += c.getBlogs().size();
		}
	}
}