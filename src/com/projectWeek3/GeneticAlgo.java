package com.projectWeek3;

import java.util.*;

public class GeneticAlgo{
	private ArrayList<Pair<Double,Registrar>> population = new ArrayList<Pair<Double,Registrar>>();
	private ArrayList<Pair<Double,Registrar>> aux = new ArrayList<Pair<Double,Registrar>>();
	private ArrayList<Person> allPeople = new ArrayList<Person>();
	private HashMap<Integer, Project> allCourses = new HashMap<>();
	private ArrayList<Integer> underfilledProjects;
	private StatWizard dylan;
	private Database db;
	private int size;
	private double totalScore;
	private String url;

	/**
	 * Creates a new genetic alagorithm object, making a Database lookup at URL
	 *
	 * public GeneticAlgo(String url, ArrayList<Integer> underfilledProjects)
	 *
	 * @param url - url of DB
	 * @param  underfilledProjects - Project IDs
	 */
	public GeneticAlgo(String url, ArrayList<Integer> underfilledProjects){
		this.underfilledProjects = underfilledProjects;
		db = new Database(url);

		this.url=url;
        ArrayList<Integer> tempList = db.getAllStudentIds();

        this.dylan = new StatWizard(db.getAllAverages()); //dylan is the guy who wrote the stat stuff

        for (int i=0; i < tempList.size(); i++) {
            Person p = new Person(tempList.get(i), calculateScore(tempList.get(i)), db.getGender(tempList.get(i)));
            p.setProjectIDs(db.getPreferences(p.getPersonID()));
			this.allPeople.add(p);
        }

        tempList = db.getAllCourseIds();
//        int pid;
		for (Integer pid : tempList) {
			allCourses.put(pid, new Project(pid, db.getMaxStudents(pid), db.getMinStudents(pid)));
		}
	}

	/**
	 * NEEDS JAVADOC
	 * @param studentId
	 * @return
	 */
	private int calculateScore(int studentId) {
        int prevScores[] = db.getPrevYears(studentId);
        int score=0;
        for (int i=0; i<3;i++){
            if (prevScores[i]>0) {
                score += prevScores[i]*100;
            }
            else {
                //generate a random number to switch it up
                score += Math.abs(dylan.getNextNormalValue() * 100);
            }
        }
        return score;
    }

	/**
	 * Populate Algorithm with size people
	 * @param size -number of students
	 * @return
	 */
	public void populate(int size){
		this.size = size;
		for(int i=0;i<size;i++){
			this.population.add(new Pair<Double,Registrar>(0.0, new Registrar(url, true, allPeople, allCourses, underfilledProjects)));
			this.aux.add(null);
		}
		fillProjects();
	}

	/**
	 * NEEDS JAVADOC
	 * @return
	 */
	public void fillProjects(){
		for(int i=0;i<this.population.size();i++){
			Registrar temp = fillRegistrar(this.population.get(i).getValue());
			evaluateSolutionToTotalScore(temp);
			double val = totalScore;
			this.population.set(i,new Pair<Double,Registrar>(val,temp));
		}
	}

	/**
	 * NEEDS JAVADOC
	 * @param reg
	 * @return
	 */
	public void evaluateSolutionToTotalScore(Registrar reg){
		HashMap<Integer, Project> allProjects = reg.getProjects();
		totalScore = 0;
		allProjects.forEach((k,v) -> addProjectVal(v));
		ArrayList<Person> arr = reg.getUnlucky();
	}

	/**
	 * NEEDS JAVADOC
	 * @param v
	 * @return
	 */
	public void addProjectVal(Project v){
		ArrayList<Person> arr = v.getEnrolledStudents();
		if (arr.size() == 0) return;
		double projectScore = 0;
		for(int i=0;i<arr.size();i++){
			projectScore+=studentVal(arr.get(i));
		}

		//Does not add anything to totalScore if it is underfilled, to devalue underfilled projects
		if(v.getSize() < v.getMinStudents()){ //Devalues underfilled projects
			return;	
		 }
		totalScore += projectScore;
		//totalScore+=100*v.getGenderScore();
	}

	/**
	 * NEEDS JAVADOC
	 * @param s
	 * @return
	 */
	public double studentVal(Person s){
		double scaler = s.getScore();
		double choicenum = s.getCurrentPreference();
		double numerator = (Math.pow(choicenum,2)*-1)+65;

		double denom1 = 200/scaler;
		double denom2 = Math.floor((1/5.0)*choicenum)+1;

		double studentscore = numerator/(denom1+denom2);
		return studentscore/2;
	}

	/**
	 * public void sort()
	 *
	 * Begins mergesort process; array with indexes 0 to population size
	 */
	public void sort() {
		sort(0,population.size()-1);
	}

	/**
	 * Recusive part of mergesort
	 * @param lo, hi
	 * @return
	 */
	private void sort(int lo, int hi){

		//Base case
		if (hi <= lo)
			return;

		//separate, sort each, then merge
		int mid = (lo + hi) / 2;
		sort(lo, mid);
		sort(mid+1, hi);
		merge(lo, mid, hi);

	}

	/**
	 * Merge 2 lists
	 *
	 * @param lo
	 * @param mid
	 * @param hi
	 */
	private void merge(int lo, int mid, int hi){
		//PRE:  nums is sorted from lo to mid
		//PRE:  nums is sorted from mid+1 to hi
		//POST: nums is sorted from lo to hi
		for (int i = lo; i <= hi; i++){
			this.aux.set(i,this.population.get(i));
		}
		int i = lo;
		int j = mid + 1;

		for (int k = lo; k <= hi; k++){
			//Case 3 - all that remains is the right half
			if (i > mid)
				this.population.set(k,this.aux.get(j++));
			//Case 4 - all that remains is the left half
			else if (j > hi)
				this.population.set(k,this.aux.get(i++));
			//Case 1 - right val is less than left val
			else if (this.aux.get(j).getKey() > this.aux.get(i).getKey())
				this.population.set(k,this.aux.get(j++));
			//case 2 - left val is less than right val
			else 
				this.population.set(k,this.aux.get(i++));
		}
	}

	public void killAndMate(){
		for(int i=0;((2*this.size)/3)+(i/2)<this.size;i+=2){
			Registrar temp = matePair(this.population.get(i).getValue(),this.population.get(i+1).getValue());
			evaluateSolutionToTotalScore(temp);
			this.population.set(((2*this.size)/3)+(i/2), new Pair<Double, Registrar>(totalScore,temp));
		}
	}

	private int randnum(int max) {
		return (int)(Math.random()*(max+1));
	}

	private Registrar matePair(Registrar reg1, Registrar reg2) {
		Registrar regchild = new Registrar(url, true, allPeople, allCourses, underfilledProjects);
		while(regchild.hasMorePeople()){
			Person curperson = regchild.getNextPerson();
			int pref1 = reg1.getStudentPref(curperson.getPersonID());
			int pref2 = reg2.getStudentPref(curperson.getPersonID());
			

			if(randnum(100)<2){
				regchild.randPlace(curperson);
			} else if (randnum(1)==1 && regchild.canPlace(curperson,pref1)){
				regchild.place(curperson,pref1);
			} else if (regchild.canPlace(curperson,pref2)){
				regchild.place(curperson,pref2);
			} else if (regchild.canPlace(curperson,pref1)){
				regchild.place(curperson,pref1);
			} else {
				regchild.randPlace(curperson);
			}
		}
		return regchild;
	}

	private Registrar fillRegistrar(Registrar reg) {
		Person nextP;
		//while registrar has more unsorted people
		while (reg.hasMorePeople()) {
			nextP = reg.getNextPerson();

			while (nextP != null) {
				nextP = reg.tryPlacePerson(nextP);
			}
		}
		return reg;
	}

	/**
	 *
	 */
	public void printVals(){
		for(int i=0;i<population.size();i++){
			System.out.println("\n\nScore=" + population.get(i).getKey());
		}
	}

	/**
	 *
	 */
	public void print(){
		for(int i=0;i<population.size();i++){
			System.out.println("\n\nScore=" + population.get(i).getKey()+ " for :");
			population.get(i).getValue().outputResults();
		}
	}

	/**
	 *
	 * @return- top Person
	 */
	public Registrar getTopReg(){
		return this.population.get(0).getValue();
	}
}
