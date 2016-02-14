package datalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import database.Schema;

import java.util.Map.Entry;

public class Predicate {

	// eine Relation innerhalb einer Query, Bsp. A(?x,?y)
	private String kind;  // --> A
	private ArrayList<String> schema;
	private ArrayList<String> scheme;
	private int anz; // --> 2
	//private ArrayList<String> werte; // --> (?x,?y)
	private Map<String, String> werte;
	private int timestamp;
	private boolean isNot=false;
	private int stratum=0;
	private int ranking=0;
	private ArrayList<Map<String, String>> resultMap;
	private ArrayList<ArrayList<String>> resultMap2;
	
	public Predicate(String kind, int anz, ArrayList<String> schema, Map<String, String> werte) {
		super();
		this.kind = kind;
		this.anz = anz;
		this.werte = werte;
	}
	
	public Predicate(String kind, int anz, ArrayList<String> scheme, ArrayList<ArrayList<String>> resultMap2) {
		super();
		this.kind = kind;
		this.anz = anz;
		this.scheme=scheme;
		this.resultMap2 = resultMap2;
	}
	public String getKind() {
		return kind;
	}
	public int getAnz() {
		return anz;
	}
	public Map<String, String> getWerte() {
		return werte;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public void setAnz(int anz) {
		this.anz = anz;
	}
	public void setWerte(Map<String, String> werte) {
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
	public ArrayList<Map<String, String>> getResultMap() {
		return resultMap;
	}
	public void setResultMap(ArrayList<Map<String, String>> resultMap) {
		this.resultMap = resultMap;
	}
	
	@Override
	public String toString(){
		String predicate = "";
		if (isNot) predicate = "not ";
		predicate = predicate + kind + "("; 
		for (Entry<String, String> e : werte.entrySet()){
			if (e.getValue() != "") predicate = predicate + e.getValue() + ",";
			else predicate = predicate + e.getKey() + ",";
		}
		predicate = predicate.substring(0, predicate.length()-1) + ")";
		//predicate = predicate + werte.toString() + ")";
		return predicate;
	}
	public ArrayList<String> getSchema() {
		return schema;
	}
	public void setSchema(ArrayList<String> schema) {
		this.schema = schema;
	}
	public ArrayList<ArrayList<String>> getResultMap2() {
		return resultMap2;
	}
	public void setResultMap2(ArrayList<ArrayList<String>> resultMap2) {
		this.resultMap2 = resultMap2;
	}

	public ArrayList<String> getScheme() {
		return scheme;
	}

	public void setScheme(ArrayList<String> scheme) {
		this.scheme = scheme;
	}


}
