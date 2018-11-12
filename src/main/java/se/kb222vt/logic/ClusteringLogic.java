package se.kb222vt.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


import se.kb222vt.entity.Blog;
import se.kb222vt.entity.Centroid;
import se.kb222vt.entity.Cluster;
import se.kb222vt.entity.Point;

public class ClusteringLogic {
	
	private Random rand = new Random();
	
	/**
	 * Cluster blogs with k-means-clustering algorithm. 
	 * Algorithm retrieved from: http://coursepress.lnu.se/kurs/web-intelligence/files/2018/10/3.-Clustering.pdf 5/11 2018
	 * @param maxIterations how many iterations of k-means algorithm should be runned
	 * @param blogs the blogs to cluster
	 * @param numberOfCentroids number of centroids in the cluster
	 * @param stopOnNoChange wheter to stop iterating when the blogs have been assigned to the same centroid twice ie. there will be no more change
	 * @return Cluster
	 */
	public Cluster kMeansCluster(int maxIterations, HashMap<String, Blog> blogs, int numberOfCentroids, boolean stopOnNoChange) {
		//Generate K random centroids
		HashMap<Integer, ArrayList<Point>> pointiterations = new HashMap<>();
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
				double bestDist = Double.MAX_VALUE; 
				Centroid closest = centroids.get(0);
				//Find closest centroid
				for (Centroid c : centroids) {
					
					double distance = pearsonCorrelation(c, blog); //compare our made up blog (centroid) with actual blog
					if (distance < bestDist) {
						closest = c;
						bestDist = distance;
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
			pointiterations.put(iterations, plot2dJavaLike(createPointListFromCluster(new Cluster(centroids, iterations, null))));
			iterations++;
			System.out.println("Iteration: " + iterations);
			if(!stopOnNoChange) {
				continue; //if we shouldnt stop on no change, skip the last part of the iterations
			}
			boolean sameAssignments = true;
			for(Centroid c : centroids) {
				sameAssignments = c.previousEqualsCurrentAssignments();
				if(!sameAssignments) //if even one centroid doesnt have the same assignment, don't keep looking
					break;
			}
			if(sameAssignments) {
				System.out.println("The centroids have the same assignments, so break loop at iteration: " + iterations);
				break;
			}
		}
		//End of iteration loop – all done
		return new Cluster(centroids, iterations, pointiterations);
	}
	
	/**
	 * Compare a blog with a centroid using pearson correlation. Result between 0 and 1, the lower result the better match. 
	 * Source: Programming Collective Intelligence: P. 35 
	 * @param centroid
	 * @param blog
	 * @return the sum of the pearson correlation between centroid and blog -1 to be achieve smaller values for more similarity 0 most similar and 1 not similar
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
		return 1-result.doubleValue();
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
	
	public double getDistanceBetween(Point point, Point otherPoint) {
		return Math.sqrt((point.x - otherPoint.x) * (point.x - otherPoint.x) + (point.y - otherPoint.y) *  (point.y - otherPoint.y));
	}
	
	/*
	public double[][] plot2d(ArrayList<ClusterEntity> entity) {
		int n = entity.size();
		double rate = 0.01;
		double realDist[][] = new double[n][n]; //fill with distance to every other point
		double outerSum = 0;
		// # Randomize locations[][]
		double loc[][] = new double[n][n];//every slot should be random value, <-- this is the one that should know what point is where since its the one getting returned
		double fakeDist[][] = new double[n][n]; //everyslot should be 0.0
		
		double lastError = 0;
		for(int m = 0; m < 1000; m++) {
			// # Find projected distances
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; i++) {
					double fakedist = 0;
					for(int x = 0; x < loc[i].length; x++) {
						fakedist += Math.pow(loc[i][x]-loc[j][x], 2);
					}
					fakeDist[i][j] = Math.sqrt(fakedist);
				}
			}
			// # Move points
			double grad[][] = new double[n][n]; //every slot should be 0.0,0.0
			double totalError = 0;
			for(int k = 0; k < n; k++) {
				for(int j = 0; j < n; j++) {
					if(j == k)
						continue;
					// # The error is percent difference between the distances
					double errorTerm = (fakeDist[j][k]-realDist[j][k])/realDist[j][k];
					// # Each point needs to be moved away or rowards the other
					// # point in proportion to how much error it has
					grad[k][0]+=((loc[k][0]-loc[j][0])/fakeDist[j][k])*errorTerm;
					grad[k][1]+=((loc[k][1]-loc[j][1])/fakeDist[j][k])*errorTerm;
					
					// # Keep track of the total error
					totalError+= Math.abs(errorTerm);
				}
			}
			System.out.println(totalError);
			// # If the answer git wirse by moving the points, we are done
			if(lastError != 0 && lastError < totalError)
				break;
			
			for(int k = 0; k < n; k++) {
				loc[k][0] -= rate * grad[k][0];
				loc[k][1] -= rate * grad[k][1];
			}
		}
		
		return loc;
	}*/
	
	public double getPearsonBetween(HashMap<String, Double> words, HashMap<String, Double> words2) {
		double sum1 = 0, sum2 = 0, sum1sq = 0, sum2sq = 0, pSum = 0;
		HashMap<String, Double> blogWords = words;
		int matchingWords = 0;
		for(Map.Entry<String, Double> entry : words2.entrySet()) {
			String word = entry.getKey();
			//System.out.println("Centroid looking for: " + word + ", " + blog2.getTitle() + " has " + blog2Words.get(word) + " instances of " + word);
			double centeroidAmount = entry.getValue();
			if(blogWords.get(word) > 0) { 
				double blog2Word = blogWords.get(word);
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
		return 1-result.doubleValue();
	}
	
	public ArrayList<Point> createPointListFromCluster(Cluster cluster) {
		ArrayList<Point> points = new ArrayList<Point>();
		int centroidID = 0;
		for(Centroid c : cluster.getCentroids()) {
			//new Point(String title, double x, double y, HashMap<String, Double> words, int centroidID)
			Point centroidPoint = new Point(c.getName(), 0, 0, c.getWords(), centroidID);
			points.add(centroidPoint);
			for(Blog blog : c.getBlogs()) {
				HashMap<String, Double> intToDouble = new HashMap<String, Double>();
				for(Entry<String, Integer> entry : blog.getWords().entrySet()) {
					intToDouble.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
				}
				Point blogPoint = new Point(blog.getTitle(), 0, 0, intToDouble, centroidID);
				points.add(blogPoint);
			}
			centroidID++;
		}
		return points;
	}
	
	/**
	 * Take an arrayList with points and then return the same list but with set x and y values.
	 * @param entity
	 * @return ArrayList<Point>
	 */
	public ArrayList<Point> plot2dJavaLike(ArrayList<Point> entity) {
		int n = entity.size();
		double rate = 0.01;
		double realDist[][] = new double[n][n]; //fill with distance to every other point
		for(int i = 0; i < realDist.length; i++) {
			for(int j = 0; j < realDist[i].length; j++) {
				realDist[i][j] = getPearsonBetween(entity.get(i).words, entity.get(j).words);
			}
		}
		//Not needed double outerSum = 0;
		// # Randomly initialize the starting points of the locations in 2D
		double loc[][] = new double[n][2]; 
		for(int i = 0; i < n; i++) {
			loc[i][0] = rand.nextDouble(); //x
			loc[i][1] = rand.nextDouble(); //y
		}
		double fakeDist[][] = new double[n][n]; //everyslot should be 0.0
		for(int i = 0; i < fakeDist.length; i++) {
			for(int j = 0; j < fakeDist[i].length; j++) {
				fakeDist[i][j] = 0.0;
			}
		}
		double lastError = 0;
		for(int m = 0; m < 1000; m++) {
			//System.out.println("M: " + m + " loc.length: " + loc.length);
			// # Find projected distances
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					double fakedist = 0;
					for(int h = 0; h < loc[i].length; h++) {
						fakedist += Math.pow(loc[i][h] - loc[j][h], 2);
						//fakedist += Math.pow(loc[i][0] - loc[j][0], 2);
						//fakedist += Math.pow(loc[i][1] - loc[j][1], 2);
					}
					fakeDist[i][j] = Math.sqrt(fakedist);
				}
			}
			// # Move points
			double grad[][] = new double[n][n]; //every slot should be 0.0,0.0 --> should be default
			double totalError = 0;
			for(int k = 0; k < n; k++) {
				for(int j = 0; j < n; j++) {
					if(j == k)
						continue;
					// # The error is percent difference between the distances
					double errorTerm = (fakeDist[j][k]-realDist[j][k])/realDist[j][k];
					// # Each point needs to be moved away or rowards the other
					// # point in proportion to how much error it has
					grad[k][0]+=((loc[k][0]-loc[j][0])/fakeDist[j][k])*errorTerm;
					grad[k][1]+=((loc[k][1]-loc[j][1])/fakeDist[j][k])*errorTerm;
					
					// # Keep track of the total error
					totalError+= Math.abs(errorTerm);
				}
			}
			System.out.println(m + ":" + totalError);
			// # If the answer git wirse by moving the points, we are done
			if(lastError != 0 && lastError < totalError)
				break;
			lastError = totalError;
			for(int k = 0; k < n; k++) {
				//loc[k][0] -= rate * grad[k][0];
				//loc[k][1] -= rate * grad[k][1];
				loc[k][0] -= 0.01 * grad[k][0];
				loc[k][1] -= 0.01 * grad[k][1];
			}
		}
		for(int i = 0; i < loc.length; i++) {
			Point currentP = entity.get(i);
			currentP.x = (loc[i][0]); //x
			currentP.y = (loc[i][1]); //y
			currentP.words = null;
		}
		return entity;
	}
	
}