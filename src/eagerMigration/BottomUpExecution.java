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
		List<Map<String, String>> mapList = null;
		String kind = rule.getHead().getKind();
		ArrayList<Predicate> predicates = rule.getPredicates();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		if (!predicates.isEmpty()) {
			if (predicates.size() > 1) {
				rulesBodyUnificationandReorder(rule);
				mapList = generateRule(rule.getPredicates());

			}
		}
		if (mapList != null) {
			if (rule.getConditions() != null) {
				mapList=generateConditions(mapList, rule.getConditions());
			}
			ArrayList<String> werte = rule.getHead().getWerte();
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
					Fact factnew = new Fact(kind,oneAnswer);
					values.add(factnew);
					answer.add(oneAnswer);
				}
			}
		}

	}

	public void rulesBodyUnificationandReorder(Rule rule) {
		if (rule.getConditions() != null)
			for (Iterator<Condition> iterator = rule.getConditions().iterator(); iterator
					.hasNext();) {
				Condition cond = iterator.next();
				if (cond.getOperator().equals("=")) {
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
		for (int i = 0; i < predicate.getWerte().size(); i++) {
			if (predicate.getWerte().get(i).equals(right))
				predicate.getWerte().set(i, left);
		}
	}

	private List<Map<String, String>> generateRule(
			ArrayList<Predicate> predicates) {
		ArrayList<Map<String, String>> temp = null;
		ArrayList<Map<String, String>> map = getMap(predicates.get(0));
		if (!map.isEmpty())
			if (predicates.get(1).isNot())
				temp = getTempNotResult(map, predicates.get(1));
			else
				temp = getTempJoinResult(map, predicates.get(1));
		ArrayList<Map<String, String>> mapList = null;
		if (temp != null) {
			if (!temp.isEmpty()) {
				int iAnz = predicates.size();
				for (int i = 2; i < iAnz; i++) {
					if (temp.isEmpty())
						break;
					ArrayList<Map<String, String>> newTemp = null;
					if (predicates.get(i).isNot())
						newTemp = getTempNotResult(temp, predicates.get(i));
					else
						newTemp = getTempJoinResult(temp, predicates.get(i));
					temp = newTemp;
				}
				mapList = temp;
			}

		} else
			mapList = getMap(predicates.get(0));

		return mapList;
	}

	private List<Map<String, String>> generateConditions(List<Map<String, String>> mapList,
			ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			mapList = getTempCondResult(mapList, cond);
		}
		return mapList;
	}

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp: C(?y,?z) :-
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

	// generiere eine Map zu einem Prädikat, Bsp. A(?x,?y) und dem EDB-Fakt
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

	// Stratifizierung der IDB Regeln
	public void orderStratum(ArrayList<Rule> rules) throws Exception {
		int size=rules.size()-1;
		Map<String, Integer> mapStratum = new HashMap<String, Integer>();

		for (Rule rule : rules){
				String kindHead = rule.getHead().getKind();
				System.out.println(kindHead);	
				mapStratum.put(kindHead, 0);
				for (Predicate predicate : rule.getPredicates()){
					String kindPredicate = predicate.getKind();
					mapStratum.put(kindPredicate, 0);
				}
		}
		
		boolean changed;
		do {
			changed = false;						
			for (Rule rule : rules)
				for (Predicate predicate : rule.getPredicates()){
					String head = rule.getHead().getKind();
					if (predicate.isNot()) {
						int headStratum = mapStratum.get(head);
						int predicateStratum = mapStratum.get(predicate.getKind());
						int newStratum = Math.max(headStratum, predicateStratum + 1);
						if (newStratum>size) throw new Exception("no stratification possible");
						if (headStratum < newStratum) {
							mapStratum.put(head, newStratum);
							changed = true;
						}
					} else {
						int headStratum = mapStratum.get(head);
						int predicateStratum = mapStratum.get(predicate.getKind());
						int newStratum = Math.max(headStratum,predicateStratum);
						if (headStratum < newStratum) {
							mapStratum.put(head, newStratum);
							changed = true;
						}
					}
				}
			} while (changed);

		//schreibe die Stratum Werte in die Prädikatsobjekte
		for (Rule r : rules){
			String head = r.getHead().getKind();
			r.getHead().setStratum(mapStratum.get(head));
		}
		
		sortRules(rules, mapStratum);

	}

	//generiere eine Abhängigkeits-Map
	//return : z.B. [Mission2, ( Mission, latestMission, Player, latestPlayer )]
	public Map<String, ArrayList<String>> generateDependencyMap(ArrayList<Rule> rules){
		Map<String, ArrayList<String>> dependencyMap = new HashMap<String, ArrayList<String>>();
		for (Rule rule : rules){
			String headKind = rule.getHead().getKind();
			ArrayList <String> bodyKind = rule.getDependencies();
			if (dependencyMap.get(headKind) == null) dependencyMap.put(headKind, bodyKind);
			else
			{
				ArrayList<String> currentDependencies = new ArrayList<String>();
				currentDependencies = dependencyMap.get(headKind);
				for (String s: bodyKind){
					if (!currentDependencies.contains(s)) currentDependencies.add(s); 
				}
			}
		}		
		
		return dependencyMap;
	}

	//sortiere die IDB Regeln mithilfe der stratum- Werte und den Abhängigkeiten
	public void sortRules(ArrayList<Rule> rules, Map<String, Integer> mapStratum){
		System.out.println("Vor dem Sortieren:");
		Map<String, ArrayList<String>> dependencyMap = generateDependencyMap(rules);
		Map<String, Integer> rankingMap = mapStratum;
		
		//setze default Werte von Ranking auf stratum Werte
		for (Rule r : rules){
			int stratum = r.getHead().getStratum();
			r.getHead().setRanking(stratum);
			System.out.println(r.getHead().getKind() + ":" + stratum + ":" + r.getHead().getRanking());
		}
		
		boolean changed = true;
		do {
			changed = false;
			for (Rule r : rules){
				String head = r.getHead().getKind();
				int headRanking = r.getHead().getRanking();
				
				ArrayList<String> dependentPredicates = dependencyMap.get(head);
				for (String predicate: dependentPredicates){
					int predicateStratum = rankingMap.get(predicate);
					if (headRanking <= predicateStratum) {
						rankingMap.put(r.getHead().getKind(), predicateStratum + 1);
						r.getHead().setRanking(predicateStratum + 1);
						changed = true;
					}
				}				
			}
		}while (changed);
		
		Collections.sort(rules, new Comparator<Rule>() {
			@Override
			public int compare(Rule z1, Rule z2) {
				if (z1.getHead().getRanking()> z2.getHead()
						.getRanking())
					return 1;
				if (z1.getHead().getRanking() < z2.getHead()
						.getRanking())
					return -1;
				return 0;
			}
		});		
		
		System.out.println("Nach dem Sortieren:");
		for (Rule r : rules){
			String head = r.getHead().getKind();
			int stratum = r.getHead().getStratum();
			int headRanking = r.getHead().getRanking();
			System.out.println(head + ":" + stratum + ":" + headRanking);
		}
	}	
	
}