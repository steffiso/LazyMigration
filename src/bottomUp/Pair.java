package bottomUp;

import java.util.ArrayList;

public class Pair {
	public ArrayList<Relation> relations = null;
	public ArrayList<Condition> conditions = null;
	public Pair(ArrayList<Relation> values, ArrayList<Condition> conditons) {
		super();
		this.relations = values;
		this.conditions = conditons;
	}
}
