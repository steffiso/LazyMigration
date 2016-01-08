package bottomUp;

import java.util.ArrayList;

public class Predicate {

	// eine Relation innerhalb einer Query, Bsp. A(?x,?y)
	private String kind;  // --> A
	private int anz; // --> 2
	private ArrayList<String> werte; // --> (?x,?y)
	private boolean isNot=false;
	private int stratum=0;
	
	public Predicate(String kind, int anz, ArrayList<String> werte) {
		super();
		this.kind = kind;
		this.anz = anz;
		this.werte = werte;
	}
	public String getKind() {
		return kind;
	}
	public int getAnz() {
		return anz;
	}
	public ArrayList<String> getWerte() {
		return werte;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public void setAnz(int anz) {
		this.anz = anz;
	}
	public void setWerte(ArrayList<String> werte) {
		this.werte = werte;
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
}
