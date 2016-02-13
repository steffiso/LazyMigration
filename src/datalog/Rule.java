package datalog;

import java.util.ArrayList;

public class Rule {

	private Predicate ruleHead;
	private RuleBody ruleBody;
	private ArrayList<String> dependencies;
	
	public Rule(Predicate ruleHead, RuleBody ruleBody){
		this.ruleHead = ruleHead;
		this.setRuleBody(ruleBody);
		setDependencies();
	}

	public Predicate getHead() {
		return ruleHead;
	}

	public void setHead(Predicate ruleHead) {
		this.ruleHead = ruleHead;
	}

	public RuleBody getRuleBody() {
		return ruleBody;
	}

	public void setRuleBody(RuleBody ruleBody) {
		this.ruleBody = ruleBody;
	}

	public ArrayList<Predicate> getPredicates() {
		return ruleBody.getPredicates();
	}

	public void setPredicates(ArrayList<Predicate> predicates) {
		ruleBody.setPredicates(predicates);
	}

	public ArrayList<Condition> getConditions() {
		return ruleBody.getConditions();
	}

	public void setConditions(ArrayList<Condition> conditions) {
		ruleBody.setConditions(conditions);
	}
	
	public void setDependencies(){
		dependencies = new ArrayList<String>();
		for (Predicate p:ruleBody.getPredicates()) {
			String kind = p.getKind();
			if (!dependencies.contains(kind)) dependencies.add(kind);
		}		
	}

	public ArrayList<String> getDependencies(){
		return dependencies;
	}
	
	@Override
	public String toString(){
		String rule =  ruleHead.toString() + ":-" ;
		ArrayList<Predicate> predicates = ruleBody.getPredicates();
		ArrayList<Condition> conditions = ruleBody.getConditions();
		if (predicates != null) rule = rule + predicates.toString();
		if (conditions != null) rule = rule + conditions.toString();
		
		return rule;
	}

}
