package se.kb222vt.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

public class Blog {
	private static Gson gson = new Gson();
	private String title; 
	protected HashMap<String, Integer> words = new HashMap<String, Integer>();//<Word, WordCount>
	
	public Blog(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Word count in blog 
	 * @return HashMap is structured <Word, WordCount>
	 */
	public HashMap<String, Integer> getWords() {
		return this.words;
	}
	public void setWords(Map<String, String> wordMap) {
		for(Entry<String, String> entry : wordMap.entrySet()) {
			this.words.put(entry.getKey(), Integer.parseInt(entry.getValue()));
		}
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
}