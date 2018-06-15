package objects;

import java.util.ArrayList;

public class Camper {

	public static ArrayList<Camper> campers = new ArrayList<Camper>();
	
	//For seeding population purposes
	public static ArrayList<Camper> topOnlyCampers = new ArrayList<Camper>();
	public static ArrayList<Camper> botOnlyCampers = new ArrayList<Camper>();
	public static ArrayList<Camper> noTopBotPrefCampers = new ArrayList<Camper>();
	
	private String name;
	private boolean topBunkAllowed;
	private boolean botBunkAllowed;
	private String bunkMatePref1;
	private String bunkMatePref2;
	private String bunkMatePref3;
	private String nearPref1;
	private String nearPref2;
	private String nearPref3;
	
	public Camper(String name, boolean topBunkAllowed, boolean botBunkAllowed,
			String bunkMatePref1, String bunkMatePref2, String bunkMatePref3,
			String nearPref1, String nearPref2, String nearPref3) {
		this.name = name;
		this.topBunkAllowed = topBunkAllowed;
		this.botBunkAllowed = botBunkAllowed;
		this.bunkMatePref1 = bunkMatePref1;
		this.bunkMatePref2 = bunkMatePref2;
		this.bunkMatePref3 = bunkMatePref3;
		this.nearPref1 = nearPref1;
		this.nearPref2 = nearPref2;
		this.nearPref3 = nearPref3;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean getTopBunkAllowed() {
		return this.topBunkAllowed;
	}
	
	public boolean getBotBunkAllowed() {
		return this.botBunkAllowed;
	}
	
	public boolean bunkMatePrefMatches(String otherName) {
		return bunkMatePref1.equalsIgnoreCase(otherName) || bunkMatePref2.equalsIgnoreCase(otherName)
				|| bunkMatePref3.equalsIgnoreCase(otherName);
	}
	
	public boolean nearPrefMatches(String otherName) {
		return nearPref1.equalsIgnoreCase(otherName) || nearPref2.equalsIgnoreCase(otherName)
				|| nearPref3.equalsIgnoreCase(otherName);
	}
	
}
