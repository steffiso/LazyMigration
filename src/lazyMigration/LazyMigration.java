package lazyMigration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.Database;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class LazyMigration {
	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private Predicate goal;
	private List<Map<String,String>> unificationMap;

	public LazyMigration(ArrayList<Fact> facts, ArrayList<Rule> rules,Predicate goal
			,List<Map<String,String>> unificationMap) {
		this.facts = facts;
		this.rules = rules;
		this.goal=goal;
		this.unificationMap=unificationMap;
	}

	public String writeAnswersInDatabase() {

		//Database db = new Database();
		String answerString = "";
		TopDownExecutionNew lazy = new TopDownExecutionNew(facts, rules,
				goal, unificationMap);
		ArrayList<Fact> answers2 = lazy.getAnswers();
		ArrayList<Pair> unicateRuleNames = new ArrayList<Pair>();
		
		//Alle RuleHead-Duplikate eliminieren, damit wir keine Duplikate in der DB erhalten
		for (Rule rule : rules) {
			if (!unicateRuleNames.contains(new Pair(rule.getHead().getKind(),
					rule.getHead().getScheme().size())))
				unicateRuleNames.add(new Pair(rule.getHead().getKind(), rule
						.getHead().getScheme().size()));
		}
		for (Pair pair : unicateRuleNames) {
			ArrayList<ArrayList<String>> answers = lazy.getFact(
					pair.ruleName, pair.ruleAnz);

			//if (pair.ruleName.startsWith("legacy")) {
				// PrintWriter out;
				// try {
				// out = new PrintWriter (new BufferedWriter(new
				// FileWriter("data/legacyEntities")));
				// String legacyEntities = "";
				// for (ArrayList<String> answer : answers){
				// //put to legacy file
				// String valueString = "";
				// for (String s: answer) {
				// valueString = valueString + s + ",";
				// }
				// valueString = valueString.substring(0,valueString.length() -
				// 1);
				// legacyEntities = legacyEntities + rule.getHead().getKind()
				// +"(" + valueString + ")\n";
				//
				// }
				// out.append(legacyEntities);
				// out.close();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			//} else if (!pair.ruleName.startsWith("latest")
			//	&& !query.startsWith("get")) {
			//	for (ArrayList<String> answer : answers) {
					// put to database file

			//	String values = "";
			//	for (String s : answer) {
			//			values += s + ", ";
			//		}
		//
			//		values = values.substring(0, values.length() - 2);
			//		String tempKind = pair.ruleName;
					// toDo:
			//		String datalogFact = tempKind + "(" + values + ").";
			//		db.putToDatabase(datalogFact);
			//	}
			//	}

			for (ArrayList<String> answer : answers)
				answerString = answerString + pair.ruleName + answer.toString()
						+ "\n";

		}

		return answerString+"\n Answer Get: "+answers2;
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
