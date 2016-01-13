package bottomUp;

import java.util.ArrayList;

public class Rule {

	private Predicate ruleHead;
	private ArrayList<Predicate> predicates;
	private ArrayList<Condition> conditions;
	private ArrayList<String> dependencies;

	public Rule(Predicate ruleHead, ArrayList<Predicate> predicates) {
		super();
		this.ruleHead = ruleHead;
		this.predicates = predicates;
		setDependencies();
	}

	public Predicate getHead() {
		return ruleHead;
	}

	public void setHead(Predicate ruleHead) {
		this.ruleHead = ruleHead;
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
	
	public void setDependencies(){
		dependencies = new ArrayList<String>();
		for (Predicate p:predicates) {
			String kind = p.getKind();
			if (!dependencies.contains(kind)) dependencies.add(kind);
		}		
	}

	public ArrayList<String> getDependencies(){
		return dependencies;
	}

}
