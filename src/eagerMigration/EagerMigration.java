package eagerMigration;

import java.util.ArrayList;

import database.Database;
import datalog.Fact;
import datalog.Rule;

public class EagerMigration {

	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private String query;
	private int number;

	public EagerMigration(ArrayList<Fact> facts, ArrayList<Rule> rules,
			String query) {
		this.facts = facts;
		this.rules = rules;
		this.query = query;
	}

	public ArrayList<String> writeAnswersInDatabase() throws Exception {

		Database db = new Database("/data/EDBEager.json", "/data/Schema.json");
		BottomUpExecution bottomUp = new BottomUpExecution(facts, rules);
		bottomUp.generateAllRules();
		ArrayList<Pair> unicateRuleNames = new ArrayList<Pair>();
		ArrayList<String> list = null;
		// eliminate duplicate entries
		for (Rule rule : rules) {
			if (!unicateRuleNames.contains(new Pair(rule.getHead().getKind(),
					rule.getHead().getScheme().size())))
				unicateRuleNames.add(new Pair(rule.getHead().getKind(), rule
						.getHead().getScheme().size()));
		}

		for (Pair pair : unicateRuleNames) {
			ArrayList<ArrayList<String>> answers = bottomUp.getResultsOfKind(
					pair.ruleName, pair.ruleAnz);

			if (!pair.ruleName.startsWith("latest") && !query.startsWith("get")
					&& !pair.ruleName.startsWith("legacy")) {
				for (ArrayList<String> answer : answers) {
					String values = "";
					for (String s : answer) {
						values += s + ", ";
					}

					values = values.substring(0, values.length() - 2);
					String tempKind = pair.ruleName;

					// put to database file
					String datalogFact = tempKind + "(" + values + ").";
					db.putToDatabase(datalogFact);
					setNumber(getNumber() + 1);
				}
			} else if (query.startsWith("get")
					&& pair.ruleName.startsWith("get")) {

				for (ArrayList<String> answer : answers) {
					if (list == null)
						list = new ArrayList<String>();
					list.add(answer.toString());
				}
			}
		}

		return list;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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