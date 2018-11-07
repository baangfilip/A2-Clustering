package se.kb222vt.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

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
			Centroid c = new Centroid("Centroid " + i);
			for(Entry<String, Integer> entry : meta.entrySet()) {
				//Random word count
				int max = entry.getValue();
				int random = (max > 0 ? rand.nextInt(max) + 0 : 0);
				c.addWord(entry.getKey(), random);
			}
			centroids.add(c);
		}
		//Iteration loop
		int iterations = 0;
		while(iterations < maxIterations) { 
			//Clear assignments for all centroids
			for(Centroid c : centroids)
				c.clearBlogs();
			
			//Assign each blog to closest centroid
			for(Blog blog : blogs.values()){ 
				double bestSim = -1; //-1 is the worst correlation
				Centroid closest = centroids.get(0);
				//Find closest centroid
				for (Centroid c : centroids) {
					
					double similarity = pearsonCorrelation(c, blog); //compare our made up blog (centroid) with actual blog
					if (similarity > bestSim) {
						closest = c;
						bestSim = similarity;
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
					entry.setValue(c.getBlogs().size() < 1 ? 0 : total/c.getBlogs().size());
				}
			}
			boolean sameAssignments = true;
			for(Centroid c : centroids) {
				sameAssignments = c.previousEqualsCurrentAssignments();
				if(!sameAssignments) //if even one centroid doesnt have the same assignment, don't keep looking
					break;
			}
			System.out.println("Iteration: " + iterations);
			if(sameAssignments) {
				System.out.println("The centroids have the same assignments, so break loop at iteration: " + iterations);
				break;
			}
			iterations++;
		}
		for(Centroid c : centroids)
			c.clearPrevAssignments();
		//End of iteration loop – all done
		return new Cluster(centroids, iterations);
	}
	
	/**
	 * Compare a blog with a centroid using pearson correlation. Result between -1 and 1, the higher result the better match. Source: https://www.researchgate.net/publication/317349295_Calculating_the_User-item_Similarity_using_Pearson's_and_Cosine_Correlation_Senthilkumar_M 
	 * @param centroid
	 * @param blog
	 * @return the sum of the pearson correlation between blogs
	 */
	private double pearsonCorrelation(Centroid centroid, Blog blog) {
		double sum1 = 0, sum2 = 0, sum1sq = 0, sum2sq = 0, pSum = 0;
		HashMap<String, Integer> blogWords = blog.getWords();
		int matchingWords = 0;
		for(Map.Entry<String, Double> entry : centroid.getWords().entrySet()) {
			String word = entry.getKey();
			//System.out.println("Centroid looking for: " + word + ", " + blog2.getTitle() + " has " + blog2Words.get(word) + " instances of " + word);
			double centeroidAmount = entry.getValue();
			if(blogWords.get(word) > 0) { 
				int blog2Word = blogWords.get(word);
				sum1 += centeroidAmount;
				sum2 += blog2Word;
				sum1sq += Math.pow(centeroidAmount, 2);
				sum2sq += Math.pow(blog2Word, 2);
				pSum += centeroidAmount * blog2Word;
				matchingWords++;
			}else {
				continue; //both blogs doesnt have this word
			}
		}
		if(matchingWords < 1) {
			return 0;
		}
		double num = pSum - ((sum1 * sum2) / matchingWords);
		double den = Math.sqrt((sum1sq - Math.pow(sum1, 2) / matchingWords) * (sum2sq - Math.pow(sum2, 2) / matchingWords));
		Double result = new Double(num/den);
		if(result.isNaN()) {
			return 0;
		}
		return result.doubleValue();
	}
	
	public HashMap<String, Integer> getMetadataFromBlogs(HashMap<String, Blog> blogs){
		HashMap<String, Integer> words = new HashMap<>();
		for(Blog blog : blogs.values()) {
			for(Entry<String, Integer> blogWord : blog.getWords().entrySet()) {
				int amount = blogWord.getValue();
				String word = blogWord.getKey();
				//if HashMap words doesnt already contain the word or if the amount of the word is bigger add it (no duplicates allowed anyways)
				if(!words.containsKey(word) || (words.containsKey(word) && amount > words.get(word))) {
					words.put(word, amount);
				}
			}
		}
		return words;
	}
	
}