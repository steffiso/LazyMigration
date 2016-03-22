package datalog;

import java.util.ArrayList;

public class MigrationExecution {
	
	// all edb facts and generated idb
	protected ArrayList<Fact> facts;

	// set edb facts
    public MigrationExecution(ArrayList<Fact> facts) {
			super();
			this.facts = facts;
		}
	protected void renameVariablesOfAllPredicates(Rule rule, String left,
			String right) {
		renameVariablesOfPredicate(rule.getHead(), left, right);
		for (Predicate pred : rule.getPredicates())
			renameVariablesOfPredicate(pred, left, right);
	}

	private void renameVariablesOfPredicate(Predicate predicate, String left,
			String right) {
		if (predicate.getScheme().contains(right)) {
			predicate.getScheme().set(predicate.getScheme().indexOf(right),
					left);
		}
	}
}
