package se.kb222vt.entity;

import java.util.ArrayList;
import java.util.Collections;
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
			
			//copy list of blogs in centroid -> remove blogs in centroid -> create new blogs from the copied list but without the words
			ArrayList<Blog> centroidBlogs = new ArrayList<>(c.getBlogs());
			c.getBlogs().clear();//blog.getWords().clear();//saves 700kb for 99 blogs with 706 words (97%)
			for(Blog blog: centroidBlogs) {
				c.assignBlog(new Blog(blog));
			}
			Collections.sort(c.getBlogs(), Blog.getBlogByTitle());
			
		}
	}
}