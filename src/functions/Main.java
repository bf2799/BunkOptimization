package functions;

import java.util.ArrayList;

import objects.Arrangement;


public class Main {

	public static int trials = 50;
	public static double minutesRunPerLoop = 3;
	public static int loops = 1;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		Helpers.initCampers();
		Helpers.initSections();
		Helpers.setAllowedBunkConfigs();
		
		for (int j = 0; j < trials; j++) {
		
			double startTime = System.currentTimeMillis();
			int prevSecsRemaining = 0;
			
			for (int i = 1; i <= loops; i++) {
				
				GeneticAlgorithms.initPopulation();
				
				double tempStartTime = System.currentTimeMillis();
				
				while ((System.currentTimeMillis() - tempStartTime) / 1000 < minutesRunPerLoop * 60 && (minutesRunPerLoop * 60 * loops - (System.currentTimeMillis() - startTime) / 1000) > 0) {
					GeneticAlgorithms.createGen();
					int secsRemaining = (int) Math.round(minutesRunPerLoop * 60 * loops - (System.currentTimeMillis() - startTime) / 1000);
					if (secsRemaining != prevSecsRemaining) {
						if (trials == 1)
							Helpers.timeOutput(secsRemaining);
						prevSecsRemaining = secsRemaining;
					}
				}
			}
			
			Helpers.highScoreArrangements = (ArrayList<Arrangement>) Helpers.arrangementStdevFilter().clone();
			
			double secsTotal = (System.currentTimeMillis() - startTime) / Math.pow(10, 3);
			
			Helpers.printFormat(secsTotal);
			
			if (trials != 1) {
				System.out.println("Trial " + j + " complete");
			}
			
			Helpers.highScore = 0;
			Helpers.highScoreArrangements.clear();
		}
	}

}
