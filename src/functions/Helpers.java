package functions;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import objects.Arrangement;
import objects.Camper;
import objects.Section;


public class Helpers {

	public static ArrayList<Arrangement> highScoreArrangements = new ArrayList<Arrangement>();
	
	//The number of campers with bottom only allowed minus number of bottom bunks
	public static int botBunkOnlyThreshold = 0;
	public static int topBunkOnlyThreshold = 0;
	public static double highScore = -Double.MAX_VALUE;
	public static int fileNumber = 0;
	
	public static final int FORMAT_NAME_LENGTH = 25;
	
	public static final double BUNK_LEVEL_ADDITION = 10;
	public static final double BUNK_BUDDY_ADDITION = 1;
	public static final double SIDE_ADDITION = 1;
	public static final double DIAGONAL_ADDITION = 0.5;
	
	public static final String FILE_PATH = "/Users/Benjamin/PyCharmProjects/BunkAssignments";
	public static final String DELIMITER = ",";
	
	private static final Scanner reader = new Scanner(System.in);
	
	private static File file;
	
	/**
	 * Gets file to read from
	 * @throws IOException
	 */
	public static void initFile() throws IOException {
		
		JFrame.setDefaultLookAndFeelDecorated(true);
	    JDialog.setDefaultLookAndFeelDecorated(true);
	    JFrame frame = new JFrame("JComboBox Test");
	    frame.setLayout(new FlowLayout());
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JButton button = new JButton("Select File");
	    button.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    		int returnValue = fileChooser.showOpenDialog(null);
	    		if (returnValue == JFileChooser.APPROVE_OPTION) {
	    			file = fileChooser.getSelectedFile();
	    		}
	    		frame.dispose();
	    		Main.fileSelectedAction();
	    	}
	    });
	    frame.add(button);
	    frame.pack();
	    frame.setVisible(true);
	}
	
	/**
	 * Creates list of campers and attributes from file
	 */
	public static void initCampers() {
		
		boolean doneReading = false;
		
		BufferedReader br = null;
		
		try {
			
			//Minus 1 exists because file number is one more than index in array list
			br = new BufferedReader(new FileReader(file));
			//br = new BufferedReader(new FileReader(PrintFiles.files.get(fileNumber - 1).toString()));
			
			br.readLine();
			
			while(!doneReading) {
				
				String[] line = br.readLine().split(DELIMITER);
				
				if (line.length < 2) {
					doneReading = true;
				} else {

					boolean topBunkAllowed = false;
					boolean botBunkAllowed = false;
					
					if (!line[2].equals("")) {
						topBunkAllowed = true;
					}
					if (!line[3].equals("")) {
						botBunkAllowed = true;
					}
					
					Camper.campers.add(new Camper(line[0], topBunkAllowed, botBunkAllowed, line[4], line[5], line[6],
							line[7], line[8], line[9]));
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	/**
	 * Creates right number of sections and bunks in each with user input
	 */
	public static void initSections() {
		
		int numSections, numBotBunks, numTopBunks;
		
		int totalBunksEntered = 0;
		boolean bunkDifferenceAllowed = true;
		
		while (totalBunksEntered != Camper.campers.size() || !bunkDifferenceAllowed) {
			
			bunkDifferenceAllowed = true;
			totalBunksEntered = 0;
			
			System.out.print("Sections in Bunk: ");
			numSections = reader.nextInt();
			
			for (int i = 1; i < numSections + 1; i++) {
				
				System.out.print("Bottom bunks in section " + i + ": ");
				numBotBunks = reader.nextInt();
				System.out.print("Top bunks in section " + i + ": ");
				numTopBunks = reader.nextInt();
				Section.sections.add(new Section(numBotBunks, numTopBunks));
				totalBunksEntered += numBotBunks + numTopBunks;
				if (!(numBotBunks - numTopBunks == 0 || numBotBunks - numTopBunks == 1)) {
					bunkDifferenceAllowed = false;
				}
			}
			
			if (totalBunksEntered != Camper.campers.size()) {
				System.out.println("Number of campers incorrect. Please try again.");
			}
			
		}
	}

	
	/**
	 * Checks to see if the bunk height is wrong
	 * @param arrangement
	 * 			The arrangement to check
	 * @return
	 * 			true: The bunk height is not allowed
	 * 			false: The bunk height is allowed
	 */
	public static boolean bunkHeightWrong(Arrangement arrangement) {
		
		boolean wrong = false;
		
		int wrongBotBunks = 0;
		int wrongTopBunks = 0;
		
		//Assign sections
		int counter = 0;
		for (int i = 0; i < Section.sections.size(); i++) {
			
			//For each bottom bunk
			for (int j = 0; j < Section.sections.get(i).getNumBotBunks(); j++) {
				if (!arrangement.getCamper(counter).getBotBunkAllowed()) {
					wrongTopBunks += 1;
				}
				counter += 1;
			}
			
			//For each top bunk
			for (int k = 0; k < Section.sections.get(i).getNumTopBunks(); k++) {
				if (!arrangement.getCamper(counter).getTopBunkAllowed()) {
					wrongBotBunks += 1;
				}
				counter += 1;
			}
		}
		
		if (wrongBotBunks > botBunkOnlyThreshold || wrongTopBunks > topBunkOnlyThreshold) {
			wrong = true;
		}
		
		return wrong;
	}

	
	/**
	 * Keeps the lowest standard deviation of the high scores
	 * @return
	 * 		The new list of low standard deviation high scores
	 */
	public static ArrayList<Arrangement> arrangementStdevFilter() {
		
		ArrayList<Arrangement> tempHighScoreArrangements = new ArrayList<Arrangement>();
		
		double lowStdev = Double.MAX_VALUE;
		
		for (int i = 0; i < highScoreArrangements.size(); i++) {
			
			double tempStdev = calcStdev(highScoreArrangements.get(i));
			
			if (tempStdev == lowStdev) {
				tempHighScoreArrangements.add(highScoreArrangements.get(i));
			}
			else if (tempStdev < lowStdev) {
				lowStdev = tempStdev;
				tempHighScoreArrangements.clear();
				tempHighScoreArrangements.add(highScoreArrangements.get(i));
			}
		}
		
		return tempHighScoreArrangements;
		
	}
	
	
	/**
	 * Calculates the number of points of the arrangement
	 * @param arrangement
	 * 			The arrangement to calculate
	 * @return
	 * 			The number of points of that arrangement
	 */
	public static double calcPoints(Arrangement arrangement) {
		return calcArrangement(arrangement, true, false);
	}
	
	
	/**
	 * Calculates the standard deviation of the arrangement 
	 * @param arrangement
	 * 			The arrangement to calculate
	 * @return
	 * 			The standard deviation of that arrangement
	 */
	public static double calcStdev(Arrangement arrangement) {
		return calcArrangement(arrangement, false, true);
	}
	
	
	/**
	 * Actually performs the calculations of points for each camper
	 * @param arrangement
	 * 			The arrangement to calculate
	 * @param pointsBool
	 * 			true: Points should be calculated
	 * 			false: Points should not be calculated
	 * @param stdevBool
	 * 			true: Standard deviation should be calculated
	 * 			false: Standard deviation should not be calculated
	 * @return
	 */
	public static double calcArrangement(Arrangement arrangement, boolean pointsBool, boolean stdevBool) {
		
		//Assign sections
		int counter = 0;
		for (int i = 0; i < Section.sections.size(); i++) {
			for (int j = 0; j < Section.sections.get(i).getNumBotBunks(); j++) {
				Section.sections.get(i).addBotBunk(arrangement.getCamper(counter));
				counter += 1;
			}
			for (int k = 0; k < Section.sections.get(i).getNumTopBunks(); k++) {
				Section.sections.get(i).addTopBunk(arrangement.getCamper(counter));
				counter += 1;
			}
		}
		
		ArrayList<Double> pointsArray = new ArrayList<Double>();
		
		//Loop through sections, determining points for each camper
		for (int l = 0; l < Section.sections.size(); l++) {
			
			//For each bottom bunk
			for (int m = 0; m < Section.sections.get(l).getNumBotBunks(); m++) {
				
				double camperPoints = 0;
				
				//Add points for being on right level
				if (Section.sections.get(l).getBotBunk(m).getBotBunkAllowed()) {
					camperPoints += BUNK_LEVEL_ADDITION;
				}
				
				//Add points for bunk buddy
				if ((Section.sections.get(l).getNumTopBunks() > m) && Section.sections.get(l).getBotBunk(m)
						.bunkMatePrefMatches(Section.sections.get(l).getTopBunk(m).getName())) {
					camperPoints += BUNK_BUDDY_ADDITION;
				}
				
				//Add points for side near preferences
				if (m > 0 && Section.sections.get(l).getBotBunk(m)
						.nearPrefMatches(Section.sections.get(l).getBotBunk(m - 1).getName())) {
					camperPoints += SIDE_ADDITION;
				}
				if (m < Section.sections.get(l).getNumBotBunks() - 1 && Section.sections.get(l).getBotBunk(m)
						.nearPrefMatches(Section.sections.get(l).getBotBunk(m + 1).getName())) {
					camperPoints += SIDE_ADDITION;
				}
				
				//Add points for diagonal near preferences
				if (m > 0 && Section.sections.get(l).getBotBunk(m)
						.nearPrefMatches(Section.sections.get(l).getTopBunk(m - 1).getName())) {
					camperPoints += DIAGONAL_ADDITION;
				}
				if (m < Section.sections.get(l).getNumTopBunks() - 1 && Section.sections.get(l).getBotBunk(m)
						.nearPrefMatches(Section.sections.get(l).getTopBunk(m + 1).getName())) {
					camperPoints += DIAGONAL_ADDITION;
				}
				
				pointsArray.add(camperPoints);
			}
			
			//For each top bunk
			for (int n = 0; n < Section.sections.get(l).getNumTopBunks(); n++) {
				
				double camperPoints = 0;
				
				//Add points for being on right level
				if (Section.sections.get(l).getTopBunk(n).getTopBunkAllowed()) {
					camperPoints += BUNK_LEVEL_ADDITION;
				}
				
				//Add points for bunk buddy
				if ((Section.sections.get(l).getNumBotBunks() > n) && Section.sections.get(l).getTopBunk(n)
						.bunkMatePrefMatches(Section.sections.get(l).getBotBunk(n).getName())) {
					camperPoints += BUNK_BUDDY_ADDITION;
				}
				
				//Add points for side near preferences
				if (n > 0 && Section.sections.get(l).getTopBunk(n)
						.nearPrefMatches(Section.sections.get(l).getTopBunk(n - 1).getName())) {
					camperPoints += SIDE_ADDITION;
				}
				if (n < Section.sections.get(l).getNumTopBunks() - 1 && Section.sections.get(l).getTopBunk(n)
						.nearPrefMatches(Section.sections.get(l).getTopBunk(n + 1).getName())) {
					camperPoints += SIDE_ADDITION;
				}
				
				//Add points for diagonal near preferences
				if (n > 0 && Section.sections.get(l).getTopBunk(n)
						.nearPrefMatches(Section.sections.get(l).getBotBunk(n - 1).getName())) {
					camperPoints += DIAGONAL_ADDITION;
				}
				if (n < Section.sections.get(l).getNumBotBunks() - 1 && Section.sections.get(l).getTopBunk(n)
						.nearPrefMatches(Section.sections.get(l).getBotBunk(n + 1).getName())) {
					camperPoints += DIAGONAL_ADDITION;
				}
				
				pointsArray.add(camperPoints);
			}
		}	
		
		//Reset sections
		for (int i = 0; i < Section.sections.size(); i++) {
			Section.sections.get(i).reset();
		}
		
		double totalPoints = 0;
		double sumOfSquares = 0;
		
		for (int i = 0; i < pointsArray.size(); i++) {
			totalPoints += pointsArray.get(i);
		}
		
		double mean = totalPoints / Camper.campers.size();
		
		if (pointsBool) {
			return mean;
		}
		
		for (int i = 0; i < pointsArray.size(); i++) {
			sumOfSquares += Math.pow(pointsArray.get(i) - mean, 2);
		}
		
		if (stdevBool) 
			return Math.pow(sumOfSquares, 0.5);
		
		return 0;
	}
	
	
	/**
	 * Outputs time in HH:MM:SS notation
	 * @param secs
	 * 			The number of seconds to format
	 */
	public static void timeOutput(double secs) {
		
		int formatSecs = (int) Math.round(secs);
		int hoursRemaining = formatSecs / 3600;
		int minutesRemaining = (formatSecs - 3600 * hoursRemaining) / 60;
		formatSecs -= hoursRemaining * 3600 + minutesRemaining * 60;
		System.out.println(hoursRemaining + ":" + minutesRemaining + ":" + formatSecs);
		
	}

	
	/**
	 * Sets the allowed wrong top and bottom bunks for the particular data set
	 * Used for seeing if bunk height is wrong
	 */
	public static void setAllowedBunkConfigs() {
		
		//Get number of allowed bunks
		int numBotOnlyAllowed = 0;
		int numTopOnlyAllowed = 0;
		for (Camper i : Camper.campers) {
			if (i.getTopBunkAllowed() && !i.getBotBunkAllowed()) {
				numTopOnlyAllowed += 1;
			}
			else if (i.getBotBunkAllowed() && !i.getTopBunkAllowed()) {
				numBotOnlyAllowed += 1;
			}
		}
		
		//Get number of bunks
		int numBotBunks = 0;
		int numTopBunks = 0;
		for (Section i : Section.sections) {
			numBotBunks += i.getNumBotBunks();
			numTopBunks += i.getNumTopBunks();
		}
		
		botBunkOnlyThreshold = numBotOnlyAllowed - numBotBunks;
		topBunkOnlyThreshold = numTopOnlyAllowed - numTopBunks;
		
		botBunkOnlyThreshold = Math.max(botBunkOnlyThreshold, 0);
		topBunkOnlyThreshold = Math.max(topBunkOnlyThreshold, 0);
			
	}
	
	
	/**
	 * Prints out the most optimal arrangements and total seconds in readable way
	 * @param secsTotal
	 * 			The number of seconds the whole program took
	 */
	public static void printFormat(double secsTotal) {
		
		if (Main.trials == 1) {
			for (int j = 0; j < highScoreArrangements.size(); j++) {
				System.out.println();
				System.out.println("Optimal Arrangement " + (j + 1));
				System.out.println(calcPoints(highScoreArrangements.get(j)));
				System.out.println();
				
				int camperIndex = 0;
				for (Section s : Section.sections) {
					String tempTopString = "";
					String tempBotString = "";
					for (int camper = 0; camper < s.getNumBotBunks(); camper++) {
						String camperName = highScoreArrangements.get(j).getCamper(camperIndex).getName();
						tempBotString += camperName;
						for (int chars = camperName.length(); chars < FORMAT_NAME_LENGTH; chars++) {
							tempBotString += " ";
						}
						camperIndex++;
					}
					for (int camper = 0; camper < s.getNumTopBunks(); camper++) {
						String camperName = highScoreArrangements.get(j).getCamper(camperIndex).getName();
						tempTopString += camperName;
						for (int chars = camperName.length(); chars < FORMAT_NAME_LENGTH; chars++) {
							tempTopString += " ";
						}
						camperIndex++;
					}
					System.out.println(tempTopString);
					System.out.println(tempBotString);
					System.out.println();
				}
				
			}
			
			System.out.println("Total Time");
			timeOutput(secsTotal);
			
		} else {
			System.out.println(calcPoints(highScoreArrangements.get(0)));
		}
		
		
		
	}
}
