package com.projectWeek3;

import java.util.*;
 
import java.io.FileWriter;
import java.io.IOException;

class Saver {
	private String filePath;
	private FileWriter csvWriter;

	public Saver(String path) {
		filePath = path;
		try {
			csvWriter = new FileWriter(filePath);
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	public void write(ArrayList output) {
		try {
			for (int i=0; i<output.size(); i++) {
				csvWriter.append(output.get(i).toString());
				csvWriter.append(",");
			}
			csvWriter.append("\n");
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	public void close() {
		try {
			csvWriter.flush();
			csvWriter.close();
		}	
		catch (IOException e) {
			System.out.println(e);
		}
	}

	
	// public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
	//     File csvOutputFile = new File(CSV_FILE_NAME);
	//     try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
	//         dataLines.stream()
	//           .map(this::write)
	//           .forEach(pw::println);
	//     }
	//     assertTrue(csvOutputFile.exists());
	// }
}