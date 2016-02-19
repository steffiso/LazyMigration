package junitTests;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import lazyMigration.TopDownExecutionNew;

import org.junit.Before;
import org.junit.Test;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;

public class TopDownTest {

	ArrayList<Fact> facts = new ArrayList<Fact>();

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
		TopDownExecutionNew execute = new TopDownExecutionNew(facts);

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
		TopDownExecutionNew execute = new TopDownExecutionNew(facts);

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
		SortedMap <String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("?id", "1");
		
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
		
		TopDownExecutionNew lazy = new TopDownExecutionNew(facts, rules, goal,attributeMap);
		assertEquals("[getPlayer1([1, 'Lisa. M', 20, 5]).]",lazy.getAnswers().toString());
		
	}
	
	@Test
	public void testForOnePredicate() throws parserRuletoJava.ParseException {
		
		//test für get
		SortedMap <String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("?id", "1");
		
		ArrayList<String> schema = new ArrayList<String>();
		schema.add("1");
		schema.add("?ts");
		Predicate goal = new Predicate("getPlayer1", 2, schema);		
		
		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader( 
								"latestPlayer1(?id,?ts):- Player1(?id,?name,?score,?ts), ?ts=5." +
								"getPlayer1(?id,?ts):- latestPlayer1(?id,?ts)."))
				
				.start();
		
		TopDownExecutionNew lazy = new TopDownExecutionNew(facts, rules, goal,attributeMap);
		assertEquals("[getPlayer1([1, 5]).]",lazy.getAnswers().toString());
		
	}


}
