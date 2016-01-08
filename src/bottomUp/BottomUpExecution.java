package bottomUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BottomUpExecution {

	// Alle EDB-Fakten und generierten IDB-Fakten
	private ArrayList<Fact> values;

	// Setze EDB-Fakten
	public BottomUpExecution(ArrayList<Fact> values) {
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

	public void generateQueries(ArrayList<Rule> rules) {
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

	// Generiere alle Ergebnisse einer IDB Query und speichere sie in den Fakten
	// ab
	public void getAnswer(Rule rule) {
		ArrayList<Map<String, String>> mapList = null;
		String kind = rule.getHead().getKind();
		ArrayList<String> werte = rule.getHead().getWerte();
		ArrayList<Predicate> predicates = rule.getPredicates();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		if (!predicates.isEmpty()) {
			if (predicates.size() > 1) {
				ArrayList<Map<String, String>> temp = null;
				ArrayList<Map<String, String>> map = getMap(predicates.get(0));
				if (!map.isEmpty())
					if (predicates.get(1).isNot())
						temp = getTempNotResult(map, predicates.get(1));
					else
						temp = getTempJoinResult(map, predicates.get(1));
				if (temp != null) {
					if (!temp.isEmpty()) {
						int iAnz = predicates.size();
						for (int i = 2; i < iAnz; i++) {
							if (temp.isEmpty())
								break;
							ArrayList<Map<String, String>> temp1 = null;
							if (predicates.get(i).isNot())
								temp1 = getTempNotResult(temp, predicates.get(i));
							else
								temp1 = getTempJoinResult(temp,
										predicates.get(i));
							temp = temp1;
						}
						mapList = temp;
					}
				}
			} else
				mapList = getMap(predicates.get(0));
		}
		if (mapList != null) {
			if (rule.getConditions() != null) {
				ArrayList<Condition> conds = rule.getConditions();
				for (Condition cond : conds) {
					mapList = getTempCondResult(mapList, cond);
				}
			}

			for (Map<String, String> oneMap : mapList) {
				ArrayList<String> oneAnswer = new ArrayList<String>();
				for (String wert : werte)
					if (wert.startsWith("?"))
						oneAnswer.add(oneMap.get(wert));
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

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z),?y=?z.
	// Füge nur Werte von A und B mit der Bedingung ?y=?z ein.
	private ArrayList<Map<String, String>> getTempCondResult(
			ArrayList<Map<String, String>> mapList, Condition cond) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();
		String rightOperand = cond.getRightOperand();
		String leftOperand = cond.getLeftOperand();
		String operator = cond.getOperator();
		for (Map<String, String> mapOfMapList : mapList) {
			String left=""; String right="";
			if (leftOperand.startsWith("?")) 
			left=mapOfMapList.get(leftOperand);
			else left=leftOperand;
			if (rightOperand.startsWith("?")) 
			right=mapOfMapList.get(rightOperand);
			else right=rightOperand;
			boolean condPredicate = false;
			switch (operator) {
			case "=":
				if (left.equals(
						right))
					condPredicate = true;
				break;
			case "!":
				if (!left.equals(
						right))
					condPredicate = true;
				break;
			case "<":
				if (left.compareTo(
						right) < 0)
					condPredicate = true;
				break;
			case ">":
				if (left.compareTo(
						right) > 0)
					condPredicate = true;
				break;
			}
			if (condPredicate == true) {
				facts.add(mapOfMapList);
			}
		}
		return facts;

	}

	// generiere Zwischenergebnisse bei einem Join, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z).
	// Joine die Werte von A und B nach ?x
	private ArrayList<Map<String, String>> getTempJoinResult(
			ArrayList<Map<String, String>> fact1, Predicate relation2) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();

		ArrayList<Map<String, String>> fact2 = getMap(relation2);
		ArrayList<String> equalList = getEqualList(fact1.get(0).keySet()
				.toArray(new String[fact1.get(0).size()]), relation2.getWerte());
		for (Map<String, String> mapOfFact1 : fact1)
			for (Map<String, String> mapOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (String wert : equalList) {
					if (!mapOfFact1.get(wert).equals(mapOfFact2.get(wert)))
						joinPredicate = false;
				}
				if (joinPredicate == true) {
					Map<String, String> temp = new HashMap<String, String>();
					for (Map.Entry<String, String> e : mapOfFact1.entrySet()) {
						temp.put(e.getKey(), e.getValue());
					}
					for (Map.Entry<String, String> e : mapOfFact2.entrySet()) {
						if (!equalList.contains(e.getKey()))
							temp.put(e.getKey(), e.getValue());
					}
					facts.add(temp);
				}
			}
		return facts;

	}

	// generiere Zwischenergebnisse bei einem Not, Bsp: C(?y,?z) :- A(?x,?y),
	// not B(?x,?z).
	// Alle ?x Werte von A die nicht in ?x Werte von B sind
	private ArrayList<Map<String, String>> getTempNotResult(
			ArrayList<Map<String, String>> fact1, Predicate relation2) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();

		ArrayList<Map<String, String>> fact2 = getMap(relation2);
		ArrayList<String> equalList = getEqualList(fact1.get(0).keySet()
				.toArray(new String[fact1.get(0).size()]), relation2.getWerte());
		int pos = 0;
		ArrayList<Integer> positionen = new ArrayList<Integer>();
		for (Map<String, String> mapOfFact1 : fact1) {

			for (Map<String, String> mapOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (String wert : equalList) {
					if (!mapOfFact1.get(wert).equals(mapOfFact2.get(wert)))
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
		return facts;

	}

	// generiere die Join Prädikate, Bsp: C(?y,?z) :- A(?x,?y),B(?x,?z). --> ?x
	// ist Join Prädikat
	private ArrayList<String> getEqualList(String[] strings,
			ArrayList<String> strings2) {
		ArrayList<String> list = new ArrayList<String>();
		for (String wert : strings)
			for (String wert2 : strings2)
				if (wert.equals(wert2))
					list.add(wert);
		return list;
	}

	// generiere eine Map zu einer Relation, Bsp. A(?x,?y) und dem EDB-Fakt
	// A(1,2)
	// neue Map für A: "?x" : 1, "?y" : 2
	private ArrayList<Map<String, String>> getMap(Predicate predicate) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();
		String kind = predicate.getKind();
		int anz = predicate.getAnz();
		for (Fact value : values) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				Map<String, String> m = new HashMap<String, String>();
				int i = 0;
				for (String wert : predicate.getWerte()) {
					m.put(wert, value.getListOfValues().get(i));
					i++;
				}
				facts.add(m);
			}
		}
		return facts;

	}

	// Stratifizierung der IDB Queries
	public void orderStratum(ArrayList<Rule> rules) throws Exception {
		int size=rules.size()-1;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Rule rule : rules) {
			map.put(rule.getHead().getKind(), 0);
			for (Predicate predicate : rule.getPredicates())
				map.put(predicate.getKind(), 0);
		}
		boolean changed;
		do {
			changed = false;
			for (Rule rule : rules)
				for (Predicate predicate : rule.getPredicates()){
					System.out.println(rule.getHead().getKind()+" "+rule.getHead().getStratum());
					System.out.println(predicate.getKind()+" "+predicate.getStratum());
					if (predicate.isNot()) {
						String left = rule.getHead().getKind();
						int oldVal = map.get(left);
						int newVal = Math.max(map.get(left),
								map.get(predicate.getKind()) + 1);
						System.out.println("new: "+newVal);
						if (newVal>size) throw new Exception("no stratification possible");
						if (oldVal < newVal) {
							map.put(left, newVal);
							changed = true;
							rule.getHead().setStratum(newVal);
						}
					} else {
						String left = rule.getHead().getKind();
						int oldVal = map.get(left);
						int newVal = Math.max(map.get(left),
								map.get(predicate.getKind()));
						System.out.println("new: "+newVal);
						if (oldVal < newVal) {
							map.put(left, newVal);
							changed = true;
							rule.getHead().setStratum(newVal);
						}
					}
				}} while (changed);

		Collections.sort(rules, new Comparator<Rule>() {
			@Override
			public int compare(Rule z1, Rule z2) {
				if (z1.getHead().getStratum() > z2.getHead()
						.getStratum())
					return 1;
				if (z1.getHead().getStratum() < z2.getHead()
						.getStratum())
					return -1;
				return 0;
			}
		});
	}

}