package se.kb222vt.app;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;

import se.kb222vt.endpoint.ClusterController;
import se.kb222vt.entity.Blog;
import spark.servlet.SparkApplication;

//Start Application by web.xml
public class Application implements SparkApplication {
	//putting some logic here since it will be so much overhead to put it somewhere else
	
	private Gson gson = new Gson();
	private String blogdataCSV = "webapps/rec/WEB-INF/classes/data/blogdata.txt"; //This will not work for any name for the webapp: https://github.com/perwendel/spark/pull/658/files
	public static HashMap<String, Blog> blogs = new HashMap<String, Blog>();
	
	@Override
	public void init() {
		System.out.println("Start endpoints");
		exception(IllegalArgumentException.class, (e, req, res) -> {
			  res.status(404);
			  res.body(gson.toJson(e));
			});
        

        get("/API/blogs/cluster/", ClusterController.cluster);

        get("/API/blogs/", ClusterController.blogs);
        
        try {
        	readData();
			System.out.println("Found: " + blogs.size() + " blogs");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readData() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(blogdataCSV).toAbsolutePath());
        CSVParser csv = new CSVParser(reader, CSVFormat.MYSQL.withFirstRecordAsHeader());
        for (CSVRecord line : csv) {
            String title = line.get(0);
            Map<String, String> lineMap = line.toMap();
            lineMap.remove("Blog"); //remove the first column, its the title not a word
            Blog blog = new Blog(title);
            blog.setWords(lineMap);
            blogs.put(title, blog);
        }
        csv.close();
	}
	
	public void setBlogdataCSV(String path) {
		this.blogdataCSV = path;
	}
	
	
}