package com.projectWeek3;

import java.util.Date;
import java.text.SimpleDateFormat;



public class Person{
	private int personID;
	private int currentPreference;
	private int[] prefProjectIDs;
	private String gender;
	private int grade; // 1-4; 4-senior, 1-freshman

	private int score; //Person's priority at the current time

	/*
	* I wasn't positive exactly what constructors we were going to need
	* so I just made all of them. We can delete the redundant ones later
	*/
//	public Person(int studentID, String gender, int gradYear){
//		this.personID = studentID;
//		this.gender = gender;
//
//		SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
//		Date date = new Date();
//		String strDate = formatter.format(date);
//
//		int month = Integer.parseInt(strDate.substring(0, 2));
//		int year = Integer.parseInt(strDate.substring(2));
//		if(month > 8)
//			year++; // if it is past august add 1 to the year
//		this.grade = 4 - (gradYear - year);
//
//
//		this.prefProjectIDs = new int[8];
//		this.currentPreference = 1;
//	}

	public Person(int studentID, int currentPreference, int score){

		this.personID = studentID;
		this.currentPreference = currentPreference;
		this.score = score;
		this.prefProjectIDs =new int[8];
	}

//	public Person(int studentID){
//		this.personID = studentID;
//		this.currentPreference  = 1;
//		this.score = 0;
//		this.prefProjectIDs =new int[8];
//	}

	public Person(int id, int score, String gender){

		this.gender = gender;
		this.personID = id;
		this.score = score;
		this.currentPreference = 1;
		this.prefProjectIDs =new int[8];
	}

	//getter
	public int getPersonID(){

		return this.personID;
	}
	
	public int[] getPrefProjectIDs(){

		return this.prefProjectIDs;
	}

	public void setCurrentPreference(int x){

		if (x == 1){
//			System.out.println("We may be on to something");
		}
		this.currentPreference = x;
	}

//	public void setProjectID(int id, int pref){
//		this.prefProjectIDs[pref]=id;
//	}

	public void setProjectIDs(int[] arr) {

		this.prefProjectIDs = arr.clone();
	}

	public int prefToProjectID(int pref){

		return prefProjectIDs[pref-1];
	}

	//getter
	public int getCurrentPreference(){

		if (this.currentPreference > 1){
//			System.out.println("Test message");
		}
		return this.currentPreference;
	}


	public void increaseCurrentPreference() {

		this.currentPreference++;
	}


	public Person getClone(){

		Person p = new Person(personID, currentPreference, score);
		p.setProjectIDs(prefProjectIDs.clone());
		return p;
	}

	//getter
	public int getScore(){

		return this.score;
	}

	public boolean isMatchingData(int[] dbprefs){
		for(int i=0;i<dbprefs.length;i++){
			if(dbprefs[i]!=prefProjectIDs[i]){
				return false;
			}
		}
		return true;
	}

	public String toString() {

		return "#" + personID + ":" + currentPreference;
	}
}
