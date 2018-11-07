package se.kb222vt.endpoint;

import com.google.gson.Gson;

import se.kb222vt.app.Application;
import se.kb222vt.entity.Cluster;
import se.kb222vt.logic.ClusteringLogic;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClusterController {
	private static Gson gson = new Gson();
	private static ClusteringLogic logic = new ClusteringLogic();
	
	//http://localhost:8080/API/cluster/?maxIterations=100&centroids=5
	public static Route cluster = (Request request, Response response) -> {
		String stopOnNoChange = request.queryParams("stopOnNoChange");
		String maxIterations = request.queryParams("maxIterations");
		String centroids = request.queryParams("centroids");
		String clean = request.queryParams("clean");
		if(clean == null)
			clean = "0";
		if(stopOnNoChange == null)
			stopOnNoChange = "0";
		if(maxIterations == null)
    		throw new IllegalArgumentException("Missing maxIterations param");
		if(centroids == null)
    		throw new IllegalArgumentException("Missing centroids param");

    	//TODO: Decide centroids if we dont get any from request maybe
    	boolean bStopOnNoChange = Integer.parseInt(stopOnNoChange) > 0;
    	int iMaxIterations = Integer.parseInt(maxIterations);
    	int iCentroids = Integer.parseInt(centroids);
    	if(iMaxIterations < 1 || iCentroids < 1) {
    		throw new IllegalArgumentException("Must have more then 0 iterations and more then 0 centroids");
    	}

    	Cluster cluster = logic.kMeansCluster(iMaxIterations, Application.blogs, iCentroids, bStopOnNoChange);
    	if(Integer.parseInt(clean) > 0)
    		cluster.cleanForResponse();
    	return gson.toJson(cluster);
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