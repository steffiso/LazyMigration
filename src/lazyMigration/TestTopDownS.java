package lazyMigration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;

public class TestTopDownS {
	public static void main(String[] args) throws ParseException,
	 parserRuletoJava.ParseException {
	
		
	Fact ff1 = new ParserforDatalogToJava(new StringReader(
			"Player1(1,'Lisa',20, '0').")).start();
	Fact ff2 = new ParserforDatalogToJava(new StringReader(
			"Player1(2,'Homer',20, '1').")).start();
	Fact ff3 = new ParserforDatalogToJava(new StringReader(
			"Player1(1,'LisaS',20, '2')."))
			.start();
	Fact ff4 = new ParserforDatalogToJava(new StringReader(
			"Mission1(2,'find the ring2',2,'2015-12-01 18:29:50.589')."))
			.start();
	
	ArrayList<Fact> facts = new ArrayList<Fact>();
	facts.add(ff1);
	facts.add(ff2);
	facts.add(ff3);
	facts.add(ff4);

	
	//test f�r add
//	SortedMap <String, String> attributeMap = new TreeMap<String, String>();
//	attributeMap.put("?id", "1");
//	attributeMap.put("?name", "");
//	attributeMap.put("?score", "");
//	attributeMap.put("?score2", "");
//	attributeMap.put("?ts", "");
//	Predicate goal = new Predicate("getPlayer2", 5, attributeMap);		
//	
//	ArrayList<Rule> rules = new ParserRuleToJava(
//			new StringReader("legacyPlayer1(?id,?ts):-Player1(?id, ?name,?score, ?ts),Player1(?id, ?name2,?score2,?nts), ?ts < ?nts." + 
//							"latestPlayer1(?id,?ts):-Player1(?id, ?name,?score,?ts), not legacyPlayer1(?id,?ts)." + 
//							"Player2(?id,?name,?score,400,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id, ?ts)." + 
//							"legacyPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2, ?ts),Player2(?id, ?name2,?score2,?score22,?nts), ?ts < ?nts." + 
//							"latestPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2,?ts), not legacyPlayer2(?id,?ts)." +
//							"getPlayer2(?id,?name,?score,?score2,?ts):-Player2(?id, ?name,?score,?score2, ?ts), latestPlayer2(?id,?ts),?id=1."))
//			
//			.start();
	
	
	//test f�r get
	SortedMap <String, String> attributeMap = new TreeMap<String, String>();
	attributeMap.put("?id", "1");
	attributeMap.put("?name", "");
	attributeMap.put("?score", "");
	attributeMap.put("?ts", "");
	Predicate goal = new Predicate("getPlayer1", 4, attributeMap);		
	
	ArrayList<Rule> rules = new ParserRuleToJava(
			new StringReader("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name,?score,?nts), ?ts < ?nts." + 
							"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)." +
							"getPlayer1(?id,?name,?score,?ts):-Player1(?id, ?name,?score,?ts), latestPlayer1(?id,?ts),?id=1."))
			
			.start();
	
	TopDownExecution lazy = new TopDownExecution(facts, rules, goal);
	ArrayList<Fact> answers = lazy.getAnswers();
	System.out.println(answers.toString());
	}
	
	
		
}