package se.kb222vt.endpoint;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import se.kb222vt.logic.ClusteringLogic;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClusterController {
	private static ClusteringLogic logic = new ClusteringLogic();
	private static Gson gson = new Gson();
	private static List<String> supportedRecommendations = Arrays.asList(new String[]{"pearson", "euclidean"});
	
    public static Route userBasedRecommendation = (Request request, Response response) -> {
    	String measure = request.params("measure");
    	if(!supportedRecommendations.contains(measure)) {
			throw new IllegalArgumentException("Measure: " + measure + " is not a supported similarity measure, valid similarity measures are: " + supportedRecommendations.toString());
    	}
    	return gson.toJson(logic.doNothing(null, measure));
    };      
}