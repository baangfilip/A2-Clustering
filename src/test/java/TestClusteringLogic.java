import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import se.kb222vt.entity.Blog;
import se.kb222vt.entity.Cluster;
import se.kb222vt.logic.ClusteringLogic;


public class TestClusteringLogic {
	
	private ClusteringLogic logic = new ClusteringLogic();
	
	@Test
	public void testKMeansCluster() {
		HashMap<String, Blog> blogs = new HashMap<String, Blog>();
		//create blog1
		Blog blog1 = new Blog("Blog 1");
		Map<String, String> words = new HashMap<String, String>();
		words.put("Word 1", "1");
		words.put("Word 2", "2");
		blog1.setWords(words);
		blogs.put(blog1.getTitle(), blog1);
		
		//create blog2
		Blog blog2 = new Blog("Blog 2");
		words.put("Word 1", "3");
		words.put("Word 2", "2");
		blog2.setWords(words);
		blogs.put(blog2.getTitle(), blog2);
		
		//create blog3
		Blog blog3 = new Blog("Blog 3");
		words.put("Word 1", "50");
		words.put("Word 2", "100");
		blog3.setWords(words);
		blogs.put(blog3.getTitle(), blog3);
		
		//create blog4
		Blog blog4 = new Blog("Blog 4");
		words.put("Word 1", "10");
		words.put("Word 2", "50");
		blog4.setWords(words);
		blogs.put(blog4.getTitle(), blog4);
		
		//create blog5
		Blog blog5 = new Blog("Blog 5");
		words.put("Word 1", "11");
		words.put("Word 2", "1");
		blog5.setWords(words);
		blogs.put(blog5.getTitle(), blog5);
		
		int nbrOfCenteroids = 2;
		int iterations = 10000;
		Cluster cluster = logic.kMeansCluster(iterations, blogs, nbrOfCenteroids);
		assertEquals("There should be 1 iteration for this set of blogs", 1, cluster.getIterations());
	}
	
	
}