package datalog;

import java.util.ArrayList;

public class RuleBody {
	private ArrayList<Predicate> predicates;
	private ArrayList<Condition> conditions;
	
	public RuleBody(ArrayList<Predicate> values, ArrayList<Condition> conditons) {
		super();
		this.setPredicates(values);
		this.setConditions(conditons);
	}

	public ArrayList<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(ArrayList<Predicate> predicates) {
		this.predicates = predicates;
	}

	public ArrayList<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public String toString(){
		return predicates.toString() + "," + conditions.toString();
	}
	
	
}
