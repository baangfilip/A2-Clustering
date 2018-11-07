package se.kb222vt.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Centroid {

	private HashMap<String, Double> words = new HashMap<String, Double>();//<Word, WordCount>
	private ArrayList<Blog> blogs = new ArrayList<>(); //blogs assigned to this centroid
	private ArrayList<Blog> previousAssignments = new ArrayList<>();
	private String name;
	
	public Centroid(String name) {
		this.name = name;
	}
	
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
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Test if the previous assigned blogs are the same as the current ones
	 * @return
	 */
	public boolean previousEqualsCurrentAssignments() {
		if(this.blogs.size() != this.previousAssignments.size()) 
			return false; //the number of blogs doesnt match
		
		for(Blog previousBlog : this.previousAssignments) {
			if(!this.blogs.contains(previousBlog))
				return false; //the previous blog isnt in current collection of blogs
		}
		return true;
	}
	
	public void clearPrevAssignments() {
		this.previousAssignments.clear();
	}
	
	/**
	 * Clear current assigned blogs, keep previous assigned blogs in previousAssignments
	 */
	public void clearBlogs() {
		clearPrevAssignments();
		for(Blog blog : this.blogs) {
			this.previousAssignments.add(blog);
		}
		this.blogs.clear();
	}
	
}