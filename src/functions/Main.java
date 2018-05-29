package functions;

import java.util.ArrayList;

import objects.Arrangement;


public class Main {

	public static double minutesRun = 0 + 60/60.0;
	public static int trials = 30;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		Helpers.initCampers();
		Helpers.initSections();
		Helpers.setAllowedBunkConfigs();
		
		for (int i = 1; i <= trials; i++) {
			
			double startTime = System.currentTimeMillis();
			
			Helpers.highScore = 0;
			Helpers.highScoreArrangements.clear();
			
			GeneticAlgorithms.initPopulation();
			
			int prevSecsRemaining = 0;
			
			while ((System.currentTimeMillis() - startTime) / 1000 < minutesRun * 60) {
				GeneticAlgorithms.createGen();
				int secsRemaining = (int) Math.round(minutesRun * 60 - (System.currentTimeMillis() - startTime) / 1000);
				if (secsRemaining != prevSecsRemaining) {
					if (trials == 1) {
						Helpers.timeOutput(secsRemaining);
					}
					prevSecsRemaining = secsRemaining;
				}
			}
			
			Helpers.highScoreArrangements = (ArrayList<Arrangement>) Helpers.arrangementStdevFilter().clone();
			
			double secsTotal = (System.currentTimeMillis() - startTime) / Math.pow(10, 3);
			
			Helpers.printFormat(secsTotal);
			
			if (trials > 1) {
				System.out.println("Trial " + i + " complete");
			}
		}

	}

}
