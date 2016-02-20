package lazyMigration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;

public class TestTopDownSNew {
	public static void main(String[] args) throws ParseException,
	 parserRuletoJava.ParseException {
	
		
	Fact ff1 = new ParserforDatalogToJava(new StringReader(
			"Player1(1,'Lisa',20, 1).")).start();
	Fact ff2 = new ParserforDatalogToJava(new StringReader(
			"Player1(2,'Homer',20, 2).")).start();
	Fact ff3 = new ParserforDatalogToJava(new StringReader(
			"Player1(1,'LisaS',20, 3)."))
			.start();
	Fact ff4 = new ParserforDatalogToJava(new StringReader(
			"Mission1(1,'find the ring1',2,4)."))
			.start();
	Fact ff5 = new ParserforDatalogToJava(new StringReader(
			"Player1(1,'LisaS',20,5)."))
			.start();
	Fact ff6 = new ParserforDatalogToJava(new StringReader(
			"Mission1(2,'find the ring2',2,6)."))
			.start();
	Fact ff7 = new ParserforDatalogToJava(new StringReader(
			"Mission1(3,'find the ring3',2,7)."))
			.start();
	
	ArrayList<Fact> facts = new ArrayList<Fact>();
	facts.add(ff1);
	facts.add(ff2);
	facts.add(ff3);
	facts.add(ff4);
	facts.add(ff5);
	facts.add(ff6);
	facts.add(ff7);

	//test für get
//	SortedMap <String, String> attributeMap = new TreeMap<String, String>();
//	attributeMap.put("?id", "1");
//	
//	ArrayList<String> schema = new ArrayList<String>();
//	schema.add("1");
//	schema.add("?name");
//	schema.add("?score");
//	schema.add("?ts");
//	Predicate goal = new Predicate("Player1", 4, schema);		
//	
//	ArrayList<Rule> rules = new ParserRuleToJava(
//			new StringReader("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts." + 
//							"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)." +
//							"Player1(?id,?name,?score,?ts):-Player1(?id, ?name,?score,?ts), latestPlayer1(?id,?ts)."))
//			
//			.start();
	
	//test für add + get
	SortedMap <String, String> attributeMap = new TreeMap<String, String>();
	attributeMap.put("?id", "2");
	
	ArrayList<String> schema = new ArrayList<String>();
	schema.add("?id");
	schema.add("?name");
	schema.add("?score");
	schema.add("?score2");
	
	Predicate goal = new Predicate("getPlayer2", 5, schema);		
	
	ArrayList<Rule> rules = new ParserRuleToJava(
			new StringReader("legacyPlayer1(?id,?ts):-Player1(?id, ?name,?score, ?ts),Player1(?id, ?name2,?score2,?nts), ?ts < ?nts." + 
							"latestPlayer1(?id,?ts):-Player1(?id, ?name,?score,?ts), not legacyPlayer1(?id,?ts)." + 
							"Player2(?id,?name,?score,400,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id, ?ts)." + 
							"legacyPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2, ?ts),Player2(?id, ?name2,?score2,?score22,?nts), ?ts < ?nts." + 
							"latestPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2,?ts), not legacyPlayer2(?id,?ts)." +
							"getPlayer2(?id,?name,?score,?score2,?ts):-Player2(?id, ?name,?score,?score2, ?ts), latestPlayer2(?id,?ts)."))
			
			.start();
	

	//test für add, copy and get
//	SortedMap <String, String> attributeMap = new TreeMap<String, String>();
//	attributeMap.put("?id", "3");
//	
//	ArrayList<String> schema = new ArrayList<String>();
//	schema.add("?id");
//	schema.add("?title");
//	schema.add("?pid");
//	schema.add("?points");
//	schema.add("?ts");
//	
//	Predicate goal = new Predicate("getMission2", 5, schema);		
//	
//	ArrayList<Rule> rules = new ParserRuleToJava(
//			new StringReader("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts." +
//							"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)." +
//							"Player2(?id,?name,?score,333,12):-Player1(?id,?name,?score,?ts), latestPlayer1(?id, ?ts)." +
//							"legacyPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts),Player2(?id,?name2,?score2,?points2,?nts), ?ts < ?nts."+
//							"latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)."+
//							"legacyMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts),Mission1(?id,?title2,?pid2,?nts), ?ts < ?nts."+
//							"latestMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts), not legacyMission1(?id,?ts)."+
//							"Mission2(?id,?title,?pid,?points,13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1),Player2(?id2,?name,?score,?points,?ts2), latestPlayer2(?id2, ?ts2),?id2 = ?pid."+
//							"Mission2(?id,?title,?pid,'',13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1), not Player2(?id2,?name,?score,?points,?ts2),?id2 = ?pid."+							
//							"legacyMission2(?id,?ts):-Mission2(?id1,?title,?pid,?points,?ts),Mission2(?id12,?title2,?pid2,?points2,?nts), ?ts < ?nts."+
//							"latestMission2(?id,?ts):-Mission2(?id1,?title,?pid,?points,?ts), not legacyMission2(?id,?ts)."+
//							"getMission2(?id,?title,?pid,?points,?ts):-Mission2(?id,?title,?pid,?points, ?ts), latestMission2(?id,?ts)."))
//			
//			.start();
	
	TopDownExecutionNew lazy = new TopDownExecutionNew(facts, rules, goal,attributeMap);
	ArrayList<Fact> answers = lazy.getAnswers();
	System.out.println(answers.toString());
	}
	
	
		
}
