package se.kb222vt.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import se.kb222vt.entity.Blog;
import se.kb222vt.entity.Centroid;
import se.kb222vt.entity.Cluster;

public class ClusteringLogic {
	
	private Random rand = new Random();
	
	/**
	 * Cluster blogs with k-means-clustering algorithm. 
	 * Algorithm retrieved from: http://coursepress.lnu.se/kurs/web-intelligence/files/2018/10/3.-Clustering.pdf 5/11 2018
	 * @param maxIterations how many iterations of k-means algorithm should be runned, if zero it will run until centroids doesnt change anymore
	 * @param blogs the blogs to cluster
	 * @param numberOfCentroids number of centroids in the cluster
	 * @return Cluster
	 */
	public Cluster kMeansCluster(int maxIterations, HashMap<String, Blog> blogs, int numberOfCentroids) {
		//Generate K random centroids
		if(blogs.size() < 1) {
			throw new IllegalArgumentException("There are no blogs to cluster");
		}
		HashMap<String, Integer> meta = getMetadataFromBlogs(blogs);
		ArrayList<Centroid> centroids = new ArrayList<>();
		for(int i = 0; i < numberOfCentroids; i++) {
			Centroid c = new Centroid();
			for(Entry<String, Integer> entry : meta.entrySet()) {
				//Random word count
				c.addWord(entry.getKey(), rand.nextInt(entry.getValue()) + 0);
			}
			centroids.add(c);
		}
		//Iteration loop
		for (int i = 0; i < maxIterations; i++) { 
			//Clear assignments for all centroids
			for(Centroid c : centroids)
				c.clearBlogs();
			
			//Assign each blog to closest centroid
			for(Blog blog : blogs.values()){ 
				double distance = Double.MAX_VALUE;
				Centroid closest = null;
				//Find closest centroid
				for (Centroid c : centroids) {
					double cDist = pearsonCorrelation(c, blog); //compare our made up blog (centroid) with actual blog
					if (cDist < distance) {
						closest = c;
						distance = cDist;
					}
				}
				//Assign blog to centroid
				closest.assignBlog(blog);
			}
			
			//Re-calculate center for each centroid
			for(Centroid c : centroids){
				//Find average count for each word
				for(Entry<String, Double> entry : c.getWords().entrySet()){
					double total = 0;
					String word = entry.getKey();
					//Iterate over all blogs assigned to this centroid
					for(Blog blog : c.getBlogs()) {
						total += blog.getWords().get(word);
					}
					//Update word count for the centroid
					entry.setValue(total/c.getBlogs().size());
				}
			}
			//TODO: if each centroid have the same blogs as last run, break out of maxIterations loop break;
		}
		//End of iteration loop – all done
		return new Cluster(centroids);
	}
	
	/**
	 * Compare a blog with a centroid using pearson correlation
	 * @param centroid
	 * @param anotherBlog
	 * @return the sum of the pearson correlation between blogs
	 */
	private double pearsonCorrelation(Centroid centroid, Blog blog2) {
		double sum1 = 0, sum2 = 0, sum1sq = 0, sum2sq = 0, pSum = 0;
		HashMap<String, Integer> blog2Words = blog2.getWords();
		int matchingWords = 0;
		for(Map.Entry<String, Double> entry : centroid.getWords().entrySet()) {
			String word = entry.getKey();
			double blog1WordCount = entry.getValue();
			if(blog2Words.get(word) > 0) { 
				int blog2WordCount = blog2Words.get(word);
				sum1 += blog1WordCount;
				sum2 += blog2WordCount;
				sum1sq += Math.pow(blog1WordCount, 2);
				sum2sq += Math.pow(blog2WordCount, 2);
				pSum += blog1WordCount * blog2WordCount;
				matchingWords++;
			}else {
				continue; //both blogs doesnt have this word
			}
		}
		if(matchingWords < 1) {
			return 0;
		}
		double num = pSum - (sum1 * sum2 / matchingWords);
		double den = Math.sqrt((sum1sq - Math.pow(sum1, 2) / matchingWords) * (sum2sq - Math.pow(sum2, 2) / matchingWords));
		return num/den;
	}
	
	public HashMap<String, Integer> getMetadataFromBlogs(HashMap<String, Blog> blogs){
		HashMap<String, Integer> words = new HashMap<>();
		for(Blog blog : blogs.values()) {
			for(Entry<String, Integer> blogWord : blog.getWords().entrySet()) {
				int amount = blogWord.getValue();
				String title = blogWord.getKey();
				//if HashMap words doesnt already contain the word or if the amount of the word is bigger add it (no duplicates allowed anyways)
				if(words.containsKey(title) && amount > words.get(title)) {
					words.put(title, amount);
				}
			}
		}
		return words;
	}
	
}