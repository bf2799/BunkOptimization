package functions;

public class Main {

	public static double minutesRun = 30/60.0;
	
	public static void main(String[] args) {
		
		Helpers.initCampers();
		Helpers.initSections();
		Helpers.setAllowedBunkConfigs();
		
		double startTime = System.currentTimeMillis();
		
		GeneticAlgorithms.initPopulation();
		
		int prevSecsRemaining = 0;
		
		while ((System.currentTimeMillis() - startTime) / 1000 < minutesRun * 60) {
			GeneticAlgorithms.createGen();
			int secsRemaining = (int) Math.round(minutesRun * 60 - (System.currentTimeMillis() - startTime) / 1000);
			if (secsRemaining != prevSecsRemaining) {
				Helpers.timeOutput(secsRemaining);
				prevSecsRemaining = secsRemaining;
			}
		}
		
		Helpers.highScoreArrangements = Helpers.arrangementStdevFilter();
		
		double secsTotal = (System.currentTimeMillis() - startTime) / Math.pow(10, 3);
		
		Helpers.printFormat(secsTotal);

	}

}
