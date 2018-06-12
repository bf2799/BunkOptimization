package functions;

import java.util.ArrayList;
import java.util.Random;

import objects.Arrangement;
import objects.Camper;

public class GeneticAlgorithms {

	public static final int initPopulationSize = 420;
	public static final double KEEP_PERCENT = 0.1;
	
	public static ArrayList<Arrangement> initPopulation = new ArrayList<Arrangement>();
	
	private static Random generator = new Random();
	
	
	public static void initPopulation() {
		
		initPopulation.clear();
		
		while (initPopulation.size() < initPopulationSize) {
			
			//Generate random permutation
			@SuppressWarnings("unchecked")
			ArrayList<Camper> subArrangement = (ArrayList<Camper>) Camper.campers.clone();
			ArrayList<Camper> tempArrangement = new ArrayList<Camper>();
			
			while (subArrangement.size() > 0) {
				int index = generator.nextInt(subArrangement.size());
			
				tempArrangement.add(subArrangement.get(index));
				subArrangement.remove(index);
			}
			
			initPopulation.add(new Arrangement(tempArrangement));
		}
		
	}

	
	//Stochastic universal sampling
	public static ArrayList<Arrangement> SUS(ArrayList<Arrangement> population, int numKeep) {
		
		double fitness = 0;
		
		for (Arrangement a : population) {
			
			double tempScore = Helpers.calcPoints(a);
			fitness += tempScore;
			
			if (tempScore == Helpers.highScore) {
				
				//Makes sure that a repeat of previous was not created
				boolean same = false;
				for (Arrangement arr : Helpers.highScoreArrangements) {
					int numSame = 0;
					for (int i = 0; i < arr.getLength(); i++) {
						if (a.getCamper(i).equals(arr.getCamper(i))) {
							numSame++;
						}
					}
					if (numSame == arr.getLength()) {
						same = true;
					}
				}
				if (!same) {
					Helpers.highScoreArrangements.add(a);
				}
			} 
			else if (tempScore > Helpers.highScore) {
				Helpers.highScore = tempScore;
				Helpers.highScoreArrangements.clear();
				Helpers.highScoreArrangements.add(a);
			}
		}
		
		double p = fitness / numKeep;
		
		double start = generator.nextDouble() * p;
		
		ArrayList<Double> pointers = new ArrayList<Double>();
		
		for (int i = 0; i < numKeep; i++) {
			pointers.add(start + (i * p));
		}
		
		ArrayList<Arrangement> keep = new ArrayList<Arrangement>();
		
		for (double point : pointers) {
			
			int j = 0;
			double fitnessSum = 0;
			
			while (fitnessSum < point) {
				fitnessSum += Helpers.calcPoints(initPopulation.get(j));
				j++;
			}
			
			keep.add(population.get(j - 1));
		}
		
		return keep;
	}


	@SuppressWarnings("unchecked")
	public static ArrayList<Arrangement> PMX(Arrangement arr1, Arrangement arr2) {
		
		int crossPoint1 = generator.nextInt(arr1.getLength());
		int crossPoint2 = generator.nextInt(arr1.getLength());
		
		ArrayList<Camper> offspring1 = new ArrayList<Camper>();
		ArrayList<Camper> offspring2 = new ArrayList<Camper>();
		
		for (int i = 0; i < arr1.getLength(); i++) {
			offspring1.add(null);
			offspring2.add(null);
		}
		
		//Map 1 contains cross points from parent 1
		ArrayList<Camper> tempMap1 = new ArrayList<Camper>();
		ArrayList<Camper> tempMap2 = new ArrayList<Camper>();
		
		//Set spots between cross points to offspring and get initial maps
		for (int i = Math.min(crossPoint1, crossPoint2); i < Math.max(crossPoint1, crossPoint2); i++) {
			offspring1.set(i, arr2.getCamper(i));
			offspring2.set(i, arr1.getCamper(i));
			tempMap1.add(arr1.getCamper(i));
			tempMap2.add(arr2.getCamper(i));
		}
		
		ArrayList<Camper> map1 = (ArrayList<Camper>) tempMap1.clone();
		ArrayList<Camper> map2 = (ArrayList<Camper>) tempMap2.clone();
		
		//Create thorough map
		boolean mapFinished = false;
		while (!mapFinished) {
			boolean switched = false;
			int index = 0;
			while (!switched) {
				
				//Switches tempMap so a new set of mappings overlap (to later check for deletions)
				if (tempMap1.size() > 0 && tempMap2.contains(tempMap1.get(index))) {
					int tempIndex = tempMap2.indexOf(tempMap1.get(index));
					Camper tempCamper1 = tempMap1.get(index);
					Camper tempCamper2 = tempMap1.get(tempIndex);
					tempMap1.set(index, tempCamper2);
					tempMap1.set(tempIndex, tempCamper1);
					switched = true;
					
					map1.clear();
					map2.clear();
					
					//Delete any mappings in which both maps have same camper
					for (int i = 0; i < tempMap1.size(); i++) {
						if (!tempMap1.get(i).equals(tempMap2.get(i))) {
							map1.add(tempMap1.get(i));
							map2.add(tempMap2.get(i));
						}
					}
					
					tempMap1 = (ArrayList<Camper>) map1.clone();
					tempMap2 = (ArrayList<Camper>) map2.clone();
				}
				
				if (index >= tempMap1.size() - 1 && !switched) {
					switched = true;
					mapFinished = true;
				}
				
				index++;
				
			}
		}
		
		//Set offpsring points before crossover
		for (int i = 0; i < Math.min(crossPoint1, crossPoint2); i++) {
			
			if (map2.contains(arr1.getCamper(i))) {
				offspring1.set(i, map1.get(map2.indexOf(arr1.getCamper(i))));
			} else {
				offspring1.set(i, arr1.getCamper(i));
			}
			
			if (map1.contains(arr2.getCamper(i))) {
				offspring2.set(i, map2.get(map1.indexOf(arr2.getCamper(i))));
			} else {
				offspring2.set(i, arr2.getCamper(i));
			}
			
		}
		
		for (int i = Math.max(crossPoint1, crossPoint2); i < offspring1.size(); i++) {
			
			if (map2.contains(arr1.getCamper(i))) {
				offspring1.set(i, map1.get(map2.indexOf(arr1.getCamper(i))));
			} else {
				offspring1.set(i, arr1.getCamper(i));
			}
			
			if (map1.contains(arr2.getCamper(i))) {
				offspring2.set(i, map2.get(map1.indexOf(arr2.getCamper(i))));
			} else {
				offspring2.set(i, arr2.getCamper(i));
			}
			
		}
		
		ArrayList<Arrangement> offspring = new ArrayList<Arrangement>();
		
		offspring.add(new Arrangement(offspring1));
		offspring.add(new Arrangement(offspring2));
		
		return offspring;
	}
	

	//Create new generation
	@SuppressWarnings("unchecked")
	public static void createGen() {
		
		ArrayList<Arrangement> generation = new ArrayList<Arrangement>();
		
		for (int j = 0; j < (1 / KEEP_PERCENT); j++) {
		
			ArrayList<Arrangement> subParents = SUS(initPopulation, (int) Math.round(KEEP_PERCENT * initPopulation.size()));
			ArrayList<Arrangement> tempParents = new ArrayList<Arrangement>();
			
			while (subParents.size() > 0) {
				int index = generator.nextInt(subParents.size());
				
				tempParents.add(subParents.get(index));
				subParents.remove(index);
				
			}
			
			for (int i = 0; i < tempParents.size() - 1; i += 2) {
				ArrayList<Arrangement> offspring = PMX(tempParents.get(i), tempParents.get(i + 1));
				generation.add(offspring.get(0));
				generation.add(offspring.get(1));
			}
		
		}
		
		initPopulation = (ArrayList<Arrangement>) generation.clone();
	}
}
