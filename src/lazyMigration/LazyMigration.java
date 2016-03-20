package lazyMigration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class LazyMigration {
	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private Predicate goal;
	private List<Map<String, String>> unificationMap;
	private int numberOfPuts;

	public LazyMigration(ArrayList<Fact> facts, ArrayList<Rule> rules,
			Predicate goal, List<Map<String, String>> unificationMap) {
		this.facts = facts;
		this.rules = rules;
		this.goal = goal;
		this.unificationMap = unificationMap;
	}

	public String writeAnswersInDatabase() {

		String answerString = "";
		TopDownExecutionNew lazy = new TopDownExecutionNew(facts, rules, goal,
				unificationMap);
		ArrayList<Fact> answers2 = lazy.getAnswers();
		ArrayList<Pair> unicateRuleNames = new ArrayList<Pair>();

		// eliminate rule head duplicates
		for (Rule rule : rules) {
			if (!unicateRuleNames.contains(new Pair(rule.getHead().getKind(),
					rule.getHead().getScheme().size())))
				unicateRuleNames.add(new Pair(rule.getHead().getKind(), rule
						.getHead().getScheme().size()));
		}
		for (Pair pair : unicateRuleNames) {
			ArrayList<ArrayList<String>> answers = lazy.getFact(pair.ruleName,
					pair.ruleAnz);

			for (ArrayList<String> answer : answers)
				answerString = answerString + pair.ruleName + answer.toString()
						+ "\n";

		}
		setNumberOfPuts(lazy.getNumberOfPuts());
		return answers2.toString();
	}

	public int getNumberOfPuts() {
		return numberOfPuts;
	}

	public void setNumberOfPuts(int numberOfPuts) {
		this.numberOfPuts = numberOfPuts;
	}

	private class Pair {
		String ruleName;
		int ruleAnz;

		public Pair(String ruleName, int ruleAnz) {
			this.ruleName = ruleName;
			this.ruleAnz = ruleAnz;
		}

		@Override
		public boolean equals(Object object) {
			boolean isEqual = false;

			if (object != null && object instanceof Pair) {
				isEqual = (this.ruleName.equals(((Pair) object).ruleName) && this.ruleAnz == ((Pair) object).ruleAnz);
			}

			return isEqual;
		}

		@Override
		public int hashCode() {
			return this.hashCode();
		}
	}
}
