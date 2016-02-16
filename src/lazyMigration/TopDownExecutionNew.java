package lazyMigration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

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
	private ArrayList<ArrayList<Map<String, String>>> resultMap;

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

	public ArrayList<Fact> getValues() {
		return facts;
	}

	public void setValues(ArrayList<Fact> facts) {
		this.facts = facts;
	}

	// Testgenerierung einer Rule mit Top Down
	public ArrayList<ArrayList<String>> getAnswer(Rule rule) {
		// ArrayList<ArrayList<Map<String, String>>> maps = new
		// ArrayList<ArrayList<Map<String, String>>>();
		for (Predicate p : rule.getPredicates()) {
			getMap(p);
			// maps.add(getMap(p));
		}

		// List<Map<String, String>> temp = join(rule.getPredicates(), maps);
		Predicate temp = join(rule.getPredicates());
		if (rule.getConditions() != null)
			temp = generateConditions(temp, rule.getConditions());
		return temp.getResultMap2();
	}

	public ArrayList<Fact> getAnswers() {
		resultMap = new ArrayList<ArrayList<Map<String, String>>>();
		ArrayList<Fact> answer = new ArrayList<Fact>();
		unificationMap = new HashMap<String, String>();

		// check if a mapping for predicate goal exists
		if (getMap(goal) == 0) {
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
				// no mapping found -> children rules exist
				// compute the answers of corresponding rule goal tree
				System.out.println("Unifizierte Regeln: "
						+ childrenRules.toString());
				tree = new RuleGoalTree(childrenRules);
				goal = getAnswersForSubtree(tree);

				// join head predicates of all rule bodies (children of goal)
				// ArrayList<Predicate> childrenHeads = new
				// ArrayList<Predicate>();
				// for (Rule r: childrenRules){
				// childrenHeads.add(r.getHead());
				// }
				// goal.setResultMap(join(childrenHeads));
			}

		}

		else
			System.out.println("Mapping gefunden:" + goal.toString() + " "
					+ goal.getResultMap2().toString());

		System.out.println("Ergebnis: " + goal.getResultMap2());
		// toDo: goal.getResultMap() to Facts !!!
		return answer;
	}

	public Predicate getAnswersForSubtree(RuleGoalTree tree) {
		Predicate a = null;
		for (Rule rulesChildren : tree.getChildren()) {
			RuleBody body = rulesChildren.getRuleBody();
			for (Predicate subgoal : body.getPredicates()) {
				// check if a mapping for predicate goal exists
				if (getMap(subgoal) == 0) {
					ArrayList<Rule> childrenRules = new ArrayList<Rule>();
					for (Rule r : rules) {
						// look for corresponding rules and unify
						Predicate ruleHead = r.getHead();
						if (ruleHead.getKind().equals(subgoal.getKind())
								&& ruleHead.getAnz() == subgoal.getAnz()) {
							Rule unifiedRule = unifyRule(subgoal, r);
							childrenRules.add(unifiedRule);
						}
					}
					for (int i = 0; i < childrenRules.size(); i++) {
						System.out.println(childrenRules.get(i).toString());
					}

					if (childrenRules.size() == 0) {
						System.out.println("Keine children rules!"
								+ subgoal.toString());
					} else
						// no mapping found -> children rules exist
						// compute the answers of corresponding rule goal tree
						System.out.println("Unifizierte Regeln: "
								+ childrenRules.toString());
					RuleGoalTree subTree = new RuleGoalTree(childrenRules);

					Map<String, String> attributeHead = subTree.getGoal()
							.getWerte();
					subgoal = getAnswersForSubtree(subTree);
					for (Map.Entry<String, String> entryHead : attributeHead
							.entrySet()) {
						if (!entryHead.getValue().equals("")) {
							// toDo: hier put ausführen mit ResultMap??
						}
					}
				} else
					System.out.println("Mapping gefunden!"
							+ subgoal.getResultMap2().toString());
			}
			a = join(body.getPredicates());
			if (body.getConditions() != null)
				a = generateConditions(a, body.getConditions());
			System.out.println("Join: " + a.toString());

		}

		// join head predicates of all rule bodies (children of goal)
		// ArrayList<Predicate> childrenHeads = new ArrayList<Predicate>();
		// for (Rule r: tree.getChildren()){
		// childrenHeads.add(r.getHead());
		// }
		// goal.setResultMap(join(childrenHeads));
		return a;
	}

	public Rule unifyRule(Predicate goal, Rule childrenRule) {
		// nur Prädikate im RuleBody
		Predicate head = childrenRule.getHead();
		RuleBody body = childrenRule.getRuleBody();
		if (!goal.getKind().equals(head.getKind())
				|| goal.getAnz() != head.getAnz()) {
			System.out.println("Unifizierung nicht möglich:goal "
					+ goal.toString() + ",head " + head.toString());
		} else {
			// create unification Map
			Map<String, String> valuesGoal = goal.getWerte();
			Map<String, String> valuesHead = head.getWerte();

			for (Map.Entry<String, String> entryGoal : valuesGoal.entrySet()) {
				boolean containsKey = valuesHead
						.containsKey(entryGoal.getKey());
				String headValue = valuesHead.get(entryGoal.getKey());
				String goalValue = entryGoal.getValue();
				if (containsKey && !headValue.equals(goalValue)) {
					unificationMap
							.put(entryGoal.getKey(), entryGoal.getValue());
				}
			}

			for (Map.Entry<String, String> unificationMapEntry : unificationMap
					.entrySet()) {
				Map<String, String> attributesRuleHead = childrenRule.getHead()
						.getWerte();
				// unify head
				if (attributesRuleHead
						.containsKey(unificationMapEntry.getKey())) { // &&
																		// !attributesRuleHead.get(unificationMapEntry.getKey()).equals("")){
					attributesRuleHead.put(unificationMapEntry.getKey(),
							unificationMapEntry.getValue());
					int i = childrenRule.getHead().getScheme()
							.indexOf(unificationMapEntry.getKey());
					childrenRule.getHead().getScheme()
							.set(i, unificationMapEntry.getValue());
				}
				// unify body
				for (Predicate p : body.getPredicates()) {
					if (p.getWerte().containsKey(unificationMapEntry.getKey())) {
						p.getWerte().put(unificationMapEntry.getKey(),
								unificationMapEntry.getValue());
						int i = p.getScheme().indexOf(
								unificationMapEntry.getKey());
						p.getScheme().set(i, unificationMapEntry.getValue());
					}
				}
			}
		}

		return childrenRule;
	}

	public void putNewFact(String newFact) {
		Database db = new Database();
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

	// generiere eine Map zu einem Prädikat, Bsp. A(?x,?y) und dem EDB-Fakt
	// A(1,2)
	// neue Map für A: "?x" : 1, "?y" : 2
	private int getMap(Predicate predicate) {
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		String kind = predicate.getKind();
		int anz = predicate.getAnz();
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
		predicate.setResultMap2(values);
		if (values.isEmpty())
			return 0;
		else
			return 1;

	}

	// generiere Zwischenergebnisse zu allen Joins einer Rule nacheinander
	public Predicate join(ArrayList<Predicate> predicates) {
		// ArrayList<ArrayList<Map<String, String>>> map) {
		// ArrayList<Map<String, String>> temp = map.get(0);

		Predicate temp = predicates.get(0);
		if (temp != null) {
			if (!temp.getResultMap2().isEmpty()) {
				Predicate facts2;
				for (int i = 1; i < predicates.size(); i++) {
					facts2 = predicates.get(i);

					if (facts2.isNot()) {
						temp = getTempNotResult(temp, facts2);
					}

					else {
						temp = getTempJoinResult(temp, facts2);

					}
				}
			}
		}
		return temp;

	}

	public void rulesBodyUnificationandReorder(Rule rule) {
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
		if (predicate.getWerte().containsKey(right)) {
			String value = predicate.getWerte().get(right);
			predicate.getWerte().remove(right);
			predicate.getWerte().put(left, value);
		}
	}

	private Predicate generateConditions(Predicate mapList,
			ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			mapList = getTempCondResult(mapList, cond);
		}
		return mapList;
	}

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z),?y=?z.
	// Füge nur Werte von A und B mit der Bedingung ?y=?z ein.
	private Predicate getTempCondResult(Predicate p, Condition cond) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		String rightOperand = cond.getRightOperand();
		String leftOperand = cond.getLeftOperand();
		String operator = cond.getOperator();
		List<ArrayList<String>> mapList = p.getResultMap2();
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
				if (left.equals(right))
					condPredicate = true;
				break;
			case "!":
				if (!left.equals(right))
					condPredicate = true;
				break;
			case "<":
				if (left.compareTo(right) < 0)
					condPredicate = true;
				break;
			case ">":
				if (left.compareTo(right) > 0)
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
	private Predicate getTempJoinResult(Predicate predicate1,
			Predicate predicate2) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> fact1 = predicate1.getResultMap2();
		ArrayList<ArrayList<String>> fact2 = predicate2.getResultMap2();
		ArrayList<Integer[]> equalList = getEqualList(predicate1.getScheme(),
				predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Integer[] wert : equalList)
			liste.add(wert[1]);
		for (ArrayList<String> mapOfFact1 : fact1) {
			for (ArrayList<String> mapOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (Integer[] wert : equalList) {
					if (!mapOfFact1.get(wert[0])
							.equals(mapOfFact2.get(wert[1])))
						joinPredicate = false;
				}
				if (joinPredicate == true) {
					ArrayList<String> temp = new ArrayList<String>();
					for (String e : mapOfFact1) {
						temp.add(e);
					}

					for (int i = 0; i < mapOfFact2.size(); i++) {
						if (!liste.contains(i))
							temp.add(mapOfFact2.get(i));
					}
					facts.add(temp);
				}
			}
		}
		ArrayList<String> newSchema = predicate1.getScheme();
		for (int i = 0; i < predicate2.getScheme().size(); i++)
			if (!liste.contains(i))
				newSchema.add(predicate2.getScheme().get(i));
		return new Predicate("temp", newSchema.size(), newSchema, facts);

	}

	// generiere Zwischenergebnisse bei einem Not, Bsp: C(?y,?z) :- A(?x,?y),
	// not B(?x,?z).
	// Alle ?x Werte von A die nicht in ?x Werte von B sind
	private Predicate getTempNotResult(Predicate predicate1,
			Predicate predicate2) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> fact1 = predicate1.getResultMap2();
		ArrayList<ArrayList<String>> fact2 = predicate2.getResultMap2();
		ArrayList<Integer[]> equalList = getEqualList(predicate1.getScheme(),
				predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Integer[] wert : equalList)
			liste.add(wert[1]);
		int pos = 0;
		ArrayList<Integer> positionen = new ArrayList<Integer>();
		for (ArrayList<String> mapOfFact1 : fact1) {
			for (ArrayList<String> mapOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (Integer[] wert : equalList) {
					if (!mapOfFact1.get(wert[0])
							.equals(mapOfFact2.get(wert[1])))
						joinPredicate = false;
				}
				if (joinPredicate == true) {
					positionen.add(pos);
				}
			}
			pos++;
		}

		for (int i = 0; i < fact1.size(); i++) {
			if (!positionen.contains(i))
				facts.add(fact1.get(i));
		}
		return new Predicate("temp", predicate1.getScheme().size(),
				predicate1.getScheme(), facts);

	}

	// generiere die Join Prädikate, Bsp: C(?y,?z) :- A(?x,?y),B(?x,?z). --> ?x
	// ist Join Prädikat
	private ArrayList<Integer[]> getEqualList(ArrayList<String> strings,
			ArrayList<String> strings2) {
		ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		for (int i = 0; i < strings.size(); i++)
			for (int j = 0; j < strings2.size(); j++)
				if (strings.get(i).startsWith("?"))
					if (strings.get(i).equals(strings2.get(j)))
						list.add(new Integer[] { i, j });
		return list;
	}

}
