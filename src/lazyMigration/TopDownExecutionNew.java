package lazyMigration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import database.Database;
import datalog.Condition;
import datalog.Fact;
import datalog.MagicCondition;
import datalog.Predicate;
import datalog.Rule;
import datalog.RuleBody;

public class TopDownExecutionNew {
	// All EDB-facts and generated IDB-facts
	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private Predicate goal;
	private RuleGoalTree tree;
	private List<Map<String, String>> unificationMap;
	private ArrayList<Fact> putFacts;
	private List<MagicCondition> magicList = null;
	private int number=0;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	// set edb facts
	public TopDownExecutionNew(ArrayList<Fact> facts) {
		super();
		this.facts = facts;
	}

	public TopDownExecutionNew(ArrayList<Fact> facts, ArrayList<Rule> rules,
			Predicate goal) {
		this.facts = facts;
		this.rules = rules;
		this.goal = goal;
	}

	public TopDownExecutionNew(ArrayList<Fact> facts, ArrayList<Rule> rules,
			Predicate goal, List<Map<String, String>> attributeMap) {
		this.facts = facts;
		this.rules = rules;
		this.goal = goal;
		this.unificationMap = attributeMap;
	}

	public ArrayList<Fact> getValues() {
		return facts;
	}

	public void setValues(ArrayList<Fact> facts) {
		this.facts = facts;
	}

	public ArrayList<ArrayList<String>> getFact(String kind, int anz) {
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();

		for (Fact value : facts) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				answer.add(value.getListOfValues());
			}
		}
		return answer;
	}

	// Testgenerierung einer Rule mit Top Down
	public ArrayList<ArrayList<String>> getAnswer(Rule rule) {

		rulesRenameandReorder(rule);
		for (Predicate p : rule.getPredicates()) {
			getFacts(p);
		}

		Predicate temp = join(rule.getPredicates());
		if (rule.getConditions() != null)
			temp = selection(temp, rule.getConditions());

		ArrayList<String> werte = rule.getHead().getScheme();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> oneMap : temp.getRelation()) {
			ArrayList<String> oneAnswer = new ArrayList<String>();
			for (String wert : werte)
				if (wert.startsWith("?"))
					oneAnswer.add(oneMap.get(temp.getScheme().indexOf(wert)));
				else
					oneAnswer.add(wert);
			answer.add(oneAnswer);
		}

		return answer;
	}

	public ArrayList<Fact> getAnswers() {

		putFacts = new ArrayList<Fact>();

		ArrayList<Fact> answer = new ArrayList<Fact>();

		ArrayList<Rule> childrenRules = new ArrayList<Rule>();
		for (Rule r : rules) {
			// durchsuche die Head Prädikate der Rules nach benötigtem Goal-
			// Prädikat
			// unifiziere alle gefundenen Regeln
			Predicate ruleHead = r.getHead();
			if (ruleHead.getKind().equals(goal.getKind())
					&& ruleHead.getAnz() == goal.getAnz()) {
				if (ruleHead.getKind().contains("Mission2"))
					System.out.println("Mission2");
				Rule unifiedRule = unifyRule(goal, r);
				childrenRules.add(unifiedRule);
			}
		}

		if (childrenRules.size() == 0) {
			System.out.println("Keine children rules!" + goal.getKind()
					+ goal.getScheme().toString());
		} else {
			// vorläufige toString Methode muss angepasst werden
			System.out.println("Unifizierte Regeln: ");
			for (Rule rr : childrenRules) {
				System.out.print(rr.getHead().getKind());
				System.out.print(rr.getHead().getScheme().toString() + ":-");
				for (Predicate pr : rr.getPredicates()) {
					System.out.print(pr.getKind());
					System.out.print(pr.getScheme().toString());
				}
			}
			System.out.println();

			// compute the answers of corresponding rule goal tree
			tree = new RuleGoalTree(childrenRules);
			goal.setRelation(getAnswersForSubtree(tree));
		}

		if (goal.getRelation() != null) {
			System.out.println("Ergebnis: " + goal.getRelation().toString());

			// speichert Result Map des Goal in Facts ab
			for (ArrayList<String> str : goal.getRelation()) {
				answer.add(new Fact(goal.getKind(), str));
			}
		} else
			System.out.println("Ergebnis ist null");

		if (putFacts.size() != 0) {
			for (Fact f : putFacts) {
				putFactToDB(f);
				number++;
				System.out.println("put in DB: " + f.toString());
			}

		}
		return answer;

	}

	public boolean factExists(ArrayList<Fact> factList, Fact putFact) {
		boolean exists = false;
		ArrayList<String> valuesPutFact = putFact.getListOfValues();
		for (Fact f : factList) {
			ArrayList<String> valuesFactList = f.getListOfValues();
			if (valuesPutFact.size() + 1 == valuesFactList.size()) {
				for (int i = 0; i < valuesPutFact.size() - 1; i++) {
					if (valuesFactList.get(i) != valuesPutFact.get(i)) {
						exists = false;
						break;
					}
					exists = true;
				}
				if (exists == true)
					break;
			}
			if (valuesPutFact.size() == valuesFactList.size()) {
				for (int i = 0; i < valuesPutFact.size(); i++) {
					if (valuesFactList.get(i) != valuesPutFact.get(i)) {
						exists = false;
						break;
					}
					exists = true;
				}
				if (exists == true)
					break;
			}

		}
		return exists;
	}

	public ArrayList<ArrayList<String>> getAnswersForSubtree(RuleGoalTree tree) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		// ArrayList<String> scheme = tree.getGoal().getScheme();
		for (Rule childRule : tree.getChildren()) {
			Predicate resultPredicate = null;
			RuleBody body = childRule.getRuleBody();

			for (Predicate subgoal : body.getPredicates()) {
				if (subgoal.getKind().equals("Player2")) {
					System.out.println("Player2!!!");
				}
				// existiert ein Fact zu benötigtem Subgoal?
				if (getFacts(subgoal) == 0) {
					ArrayList<Rule> unifiedChildrenRules = new ArrayList<Rule>();
					for (Rule r : rules) {
						// durchsuche die Head Prädikate der Rules nach
						// benötigtem Goal- Prädikat
						// & unifiziere alle gefundenen Regeln
						Predicate ruleHead = r.getHead();
						if (ruleHead.getKind().equals(subgoal.getKind())
								&& ruleHead.getAnz() == subgoal.getAnz()) {
							Rule unifiedRule = unifyRule(subgoal, r);
							unifiedChildrenRules.add(unifiedRule);
						}
					}

					// temporär für Testzwecke
					if (unifiedChildrenRules.size() == 0) {
						System.out.println("Keine children rules!"
								+ subgoal.getKind()
								+ subgoal.getScheme().toString());
					} else {

						// vorläufige toString Methode muss angepasst werden
						System.out.println("Unifizierte Regeln: ");
						for (Rule rr : unifiedChildrenRules) {
							System.out.print(rr.getHead().getKind());
							System.out.print(rr.getHead().getScheme()
									.toString()
									+ ":-");
							for (Predicate pr : rr.getPredicates()) {
								System.out.print(pr.getKind());
								System.out.print(pr.getScheme().toString());
							}
						}

						System.out.println();

						// erzeuge für unifizierte Kinder (Rules) neuen
						// RuleGoalTree
						// und speichere ResultMap in Prädikat subgoal ab
						RuleGoalTree subTree = new RuleGoalTree(
								unifiedChildrenRules);
						subgoal.setRelation(getAnswersForSubtree(subTree));

						// füge neu erzeugte Facts zu temporärer Fact Liste
						// hinzu
						for (ArrayList<String> str : subgoal.getRelation()) {
							facts.add(new Fact(subgoal.getKind(), str));
						}
					}

					// temporär für Testzwecke
				} else
					System.out.println("Facts gefunden!"
							+ subgoal.getRelation().toString());
			}

			if (childRule.getHead().getKind().equals("Mission2"))
				System.out.println("Kind: " + childRule.getHead().getKind());
			// Berechnung des Rule Bodys: join der Prädikate + Bedingungen
			if (body.getPredicates().size() > 1)
				resultPredicate = join(body.getPredicates());
			else if (!body.getPredicates().isEmpty())
				resultPredicate = body.getPredicates().get(0);
			if (body.getConditions() != null && !body.getConditions().isEmpty())
				resultPredicate = selection(resultPredicate,
						body.getConditions());
			System.out.println("Kind: " + childRule.getHead().getKind());
			// speichere Ergebnis in ResultMap des Head-Prädikates des Kindes ab
			childRule.getHead()
					.setRelation(
							getResults(resultPredicate, childRule.getHead()
									.getScheme()));
			System.out.println("Join: "
					+ childRule.getHead().getRelation().toString());

			result.addAll(childRule.getHead().getRelation());

		}

		if (tree.getGoal().isHead()) {
			// add put facts to list
			for (ArrayList<String> str : result) {
				Fact newFact = new Fact(tree.getGoal().getKind(), str);
				if (!factExists(facts, newFact)
						&& !factExists(putFacts, newFact)
						&& !newFact.getKind().startsWith("get"))
					putFacts.add(newFact);
			}
		}
		return result;
	}

	// only get the result-attributes of predicate which are in the scheme of
	// the head
	// Example A(?x):-B(?x,?y)--> only extract the ?x attribute of B
	private ArrayList<ArrayList<String>> getResults(Predicate results,
			ArrayList<String> scheme) {
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> oneMap : results.getRelation()) {
			ArrayList<String> oneAnswer = new ArrayList<String>();
			for (String wert : scheme)
				if (wert.startsWith("?"))
					if (results.getScheme().contains(wert))
						oneAnswer.add(oneMap.get(results.getScheme().indexOf(
								wert)));
					else {
						// System.out.println(wert + " existiert nicht");
						oneAnswer.add("");
					}
				else
					oneAnswer.add(wert);
			boolean alreadyExist = false;
			for (ArrayList<String> iterateAnswer : answer)
				if (oneAnswer.containsAll(iterateAnswer)
						&& iterateAnswer.containsAll(oneAnswer))
					alreadyExist = true;
			if (!alreadyExist) {
				answer.add(oneAnswer);
			}
		}
		return answer;
	}

	// unificate all attributes which are in the unificationMap
	public Rule unifyRule(Predicate goal, Rule childrenRule) {
		// first rename all rules according to conditions
		rulesRenameandReorder(childrenRule);
		Predicate head = childrenRule.getHead();
		RuleBody body = childrenRule.getRuleBody();
		if (!goal.getKind().equals(head.getKind())
				|| goal.getAnz() != head.getAnz()) {
			System.out.println("unification not possible:" + "goal"
					+ goal.toString() + "head" + head.toString());
		} else {
			for (Map<String, String> unificationMapEntry : unificationMap) {
				String kind = unificationMapEntry.get("kind");
				int positionValue = Integer.parseInt(unificationMapEntry
						.get("position"));
				String value = unificationMapEntry.get("value");
				if (head.getKind().contains(kind)) {
					ArrayList<String> attributesRuleHead = head.getScheme();
					String idValue = attributesRuleHead.get(positionValue);
					// unificate head-predicates which match with kind
					unificatePredicate(attributesRuleHead, idValue, value);

				}
				// unificate body-predicates which match with kind
				String id = null;
				for (Predicate p : body.getPredicates()) {
					if (p.getKind().contains(kind)) {
						ArrayList<String> attributesRule = p.getScheme();
						String idValue = attributesRule.get(positionValue);
						if (id == null)
							id = idValue;
						unificatePredicate(attributesRule, idValue, value);
					}
				}
				if (id != null) {
					if (!head.getKind().contains(kind)) {
						// unificate head-predicates which don't match with kind
						unificatePredicate(head.getScheme(), id, value);
					}
					// unificate body-predicates which don't match with kind
					for (Predicate p : body.getPredicates()) {
						if (!p.getKind().contains(kind)) {
							unificatePredicate(p.getScheme(), id, value);
						}
					}
				}
			}
		}

		return childrenRule;
	}

	// unification of Rule: e.g. A(?x,?y) and method call
	// unificatePredicate(["?x","?y"],"?x","1") --> A(1,?y)
	private void unificatePredicate(ArrayList<String> attributesRule,
			String idValue, String value) {
		if (attributesRule.contains(idValue)) {
			attributesRule.set(attributesRule.indexOf(idValue), value);
		}

	}

	public void putFactToDB(Fact newFact) {
		Database db = new Database("data/EDBLazy.json","data/Schema.json");
		// newFact sowas wie ; "Player2(4,'Lisa',40)" (ohne timestamp)
		db.putToDatabase(newFact.toString());
	}

	public ArrayList<Rule> getRules(Predicate p) {
		ArrayList<Rule> rule = new ArrayList<Rule>();
		for (Rule r : rules) {
			if (r.getHead().getKind().equals(p.getKind())
					&& r.getHead().getAnz() == p.getAnz()) {
				rule.add(r);
			}
		}
		return rule;
	}

	// generate temporary results of all joins of a rule step by step
	public Predicate join(ArrayList<Predicate> predicates) {
		Predicate temp = null;
		if (predicates.size() > 0) {
			temp = predicates.get(0);
			if (temp.getRelation() != null) {
				if (!temp.getRelation().isEmpty()) {
					ArrayList<ArrayList<String>> facts2;
					for (int i = 1; i < predicates.size(); i++) {
						Predicate p2 = predicates.get(i);
						ArrayList<PairofInteger> equalList = getEqualList(
								temp.getScheme(), p2.getScheme());
						ArrayList<ArrayList<String>> restemp = new ArrayList<ArrayList<String>>();
						facts2 = p2.getRelation();

						if (predicates.get(i).isNot()) {
							for (ArrayList<String> fact1 : temp.getRelation()) {
								if (facts2 == null
										|| getTempNotResult(fact1, equalList,
												facts2)) {
									restemp.add(fact1);
								}
							}
							temp = new Predicate("temp", temp.getScheme()
									.size(), temp.getScheme(), restemp);
						}

						else {
							if (facts2 != null) {
								for (ArrayList<String> fact1 : temp
										.getRelation()) {
									ArrayList<ArrayList<String>> results = getTempJoinResult(
											fact1, equalList, facts2);
									if (results != null)
										restemp.addAll(results);
								}
							}
							ArrayList<Integer> liste = new ArrayList<Integer>();
							for (PairofInteger wert : equalList)
								liste.add(wert.p2);
							ArrayList<String> newSchema = new ArrayList<String>();
							newSchema.addAll(temp.getScheme());
							for (int j = 0; j < p2.getScheme().size(); j++)
								if (!liste.contains(j))
									newSchema.add(p2.getScheme().get(j));
							temp = new Predicate("temp", newSchema.size(),
									newSchema, restemp);
						}

					}
				}
			}
		}
		return temp;

	}

	// generate temporary results of all conditions of a rule step by step
	private Predicate selection(Predicate predResult,
			ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			predResult = getTempCondResult(predResult, cond);
		}
		return predResult;
	}

	// generate temporary results of condition, e.g.: C(?y,?z) :-
	// A(?x,?y),B(?x,?z),?y=?z.
	// Only put values of A and B to end result which satisfies condition ?y=?z.
	private Predicate getTempCondResult(Predicate p, Condition cond) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		String rightOperand = cond.getRightOperand();
		String leftOperand = cond.getLeftOperand();
		String operator = cond.getOperator();
		List<ArrayList<String>> relation = p.getRelation();
		for (ArrayList<String> oneResult : relation) {
			String left = "";
			String right = "";
			if (leftOperand.startsWith("?"))
				if (p.getScheme().contains(leftOperand))
					left = oneResult.get(p.getScheme().indexOf(leftOperand));
				else {
					// System.out.println(leftOperand + " existiert nicht");
					facts.add(oneResult);
					continue;
				}
			else
				left = leftOperand;
			if (rightOperand.startsWith("?"))
				if (p.getScheme().contains(rightOperand))
					right = oneResult.get(p.getScheme().indexOf(rightOperand));
				else {
					// System.out.println(leftOperand + " existiert nicht");
					facts.add(oneResult);
					continue;
				}
			else
				right = rightOperand;
			boolean condPredicate = false;
			switch (operator) {
			case "=":
				if (isInteger(left) && isInteger(right)) {
					if (Integer.parseInt(left) == Integer.parseInt(right))
						condPredicate = true;
				} else if (left.equals(right))
					condPredicate = true;
				break;
			case "!":
				if (isInteger(left) && isInteger(right)) {
					if (Integer.parseInt(left) != Integer.parseInt(right))
						condPredicate = true;
				} else if (!left.equals(right))
					condPredicate = true;
				break;
			case "<":
				if (isInteger(left) && isInteger(right)) {
					if (Integer.parseInt(left) < Integer.parseInt(right))
						condPredicate = true;
				} else if (left.compareTo(right) < 0)
					condPredicate = true;
				break;
			case ">":
				if (isInteger(left) && isInteger(right)) {
					if (Integer.parseInt(left) > Integer.parseInt(right))
						condPredicate = true;
				} else if (left.compareTo(right) > 0)
					condPredicate = true;
				break;
			}
			if (condPredicate == true) {
				facts.add(oneResult);
			}
		}
		return new Predicate("temp", p.getScheme().size(), p.getScheme(), facts);

	}

	// generate temporary results of join, e.g.: C(?y,?z) :-
	// A(?x,?y),B(?x,?z).
	// join values of A und B on attribute ?x
	private ArrayList<ArrayList<String>> getTempJoinResult(
			ArrayList<String> fact1, ArrayList<PairofInteger> equalList,
			ArrayList<ArrayList<String>> fact2) {
		ArrayList<ArrayList<String>> result = null;
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.p2);

		for (ArrayList<String> oneOfFact2 : fact2) {
			boolean joinPredicate = true;
			for (PairofInteger wert : equalList) {
				if (!fact1.get(wert.p1).equals(oneOfFact2.get(wert.p2)))
					joinPredicate = false;
			}
			if (joinPredicate == true) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.addAll(fact1);

				for (int i = 0; i < oneOfFact2.size(); i++) {
					if (!liste.contains(i))
						temp.add(oneOfFact2.get(i));
				}

				if (result == null)
					result = new ArrayList<ArrayList<String>>();
				result.add(temp);
			}
		}

		return result;

	}

	// generate temporary results of not, e.g.: C(?y,?z) :- A(?x,?y),
	// not B(?x,?z).
	// Put all values of ?x from A which aren't in the ?x values of B
	private boolean getTempNotResult(ArrayList<String> fact1,
			ArrayList<PairofInteger> equalList,
			ArrayList<ArrayList<String>> fact2) {
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.p2);

		for (ArrayList<String> oneOfFact1 : fact2) {
			boolean joinPredicate = true;
			for (PairofInteger wert : equalList) {
				if (!fact1.get(wert.p1).equals(oneOfFact1.get(wert.p2)))
					joinPredicate = false;
			}

			if (joinPredicate == true) {
				return false;
			}
		}

		return true;
	}

	// find all equal attributes of two predicates for a join, e.g.: C(?y,?z) :-
	// A(?x,?y),B(?x,?z). -->
	// ?x
	// is a join attribute
	private ArrayList<PairofInteger> getEqualList(ArrayList<String> strings,
			ArrayList<String> strings2) {
		ArrayList<PairofInteger> list = new ArrayList<PairofInteger>();
		for (int i = 0; i < strings.size(); i++)
			for (int j = 0; j < strings2.size(); j++)
				if (strings.get(i).startsWith("?"))
					if (strings.get(i).equals(strings2.get(j)))
						list.add(new PairofInteger(i, j));
		return list;
	}

	// generate results of a predicate based on the facts, e.g. A(?x,?y) and
	// edb-fact
	// A(1,2)
	// result is [1,2] and return is true if there are results
	private int getFacts(Predicate predicate) {
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		String kind = predicate.getKind();
		int anz = predicate.getScheme().size();
		for (Fact value : facts) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				boolean set = true;
				int i = 0;
				for (String wert : predicate.getScheme()) {
					if (!wert.startsWith("?"))
						if (!value.getListOfValues().get(i).equals(wert)) {
							set = false;
							break;
						}
					i++;

				}
				if (set)
					values.add(value.getListOfValues());
			}
		}
		generateMagicSet(values, predicate.getKind());
		predicate.setRelation(values);
		if (values.isEmpty())
			return 0;
		else
			return 1;

	}

	private ArrayList<ArrayList<String>> generateMagicSet(
			ArrayList<ArrayList<String>> values, String kind) {
		/*
		 * if (kind.equals("Player2") || kind.equals("latestPlayer2"))
		 * System.out.println("Gefunden");
		 */
		if (kind.equals("Mission1"))
			System.out.println("Gefunden");
		if (magicList != null && !magicList.isEmpty()) {
			int i = 0;
			for (MagicCondition magicCondition : magicList) {
				if (magicCondition.getKindLeft().contains(kind)) {
					if (magicCondition.hasAlreadyResults() == false) {
						String newViewName = "MGView" + i;
						for (ArrayList<String> valueList : values) {
							facts.add(new Fact(
									newViewName,
									new ArrayList<String>(
											valueList.subList(
													magicCondition
															.getPositionLeft(),
													magicCondition
															.getPositionLeft() + 1))));
						}
						magicCondition.setAlreadyFoundResults(true);
						setNewMagicPredicateToCorrespondingRules(
								magicCondition, newViewName);
					}
				}
				i++;
			}
		}
		return values;
	}

	private void setNewMagicPredicateToCorrespondingRules(MagicCondition m,
			String newViewName) {
		for (Rule rule : rules) {
			if (rule.getHead().getKind().equals(m.getKindRight())) {
				ArrayList<String> scheme = new ArrayList<String>();
				scheme.add(rule.getHead().getScheme().get(m.getPositionRight()));
				rule.getPredicates().add(new Predicate(newViewName, 1, scheme));
			}
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void rulesRenameandReorder(Rule rule) {
		if (rule.getConditions() != null)
			for (Iterator<Condition> iterator = rule.getConditions().iterator(); iterator
					.hasNext();) {
				Condition cond = iterator.next();
				if (cond.getOperator().equals("=")
						&& cond.getLeftOperand().startsWith("?")
						&& cond.getRightOperand().startsWith("?")) {

					String left = cond.getLeftOperand();
					String right = cond.getRightOperand();
					generateMagicCondition(rule.getRuleBody().getPredicates(),
							left, right);
					renameVariablesOfAllPredicates(rule, left, right);
					iterator.remove();
				}
			}

		// toDo: reordering of Predicates
	}

	private void generateMagicCondition(ArrayList<Predicate> predicates,
			String left, String right) {
		for (int i = 0; i < predicates.size(); i++) {
			for (int j = i + 1; j < predicates.size(); j++) {
				if (predicates.get(i).getScheme().contains(right)
						&& predicates.get(j).getScheme().contains(left)) {

					MagicCondition magicCond = new MagicCondition(predicates
							.get(i).getKind(), predicates.get(j).getKind(),
							predicates.get(i).getScheme().indexOf(right),
							predicates.get(j).getScheme().indexOf(left));
					if (magicList != null)
						if (!magicList.isEmpty()) {
							if (!magicList.contains(magicCond))
								magicList.add(magicCond);
						} else
							magicList.add(magicCond);
					else {
						magicList = new ArrayList<MagicCondition>();
						magicList.add(magicCond);
					}

				}
			}

		}
	}

	private void renameVariablesOfAllPredicates(Rule rule, String left,
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

	private class PairofInteger {
		int p1, p2;

		PairofInteger(int p1, int p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}

}
