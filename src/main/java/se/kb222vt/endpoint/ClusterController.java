package se.kb222vt.endpoint;

import com.google.gson.Gson;

import se.kb222vt.app.Application;
import se.kb222vt.logic.ClusteringLogic;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClusterController {
	private static Gson gson = new Gson();
	private static ClusteringLogic logic = new ClusteringLogic();
	
	//http://localhost:8080/API/cluster/?maxIterations=100&centroids=5
	public static Route cluster = (Request request, Response response) -> {
    	Integer maxIterations = Integer.parseInt(request.queryParams("maxIterations"));
    	Integer centroids = Integer.parseInt(request.queryParams("centroids"));
    	//TODO: Decide centroids if we dont get any from request maybe
    	if(maxIterations < 1 || centroids < 1) {
    		throw new IllegalArgumentException("Must have more then 0 iterations and more then 0 centroids");
    	}
    	return gson.toJson(logic.kMeansCluster(maxIterations, Application.blogs, centroids));
	};
    
	//Ex1. http://localhost:8080/API/blogs/?title=BuzzMachine
	//Ex2. http://localhost:8080/API/blogs/
    public static Route blogs = (Request request, Response response) -> {
    	String blogtitle = request.queryParams("title");
    	if(blogtitle == null || blogtitle.isEmpty()) {
        	return gson.toJson(Application.blogs);
    	}else {
    		if(Application.blogs.containsKey(blogtitle)) {
    			return gson.toJson(Application.blogs.get(blogtitle));
    		}else {
    			throw new IllegalArgumentException("Could not find entry for blog: " + blogtitle);
    		}
    	}

    };      
}