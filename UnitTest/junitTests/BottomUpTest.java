package junitTests;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import datalog.Fact;
import datalog.Rule;
import eagerMigration.BottomUpExecution;

public class BottomUpTest {
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
	public void test() throws parserRuletoJava.ParseException {

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"Join1(?id,?name,?title,?ts,10):-Player1(?id, ?name,?score, ?ts),Mission1(?id1, ?title,?pid,?ts1),?id=?pid,?ts>1."
								+ "Join2(?id1,?name,?score,?ts):-Player1(?id1, ?name,?score, ?ts), not Join1(?id2,?name2,?title2,?ts,?nb), ?id1=?id2."))
				.start();
		BottomUpExecution execute = new BottomUpExecution(facts, rules);
		try {
			execute.generateAllRules();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Testfall 1
		assertEquals(
				"[[2, 'Homer', 'collect', 2, 10], [1, 'Lisa. M', 'find the ring', 5, 10]]",
				execute.getResultsOfKind("Join1", 5).toString());

		// Testfall 2
		assertEquals("[[1, 'Lisa', 20, 1]]", execute.getResultsOfKind("Join2", 4)
				.toString());

	}
}
