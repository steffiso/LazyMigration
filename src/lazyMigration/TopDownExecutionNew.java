package lazyMigration;

import java.util.ArrayList;
import java.util.HashMap;
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
	public TopDownExecutionNew(ArrayList<Fact> facts, ArrayList<Rule> rules, Predicate goal){
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
	public List<Map<String, String>> getAnswer(Rule rule) {
		//ArrayList<ArrayList<Map<String, String>>> maps = new ArrayList<ArrayList<Map<String, String>>>();
		for (Predicate p : rule.getPredicates()){
			getMap(p);
			//maps.add(getMap(p));
		}
			
		//List<Map<String, String>> temp = join(rule.getPredicates(), maps);
		ArrayList<Map<String, String>> temp = join(rule.getPredicates());
		if (rule.getConditions() != null)
			temp = generateConditions(temp, rule.getConditions());
		return temp;
	}
		
	public ArrayList<Fact> getAnswers(){
		resultMap = new ArrayList<ArrayList<Map<String,String>>>();
		ArrayList<Fact> answer = new ArrayList<Fact>();	
		unificationMap = new HashMap<String, String>();
		
		//check if a mapping for predicate goal exists
		if (getMap(goal) == 0){
			ArrayList<Rule> childrenRules = new ArrayList<Rule>();			
			for (Rule r: rules){
				//look for corresponding rules
				Predicate ruleHead = r.getHead();
				if (ruleHead.getKind().equals(goal.getKind()) && ruleHead.getAnz() == goal.getAnz()){
					Rule unifiedRule = unifyRule(goal, r);
					childrenRules.add(unifiedRule);				
				}
			}
			
			if (childrenRules.size() == 0){
				System.out.println("No matching rules found!");				
			}
			else {
				//no mapping found -> children rules exist
				//compute the answers of corresponding rule goal tree
				System.out.println("Unifizierte Regeln: " + childrenRules.toString());
				tree = new RuleGoalTree(childrenRules);
				goal.setResultMap(getAnswersForSubtree(tree));
				
				//join head predicates of all rule bodies (children of goal)
//				ArrayList<Predicate> childrenHeads = new ArrayList<Predicate>();
//				for (Rule r: childrenRules){
//					childrenHeads.add(r.getHead());
//				}
//				goal.setResultMap(join(childrenHeads));
			}
			
		}			
		
		else			
			System.out.println("Mapping gefunden:" + goal.toString() + " " + goal.getResultMap().toString());				
		
		System.out.println("Ergebnis: " + goal.getResultMap());
		//toDo: goal.getResultMap() to Facts !!!
		return answer;
	}
	
	public ArrayList<Map<String, String>> getAnswersForSubtree(RuleGoalTree tree){
		ArrayList<Map<String, String>> resultMap = new ArrayList<Map<String, String>>();
		
		for (Rule rulesChildren: tree.getChildren()){
			RuleBody body = rulesChildren.getRuleBody();
			for (Predicate subgoal: body.getPredicates()){
				//check if a mapping for predicate goal exists
				if (getMap(subgoal) == 0){
					ArrayList<Rule> childrenRules = new ArrayList<Rule>();
					for (Rule r: rules){
						//look for corresponding rules and unify
						Predicate ruleHead = r.getHead();
						if (ruleHead.getKind().equals(subgoal.getKind()) && ruleHead.getAnz() == subgoal.getAnz()){
							Rule unifiedRule = unifyRule(subgoal, r);
							childrenRules.add(unifiedRule);				
						}						
					}
					for (int i = 0; i < childrenRules.size(); i++){
						System.out.println(childrenRules.get(i).toString());
					}
						
					if (childrenRules.size() == 0){
						System.out.println("Keine children rules!" + subgoal.toString());
					}
					else
						//no mapping found -> children rules exist
						//compute the answers of corresponding rule goal tree
						System.out.println("Unifizierte Regeln: " + childrenRules.toString());
						RuleGoalTree subTree = new RuleGoalTree(childrenRules);
						
						Map<String, String> attributeHead = subTree.getGoal().getWerte();
						subgoal.setResultMap(getAnswersForSubtree(subTree));
						for (Map.Entry<String, String> entryHead : attributeHead.entrySet()){
							if (!entryHead.getValue().equals("")){
								//toDo: hier put ausführen mit ResultMap??
							}
						}
				}
				else
					System.out.println("Mapping gefunden!" + subgoal.getResultMap().toString());
			}
			//toDo: join der Prädikate im Rule Body
//			resultMap = join(body.getPredicates());
//			if (body.getConditions() != null)
//				resultMap = generateConditions(resultMap, body.getConditions());			
//			System.out.println("Join: " + resultMap.toString());
			
		}
		
		//join head predicates of all rule bodies (children of goal)
//		ArrayList<Predicate> childrenHeads = new ArrayList<Predicate>();
//		for (Rule r: tree.getChildren()){
//			childrenHeads.add(r.getHead());
//		}
//		goal.setResultMap(join(childrenHeads));
		return resultMap;
	}
	
	public Rule unifyRule(Predicate goal, Rule childrenRule){		
		//nur Prädikate im RuleBody
		Predicate head = childrenRule.getHead();
		RuleBody body = childrenRule.getRuleBody();
		if (!goal.getKind().equals(head.getKind()) || goal.getAnz() != head.getAnz()) {
			System.out.println("Unifizierung nicht möglich:goal " + goal.toString() + ",head " + head.toString());
		}
		else {
			// create unification Map			
			Map<String, String> valuesGoal = goal.getWerte();
			Map<String, String> valuesHead = head.getWerte();			
			
			for (Map.Entry<String, String> entryGoal : valuesGoal.entrySet()) {
				boolean containsKey = valuesHead.containsKey(entryGoal.getKey());
				String headValue = valuesHead.get(entryGoal.getKey());
				String goalValue = entryGoal.getValue();
				if (containsKey  && !headValue.equals(goalValue)){
					unificationMap.put(entryGoal.getKey(), entryGoal.getValue());
				}
			}	
			
			for (Map.Entry<String, String> unificationMapEntry : unificationMap.entrySet()){
				Map<String, String> attributesRuleHead = childrenRule.getHead().getWerte();
				//unify head
				if (attributesRuleHead.containsKey(unificationMapEntry.getKey())){ //&& !attributesRuleHead.get(unificationMapEntry.getKey()).equals("")){
					attributesRuleHead.put(unificationMapEntry.getKey(), unificationMapEntry.getValue());
				}
				//unify body
				for (Predicate p:body.getPredicates()){
					if (p.getWerte().containsKey(unificationMapEntry.getKey())){
						p.getWerte().put(unificationMapEntry.getKey(), unificationMapEntry.getValue());
					}
				}
			}
		}
		
		return childrenRule;
	}
	
	public void putNewFact(String newFact){
		Database db = new Database();
		db.putToDatabase(newFact);
	}
	
	public ArrayList<Rule> getRules(Predicate p){
		ArrayList<Rule> rule = new ArrayList<Rule>();
		for (Rule r: rules){
			if (r.getHead().getKind().equals(p.getKind()) && r.getHead().getAnz() == p.getAnz()){
				rule.add(r);
			}
		}
		return rule;
	}
	
	// generiere Zwischenergebnisse zu allen Joins einer Rule nacheinander
	public ArrayList<Map<String, String>> join(ArrayList<Predicate> predicates){
		//ArrayList<ArrayList<Map<String, String>>> map) {
		//ArrayList<Map<String, String>> temp = map.get(0);

		ArrayList<Map<String, String>> temp = predicates.get(0).getResultMap();
		if (temp != null) {
			if (!temp.isEmpty()) {
				ArrayList<Map<String, String>> facts2;
				for (int i = 1; i < predicates.size(); i++) {
					ArrayList<Map<String, String>> restemp = new ArrayList<Map<String, String>>();
					facts2 = predicates.get(i).getResultMap();
	
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

//	// generiere Zwischenergebnisse zu allen Joins einer Rule nacheinander
//		public ArrayList<Map<String, String>> join(ArrayList<Predicate> predicates,
//				ArrayList<ArrayList<Map<String, String>>> map) {
//			ArrayList<Map<String, String>> temp = map.get(0);
//			if (temp != null) {
//				if (!temp.isEmpty()) {
//					for (int i = 1; i < predicates.size(); i++) {
//						ArrayList<Map<String, String>> restemp = new ArrayList<Map<String, String>>();
//						ArrayList<Map<String, String>> facts2 = map.get(i);
//
//						if (predicates.get(i).isNot()) {
//							for (Map<String, String> fact1 : temp) {
//								if (facts2 == null
//										|| getTempNotResult(fact1, facts2)) {
//									restemp.add(fact1);
//								}
//							}
//						}
//
//						else {
//							if (facts2 != null) {
//								for (Map<String, String> fact1 : temp) {
//									ArrayList<Map<String, String>> maps = getTempJoinResult(
//											fact1, facts2);
//									if (maps != null)
//										restemp.addAll(getTempJoinResult(fact1,
//												facts2));
//								}
//							}
//						}
//						temp = restemp;
//					}
//				}
//			}
//			return temp;
//		}
//		
	// generiere eine Map zu einem Prädikat, Bsp. A(?x,?y) und dem EDB-Fakt
	// A(1,2)
	// neue Map für A: "?x" : 1, "?y" : 2
	public int getMap(Predicate predicate) {
		ArrayList<Map<String, String>> values = null;
		String kind = predicate.getKind();
		int anz = predicate.getAnz();
		for (Fact value : facts) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == anz) {
				boolean set = true;
				Map<String, String> tempMap = new HashMap<String, String>();
				int i = 0;
				for (Entry<String, String> wert : predicate.getWerte().entrySet()){
					if (wert.getValue().equals(""))
						tempMap.put(wert.getKey(), value.getListOfValues().get(i));
					else if (value.getListOfValues().get(i).equals(wert.getValue()))
							tempMap.put(wert.getKey(), value.getListOfValues().get(i));
					else {
						set = false;
						break;
					}
					i++;
				}
				if (set) {
					if (values == null)
						values = new ArrayList<Map<String, String>>();
					values.add(tempMap);
				}
			}
		}
		predicate.setResultMap(values);
		if (values == null) return 0;
		else return 1;

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

	private ArrayList<Map<String, String>> generateConditions(
			ArrayList<Map<String, String>> mapList, ArrayList<Condition> conditions) {
		for (Condition cond : conditions) {
			mapList = getTempCondResult(mapList, cond);
		}
		return mapList;
	}

	// generiere Zwischenergebnisse bei einer Bedingunge, Bsp:
	// A(?x,?y),B(?x,?z),?y=?z.
	// Füge nur Werte von A und B mit der Bedingung ?y=?z ein.
	private ArrayList<Map<String, String>> getTempCondResult(
			ArrayList<Map<String, String>> mapList, Condition cond) {
		ArrayList<Map<String, String>> facts = new ArrayList<Map<String, String>>();
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
