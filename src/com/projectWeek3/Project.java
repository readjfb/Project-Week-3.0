package com.projectWeek3;

import java.util.*;
import static java.lang.Math.*;

public class Project{
	private ArrayList<Person> enrolledStudents;
	private int maxStudents;
	private int minStudents;
	private int projectID;
	private int numBoys;
	private int numGirls;
	private int lowestScore;

	public Project(int projectID, int maxStudents, int minStudents){
		this.projectID = projectID;
		this.maxStudents = maxStudents;
		this.minStudents = minStudents;
		this.enrolledStudents = new ArrayList<Person>();
		this.numBoys = 0;
		this.numGirls = 0;
	}

	public Project(int projectID, ArrayList<Person> enrolledStudents, int numBoys, int numGirls){
		this.projectID = projectID;
		this.enrolledStudents = enrolledStudents;
		this.numBoys = numBoys;
    	this.numGirls = numGirls;
	}

	//getters
	public int getMinStudents(){
		return this.minStudents;
	}
	public int getProjectID(){
		return this.projectID;
	}
	public int getNumBoys(){
		return this.numBoys;
	}
	public int getNumGirls(){
		return this.numGirls;
	}
	public int getMaxStudents(){
		return this.maxStudents;
	}
	public void setEnrolledStudents(ArrayList<Person> newStudents){
		this.enrolledStudents = newStudents;
	}
	public ArrayList<Person> getEnrolledStudents(){
		return enrolledStudents;
	}
	public void addStudent(Person p){
		this.enrolledStudents.add(p);
	}
	public void removeStudent(Person p){
		this.enrolledStudents.remove(p);
	}
	public void setNumBoys(int boys){
		this.numBoys = boys;
	}
	public void setNumGirls(int girls){
		this.numGirls = girls;
	}
	public Project getClone() {
		return new Project(this.projectID, this.maxStudents, this.minStudents);
	}

	public int getSize(){
		return this.enrolledStudents.size();
	}
	public Person getLowestScorePerson() {
		if (enrolledStudents.size() == 0) {
			System.out.println("Printing null");
			return null;
		}
		
		int lowestScore = enrolledStudents.get(0).getScore();
		Person lowestScorePerson = enrolledStudents.get(0);

		for (int i=0; i<enrolledStudents.size(); i++) {
			if (enrolledStudents.get(i).getScore() < lowestScore) {
				lowestScore = enrolledStudents.get(i).getScore();
				lowestScorePerson = enrolledStudents.get(i);
			}
		}
		return lowestScorePerson;
	}
	public boolean isFull() {
		return enrolledStudents.size() >= maxStudents;
	}
	public String toString() {
		return "Project#" + projectID+ enrolledStudents.toString();
	}
}
