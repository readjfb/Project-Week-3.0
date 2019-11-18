package com.projectWeek3;

import java.util.*;
import java.util.HashMap;

class Registrar {
    private ArrayList<Person> allPeople = new ArrayList<Person>(); //TODO: Convert this to a HashMap with PersonIDs as the key
    private HashMap<Integer, Project> allCourses = new HashMap<>();
    private ArrayList<Person> unluckyPeople = new ArrayList<Person>(); //People who were unable to be sorted into one of their choices
    private Database db;
    private int currentIndex; // Used when iterating through allPeople in the initial algorithm stage
    private int sizeOfPeople; //
    final private String url;
    private ArrayList<Integer> underfilledIDs;

    /**
     * Constructs Registrar object
     *
     * @param url - database location
     * @param shuffle -randomize allPeople?
     * @param allPeople
     * @param allCourses
     * @param underfilledIDs
     */
    public Registrar(String url, boolean shuffle, ArrayList<Person> allPeople, HashMap<Integer, Project> allCourses, ArrayList<Integer> underfilledIDs) {
		//If there are underfilled projects, remove them from the HashMap of all courses, as we do not want to place a
        // student in a project that cannot run
        this.underfilledIDs = underfilledIDs;

        for(Integer id : underfilledIDs){
			allCourses.remove(id);
		}

        //set url to it's final urlValue
        this.url=url;

        for (Person person : allPeople) {
            this.allPeople.add(person.getClone());
        }

        //iterate through each course
        for (Map.Entry<Integer, Project> entry : allCourses.entrySet()) {
            Integer k = entry.getKey();
            Project v = entry.getValue();
            ArrayList<Person> interest = checkInterest(v.getProjectID());
            int amountOfInterest = interest.size();

            //If there's adiquate amount of interest in a project, add it
            if (amountOfInterest > v.getMinStudents()) {
                this.allCourses.put(v.getProjectID(), v.getClone());
            }
            //if there is exactly the number of students
            else if (amountOfInterest == v.getMinStudents()) {
                Project exactlyFilled = v.getClone();
                for (Person person : interest) {
                    exactlyFilled.addStudent(person);
                    this.allPeople.remove(person);
                }
                this.allCourses.put(v.getProjectID(), exactlyFilled);
            }
            //else, add the course to underfilledIDs, to keep track
            //NOTE: Added this recently
            else {
                this.underfilledIDs.add(k);
            }
        }

        if (shuffle)
             Collections.shuffle(this.allPeople);

        currentIndex = 0;
        sizeOfPeople = this.allPeople.size();
    }

    /**
     *
     * @param p
     * @param pref
     * @return if p can be placed in their pref
     */
    public boolean canPlace(Person p, int pref){
        if(pref==9){
            return true;
        }
        int projectID = p.prefToProjectID(pref);//db.getPreference(p.getPersonID(),pref);

        if(projectID==Integer.MIN_VALUE || projectID==0 || allCourses.get(projectID) == null){
            return false;
        }
        return !allCourses.get(projectID).isFull();
    }

    /**
     *
     * @param projectID- project's id
     * @return a list of students interested in project with projectID projectID
     */
    private ArrayList<Person> checkInterest(int projectID){
		ArrayList<Person> interest = new ArrayList<Person>();
		//for each person
		for(Person person : allPeople){
			//check if they signed up for projectID's project in top 5
			int[] projects = person.getPrefProjectIDs();
			for(int i = 0; i < 5; i++){
				if (projectID == projects[i]){
					interest.add(person);
				}
			}
		}
		return interest;
	}

    /**
     * public void randPlace(Person p)
     *
     * Try to place the person p in their highest choice
     * @param p
     */
    public void randPlace(Person p){
        int curChoice = 1;

        //while p cannot be placed in the project, keep iterating through optionsLeft
        while(!canPlace(p,curChoice))
            curChoice++;

        //if they're out of options, place them in unlucky people
        if(curChoice==9){
            p.setCurrentPreference(9);
            unluckyPeople.add(p);
            return;
        }
        int projectPref = p.prefToProjectID(curChoice);//db.getPreference(p.getPersonID(),optionsLeft.get(0)
        p.setCurrentPreference(curChoice);
        allCourses.get(projectPref).addStudent(p);
    }

    /**
     * public void place(Person p, int pref)
     *
     * Tries to place p in their pref project
     * @param p - person to be placed
     * @param pref - preference
     */
    public void place(Person p, int pref){
        if(!canPlace(p, pref)){
          //  p.setCurrentPreference(9);
            randPlace(p);
            return;
        }
        if(pref==9){
            unluckyPeople.add(p);
            return;
        }
        int projectPref = p.prefToProjectID(pref);//db.getPreference(p.getPersonID(),pref);
        p.setCurrentPreference(pref);
        allCourses.get(projectPref).addStudent(p);
    }

    /**
     * public Person tryPlacePerson(Person p)
     *
     * Attempts to place a person p into their next project
     * If a person was displaced, return the person displaced
     *
     * @param p - Person to be placed
     */
    public Person tryPlacePerson(Person p) {
        //first check if the person has more choices
        if ( p.getCurrentPreference() > 8) {
            unluckyPeople.add(p);
            return null;
        }

        //nextPrefChoice - it's a project id????? wtf...
        int nextPrefChoice = p.prefToProjectID(p.getCurrentPreference());//db.getPreference(p.getPersonID(), p.getCurrentPreference());

//        if (underfilledIDs.contains(nextPrefChoice))
//			allCourses.remove(nextPrefChoice);

		//If they're out of choices, minvalue or 0 will be returned
		if (nextPrefChoice == Integer.MIN_VALUE || nextPrefChoice == 0) {
            unluckyPeople.add(p);
            return null;
        }

		//Try to place them in the next project
        Project nextProject = allCourses.get(nextPrefChoice);
		if (nextProject == null) { //if project is nonexistent, add them to unluckyPeople
		    unluckyPeople.add(p);
		    return null;
        }
		//if the project has room in it, add them
        if (!nextProject.isFull()) {
            nextProject.addStudent(p);
            return null;
        }

        //Get the person with the lowest score from the next project
        Person lowestPerson = nextProject.getLowestScorePerson();

        if (lowestPerson.getScore() < p.getScore()) {
            nextProject.removeStudent(lowestPerson);
            nextProject.addStudent(p);

            //displace lowest person; add person to Project
            lowestPerson.increaseCurrentPreference();
            return lowestPerson;
        }
        // Person was not placed, so they got 'displaced,' and are returned
        p.increaseCurrentPreference();
        return p;
    }

    /**
        public boolean hasMorePeople()

        returns true or false depending on if there are more unsorted people

        this is defined by if current index < number of people

        @return if Registrar has more unsorted People
    */
    public boolean hasMorePeople() {
        return currentIndex < sizeOfPeople;
    }

    /**
        public Person getNextPerson()

        Returns the next unsorted person

        Precondition: Registrar has more allPeople
    */
    public Person getNextPerson() {
        return allPeople.get(currentIndex++);
    }

    /**
     * @param id: Student's person ID
     * @returns person's current preference, or 0 if the student was unable to be found
     */
    public int getStudentPref(int id) {
        for (Person person : allPeople) {
            if (person.getPersonID() == id) {
                return person.getCurrentPreference();
            }
        }
        return 0;
    }

    public ArrayList<Person> getUnlucky() {
        return this.unluckyPeople;
    }

    /**
     * public HashMap<Integer, Project> getProjects()
     *
     * @returns all courses. NOTE: Does not return unluckyPeople
     */
    public HashMap<Integer, Project> getProjects() {
        return allCourses;
    }

    /**
     * public void printProjects()
     *
     * Used for debugging, to get the string representations of each project printed out
     */
    public void printProjects() {
        ArrayList<Integer> allCourses = db.getAllCourseIds();
        for (Integer course : allCourses)
            System.out.println(this.allCourses.get(course));
        System.out.println(unluckyPeople);
    }

    /**
     * Used to output the results of the Genetic algorithm at each stage
     *
     * @return
     */
    public ArrayList<Integer> outputResults(){
				int[] choiceValues = {0,0,0,0,0,0,0,0};
		
		for(Person p : allPeople){
				if (p.getCurrentPreference()-1 == 8){
				    continue;
				}
				choiceValues[p.getCurrentPreference()-1]++;
		}

		for (int i = 0; i < 8 ;  i++) {
			System.out.println("Number of people who got their #" + (i+1) + " choice: " + choiceValues[i]);
		}
		System.out.println("Number of people who are not placed is: " + unluckyPeople.size());

		ArrayList<Integer> underfilled = new ArrayList<Integer>();
        //populate list of underfilled projects
        for (Project v : allCourses.values()){
			if(v.getSize() < v.getMinStudents()){
				System.out.println("Underfilled Project!!! Project Id is : " + v.getProjectID() + " it only has: " + v.getSize() + " people and the size is " + v.getMinStudents());
				underfilled.add(v.getProjectID());
			}
        }
		return underfilled;

	}

    /**
     * public void outputResultsToCSV()
     *
     * Compiles relevant data into a long arraylist of arraylists that is passed to Saver object
     * Stores in "output.csv"
     *
     * TODO: Make it save to a place that can be easily changable
     */
    public void outputResultsToCSV() {
        db = new Database(this.url);
        Saver saver = new Saver("output.csv");

        ArrayList<Integer> tempList = db.getAllCourseIds();
        ArrayList outputStats = new ArrayList();
        ArrayList totalStats = new ArrayList();
        ArrayList<Person> studentsInProject = new ArrayList<Person>();

        Person curPerson;
        int[] scores = new int[9];

        saver.write(new ArrayList<>(Arrays.asList("ID", "ChoiceNum", "ProjID", "Gender", "Grade", "Score")));

        for (int i=0; i < tempList.size(); i++) {
			//if it's not in allCourses then that trip didn't run, so just continue through the list
			if(!allCourses.containsKey(tempList.get(i))){
				continue;
			}
            studentsInProject = allCourses.get(tempList.get(i)).getEnrolledStudents();
			//iterate through the students in a given project
            for (int p=0; p<studentsInProject.size(); p++) {
                curPerson = studentsInProject.get(p);
                outputStats.clear();
               
                outputStats.add(curPerson.getPersonID()); //Id

                outputStats.add(db.getPrefNumForProj(curPerson.getPersonID(), tempList.get(i))); //Actually correct preference number for a student and a project
 //               outputStats.add(curPerson.getCurrentPreference()); //Choice  -- still wrong
                outputStats.add(tempList.get(i)); //projectId
                outputStats.add(db.getGender(curPerson.getPersonID())); //Gender
                outputStats.add(db.getGrade(curPerson.getPersonID())); //grade
                outputStats.add((curPerson.getScore())); //score

                scores[db.getPrefNumForProj(curPerson.getPersonID(), tempList.get(i))-1]++; //Actually correct preference number for a student and a project

  //              scores[curPerson.getCurrentPreference()-1]++;  //Still wrong...
                saver.write(outputStats);
            }
        }

        //Iterate through people who didn't get placed
        //Hopefully this won't happen
        for (int i=0; i<unluckyPeople.size(); i++) {
            curPerson = unluckyPeople.get(i);
            outputStats.clear();
            
            outputStats.add(curPerson.getPersonID());
            outputStats.add(curPerson.getCurrentPreference());
            outputStats.add(-1);
            outputStats.add(db.getGender(curPerson.getPersonID()));
            outputStats.add(db.getGrade(curPerson.getPersonID()));
            outputStats.add((curPerson.getScore())); //score

            scores[curPerson.getCurrentPreference()-1]++;
            saver.write(outputStats);
        }

        saver.close();

        for (int i=0; i<9; i++) {
            System.out.println("Choice"+(i+1)+": "+scores[i]);
        }
    }


}
