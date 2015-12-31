package bottomUp;

import java.util.ArrayList;
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

	// Generiere alle Ergebnisse einer IDB Query und speichere sie in einer
	// Liste ab
	public ArrayList<ArrayList<String>> getAnswer(Query query) {
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
					} else
						mapList = new ArrayList<Map<String, String>>();
				} else
					mapList = new ArrayList<Map<String, String>>();
			} else
				mapList = getMap(relations.get(0));

		}
		for (Map<String, String> oneMap : mapList) {
			ArrayList<String> oneAnswer = new ArrayList<String>();
			for (String wert : werte)
				oneAnswer.add(oneMap.get(wert));
			Fact factnew = new Fact(kind, oneAnswer);
			boolean alreadyExist = false;
			for (ArrayList<String> iterateAnswer : answer)
				if (oneAnswer.containsAll(iterateAnswer)
						&& iterateAnswer.containsAll(oneAnswer))
					alreadyExist = true;
			if (!alreadyExist) {
				values.add(factnew);
				answer.add(oneAnswer);
			}
		}

		return answer;

	}

	// generiere Zwischenergebnisse bei einem Join, Bsp: C(?y,?z) :-
	// A(?x,?y),B(?x,?z).
	// Joine die Werte von A und B nach ?x
	public ArrayList<Map<String, String>> getTempJoinResult(
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
	public ArrayList<Map<String, String>> getTempNotResult(
			ArrayList<Map<String, String>> fact1, Relation relation2) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();

		ArrayList<Map<String, String>> fact2 = getMap(relation2);
		ArrayList<String> equalList = getEqualList(fact1.get(0).keySet()
				.toArray(new String[fact1.get(0).size()]), relation2.getWerte());
		int i = -1;
		ArrayList<Integer> positionen = new ArrayList<Integer>();
		for (Map<String, String> mapOfFact1 : fact1) {
			i++;
			for (Map<String, String> mapOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (String wert : equalList) {
					if (!mapOfFact1.get(wert).equals(mapOfFact2.get(wert)))
						joinPredicate = false;
				}
				if (joinPredicate == true) {
					positionen.add(i);
				}
			}
		}

		for (int i1 = 0; i1 < fact1.size(); i1++) {
			if (!positionen.contains(i1))
				facts.add(fact1.get(i1));
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
	// neue Map für A: "?x" : 1
	// "?y" : 2
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

	public void orderStratum(ArrayList<Query> queries) {
		for (Query query : queries)
			for (Relation relation : query.getRelations())
				if (relation.isNot())
					query.getIdbRelation().setStratum(
							Math.max(query.getIdbRelation().getStratum(),
									relation.getStratum() + 1));
				else
					query.getIdbRelation().setStratum(
							Math.max(query.getIdbRelation().getStratum(),
									relation.getStratum()));
	}
}
