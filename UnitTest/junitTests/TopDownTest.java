package junitTests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lazyMigration.TopDownExecution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import database.Database;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class TopDownTest {

	ArrayList<Fact> facts = new ArrayList<Fact>();
	Database db=new Database("/data/EDBLazy.json", "/data/Schema.json");

	@Before
	public void setUp() throws ParseException {
		Fact f1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',20,1).")).start();
		Fact f2 = new ParserforDatalogToJava(new StringReader(
				"Player1(2,'Homer',20,2).")).start();
		Fact f3 = new ParserforDatalogToJava(new StringReader(
				"Mission1(1,'find the ring',1,3).")).start();
		Fact f4 = new ParserforDatalogToJava(new StringReader(
				"Mission1(2,'collect',2,4).")).start();
		Fact f5 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa. M',20,5).")).start();
		facts.add(f1);
		facts.add(f2);
		facts.add(f3);
		facts.add(f4);
		facts.add(f5);
	}
	
	@Test
	public void testJoin() throws parserRuletoJava.ParseException {
		TopDownExecution execute = new TopDownExecution(facts);

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"Join1(1,?name,?title,?ts,10):-Player1(1, ?name,?score, ?ts),Mission1(?id1, ?title,1,?ts1),?ts>1."))
				.start();
		List<ArrayList<String>> answers=execute.getAnswer(rules.get(0));

		// Testfall
		assertEquals(
				"[[1, 'Lisa. M', 'find the ring', 5, 10]]",
				answers.toString());
	}
	
	@Test
	public void testNot() throws parserRuletoJava.ParseException {
		TopDownExecution execute = new TopDownExecution(facts);

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"Join1(?id,?name):-Player1(?id, ?name,?score, ?ts), not Mission1(?id1,'find the ring',?pid,?ts1), ?id=?pid."))
				.start();
		List<ArrayList<String>> answers=execute.getAnswer(rules.get(0));

		// Testfall
		assertEquals("[[2, 'Homer']]",answers.toString());
	}
	
	@Test
	public void testForGet() throws parserRuletoJava.ParseException {
		
		//test für get
		Map <String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "1");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Player");
		ArrayList<Map<String,String>> unificationMap=new ArrayList<Map<String,String>>();
		unificationMap.add(attributeMap);
		
		ArrayList<String> schema = new ArrayList<String>();
		schema.add("1");
		schema.add("?name");
		schema.add("?score");
		schema.add("?ts");
		Predicate goal = new Predicate("getPlayer1", 4, schema);		
		
		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts." + 
								"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)." +
								"getPlayer1(?id,?name,?score,?ts):-Player1(?id, ?name,?score,?ts), latestPlayer1(?id,?ts)."))
				
				.start();
		
		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,unificationMap);
		assertEquals("[getPlayer1(1,'Lisa. M',20,5).]",lazy.getAnswers().toString());
		
	}
	
	@Test
	public void testForOnePredicate() throws parserRuletoJava.ParseException {
		
		//test für get
		Map <String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "1");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Player");
		ArrayList<Map<String,String>> unificationMap=new ArrayList<Map<String,String>>();
		unificationMap.add(attributeMap);
		
		ArrayList<String> schema = new ArrayList<String>();
		schema.add("1");
		schema.add("?ts");
		Predicate goal = new Predicate("getPlayer1", 2, schema);		
		
		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader( 
								"latestPlayer1(?id,?ts):- Player1(?id,?name,?score,?ts), ?ts=5." +
								"getPlayer1(?id,?ts):- latestPlayer1(?id,?ts)."))
				
				.start();
		
		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,unificationMap);
		assertEquals("[getPlayer1(1,5).]",lazy.getAnswers().toString());
		
	}

	@Test
	public void testForAddandCopy() throws parserRuletoJava.ParseException {
		
		//test für add, copy and get
		Map <String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "2");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Mission");
		ArrayList<Map<String,String>> unificationMap=new ArrayList<Map<String,String>>();
		unificationMap.add(attributeMap);
		
		ArrayList<String> schema = new ArrayList<String>();
		schema.add("2");
		schema.add("?title");
		schema.add("?pid");
		schema.add("?points");
		schema.add("?ts");
		
		Predicate goal = new Predicate("getMission2", 5, schema);		
		
		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts." +
								"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)." +
								"latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)."+
								"Player2(?id,?name,?score,100,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id,?ts)."+
								"legacyPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts),Player2(?id,?name2,?score2,?points,?nts), ?ts < ?nts." +
								"latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)." +
								"legacyMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts),Mission1(?id,?title2,?pid2,?nts), ?ts < ?nts."+
								"latestMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts), not legacyMission1(?id,?ts)."+
								"Mission2(?id1,?title,?pid,?points,13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1),Player2(?id2,?name,?score,?points,?ts2), latestPlayer1(?id2, ?ts2),?id2 = ?pid."+
								"Mission2(?id1,?title,?pid,'',13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1), not Player2(?id2,?name,?score,?points,?ts2),?id2 = ?pid."+							
								"legacyMission2(?id,?ts):-Mission2(?id,?title,?pid,?points,?ts),Mission2(?id,?title2,?pid2,?score2,?nts), ?ts < ?nts."+
								"latestMission2(?id,?ts):-Mission2(?id,?title,?pid,?points,?ts), not legacyMission2(?id,?ts)."+
								"getMission2(?id,?title,?pid,?points,?ts):-Mission2(?id,?title,?pid,?points, ?ts), latestMission2(?id,?ts)."))
				
				.start();
		
		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,unificationMap);
		assertEquals("[getMission2(2,'collect',2,100,13).]",lazy.getAnswers().toString());
		
	}	
	
	@After
	public void resetDB(){
		try {
			db.resetDatabaseState();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
