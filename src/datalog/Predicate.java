package datalog;

import java.util.ArrayList;

public class Predicate {

	// eine Relation innerhalb einer Query, Bsp. A(?x,?y)
	private String kind; // --> A	
	private ArrayList<String> scheme; // --> (?x,?y)
	private int anz; // --> 2
	private int timestamp;
	private boolean isNot = false;
	private boolean isHead = false;
	private int stratum = 0;
	private int ranking = 0;
	private ArrayList<ArrayList<String>> relation;

	public Predicate(String kind, int anz, ArrayList<String> scheme,
			ArrayList<ArrayList<String>> relation) {
		super();
		this.kind = kind;
		this.anz = anz;
		this.scheme = scheme;
		this.relation = relation;
	}

	public Predicate(String kind, int anz, ArrayList<String> scheme) {
		super();
		this.kind = kind;
		this.anz = anz;
		this.scheme = scheme;
	}

	public String getKind() {
		return kind;
	}

	public int getAnz() {
		return anz;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setAnz(int anz) {
		this.anz = anz;
	}

	public boolean isNot() {
		return isNot;
	}

	public void setNot(boolean isNot) {
		this.isNot = isNot;
	}

	public int getStratum() {
		return stratum;
	}

	public void setStratum(int stratum) {
		this.stratum = stratum;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<ArrayList<String>> getRelation() {
		return relation;
	}

	public void setRelation(ArrayList<ArrayList<String>> relation) {
		this.relation = relation;
	}

	public ArrayList<String> getScheme() {
		return scheme;
	}

	public void setScheme(ArrayList<String> scheme) {
		this.scheme = scheme;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}

	@Override
	public String toString() {
		return "Predicate [kind=" + kind + ", scheme=" + scheme + ", isNot="
				+ isNot + "]";
	}
	
	

}
