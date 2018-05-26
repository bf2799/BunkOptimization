package objects;

import java.util.ArrayList;

public class Section {

	public static ArrayList<Section> sections = new ArrayList<Section>();
	
	private int numBotBunks;
	private int numTopBunks;
	
	private ArrayList<Camper> topBunks;
	private ArrayList<Camper> botBunks;
	
	public Section(int numBotBunks, int numTopBunks) {
		
		this.numBotBunks = numBotBunks;
		this.numTopBunks = numTopBunks;
		
		topBunks = new ArrayList<Camper>();
		botBunks = new ArrayList<Camper>();
	}
	
	public int getNumBotBunks() {
		return this.numBotBunks;
	}
	
	public int getNumTopBunks() {
		return this.numTopBunks;
	}
	
	public void reset() {
		topBunks.clear();
		botBunks.clear();
	}
	
	public void addBotBunk(Camper camper) {
		botBunks.add(camper);
	}
	
	public void addTopBunk(Camper camper) {
		topBunks.add(camper);
	}
	
	public Camper getBotBunk(int index) {
		return botBunks.get(index);
	}
	
	public Camper getTopBunk(int index) {
		return topBunks.get(index);
	}
}
