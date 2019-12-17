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

	//constructors
	public Person(int studentID, int currentPreference, int score){

		this.personID = studentID;
		this.currentPreference = currentPreference;
		this.score = score;
		this.prefProjectIDs =new int[8];
	}

	public Person(int id, int score, String gender){

		this.gender = gender;
		this.personID = id;
		this.score = score;
		this.currentPreference = 1;
		this.prefProjectIDs =new int[8];
	}

	//getters
	public int getPersonID(){ return this.personID; }
	public int[] getPrefProjectIDs(){ return this.prefProjectIDs; }
	public int getCurrentPreference(){ return this.currentPreference; }
	public int getScore(){ return this.score; }

	public Person getClone(){

		Person p = new Person(personID, currentPreference, score);
		p.setProjectIDs(prefProjectIDs.clone());
		return p;
	}

	//setters
	public void setCurrentPreference(int x){ this.currentPreference = x; }
	public void setProjectIDs(int[] arr) { this.prefProjectIDs = arr.clone(); }
	public void increaseCurrentPreference() { this.currentPreference++; }


	public int prefToProjectID(int pref){

		return prefProjectIDs[pref-1];
	}

	public int projIDToPref(int projID){
		for(int i=0;i<prefProjectIDs.length;i++){
			if(prefProjectIDs[i]==projID){
				return i+1;
			}
		}
		return -1;
	}

	public boolean isMatchingData(int[] dbprefs){
		for(int i=0; i < dbprefs.length; i++) {
			if(dbprefs[i] != prefProjectIDs[i]) {
				return false;
			}
		}
		return true;
	}

	public String toString() {

		return "#" + personID + ":" + currentPreference;
	}
}
