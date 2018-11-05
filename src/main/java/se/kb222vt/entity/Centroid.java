package se.kb222vt.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Centroid {

	protected HashMap<String, Double> words = new HashMap<String, Double>();//<Word, WordCount>
	private ArrayList<Blog> blogs = new ArrayList<>(); //blogs assigned to this centroid
	
	
	public void addWord(String word, double amount) {
		this.words.put(word, amount);
	}
	
	public void assignBlog(Blog blog) {
		this.blogs.add(blog);
	}
	
	public ArrayList<Blog> getBlogs(){
		return blogs;
	}
	
	public HashMap<String, Double> getWords(){
		return words;
	}
	
	public void clearBlogs() {
		this.blogs.clear();
	}
	
}