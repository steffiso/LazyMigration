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

	public void generateQueries(ArrayList<Query> queries) {
		//orderStratum(queries);
		for (Query query : queries) {
			getAnswer(query);
		}
	}

	// Generiere alle Ergebnisse einer IDB Query und speichere sie in den Fakten
	// ab
	public void getAnswer(Query query) {
		ArrayList<Map<String, String>> mapList = null;
		String kind = query.getIdbRelation().getKind();
		ArrayList<String> werte = query.getIdbRelation().getWerte();
		ArrayList<Relation> relations = query.getRelations();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		if (!relations.isEmpty()) {
			if (relations.size() > 1) {
				ArrayList<Map<String, String>> temp = null;
				ArrayList<Map<String, String>> map = getMap(relations.get(0));
				if (!map.isEmpty())
					if (relations.get(1).isNot())
						temp = getTempNotResult(map, relations.get(1));
					else
						temp = getTempJoinResult(map, relations.get(1));
				if (temp != null) {
					if (!temp.isEmpty()) {
						int iAnz = relations.size();
						for (int i = 2; i < iAnz; i++) {
							if (temp.isEmpty())
								break;
							ArrayList<Map<String, String>> temp1 = null;
							if (relations.get(i).isNot())
								temp1 = getTempNotResult(temp, relations.get(i));
							else
								temp1 = getTempJoinResult(temp,
										relations.get(i));
							temp = temp1;
						}
						mapList = temp;
					}
				}
			} else
				mapList = getMap(relations.get(0));
		}
		if (mapList != null) {
			if (query.getConditions() != null) {
				ArrayList<Condition> conds = query.getConditions();
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
			ArrayList<Map<String, String>> fact1, Relation relation2) {
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
			ArrayList<Map<String, String>> fact1, Relation relation2) {
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
	private ArrayList<Map<String, String>> getMap(Relation relation) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();
		String kind = relation.getKind();
		int anz = relation.getAnz();
		for (Fact value : values) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				Map<String, String> m = new HashMap<String, String>();
				int i = 0;
				for (String wert : relation.getWerte()) {
					m.put(wert, value.getListOfValues().get(i));
					i++;
				}
				facts.add(m);
			}
		}
		return facts;

	}

	// Stratifizierung der IDB Queries
	public void orderStratum(ArrayList<Query> queries) throws Exception {
		int size=queries.size()-1;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Query query : queries) {
			map.put(query.getIdbRelation().getKind(), 0);
			for (Relation relation : query.getRelations())
				map.put(relation.getKind(), 0);
		}
		boolean changed;
		do {
			changed = false;
			for (Query query : queries)
				for (Relation relation : query.getRelations()){
					System.out.println(query.getIdbRelation().getKind()+" "+query.getIdbRelation().getStratum());
					System.out.println(relation.getKind()+" "+relation.getStratum());
					if (relation.isNot()) {
						String left = query.getIdbRelation().getKind();
						int oldVal = map.get(left);
						int newVal = Math.max(map.get(left),
								map.get(relation.getKind()) + 1);
						System.out.println("new: "+newVal);
						if (newVal>size) throw new Exception("no stratification possible");
						if (oldVal < newVal) {
							map.put(left, newVal);
							changed = true;
							query.getIdbRelation().setStratum(newVal);
						}
					} else {
						String left = query.getIdbRelation().getKind();
						int oldVal = map.get(left);
						int newVal = Math.max(map.get(left),
								map.get(relation.getKind()));
						System.out.println("new: "+newVal);
						if (oldVal < newVal) {
							map.put(left, newVal);
							changed = true;
							query.getIdbRelation().setStratum(newVal);
						}
					}
				}} while (changed);

		Collections.sort(queries, new Comparator<Query>() {
			@Override
			public int compare(Query z1, Query z2) {
				if (z1.getIdbRelation().getStratum() > z2.getIdbRelation()
						.getStratum())
					return 1;
				if (z1.getIdbRelation().getStratum() < z2.getIdbRelation()
						.getStratum())
					return -1;
				return 0;
			}
		});
	}

}