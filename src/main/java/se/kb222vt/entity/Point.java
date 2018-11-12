package se.kb222vt.entity;

import java.util.HashMap;

public class Point {
	public String title;
	public double x, y;
	public HashMap<String, Double> words;
	public int centroidID;
	public Point(String title, double x, double y, HashMap<String, Double> words, int centroidID) {
		this.title = title;
		this.x = x;
		this.y = y;
		this.words = words;
		this.centroidID = centroidID;
	}
}
