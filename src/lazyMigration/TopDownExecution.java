package lazyMigration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datalog.Condition;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class TopDownExecution {
	// Alle EDB-Fakten und generierten IDB-Fakten
	private ArrayList<Fact> values;

	// Setze EDB-Fakten
	public TopDownExecution(ArrayList<Fact> values) {
		super();
		this.values = values;
	}

	public ArrayList<Fact> getValues() {
		return values;
	}

	public void setValues(ArrayList<Fact> values) {
		this.values = values;
	}

	// Testgenerierung einer Rule mit Top Down
	public List<Map<String, String>> getAnswer(Rule rule) {
		ArrayList<ArrayList<Map<String, String>>> maps = new ArrayList<ArrayList<Map<String, String>>>();
		for (Predicate p : rule.getPredicates())
			maps.add(getMap(p));
		List<Map<String, String>> temp = join(rule.getPredicates(), maps);
		if (rule.getConditions() != null)
			temp = generateConditions(temp, rule.getConditions());
		return temp;
	}

	// generiere Zwischenergebnisse zu allen Joins einer Rule nacheinander
	public ArrayList<Map<String, String>> join(ArrayList<Predicate> predicates,
			ArrayList<ArrayList<Map<String, String>>> map) {
		ArrayList<Map<String, String>> temp = map.get(0);
		if (temp != null) {
			if (!temp.isEmpty()) {
				for (int i = 1; i < predicates.size(); i++) {
					ArrayList<Map<String, String>> restemp = new ArrayList<Map<String, String>>();
					ArrayList<Map<String, String>> facts2 = map.get(i);

					if (predicates.get(i).isNot()) {
						for (Map<String, String> fact1 : temp) {
							if (facts2 == null
									|| getTempNotResult(fact1, facts2)) {
								restemp.add(fact1);
							}
						}
					}

					else {
						if (facts2 != null) {
							for (Map<String, String> fact1 : temp) {
								ArrayList<Map<String, String>> maps = getTempJoinResult(
										fact1, facts2);
								if (maps != null)
									restemp.addAll(getTempJoinResult(fact1,
											facts2));
							}
						}
					}
					temp = restemp;
				}
			}
		}
		return temp;
	}

	// generiere eine Map zu einem Prädikat, Bsp. A(?x,?y) und dem EDB-Fakt
	// A(1,2)
	// neue Map für A: "?x" : 1, "?y" : 2
	public ArrayList<Map<String, String>> getMap(Predicate predicate) {
		ArrayList<Map<String, String>> facts = null;
		String kind = predicate.getKind();
		int anz = predicate.getAnz();
		for (Fact value : values) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				boolean set = true;
				Map<String, String> tempMap = new HashMap<String, String>();
				int i = 0;
				for (String wert : predicate.getWerte()) {
					if (!wert.contains("~"))
						tempMap.put(wert, value.getListOfValues().get(i));
					else if (value.getListOfValues().get(i)
							.equals(wert.substring(wert.indexOf("~") + 1)))
						tempMap.put(wert.substring(0, wert.indexOf("~")), value
								.getListOfValues().get(i));
					else {
						set = false;
						break;
					}
					i++;
				}
				if (set) {
					if (facts == null)
						facts = new ArrayList<Map<String, String>>();
					facts.add(tempMap);
				}
			}
		}
		return facts;

	}

	// generiere Zwischenergebnisse bei einem Join mit jeweils einem Fakt und
	// einer zweiten Relation, Bsp: Map A(?x,?y),Liste<Map> B(?x,?z).
	// Joine die Werte von A mit den Werten von B nach ?x
	private ArrayList<Map<String, String>> getTempJoinResult(
			Map<String, String> fact1, ArrayList<Map<String, String>> fact2) {
		ArrayList<Map<String, String>> result = null;
		ArrayList<String> equalList = getEqualList(fact1.keySet(), fact2.get(0)
				.keySet());
		for (Map<String, String> mapOfFact2 : fact2) {
			boolean joinPredicate = true;
			for (String wert : equalList) {
				if (!fact1.get(wert).equals(mapOfFact2.get(wert)))
					joinPredicate = false;
			}
			if (joinPredicate == true) {
				Map<String, String> temp = new HashMap<String, String>();
				for (Map.Entry<String, String> e : fact1.entrySet()) {
					temp.put(e.getKey(), e.getValue());
				}
				for (Map.Entry<String, String> e : mapOfFact2.entrySet()) {
					if (!equalList.contains(e.getKey()))
						temp.put(e.getKey(), e.getValue());
				}
				if (result == null)
					result = new ArrayList<Map<String, String>>();
				result.add(temp);
			}
		}
		return result;
	}

	// generiere Zwischenergebnisse bei einem Join zu zwei, Bsp: A(?x,?y), not
	// B(?x).
	// Alle ?x-Werte von A die nicht in B sind
	private boolean getTempNotResult(Map<String, String> fact1,
			ArrayList<Map<String, String>> fact2) {

		ArrayList<String> equalList = getEqualList(fact1.keySet(), fact2.get(0)
				.keySet());
		for (Map<String, String> mapOfFact2 : fact2) {
			boolean joinPredicate = true;
			for (String wert : equalList) {
				if (!fact1.get(wert).equals(mapOfFact2.get(wert)))
					joinPredicate = false;
			}
			if (joinPredicate == true) {
				return false;
			}
		}

		return true;

	}

	// generiere die Join Prädikate, d.h. die Variablen, die gleich heißen, Bsp:
	// A(?x,?y),B(?x,?z). --> ?x
	// ist Join Prädikat
	private ArrayList<String> getEqualList(Set<String> keySet,
			Set<String> keySet2) {
		ArrayList<String> list = new ArrayList<String>();
		for (String wert1 : keySet)
			for (String wert2 : keySet2)
				if (wert1.equals(wert2))
					list.add(wert1);
		return list;
	}

	private List<Map<String, String>> generateConditions(
			List<Map<String, String>> mapList, ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			mapList = getTempCondResult(mapList, cond);
		}
		return mapList;
	}

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp:
	// A(?x,?y),B(?x,?z),?y=?z.
	// Füge nur Werte von A und B mit der Bedingung ?y=?z ein.
	private List<Map<String, String>> getTempCondResult(
			List<Map<String, String>> mapList, Condition cond) {
		List<Map<String, String>> facts = new ArrayList<Map<String, String>>();
		String rightOperand = cond.getRightOperand();
		String leftOperand = cond.getLeftOperand();
		String operator = cond.getOperator();
		for (Map<String, String> mapOfMapList : mapList) {
			String left = "";
			String right = "";
			if (leftOperand.startsWith("?"))
				left = mapOfMapList.get(leftOperand);
			else
				left = leftOperand;
			if (rightOperand.startsWith("?"))
				right = mapOfMapList.get(rightOperand);
			else
				right = rightOperand;
			if (left == null || right == null) {
				facts.add(mapOfMapList);
				continue;
			}
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
		return facts;

	}

}
