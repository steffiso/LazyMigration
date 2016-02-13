package lazyMigration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import datalog.Predicate;
import datalog.Rule;
import datalog.RuleBody;

public class RuleGoalTree {

	private Predicate goal;
	private ArrayList<RuleBody> children;
	
	public RuleGoalTree(ArrayList<Rule> rules){
		//the rule heads must be equal/unified
		this.goal = rules.get(0).getHead();
		children = new ArrayList<RuleBody>();
		for (Rule r: rules)
			children.add(r.getRuleBody());
	}	
	public Predicate getGoal() {
		return goal;
	}
	public void setGoal(Predicate goal) {
		this.goal = goal;
	}
	public ArrayList<RuleBody> getChildren(){
		return children;
	}
	public void setChildren(ArrayList<RuleBody> children){
		this.children = children;
	}
	
}
