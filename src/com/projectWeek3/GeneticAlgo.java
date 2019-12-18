package com.projectWeek3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneticAlgo{
	private ArrayList<Pair<Double,Registrar>> regPopulation = new ArrayList<Pair<Double,Registrar>>();
	private ArrayList<Pair<Double,Registrar>> aux = new ArrayList<Pair<Double,Registrar>>();
	private ArrayList<Person> allPeople = new ArrayList<Person>();
	private HashMap<Integer, Project> allCourses = new HashMap<>();
	private ArrayList<Integer> underfilledProjects;
	private StatWizard dylan;
	private Database db;
	private int numRegistrars;
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
        ArrayList<Integer> tempStuIds = db.getAllStudentIds();

        this.dylan = new StatWizard(db.getAllAverages()); //dylan is the guy who wrote the stat stuff

		//Sets up allPeople AL to hold all the student data
        for (int i=0; i < tempStuIds.size(); i++) {
            Person p = new Person(tempStuIds.get(i), calculateScore(tempStuIds.get(i)), db.getGender(tempStuIds.get(i)));
            p.setProjectIDs(db.getPreferences(p.getPersonID()));
			this.allPeople.add(p);
        }

        //Sets up allCourses AL to hold all the student data
		ArrayList<Integer> tempProjIds  = db.getAllCourseIds();
		for (Integer pid : tempProjIds) {
			allCourses.put(pid, new Project(pid, db.getMaxStudents(pid), db.getMinStudents(pid)));
		}
	}

	/**
	 * Populate Algorithm with size people
	 *
	 * @param size -number of registrars
	 *
	 * @return
	 */
	public void populate(int size){
		this.numRegistrars = size;

		for(int i = 0; i < numRegistrars; i++){
			//The Double is the score of how good the specific registrar is... WE THINK?!?!?
			this.regPopulation.add(new Pair<Double,Registrar>(0.0, new Registrar(url,true, allPeople, allCourses, underfilledProjects)));
			this.aux.add(null);  //For use in the mergesort ...hmmm?
		}
		fillProjects();
	}

	/**
	 * NEEDS JAVADOC
	 * @return
	 */
	public void fillProjects(){
		for(int i = 0; i < this.regPopulation.size(); i++){
			Registrar temp = fillRegistrar(this.regPopulation.get(i).getValue());

			double val = evaluateSolutionToTotalScore(temp);
			this.regPopulation.set(i,new Pair<Double,Registrar>(val,temp));
		}
	}

	private Registrar fillRegistrar(Registrar reg) {
		Person nextP;
		//while registrar has more unsorted people
		while (reg.hasMorePeople()) {
			nextP = reg.getNextPerson().getClone();  //added getClone - may need to take out?

			while (nextP != null) {
				nextP = reg.tryPlacePerson(nextP);
			}
		}
		return reg;
	}

	/**
	 * NEEDS JAVADOC
	 * @param reg
	 * @return
	 */
	public double evaluateSolutionToTotalScore(Registrar reg){
		HashMap<Integer, Project> allProjects = reg.getProjects();

		double totalScore = 0;
		for (Map.Entry<Integer, Project> entry : allProjects.entrySet()) {
			Integer k = entry.getKey();
			Project v = entry.getValue();
			totalScore += getProjectVal(v);
		}

		//TODO should this do something to compensate to get rid of folks who were not placed?
		ArrayList<Person> arr = reg.getUnlucky();

		return totalScore;
	}

	/**
	 * Calculates a student's score, based solely on previous project placement data
	 *
	 * Calculates via sum of previous placements * 100
	 *
	 * Iff student was not in a project in a given year, they are given a random value from the normal distribution of
	 * projects
	 *
	 * @param studentId
	 * @return
	 */
	private int calculateScore(int studentId) {
        int[] prevScores = db.getPrevYears(studentId);
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
	 * getProjectVal
	 *
	 * heuristic
	 *
	 * @param v
	 * @returns project score.
	 * 		0 if project is underfilled
	 * 		Otherwise, adds the sum of student's scores, as calculated by calculatedStudentVal
	 */
	public double getProjectVal(Project v){
		ArrayList<Person> arr = v.getEnrolledStudents();
		if (arr.size() == 0) return 0;

		double projectScore = 0;

		for(int i=0;i<arr.size();i++){
			projectScore += calculatedStudentVal(arr.get(i));
		}

		//Devalues underfilled projects
		if(v.getSize() < v.getMinStudents()){
			return 0;
		 }
		//totalScore+=100*v.getGenderScore();

		return projectScore;
	}

	/**
	 * NEEDS JAVADOC
	 * @param s
	 * @return
	 */
	public double calculatedStudentVal(Person s){
		double scaler = s.getScore();
		double choiceNum = s.getCurrentPreference();
		double numerator = (Math.pow(choiceNum,2)*-1)+65;

		double denom1 = 200/scaler;
		double denom2 = Math.floor((1/5.0)*choiceNum)+1;

		double studentscore = numerator/(denom1+denom2);
		return studentscore/2;
	}

	/**
	 * public void sort()
	 *
	 * Begins mergesort process; array with indexes 0 to population size
	 */
	public void sort() {
		sort(0, regPopulation.size()-1);
	}

	/**
	 * Recusive part of mergesort
	 * @param lo, hi
	 * @return
	 */
	private void sort(int lo, int hi) {
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
			this.aux.set(i,this.regPopulation.get(i));
		}
		int i = lo;
		int j = mid + 1;

		for (int k = lo; k <= hi; k++){
			//Case 3 - all that remains is the right half
			if (i > mid)
				this.regPopulation.set(k,this.aux.get(j++));
			//Case 4 - all that remains is the left half
			else if (j > hi)
				this.regPopulation.set(k,this.aux.get(i++));
			//Case 1 - right val is less than left val
			else if (this.aux.get(j).getKey() > this.aux.get(i).getKey())
				this.regPopulation.set(k,this.aux.get(j++));
			//case 2 - left val is less than right val
			else 
				this.regPopulation.set(k,this.aux.get(i++));
		}
	}

	public void killAndMate(){
		//Takes pairs of registrars from first 2/3s of AL, mates them, places them in last 1/3 of AL
		for(int i = 0; i < this.numRegistrars/2; i++){
			Registrar child = matePair(regPopulation.get(i).getValue(), regPopulation.get(i + 1).getValue());

			this.regPopulation.set(this.numRegistrars/2 + i, new Pair<Double, Registrar>(evaluateSolutionToTotalScore(child), child));
		}
	}

	private int randnum(int max) {
		return (int)(Math.random() * (max + 1));
	}

	private Registrar matePair(Registrar reg1, Registrar reg2) {

		Registrar regChild = new Registrar(url, true, allPeople, allCourses, underfilledProjects);

		//changes a random person
		//TODO: make it so the person DEF gets placed in that pweek, and use a kind of stable marriage to deal w overfill
		int changingIndex = randnum(regChild.getSizeOfPeople());
		for(int i = 0; regChild.hasMorePeople(); i++) {
			Person curperson = regChild.getNextPerson().getClone();
			if (i == changingIndex) {
				regChild.place(curperson, reg2.getStudentPref(curperson.getPersonID()));
			} else {
				regChild.place(curperson, reg1.getStudentPref(curperson.getPersonID()));
			}
		}
		return regChild;

//		while(regchild.hasMorePeople()){
//			Person curperson = regchild.getNextPerson().getClone();  // Added getClone
//			int pref1 = reg1.getStudentPref(curperson.getPersonID());  //What's the difference between pref1 and pref2
//			int pref2 = reg2.getStudentPref(curperson.getPersonID());
//
//
//			if(randnum(100) < 2){  //2% chance that a person is randomly placed
//				regchild.randPlace(curperson);
//			}
//			else if (randnum(1) == 1 && regchild.canPlace(curperson, pref1)){ //50% chance you test that you can place in pref1
//				regchild.place(curperson, pref1);
//			}
//			else if (regchild.canPlace(curperson, pref2)){
//				regchild.place(curperson, pref2);
//			}
//			else if (regchild.canPlace(curperson, pref1)){  //if chance from above didn't test pref1 and pref2 didn't work
//				regchild.place(curperson, pref1);
//			}
//			else {								//Nothing worked, good luck friends
//				regchild.randPlace(curperson);
//			}
//		}
//		return regchild;
	}



	/**
	 *
	 */
	public void printVals(){
		for(int i = 0; i< regPopulation.size(); i++){
			System.out.println("\n\nScore=" + regPopulation.get(i).getKey());
		}
	}

	/**
	 *
	 */
	public void print(){
		for(int i = 0; i< regPopulation.size(); i++){
			System.out.println("\n\nScore=" + regPopulation.get(i).getKey()+ " for :");
			regPopulation.get(i).getValue().outputResults();
		}
	}

	/**
	 *
	 * @return- top Person
	 */
	public Registrar getTopReg(){
		return this.regPopulation.get(0).getValue();
	}
}
