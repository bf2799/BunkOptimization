package functions;

import java.util.ArrayList;

import objects.Arrangement;


public class Main {

	public static double minutesRun = 1.0;
	public static int loops = 15;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		Helpers.initCampers();
		Helpers.initSections();
		Helpers.setAllowedBunkConfigs();
		
		double startTime = System.currentTimeMillis();
		
		for (int i = 1; i <= loops; i++) {
			
			GeneticAlgorithms.initPopulation();
			
			int prevSecsRemaining = 0;
			
			while ((System.currentTimeMillis() - startTime) / 1000 < minutesRun * 60 * loops) {
				GeneticAlgorithms.createGen();
				int secsRemaining = (int) Math.round(minutesRun * 60 * loops - (System.currentTimeMillis() - startTime) / 1000);
				if (secsRemaining != prevSecsRemaining) {
					Helpers.timeOutput(secsRemaining);
					prevSecsRemaining = secsRemaining;
				}
			}
		}
		
		Helpers.highScoreArrangements = (ArrayList<Arrangement>) Helpers.arrangementStdevFilter().clone();
		
		double secsTotal = (System.currentTimeMillis() - startTime) / Math.pow(10, 3);
		
		Helpers.printFormat(secsTotal);
	}

}
