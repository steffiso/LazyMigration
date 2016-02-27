package lazyMigration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	private List<Map<String, String>> unificationMap;
	private ArrayList<Fact> putFacts;

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

		
		if (putFacts.size() != 0){
			for (Fact f : putFacts) {
					putFactToDB(f);
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
			if (valuesPutFact.size() + 1 == valuesFactList.size() ) {
				for (int i = 0; i < valuesPutFact.size() - 1; i++) {
					if (valuesFactList.get(i) != valuesPutFact.get(i)) {
						exists = false;
						break;
					}
					exists = true;
				}
				if (exists == true) break;
			}
			if (valuesPutFact.size() == valuesFactList.size() ) {
				for (int i = 0; i < valuesPutFact.size(); i++) {
					if (valuesFactList.get(i) != valuesPutFact.get(i)) {
						exists = false;
						break;
					}
					exists = true;
				}
				if (exists == true) break;
			}

		}
		return exists;
	}

	public ArrayList<ArrayList<String>> getAnswersForSubtree(RuleGoalTree tree) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		//ArrayList<String> scheme = tree.getGoal().getScheme();
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
			if (body.getConditions() != null || !body.getConditions().isEmpty())
				resultPredicate = generateConditions(resultPredicate,
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
		
		if (tree.getGoal().isHead()){
			// add put facts to list
			for (ArrayList<String> str : result) {
				Fact newFact = new Fact(tree.getGoal().getKind(), str);
				if (!factExists(facts, newFact) 
					&&!factExists(putFacts,newFact) 
					&&!newFact.getKind().startsWith("get"))
					putFacts.add(newFact);
			}
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
					if (results.getScheme().contains(wert))
						oneAnswer.add(oneMap.get(results.getScheme().indexOf(
								wert)));
					else {
						System.out.println(wert + " existiert nicht");
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

	public Rule unifyRule(Predicate goal, Rule childrenRule) {
		// Unifizierung geht über unificationMap
		// bevor wir anfangen müssen wir noch die Rules umbenennen
		rulesRenameandReorder(childrenRule);
		Predicate head = childrenRule.getHead();
		RuleBody body = childrenRule.getRuleBody();
		if (!goal.getKind().equals(head.getKind())
				|| goal.getAnz() != head.getAnz()) {
			System.out.println("Unifizierung nicht möglich:goal "
					+ goal.getKind());
			System.out.print(goal.getScheme().toString());
			System.out.print(",head " + head.getKind());
			System.out.print(head.getScheme().toString());
		} else {

			for (Map<String, String> unificationMapEntry : unificationMap) {
				if (head.getKind().contains(unificationMapEntry.get("kind"))) {
					ArrayList<String> attributesRuleHead = head.getScheme();
					String idValue = attributesRuleHead.get(Integer
							.parseInt(unificationMapEntry.get("position")));
					// unifiziere Head-Prädikate die mit kind übereinstimmen
					if (attributesRuleHead.contains(idValue)) {
						attributesRuleHead.set(
								attributesRuleHead.indexOf(idValue),
								unificationMapEntry.get("value"));
					}
				}
				// unifiziere Body-Prädikate die mit kind übereinstimmen
				String id = null;
				for (Predicate p : body.getPredicates()) {
					if (p.getKind().contains(unificationMapEntry.get("kind"))) {
						ArrayList<String> attributesRule = p.getScheme();
						String idValue = attributesRule.get(Integer
								.parseInt(unificationMapEntry.get("position")));
						if (id == null)
							id = idValue;

						if (p.getScheme().contains(idValue)) {
							p.getScheme().set(p.getScheme().indexOf(idValue),
									unificationMapEntry.get("value"));
						}
					}
				}
				if (id != null) {
					if (!head.getKind().contains(
							unificationMapEntry.get("kind"))) {
						// unifiziere Head-Prädikate
						if (head.getScheme().contains(id)) {
							head.getScheme().set(head.getScheme().indexOf(id),
									unificationMapEntry.get("value"));
						}
					}
					for (Predicate p : body.getPredicates()) {
						if (!p.getKind().contains(
								unificationMapEntry.get("kind"))) {
							if (p.getScheme().contains(id)) {
								p.getScheme().set(p.getScheme().indexOf(id),
										unificationMapEntry.get("value"));
							}
						}
					}
				}
			}
		}

		return childrenRule;
	}

	public void putFactToDB(Fact newFact) {
		Database db = new Database();
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
		List<ArrayList<String>> relation = p.getRelation();
		for (ArrayList<String> oneResult : relation) {
			String left = "";
			String right = "";
			if (leftOperand.startsWith("?"))
				if (p.getScheme().contains(leftOperand))
					left = oneResult.get(p.getScheme().indexOf(leftOperand));
				else {
					System.out.println(leftOperand + " existiert nicht");
					facts.add(oneResult);
					continue;
				}
			else
				left = leftOperand;
			if (rightOperand.startsWith("?"))
				if (p.getScheme().contains(rightOperand))
					right = oneResult.get(p.getScheme().indexOf(rightOperand));
				else {
					System.out.println(leftOperand + " existiert nicht");
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
