package com.projectWeek3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RunGenetic{

	public static void main(String[] args){

		int totalGenerations = 2500; //10000
		int populationSize = 100; //100

		System.out.println("Setting Up Algorithm #1");
		ArrayList<Integer> test = new ArrayList<Integer>();

		final String url = "jdbc:sqlite:database/pweek.db";

		GeneticAlgo gen = new GeneticAlgo(url, new ArrayList<Integer>());
		gen.populate(populationSize);
		//System.out.println("AHHH"+gen.getTopReg().isMatchingData());
		gen.sort();

		System.out.println("Genetic Algorithm #1");

		for(int i=0;i<totalGenerations;i++) {
			System.out.print("\rGeneration "+ i + "/"+totalGenerations);
			gen.killAndMate();
			gen.sort();
		}
		System.out.println();

		ArrayList<Integer> badProjects = gen.getTopReg().outputResults();
		System.out.println("Bad = " + badProjects);
		System.out.println("Setting Up Algorithm #2");

		GeneticAlgo gen2 = new GeneticAlgo(url, badProjects);
		
		gen2.populate(populationSize);
		gen2.sort();

		Registrar topReg;
		if (badProjects.size() > 0) {
			System.out.println("Genetic Algorithm #2");

			for (int i = 0; i < totalGenerations; i++) {
				System.out.print("\rGeneration " + i + "/" + totalGenerations);
				gen2.killAndMate();
				gen2.sort();
			}
			System.out.println();
			topReg = gen2.getTopReg();
		}
		else {
			topReg = gen.getTopReg();
		}
		topReg.outputResultsToCSV();
		//Test output.csv
		boolean success = testResults(gen);
		System.out.println("Test success: " + success);

	}

	//Reads output.csv into two dimensional String list
	public static ArrayList<String[]> readCSV(String path){
		ArrayList<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			//Reads next line of csv
			while ((line = br.readLine()) != null) {
				//Splits into values using commas
				String[] values = line.split(",");
				data.add(values.clone());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	//Compares output.csv (results created by algorithm) to the input database
	//Returns true if all choices indicated by output are true
	public static boolean testResults(GeneticAlgo gen){
		//Gets database of student id's and choices from geneticAlgo in a 2D ArrayList
		//Each row of prefs is a studentID followed by their eight project choices (in order)
		ArrayList<ArrayList<Integer>> prefs = gen.outputDB();
		ArrayList<String[]> output = readCSV("output.csv");

		boolean success = true;
		for(int i = 1; i < output.size(); i++){

			//iterates through each student in output.csv
			String[] student = output.get(i);
			int id = Integer.parseInt(student[0]);
			int choiceNum = Integer.parseInt(student[1]);
			int proj = Integer.parseInt(student[2]);

			//Finds the matching student in the database
			for(int j = 0; j < prefs.size(); j++){
				if(prefs.get(j).get(0) == id){

					//fails if they were put on a project that was not one of their choices
					if(choiceNum == 9) {
						if(proj != -1) {
							System.out.println("failed at student with id: " + id);
							success = false;
						}
					}

					//fails if the choice reported in output was not not their choice originally
					else if (prefs.get(j).get(choiceNum) != proj){

						System.out.println("failed at student with id: " + id);
						success=false;

					}
				}
			}

		}
		return success;
	}

}


