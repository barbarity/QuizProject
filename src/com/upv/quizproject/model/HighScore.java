package com.upv.quizproject.model;

public class HighScore {
	
	private String name;
	private int scoring;
	
	public HighScore() {
		
	}
	
	public HighScore(String name, int score) {
		this.name = name;
		this.scoring = score;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScoring() {
		return scoring;
	}
	public void setScoring(int scoring) {
		this.scoring = scoring;
	}

}
