package com.projectWeek3;

import java.util.ArrayList;

public class RunGenetic{

	public static void main(String[] args){

		int totalGenerations = 100; //10000
		int populationSize = 100; //100

		System.out.println("Setting Up Algorithm #1");
		ArrayList<Integer> test = new ArrayList<Integer>();

		final String url = "jdbc:sqlite:database/pweek.db";

		GeneticAlgo gen = new GeneticAlgo(url, new ArrayList<Integer>());
		gen.populate(populationSize);
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

		if (badProjects.size() > 0) {
			System.out.println("Genetic Algorithm #2");

			for (int i = 0; i < totalGenerations; i++) {
				System.out.print("\rGeneration " + i + "/" + totalGenerations);
				gen2.killAndMate();
				gen2.sort();
			}
			System.out.println();
			gen2.getTopReg().outputResultsToCSV();
		}
		else {
			gen.getTopReg().outputResultsToCSV();
		}
	}
}
