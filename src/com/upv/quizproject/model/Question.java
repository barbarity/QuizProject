package com.upv.quizproject.model;

public class Question {
	private String answer1;
	private String answer2;
	private String answer3;
	private String answer4;
	
	private int audience;
	
	private int fifty1;
	private int fifty2;
	
	private int number;
	
	private int phone;
	
	private int right;
	
	private String text;

	public Question() {
		super();
	}

	

	public Question(String answer1, String answer2, String answer3,
			String answer4, int audience, int fifty1, int fifty2, int number,
			int phone, int right, String text) {
		super();
		this.answer1 = answer1;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
		this.audience = audience;
		this.fifty1 = fifty1;
		this.fifty2 = fifty2;
		this.number = number;
		this.phone = phone;
		this.right = right;
		this.text = text;
	}



	public String getAnswer1() {
		return answer1;
	}



	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}



	public String getAnswer2() {
		return answer2;
	}



	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}



	public String getAnswer3() {
		return answer3;
	}



	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}



	public String getAnswer4() {
		return answer4;
	}



	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}



	public int getAudience() {
		return audience;
	}

	public void setAudience(int audience) {
		this.audience = audience;
	}

	public int getFifty1() {
		return fifty1;
	}

	public void setFifty1(int fifty1) {
		this.fifty1 = fifty1;
	}

	public int getFifty2() {
		return fifty2;
	}

	public void setFifty2(int fifty2) {
		this.fifty2 = fifty2;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
