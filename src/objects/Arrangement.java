package objects;

import java.util.ArrayList;

public class Arrangement {
	
	private ArrayList<Camper> arrangement;
	
	/**
	 * Arrangements are sorted by bottom bunks of section 1, 
	 * top bunks of section 1, bottom bunks of section 2, etc.
	 */
	public Arrangement(ArrayList<Camper> arrangement) {
		this.arrangement = arrangement;
	}
	
	public Camper getCamper(int index) {
		return this.arrangement.get(index);
	}
	
	public int getLength() {
		return this.arrangement.size();
	}
	
	public ArrayList<Camper> getList() {
		return this.arrangement;
	}
}
