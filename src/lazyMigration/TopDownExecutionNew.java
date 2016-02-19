package lazyMigration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import database.Database;
import datalog.Condition;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import datalog.RuleBody;

public class TopDownExecutionNew {
	// Alle EDB-Fakten und generierten IDB-Fakten
	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private Predicate goal;
	private RuleGoalTree tree;
	private Map<String, String> unificationMap;

	// private ArrayList<ArrayList<Map<String, String>>> resultMap;

	// Setze EDB-Fakten
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

	// wir setzen die UnificationMap gleich am Anfang
	public TopDownExecutionNew(ArrayList<Fact> facts, ArrayList<Rule> rules,
			Predicate goal, SortedMap<String, String> attributeMap) {
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

	// Testgenerierung einer Rule mit Top Down
	public ArrayList<ArrayList<String>> getAnswer(Rule rule) {

		rulesRenameandReorder(rule);
		for (Predicate p : rule.getPredicates()) {
			getFacts(p);
		}

		Predicate temp = join(rule.getPredicates());
		if (rule.getConditions() != null)
			temp = generateConditions(temp, rule.getConditions());

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

		// toDo bevor wir anfangen müssen wir noch die Rules umbennnen
		//for (Rule rule : rules) rulesRenameandReorder(rule);
		 
		// resultMap = new ArrayList<ArrayList<Map<String, String>>>();
		ArrayList<Fact> answer = new ArrayList<Fact>();

		// check if a mapping for predicate goal exists
		if (getFacts(goal) == 0) {
			ArrayList<Rule> childrenRules = new ArrayList<Rule>();
			for (Rule r : rules) {
				// look for corresponding rules
				Predicate ruleHead = r.getHead();
				if (ruleHead.getKind().equals(goal.getKind())
						&& ruleHead.getAnz() == goal.getAnz()) {
					Rule unifiedRule = unifyRule(goal, r);
					childrenRules.add(unifiedRule);
				}
			}

			if (childrenRules.size() == 0) {
				System.out.println("No matching rules found!");
			} else {
				// vorläufige toString Methode muss angepasst werden
				System.out.println("Unifizierte Regeln: ");
				for (Rule rr : childrenRules) {
					System.out
							.print(rr.getHead().getScheme().toString() + ":-");
					for (Predicate pr : rr.getPredicates())
						System.out.print(pr.getScheme().toString());
				}
				System.out.println();

				// compute the answers of corresponding rule goal tree
				tree = new RuleGoalTree(childrenRules);
				goal.setRelation(getAnswersForSubtree(tree, goal.getScheme()));
			}
		}

		else
			System.out.println("Mapping gefunden:" + goal.toString() + " "
					+ goal.getRelation().toString());

		System.out.println("Ergebnis: " + goal.getRelation().toString());
		// toDo: goal.getResultMap() to Facts !!!
		for (ArrayList<String> str : goal.getRelation()) {
			answer.add(new Fact(goal.getKind(), str));
		}
		return answer;
	}

	public ArrayList<ArrayList<String>> getAnswersForSubtree(RuleGoalTree tree,
			ArrayList<String> scheme) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		for (Rule childRule : tree.getChildren()) {
			Predicate resultPredicate = null;
			RuleBody body = childRule.getRuleBody();
			for (Predicate subgoal : body.getPredicates()) {
				// check if a mapping for predicate goal exists
				if (getFacts(subgoal) == 0) {
					ArrayList<Rule> unifiedChildrenRules = new ArrayList<Rule>();
					for (Rule r : rules) {
						// look for corresponding rules and unify
						Predicate ruleHead = r.getHead();
						if (ruleHead.getKind().equals(subgoal.getKind())
								&& ruleHead.getAnz() == subgoal.getAnz()) {
							Rule unifiedRule = unifyRule(subgoal, r);
							unifiedChildrenRules.add(unifiedRule);
						}
					}

					if (unifiedChildrenRules.size() == 0) {
						System.out.println("Keine children rules!"
								+ subgoal.getScheme().toString());
					} else {
						
						// vorläufige toString Methode muss angepasst werden
						System.out.println("Unifizierte Regeln: ");
						for (Rule rr : unifiedChildrenRules) {
							System.out.print(rr.getHead().getScheme()
									.toString()
									+ ":-");
							for (Predicate pr : rr.getPredicates())
								System.out.print(pr.getScheme().toString());
						}
						System.out.println();

						// no mapping found + children rules exist
						// compute the answers of corresponding rule goal tree
						RuleGoalTree subTree = new RuleGoalTree(
								unifiedChildrenRules);
						subgoal.setRelation(getAnswersForSubtree(subTree,
								subgoal.getScheme()));

						/*
						 * Map<String, String> attributeHead = subTree.getGoal()
						 * .getWerte();
						 * 
						 * for (Map.Entry<String, String> entryHead :
						 * attributeHead .entrySet()) { if
						 * (!entryHead.getValue().equals("")) { // toDo: hier
						 * put ausführen mit ResultMap?? //
						 * System.out.println("hier put mit : " + //
						 * subgoal.getResultMap()); } }
						 */
					}
				} else
					System.out.println("Mapping gefunden!"
							+ subgoal.getRelation().toString());
			}
			// join of rule body (with conditions)
			if (body.getPredicates().size() > 1)
				resultPredicate = join(body.getPredicates());
			else if (!body.getPredicates().isEmpty())
				resultPredicate = body.getPredicates().get(0);
			if (body.getConditions() != null)
				resultPredicate = generateConditions(resultPredicate,
						body.getConditions());

			childRule.getHead()
					.setRelation(
							getResults(resultPredicate, childRule.getHead()
									.getScheme()));
			System.out.println("Join: "
					+ childRule.getHead().getRelation().toString());

			result.addAll(getResults(resultPredicate, scheme));
		}

		return result;
	}

	private ArrayList<ArrayList<String>> getResults(Predicate results,
			ArrayList<String> scheme) {
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> oneMap : results.getRelation()) {
			ArrayList<String> oneAnswer = new ArrayList<String>();
			for (String wert : scheme)
				if (wert.startsWith("?"))
					oneAnswer
							.add(oneMap.get(results.getScheme().indexOf(wert)));
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

	public Rule unifyRule(Predicate goal, Rule childrenRule) {
		// Unifizierung geht über unificationMap
		// nur Prädikate im RuleBody
		Predicate head = childrenRule.getHead();
		RuleBody body = childrenRule.getRuleBody();
		if (!goal.getKind().equals(head.getKind())
				|| goal.getAnz() != head.getAnz()) {
			System.out.println("Unifizierung nicht möglich:goal "
					+ goal.toString() + ",head " + head.toString());
		} else {

			for (Map.Entry<String, String> unificationMapEntry : unificationMap
					.entrySet()) {
				ArrayList<String> attributesRuleHead = head
						.getScheme();
				// unify head
				if (attributesRuleHead.contains(unificationMapEntry.getKey())) { // &&
																					// !attributesRuleHead.get(unificationMapEntry.getKey()).equals("")){
					attributesRuleHead.set(attributesRuleHead
							.indexOf(unificationMapEntry.getKey()),
							unificationMapEntry.getValue());
				}
				// unify body
				for (Predicate p : body.getPredicates()) {
					if (p.getScheme().contains(unificationMapEntry.getKey())) {
						p.getScheme().set(
								p.getScheme().indexOf(
										unificationMapEntry.getKey()),
								unificationMapEntry.getValue());
					}
				}
			}
		}

		return childrenRule;
	}

	public void putNewFact(String newFact) {
		Database db = new Database();
		// newFact sowas wie ; "Player2(4,'Lisa',40)" (ohne timestamp)
		db.putToDatabase(newFact);
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

	// generiere Zwischenergebnisse zu allen Joins einer Rule nacheinander
	public Predicate join(ArrayList<Predicate> predicates) {
		Predicate temp = null;
		if (predicates.size() > 0) {
			temp = predicates.get(0);
			if (temp != null) {
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
							temp = new Predicate("temp", temp.getScheme().size(),
									temp.getScheme(), restemp);
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

	private Predicate generateConditions(Predicate predResult,
			ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			predResult = getTempCondResult(predResult, cond);
		}
		return predResult;
	}

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z),?y=?z.
	// Füge nur Werte von A und B mit der Bedingung ?y=?z ein.
	private Predicate getTempCondResult(Predicate p, Condition cond) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		String rightOperand = cond.getRightOperand();
		String leftOperand = cond.getLeftOperand();
		String operator = cond.getOperator();
		List<ArrayList<String>> mapList = p.getRelation();
		for (ArrayList<String> mapOfMapList : mapList) {
			String left = "";
			String right = "";
			if (leftOperand.startsWith("?"))
				left = mapOfMapList.get(p.getScheme().indexOf(leftOperand));
			else
				left = leftOperand;
			if (rightOperand.startsWith("?"))
				right = mapOfMapList.get(p.getScheme().indexOf(rightOperand));
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
				facts.add(mapOfMapList);
			}
		}
		return new Predicate("temp", p.getScheme().size(), p.getScheme(), facts);

	}

	// generiere Zwischenergebnisse bei einem Join, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z).
	// Joine die Werte von A und B nach ?x
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

	// generiere Zwischenergebnisse bei einem Not, Bsp: C(?y,?z) :- A(?x,?y),
	// not B(?x,?z).
	// Alle ?x Werte von A die nicht in ?x Werte von B sind
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

	// generiere die Join Prädikate, Bsp: C(?y,?z) :- A(?x,?y),B(?x,?z). --> ?x
	// ist Join Prädikat
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

	// generiere eine Map zu einem Prädikat, Bsp. A(?x,?y) und dem EDB-Fakt
	// A(1,2)
	// neue Map für A: "?x" : 1, "?y" : 2
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
		predicate.setRelation(values);
		if (values.isEmpty())
			return 0;
		else
			return 1;

	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
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
					renameVariablesOfAllPredicates(rule, left, right);
					iterator.remove();
				}
			}

		// toDo: reordering of Predicates
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
