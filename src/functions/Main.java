package functions;

import java.util.ArrayList;

import objects.Arrangement;


public class Main {

	public static int trials = 1;
	public static int loops = 0;
	public static final int GEN_PER_LOOP = 400;
	
	public static void main(String[] args) {
		
		Helpers.manageUserInput();
		
	}
	
	@SuppressWarnings("unchecked")
	public static void userInputComplete() {
		
		Helpers.setAllowedBunkConfigs();
		
		for (int j = 0; j < trials; j++) {
		
			double startTime = System.currentTimeMillis();
			int prevSecsRemaining = 0;
			
			for (int i = 1; i <= loops; i++) {
				
				GeneticAlgorithms.initPopulation();
				
				for (int gensComplete = 0; gensComplete < GEN_PER_LOOP; gensComplete++) {
					GeneticAlgorithms.createGen();
					int secsRemaining = (int) Math.round((double) (GEN_PER_LOOP * loops) / (gensComplete + (i - 1) * GEN_PER_LOOP) * (System.currentTimeMillis() - startTime) / 1000.0 - (System.currentTimeMillis() - startTime) / 1000.0);
					if (secsRemaining != prevSecsRemaining) {
						if (trials == 1) {
							Helpers.timeRemainLabel.setText(Helpers.timeOutput(secsRemaining));
							Helpers.frame.pack();
						}
						prevSecsRemaining = secsRemaining;
					}
					gensComplete++;
				}
			}
			
			Helpers.highScoreArrangements = (ArrayList<Arrangement>) Helpers.arrangementStdevFilter().clone();
			
			double secsTotal = (System.currentTimeMillis() - startTime) / Math.pow(10, 3);
			
			Helpers.printFormat(secsTotal);
			
			if (trials != 1) {
				System.out.println("Trial " + j + " complete");
			}
			
			Helpers.highScore = 0;
			if (trials != 1) {
				Helpers.highScoreArrangements.clear();
			}
		}
	}

}
