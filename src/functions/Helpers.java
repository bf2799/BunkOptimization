package functions;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

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
	
	//public static DecimalFormat df = new DecimalFormat("#.#####");
	
	public static final int FORMAT_NAME_LENGTH = 25;
	
	public static final double BUNK_LEVEL_ADDITION = 10;
	public static final double BUNK_BUDDY_ADDITION = 1;
	public static final double SIDE_ADDITION = 1;
	public static final double DIAGONAL_ADDITION = 0.5;
	
	public static final String DELIMITER = ",";
	
	private static File file;
	
	public static JFrame frame;
	public static JPanel timePanel;
	public static CardLayout cardLayout;
	public static JLabel timeRemainLabel;
	public static JPanel mainPanel;
	public static JPanel outputPanel;
	
	private final static int INT_TEXT_FIELD_WIDTH = 3;
	
	public static void manageUserInput() {
		
		cardLayout = new CardLayout();
		
		JFrame.setDefaultLookAndFeelDecorated(true);
	    JDialog.setDefaultLookAndFeelDecorated(true);
	    frame = new JFrame("Bunk Optimization");
	    frame.setLayout(cardLayout);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    mainPanel = new JPanel();
	    mainPanel.setLayout(cardLayout);
	    
	    timePanel = new JPanel();
	    timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
	    
	    int maxBedsInSection = 0;
		for (Section s : Section.sections) {
			if (s.getNumBotBunks() > maxBedsInSection) {
				maxBedsInSection = s.getNumBotBunks();
			}
		}
	    
	    JPanel inputPanel = new JPanel();
	    inputPanel.setLayout(new GridLayout(1, 3, 10, 10));
	    
	    ArrayList<JTextField> textFields = new ArrayList<JTextField>();
	    
	    JButton selectFileButton = new JButton("Select CSV File");
	    JButton sectionEnterButton = new JButton("Enter");
	    
	    JLabel sectionLabel = new JLabel("Sections in Bunk");
	    JTextField sectionField = new JTextField(INT_TEXT_FIELD_WIDTH);
	    
	    JLabel loopLabel1 = new JLabel("  Enter # of loops to run");
	    JLabel loopLabel2 = new JLabel("  ~1-2 minutes each");
	    JLabel loopLabel3 = new JLabel("  ~30 loops maximizes return on time");
	    JLabel loopLabel4 = new JLabel("  More is better");
	    JTextField loopField = new JTextField(INT_TEXT_FIELD_WIDTH);
	    
	    JLabel timeLabel = new JLabel("Time Remaining: ");
	    timeRemainLabel = new JLabel("00:00:00");
	    timeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
	    timeRemainLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    timeRemainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    
	    //Init loops
	    JButton loopEnterButton = new JButton("Enter");
	    loopEnterButton.addActionListener(new ActionListener() {
	    	
	    	@Override
	    	public void actionPerformed(ActionEvent ae) {
	    		if (isInt(loopField.getText())) {
		    		Main.loops = Integer.parseInt(loopField.getText());
		    		cardLayout.next(Helpers.mainPanel);
		    		frame.pack();
		    		SwingWorker<Void, Void> runTrials = new SwingWorker<Void, Void>() {

		    			@Override
		    			protected Void doInBackground() throws Exception {
		    				Main.userInputComplete();
		    				return null;
		    			}
		    			
		    		};
		    		runTrials.execute();
		    		
	    		}
	    	}
	    	
	    });
	    
	    //Init bunk numbers
	    JButton bunkNumberEnterButton = new JButton("Enter");
	    bunkNumberEnterButton.addActionListener(new ActionListener() {
	    	
	    	@Override
	    	public void actionPerformed(ActionEvent ae) {
	    		
	    		//Ensures bottom is 1 or 0 more than top and that total entered is correct
	    		int totalBunksEntered = 0;
	    		boolean bunkDifferenceAllowed = true;
	    		boolean botBunk = true;
	    		int prev = 0;
	    		
	    		boolean allInts = true;
	    		for (JTextField num : textFields) {
	    			if(!isInt(num.getText())) {
	    				allInts = false;
	    			}
	    		}
	    		
	    		if (allInts) {
		    		for (JTextField num : textFields) {
		    			totalBunksEntered += Integer.parseInt(num.getText());
		    			if (!botBunk) {
		    				if (!(prev - Integer.parseInt(num.getText()) == 0 || prev - Integer.parseInt(num.getText()) == 1)) {
		    					bunkDifferenceAllowed = false;
		    				}
		    			} else {
		    				prev = Integer.parseInt(num.getText());
		    			}
		    			botBunk = !botBunk;
		    		}
		    		
		    		//Enforces enter rules
		    		if (totalBunksEntered == Camper.campers.size() && bunkDifferenceAllowed) {
		    			
		    			//Assign right numbers to each section
		    			for (int i = 0; i < textFields.size(); i += 2) {
							Section.sections.add(new Section(Integer.parseInt(textFields.get(i).getText()),
									Integer.parseInt(textFields.get(i + 1).getText())));
		    			}
		    			
		    			inputPanel.removeAll();
		    		    inputPanel.setLayout(new GridLayout(6, 1, 10, 10));
		    			inputPanel.add(loopLabel1);
		    			inputPanel.add(loopLabel2);
		    			inputPanel.add(loopLabel3);
		    			inputPanel.add(loopLabel4);
		    			inputPanel.add(loopField);
		    			inputPanel.add(loopEnterButton);
		    			frame.pack();
		    			
		    			
		    		} else {
		    			inputPanel.removeAll();
		    		    inputPanel.setLayout(new GridLayout(1, 3, 10, 10));
		    		    inputPanel.add(sectionLabel);
		    			sectionField.setText("Invalid layout");
		    			inputPanel.add(sectionField);
		    			inputPanel.add(sectionEnterButton);
			    		sectionField.setEnabled(true);
			    		sectionEnterButton.setEnabled(true);
			    		frame.pack();
		    			textFields.clear();
		    		}
	    		}
	    	}
	    });
	    
	    //Init sections
	    sectionEnterButton.addActionListener(new ActionListener() {
	    	
	    	@Override
	    	public void actionPerformed(ActionEvent ae) {
	    		
	    		if (isInt(sectionField.getText())) {
	    		
		    		sectionEnterButton.setEnabled(false);
		    		sectionField.setEnabled(false);
		    		int numSections = Integer.parseInt(sectionField.getText());
		    		
		    		inputPanel.setLayout(new GridLayout(numSections * 2 + 1, 3, 10, 10));
		    		for (int i = 1; i < numSections + 1; i++) {
		    			
		    			inputPanel.add(new JLabel("Bottom bunks in section " + i));
		    			textFields.add(new JTextField(INT_TEXT_FIELD_WIDTH));
		    			inputPanel.add(textFields.get(textFields.size() - 1));
		    			inputPanel.add(new JLabel(""));
		    			inputPanel.add(new JLabel("Top bunks in section " + i));
		    			textFields.add(new JTextField(INT_TEXT_FIELD_WIDTH));
		    			inputPanel.add(textFields.get(textFields.size() - 1));
		    			if (i != numSections) {
		    				inputPanel.add(new JLabel(""));
		    			}
		    		}
		    		
		    		inputPanel.add(bunkNumberEnterButton);
		    		frame.pack();
	    		}
	    	}
	    });
	    
	    //Init file
	    selectFileButton.addActionListener(new ActionListener() {
	    	
	    	@Override
	    	public void actionPerformed(ActionEvent ae) {
	    		if (initFileAndValid()) {
	    			inputPanel.remove(selectFileButton);
	    			inputPanel.add(sectionLabel);
	    			inputPanel.add(sectionField);
	    			inputPanel.add(sectionEnterButton);
		    		frame.pack();
		    		Helpers.initCampers();
	    		} else {
	    			selectFileButton.setText("File must be .csv");
	    			frame.pack();
	    		}
	    	}
	    });
	    
	    
	    inputPanel.add(selectFileButton, 0);
	    timePanel.add(timeLabel);
	    timePanel.add(timeRemainLabel);
	    mainPanel.add(inputPanel);
	    mainPanel.add(timePanel);
	    frame.add(mainPanel);
	    frame.pack();
	    frame.setVisible(true);
		
	}
	
	
	/**
	 * Gets file to read from
	 * @throws IOException
	 */
	public static boolean initFileAndValid() {
		
		boolean valid = false;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnValue = fileChooser.showDialog(null, "Select");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			if(file.getName().contains(".csv")) {
				valid = true;
			}
		}
		return valid;
		
	}
	
	/**
	 * Creates list of campers and attributes from file
	 */
	public static void initCampers() {
		
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader(new FileReader(file));
			
			br.readLine();
			
			String line = "";
			
			while((line = br.readLine()) != null) {
				
				String[] lineSplit = line.split(DELIMITER);

				boolean topBunkAllowed = false;
				boolean botBunkAllowed = false;
				
				if (!lineSplit[2].equals("")) {
					topBunkAllowed = true;
				}
				if (!lineSplit[3].equals("")) {
					botBunkAllowed = true;
				}
				
				ArrayList<String> revLineSplit = new ArrayList<String>();
				
				for (int i = 0; i <= 9; i++) {
					if (i >= lineSplit.length) {
						revLineSplit.add("");
					} else {
						revLineSplit.add(lineSplit[i]);
					}
				}
				
				Camper.campers.add(new Camper(revLineSplit.get(0), topBunkAllowed, botBunkAllowed, revLineSplit.get(4),
						revLineSplit.get(5), revLineSplit.get(6), revLineSplit.get(7), revLineSplit.get(8),
						revLineSplit.get(9)));
				
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
	public static String timeOutput(double secs) {
		
		int formatSecs = (int) Math.round(secs);
		int hoursRemaining = formatSecs / 3600;
		int minutesRemaining = (formatSecs - 3600 * hoursRemaining) / 60;
		formatSecs -= hoursRemaining * 3600 + minutesRemaining * 60;
		return (hoursRemaining + ":" + minutesRemaining + ":" + formatSecs);
		
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
		
		int maxBedsInSection = 0;
		for (Section s : Section.sections) {
			if (s.getNumBotBunks() > maxBedsInSection) {
				maxBedsInSection = s.getNumBotBunks();
			}
		}
		
		outputPanel = new JPanel();
		outputPanel.setLayout(new GridLayout(3 * Section.sections.size() + 2, Math.max(maxBedsInSection, 4), 15, 15));
		
		JLabel optimArrangeNum = new JLabel("Optimal Arrangement 1");
		JLabel optimArrangePts = new JLabel(Double.toString(calcPoints(highScoreArrangements.get(0))) + " pts");
		
		JLabel totalTimeLabel = new JLabel("Total Time:");
		JLabel totalTime = new JLabel(timeOutput(secsTotal));
		totalTimeLabel.setForeground(Color.RED);
		totalTime.setForeground(Color.RED);
		
		//Section labels
		JLabel[] sectionLabels = new JLabel[Section.sections.size()];
		JLabel[][] topBunkLabels = new JLabel[Section.sections.size()][Math.max(maxBedsInSection, 4)];
		JLabel[][] botBunkLabels = new JLabel[Section.sections.size()][Math.max(maxBedsInSection, 4)];

		//Creating section, topBunk, and botBunk labels
		for (int i = 0; i < Section.sections.size(); i++) {
			sectionLabels[i] = new JLabel();
			for (int j = 0; j < Math.max(maxBedsInSection, 4); j++) {
				topBunkLabels[i][j] = new JLabel();
				botBunkLabels[i][j] = new JLabel();
			}
		}
		
		JButton prevButton = new JButton("Previous");
		JButton nextButton = new JButton("Next");
		
		int[] arrangementIndex = {0};
		
		//Disabling buttons to start
		prevButton.setEnabled(false);
		if (highScoreArrangements.size() == 1) {
			nextButton.setEnabled(false);
		}
		
		//Setting all the top bunk / bottom bunk labels (first time)
		int camperIndex = 0;
		for (int s = 0; s < Section.sections.size(); s++) {
			
			sectionLabels[s].setText("Section " + (s + 1));
			sectionLabels[s].setForeground(Color.RED);
			
			for (int camper = 0; camper < Section.sections.get(s).getNumBotBunks(); camper++) {
				String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
				botBunkLabels[s][camper].setText(camperName);
				camperIndex++;
			}
			for (int camper = 0; camper < Section.sections.get(s).getNumTopBunks(); camper++) {
				String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
				topBunkLabels[s][camper].setText(camperName);
				camperIndex++;
			}

		}
		
		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (arrangementIndex[0] > 0) {
					arrangementIndex[0]--;
					
					//Setting all the top bunk / bottom bunk labels
					int camperIndex = 0;
					for (int s = 0; s < Section.sections.size(); s++) {
						for (int camper = 0; camper < Section.sections.get(s).getNumBotBunks(); camper++) {
							String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
							botBunkLabels[s][camper].setText(camperName);
							camperIndex++;
						}
						for (int camper = 0; camper < Section.sections.get(s).getNumTopBunks(); camper++) {
							String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
							topBunkLabels[s][camper].setText(camperName);
							camperIndex++;
						}
					}
					
					optimArrangeNum.setText("Optimal Arrangement " + (arrangementIndex[0] + 1));
					
					if (arrangementIndex[0] == 0) {
						prevButton.setEnabled(false);
					} else {
						nextButton.setEnabled(true);
						prevButton.setEnabled(true);
					}
					
					frame.pack();
				}
			}
			
		});
		
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (arrangementIndex[0] < highScoreArrangements.size() - 1) {
					arrangementIndex[0]++;
					
					//Setting all the top bunk / bottom bunk labels
					int camperIndex = 0;
					for (int s = 0; s < Section.sections.size(); s++) {
						for (int camper = 0; camper < Section.sections.get(s).getNumBotBunks(); camper++) {
							String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
							botBunkLabels[s][camper].setText(camperName);
							camperIndex++;
						}
						for (int camper = 0; camper < Section.sections.get(s).getNumTopBunks(); camper++) {
							String camperName = highScoreArrangements.get(arrangementIndex[0]).getCamper(camperIndex).getName();
							topBunkLabels[s][camper].setText(camperName);
							camperIndex++;
						}
					}
					
					optimArrangeNum.setText("Optimal Arrangement " + (arrangementIndex[0] + 1));

					if (arrangementIndex[0] == highScoreArrangements.size() - 1) {
						nextButton.setEnabled(false);
					} else {
						prevButton.setEnabled(true);
						nextButton.setEnabled(true);
					}
				
					frame.pack();
				}
				
			}
			
		});
		
		//Adding top row to outputPanel
		outputPanel.add(prevButton);
		outputPanel.add(optimArrangeNum);
		outputPanel.add(optimArrangePts);
		outputPanel.add(nextButton);
		for (int i = 4; i < Math.max(4, maxBedsInSection); i++) {
			outputPanel.add(new JLabel());
		}
		
		//Adding sections to outputPanel
		for (int s = 0; s < Section.sections.size(); s++) {
			outputPanel.add(sectionLabels[s]);
			for (int i = 1; i < Math.max(4, maxBedsInSection); i++) {
				outputPanel.add(new JLabel());
			}
			for (JLabel label : topBunkLabels[s]) {
				outputPanel.add(label);
			}
			for (JLabel label : botBunkLabels[s]) {
				outputPanel.add(label);
			}
		}
		
		//Adding time output to outputPanel
		outputPanel.add(totalTimeLabel);
		outputPanel.add(totalTime);
		for (int i = 2; i < Math.max(4, maxBedsInSection); i++) {
			outputPanel.add(new JLabel());
		}
		
		outputPanel.setVisible(true);
		mainPanel.add(outputPanel);
		cardLayout.next(mainPanel);
		frame.pack();
		
	}

	/**
	 * Returns whether a string is an integer or not
	 * @param string
	 * 			The string to check
	 * @return
	 * 			Is an integer?
	 */
	public static boolean isInt(String string) {
		boolean isInt = true;
		for (int i = 0; i < string.length(); i++) {
			if (!(string.substring(i, i + 1).equals("0") || string.substring(i, i + 1).equals("1")
					|| string.substring(i, i + 1).equals("2") || string.substring(i, i + 1).equals("3")
					|| string.substring(i, i + 1).equals("4") || string.substring(i, i + 1).equals("5")
					|| string.substring(i, i + 1).equals("6") || string.substring(i, i + 1).equals("7")
					|| string.substring(i, i + 1).equals("8") || string.substring(i, i + 1).equals("9"))) {
				isInt = false;
			}
		}
		
		return isInt;
	}
	
}
