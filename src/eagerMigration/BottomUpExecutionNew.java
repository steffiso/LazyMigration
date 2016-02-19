package eagerMigration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import datalog.Condition;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class BottomUpExecutionNew {

	// Alle EDB-Fakten und generierten IDB-Fakten
	private ArrayList<Fact> values;

	// Setze EDB-Fakten
	public BottomUpExecutionNew(ArrayList<Fact> values) {
		super();
		this.values = values;
	}

	public ArrayList<Fact> getValues() {
		return values;
	}

	public void setValues(ArrayList<Fact> values) {
		this.values = values;
	}

	public ArrayList<ArrayList<String>> getFact(String kind, int anz) {
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();

		for (Fact value : values) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				answer.add(value.getListOfValues());
			}
		}
		return answer;
	}

	public void generateAllRules(ArrayList<Rule> rules) {
		try {
			orderStratum(rules);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Rule rule : rules) {
			getAnswer(rule);
		}
	}

	// Generiere alle Ergebnisse einer IDB Regel und speichere sie in den Fakten
	// ab
	public void getAnswer(Rule rule) {
		Predicate factList = null;
		String kind = rule.getHead().getKind();
		ArrayList<Predicate> predicates = rule.getPredicates();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		if (!predicates.isEmpty()) {
			if (predicates.size() > 1) {
				rulesRenameandReorder(rule);
				factList = generateRule(rule.getPredicates());

			} else {
				getFacts(predicates.get(0));
				factList = predicates.get(0);
			}
		}
		if (factList != null) {
			if (rule.getConditions() != null || !rule.getConditions().isEmpty()) {
				factList = generateConditions(factList, rule.getConditions());
			}
			ArrayList<String> werte = rule.getHead().getScheme();
			for (ArrayList<String> oneMap : factList.getRelation()) {
				ArrayList<String> oneAnswer = new ArrayList<String>();
				for (String wert : werte)
					if (wert.startsWith("?"))
						oneAnswer.add(oneMap.get(factList.getScheme().indexOf(
								wert)));
					else
						oneAnswer.add(wert);
				boolean alreadyExist = false;
				for (ArrayList<String> iterateAnswer : answer)
					if (oneAnswer.containsAll(iterateAnswer)
							&& iterateAnswer.containsAll(oneAnswer))
						alreadyExist = true;
				if (!alreadyExist) {
					Fact factnew = new Fact(kind, oneAnswer);
					values.add(factnew);
					answer.add(oneAnswer);
				}
			}
		}

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

	private Predicate generateRule(ArrayList<Predicate> predicates) {
		Predicate temp = null;
		ArrayList<ArrayList<String>> predicateFacts = getFacts(predicates
				.get(0));
		if (!predicateFacts.isEmpty())
			if (predicates.get(1).isNot())
				temp = getTempNotResult(predicates.get(0), predicates.get(1));
			else
				temp = getTempJoinResult(predicates.get(0), predicates.get(1));
		Predicate predResult = null;
		if (temp != null) {
			if (!temp.getRelation().isEmpty()) {
				int iAnz = predicates.size();
				for (int i = 2; i < iAnz; i++) {
					if (temp.getRelation().isEmpty())
						break;
					Predicate newTemp = null;
					if (predicates.get(i).isNot())
						newTemp = getTempNotResult(temp, predicates.get(i));
					else
						newTemp = getTempJoinResult(temp, predicates.get(i));
					temp = newTemp;
				}
				predResult = temp;
			}

		} else
			predResult = temp;

		return predResult;
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
		List<ArrayList<String>> factList = p.getRelation();
		for (ArrayList<String> factOfFactList : factList) {
			String left = "";
			String right = "";
			if (leftOperand.startsWith("?"))
				left = factOfFactList.get(p.getScheme().indexOf(leftOperand));
			else
				left = leftOperand;
			if (rightOperand.startsWith("?"))
				right = factOfFactList.get(p.getScheme().indexOf(rightOperand));
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
				facts.add(factOfFactList);
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
		ArrayList<ArrayList<String>> fact1 = predicate1.getRelation();
		ArrayList<ArrayList<String>> fact2 = getFacts(predicate2);
		ArrayList<PairofInteger> equalList = getEqualList(
				predicate1.getScheme(), predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.p2);
		for (ArrayList<String> oneOfFact1 : fact1) {
			for (ArrayList<String> oneOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (PairofInteger wert : equalList) {
					if (!oneOfFact1.get(wert.p1)
							.equals(oneOfFact2.get(wert.p2)))
						joinPredicate = false;
				}
				if (joinPredicate == true) {
					ArrayList<String> temp = new ArrayList<String>();

					temp.addAll(oneOfFact1);

					for (int i = 0; i < oneOfFact2.size(); i++) {
						if (!liste.contains(i))
							temp.add(oneOfFact2.get(i));
					}
					facts.add(temp);
				}
			}
		}
		ArrayList<String> newSchema = new ArrayList<String>();
		newSchema.addAll(predicate1.getScheme());
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
		ArrayList<ArrayList<String>> fact1 = predicate1.getRelation();
		ArrayList<ArrayList<String>> fact2 = getFacts(predicate2);
		ArrayList<PairofInteger> equalList = getEqualList(
				predicate1.getScheme(), predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.p2);
		int pos = 0;
		ArrayList<Integer> positionen = new ArrayList<Integer>();
		for (ArrayList<String> oneOfFact1 : fact1) {
			for (ArrayList<String> oneOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (PairofInteger wert : equalList) {
					if (!oneOfFact1.get(wert.p1)
							.equals(oneOfFact2.get(wert.p2)))
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
	private ArrayList<ArrayList<String>> getFacts(Predicate predicate) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		String kind = predicate.getKind();
		int anz = predicate.getScheme().size();
		for (Fact value : values) {
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
					facts.add(value.getListOfValues());
			}
		}
		predicate.setRelation(facts);
		return facts;

	}

	// Stratifizierung der IDB Regeln
	public void orderStratum(ArrayList<Rule> rules) throws Exception {
		int size = rules.size() - 1;
		Map<String, Integer> mapStratum = new HashMap<String, Integer>();

		for (Rule rule : rules) {
			String kindHead = rule.getHead().getKind();
			System.out.println(kindHead);
			mapStratum.put(kindHead, 0);
			for (Predicate predicate : rule.getPredicates()) {
				String kindPredicate = predicate.getKind();
				mapStratum.put(kindPredicate, 0);
			}
		}

		boolean changed;
		do {
			changed = false;
			for (Rule rule : rules)
				for (Predicate predicate : rule.getPredicates()) {
					String head = rule.getHead().getKind();
					if (predicate.isNot()) {
						int headStratum = mapStratum.get(head);
						int predicateStratum = mapStratum.get(predicate
								.getKind());
						int newStratum = Math.max(headStratum,
								predicateStratum + 1);
						if (newStratum > size)
							throw new Exception("no stratification possible");
						if (headStratum < newStratum) {
							mapStratum.put(head, newStratum);
							changed = true;
						}
					} else {
						int headStratum = mapStratum.get(head);
						int predicateStratum = mapStratum.get(predicate
								.getKind());
						int newStratum = Math
								.max(headStratum, predicateStratum);
						if (headStratum < newStratum) {
							mapStratum.put(head, newStratum);
							changed = true;
						}
					}
				}
		} while (changed);

		// schreibe die Stratum Werte in die Prädikatsobjekte
		for (Rule r : rules) {
			String head = r.getHead().getKind();
			r.getHead().setStratum(mapStratum.get(head));
		}

		sortRules(rules, mapStratum);

	}

	// generiere eine Abhängigkeits-Map
	// return : z.B. [Mission2, ( Mission, latestMission, Player, latestPlayer
	// )]
	public Map<String, ArrayList<String>> generateDependencyMap(
			ArrayList<Rule> rules) {
		Map<String, ArrayList<String>> dependencyMap = new HashMap<String, ArrayList<String>>();
		for (Rule rule : rules) {
			String headKind = rule.getHead().getKind();
			ArrayList<String> bodyKind = rule.getDependencies();
			if (dependencyMap.get(headKind) == null)
				dependencyMap.put(headKind, bodyKind);
			else {
				ArrayList<String> currentDependencies = new ArrayList<String>();
				currentDependencies = dependencyMap.get(headKind);
				for (String s : bodyKind) {
					if (!currentDependencies.contains(s))
						currentDependencies.add(s);
				}
			}
		}

		return dependencyMap;
	}

	// sortiere die IDB Regeln mithilfe der stratum- Werte und den
	// Abhängigkeiten
	public void sortRules(ArrayList<Rule> rules, Map<String, Integer> mapStratum) {
		System.out.println("Vor dem Sortieren:");
		Map<String, ArrayList<String>> dependencyMap = generateDependencyMap(rules);
		Map<String, Integer> rankingMap = mapStratum;

		// setze default Werte von Ranking auf stratum Werte
		for (Rule r : rules) {
			int stratum = r.getHead().getStratum();
			r.getHead().setRanking(stratum);
			System.out.println(r.getHead().getKind() + ":" + stratum + ":"
					+ r.getHead().getRanking());
		}

		boolean changed = true;
		do {
			changed = false;
			for (Rule r : rules) {
				String head = r.getHead().getKind();
				int headRanking = r.getHead().getRanking();

				ArrayList<String> dependentPredicates = dependencyMap.get(head);
				for (String predicate : dependentPredicates) {
					int predicateStratum = rankingMap.get(predicate);
					if (headRanking <= predicateStratum) {
						rankingMap.put(r.getHead().getKind(),
								predicateStratum + 1);
						r.getHead().setRanking(predicateStratum + 1);
						changed = true;
					}
				}
			}
		} while (changed);

		Collections.sort(rules, new Comparator<Rule>() {
			@Override
			public int compare(Rule z1, Rule z2) {
				if (z1.getHead().getRanking() > z2.getHead().getRanking())
					return 1;
				if (z1.getHead().getRanking() < z2.getHead().getRanking())
					return -1;
				return 0;
			}
		});

		System.out.println("Nach dem Sortieren:");
		for (Rule r : rules) {
			String head = r.getHead().getKind();
			int stratum = r.getHead().getStratum();
			int headRanking = r.getHead().getRanking();
			System.out.println(head + ":" + stratum + ":" + headRanking);
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	private class PairofInteger {
		int p1, p2;

		PairofInteger(int p1, int p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}