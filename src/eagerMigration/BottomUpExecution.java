package eagerMigration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import datalog.Condition;
import datalog.Fact;
import datalog.MigrationExecution;
import datalog.Predicate;
import datalog.Rule;

public class BottomUpExecution extends MigrationExecution{

	// set edb facts
	public BottomUpExecution(ArrayList<Fact> values, ArrayList<Rule> rules) {
		super(values,rules);
	}

	public void generateAllRules() throws Exception {
		
		orderStratum(rules);
		for (Rule rule : rules) {
			getAnswer(rule);
		}
	}

	// generate all results of a rule and save it to global variable facts
	public void getAnswer(Rule rule) {
		Predicate factList = null;
		String kind = rule.getHead().getKind();
		ArrayList<Predicate> predicates = rule.getPredicates();
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		if (!predicates.isEmpty()) {
			if (predicates.size() > 1) {
				rulesRename(rule);
				factList = generateRule(rule.getPredicates());

			} else {
				getFacts(predicates.get(0));
				factList = predicates.get(0);
			}
		}
		if (factList != null) {
			if (rule.getConditions() != null || !rule.getConditions().isEmpty()) {
				factList = selection(factList, rule.getConditions());
			}
			ArrayList<String> werte = rule.getHead().getScheme();
			for (ArrayList<String> oneResult : factList.getRelation()) {
				ArrayList<String> oneAnswer = new ArrayList<String>();
				for (String wert : werte)
					if (wert.startsWith("?"))
						if (factList.getScheme().contains(wert))
							oneAnswer.add(oneResult.get(factList.getScheme()
									.indexOf(wert)));
						else {
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
					Fact factnew = new Fact(kind, oneAnswer);
					facts.add(factnew);
					answer.add(oneAnswer);
				}
			}
		}

	}

	private void rulesRename(Rule rule) {
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

	}



	// generate results of a rule
	private Predicate generateRule(ArrayList<Predicate> predicates) {
		Predicate temp = null;
		Predicate startPredicate = predicates.get(0);
		getFacts(startPredicate);
		ArrayList<ArrayList<String>> predicateFacts = startPredicate
				.getRelation();
		if (!predicateFacts.isEmpty())
			if (predicates.get(1).isNot())
				temp = getTempNotResult(startPredicate, predicates.get(1));
			else
				temp = getTempJoinResult(startPredicate, predicates.get(1));
		Predicate predResult = null;
		if (temp != null) {
			if (!temp.getRelation().isEmpty()) {
				int iNumber = predicates.size();
				for (int i = 2; i < iNumber; i++) {
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

	// generate temporary results of join, e.g.: C(?y,?z) :-
	// A(?x,?y),B(?x,?z).
	// join values of A und B on attribute ?x
	private Predicate getTempJoinResult(Predicate predicate1,
			Predicate predicate2) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> fact1 = predicate1.getRelation();
		getFacts(predicate2);
		ArrayList<ArrayList<String>> fact2 = predicate2.getRelation();
		ArrayList<PairofInteger> equalList = getEqualList(
				predicate1.getScheme(), predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.getP2());
		for (ArrayList<String> oneOfFact1 : fact1) {
			for (ArrayList<String> oneOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (PairofInteger wert : equalList) {
					if (!oneOfFact1.get(wert.getP1())
							.equals(oneOfFact2.get(wert.getP2())))
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

	// /generate temporary results of not, e.g.: C(?y,?z) :- A(?x,?y),
	// not B(?x,?z).
	// Put all values of ?x from A which aren't in the ?x values of B
	private Predicate getTempNotResult(Predicate predicate1,
			Predicate predicate2) {
		ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> fact1 = predicate1.getRelation();
		getFacts(predicate2);
		ArrayList<ArrayList<String>> fact2 = predicate2.getRelation();
		ArrayList<PairofInteger> equalList = getEqualList(
				predicate1.getScheme(), predicate2.getScheme());
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (PairofInteger wert : equalList)
			liste.add(wert.getP2());
		int pos = 0;
		ArrayList<Integer> positionen = new ArrayList<Integer>();
		for (ArrayList<String> oneOfFact1 : fact1) {
			for (ArrayList<String> oneOfFact2 : fact2) {
				boolean joinPredicate = true;
				for (PairofInteger wert : equalList) {
					if (!oneOfFact1.get(wert.getP1())
							.equals(oneOfFact2.get(wert.getP2())))
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


	// generate results of a predicate based on the facts, e.g. A(?x,?y) and
	// edb-fact
	// A(1,2)
	// result is [1,2] and return true if there are results
	private void getFacts(Predicate predicate) {
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		String kind = predicate.getKind();
		int numberOfSchemaEntries = predicate.getNumberSchemeEntries();
		for (Fact value : facts) {
			if (value.getKind().equals(kind)
					&& value.getListOfValues().size() == numberOfSchemaEntries) {
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

	}

	// stratification of the idb
	private void orderStratum(ArrayList<Rule> rules) throws Exception {
		int size = rules.size() - 1;
		Map<String, Integer> mapStratum = new HashMap<String, Integer>();

		for (Rule rule : rules) {
			String kindHead = rule.getHead().getKind();
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

		// save stratum in predicate objects
		for (Rule r : rules) {
			String head = r.getHead().getKind();
			r.getHead().setStratum(mapStratum.get(head));
		}

		sortRules(rules, mapStratum);

	}

	// generate dependency map
	// return : e.g.. [Mission2, ( Mission, latestMission, Player, latestPlayer)]
	private Map<String, ArrayList<String>> generateDependencyMap(
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

	// sort rules by stratum values and generated dependencies
	private void sortRules(ArrayList<Rule> rules, Map<String, Integer> mapStratum) {
		Map<String, ArrayList<String>> dependencyMap = generateDependencyMap(rules);
		Map<String, Integer> rankingMap = mapStratum;

		// setze default Werte von Ranking auf stratum Werte
		for (Rule r : rules) {
			int stratum = r.getHead().getStratum();
			r.getHead().setRanking(stratum);
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
	}

}