package lazyMigration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import database.Database;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserQueryToDatalogToJava.ParserQueryToDatalogToJava;

public class TestTopDownSNew {
	public static void main(String[] args) throws ParseException,
			parserRuletoJava.ParseException,
			parserQueryToDatalogToJava.ParseException {

		Fact ff1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',20, 1).")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player1(2,'Homer',60, 2).")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'LisaS',20, 3).")).start();
		Fact ff4 = new ParserforDatalogToJava(new StringReader(
				"Mission1(1,'find the ring1',2,4).")).start();
		Fact ff5 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'LisaS',20,5).")).start();
		Fact ff6 = new ParserforDatalogToJava(new StringReader(
				"Mission1(2,'find the ring2',1,6).")).start();
		Fact ff7 = new ParserforDatalogToJava(new StringReader(
				"Mission1(3,'find the ring3',1,7).")).start();

		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(ff1);
		facts.add(ff2);
		facts.add(ff3);
		facts.add(ff4);
		facts.add(ff5);
		facts.add(ff6);
		facts.add(ff7);

		// test für add + get
		Map<String, String> attributeMapAdd = new TreeMap<String, String>();
		attributeMapAdd.put("value", "1");
		attributeMapAdd.put("position", "0");
		attributeMapAdd.put("kind", "Player");
		ArrayList<Map<String, String>> unificationMapAdd = new ArrayList<Map<String, String>>();
		unificationMapAdd.add(attributeMapAdd);

		ArrayList<String> schemaAdd = new ArrayList<String>();
		schemaAdd.add("?id");
		schemaAdd.add("?name");
		schemaAdd.add("?score");
		schemaAdd.add("?points");

		Predicate goalAdd = new Predicate("getPlayer2", 5, schemaAdd);

		String inputAdd = "add  Player.points=\"4444\"\nget Player.id=\"1\"";

		String[] inputSplitAdd = inputAdd.split("\n");
		ArrayList<Rule> rulesAdd = new ArrayList<Rule>();

		// funktioniert so nicht mehr
		/*
		 * ArrayList<Rule> rules = new ParserRuleToJava( new StringReader(
		 * "legacyPlayer1(?id,?ts):-Player1(?id, ?name,?score, ?ts),Player1(?id, ?name2,?score2,?nts), ?ts < ?nts."
		 * +
		 * "latestPlayer1(?id,?ts):-Player1(?id, ?name,?score,?ts), not legacyPlayer1(?id,?ts)."
		 * +
		 * "Player2(?id,?name,?score,400,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id, ?ts)."
		 * +
		 * "legacyPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2, ?ts),Player2(?id, ?name2,?score2,?score22,?nts), ?ts < ?nts."
		 * +
		 * "latestPlayer2(?id,?ts):-Player2(?id, ?name,?score,?score2,?ts), not legacyPlayer2(?id,?ts)."
		 * +
		 * "getPlayer2(?id,?name,?score,?score2,?ts):-Player2(?id, ?name,?score,?score2, ?ts), latestPlayer2(?id,?ts)."
		 * ))
		 * 
		 * .start();
		 */

		// test für add, move und get
		Map<String, String> attributeMapAddMoveOld = new TreeMap<String, String>();
		attributeMapAddMoveOld.put("value", "1");
		attributeMapAddMoveOld.put("position", "0");
		attributeMapAddMoveOld.put("kind", "Player");
		ArrayList<Map<String, String>> unificationMapAddMoveOld = new ArrayList<Map<String, String>>();
		unificationMapAddMoveOld.add(attributeMapAddMoveOld);

		ArrayList<String> schemaAddMoveOld = new ArrayList<String>();
		schemaAddMoveOld.add("?id");
		schemaAddMoveOld.add("?score");
		schemaAddMoveOld.add("?points");
		schemaAddMoveOld.add("?ts");

		Predicate goalAddMoveOld = new Predicate("getPlayer3",
				schemaAddMoveOld.size(), schemaAddMoveOld);

		// test für add, move und get
		Map<String, String> attributeMapAddMove = new TreeMap<String, String>();
		attributeMapAddMove.put("value", "1");
		attributeMapAddMove.put("position", "0");
		attributeMapAddMove.put("kind", "Mission");
		ArrayList<Map<String, String>> unificationMapAddMove = new ArrayList<Map<String, String>>();
		unificationMapAddMove.add(attributeMapAddMove);

		ArrayList<String> schemaAddMove = new ArrayList<String>();
		schemaAddMove.add("?id");
		schemaAddMove.add("?title");
		schemaAddMove.add("?pid");
		schemaAddMove.add("?name");
		schemaAddMove.add("?ts");

		Predicate goalAddMove = new Predicate("getMission2",
				schemaAddMove.size(), schemaAddMove);
		// String inputAddMove =
		// "add \"Player\".\"points\"=\"4444\"\nmove \"Player\".\"name\" to \"Mission\" where \"Player\".\"id\"=\"Mission\".\"pid\"\nget \"Player\".\"id\"=\"1\"";
		String inputAddMove = "add Player.points=4444\nmove Player.name to Mission where Player.id=Mission.pid\nget Mission.id=1";

		String[] inputSplitAddMove = inputAddMove.split("\n");
		ArrayList<Rule> rulesAddMove = new ArrayList<Rule>();

		// funktioniert so nicht mehr
		/*
		 * ArrayList<Rule> rules = new ParserRuleToJava( new StringReader(
		 * "legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts."
		 * +
		 * "latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)."
		 * +
		 * "*Player2(?id,?name,?score,100,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id,?ts)."
		 * +
		 * "legacyPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts),Player2(?id,?name2,?score2,?points2,?nts), ?ts < ?nts."
		 * +
		 * "latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)."
		 * +
		 * "legacyMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts),Mission1(?id,?title2,?pid2,?nts), ?ts < ?nts."
		 * +
		 * "latestMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts), not legacyMission1(?id,?ts)."
		 * +
		 * "*Mission2(?id1,?title,?pid,?name,13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1),Player2(?id2,?name,?score,?points,?ts2), latestPlayer1(?id2, ?ts2),?id2 = ?pid."
		 * +
		 * "*Mission2(?id1,?title,?pid,'',13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1), not Player2(?id2,?name,?score,?points,?ts2),?id2 = ?pid."
		 * +
		 * "*Player3(?id,?score,?points,?ts):-Player2(?id,?name,?score,?points,?ts), latestPlayer2(?id,?ts)."
		 * +
		 * "legacyPlayer3(?id,?ts):-Player3(?id,?score,?points,?ts),Player3(?id,?score2,?points2,?nts), ?ts < ?nts."
		 * +
		 * "latestPlayer3(?id,?ts):-Player3(?id,?score,?points,?ts), not legacyPlayer3(?id,?ts)."
		 * +
		 * "getPlayer3(?id,?score,?points,?ts):-Player3(?id,?score,?points,?ts), latestPlayer3(?id,?ts)."
		 * +
		 * "legacyMission2(?id,?ts):-Mission2(?id,?title,?pid,?name,?ts),Mission2(?id,?title2,?pid2,?name2,?nts), ?ts < ?nts."
		 * +
		 * "latestMission2(?id,?ts):-Mission2(?id,?title,?pid,?name,?ts), not legacyMission2(?id,?ts)."
		 * +
		 * "getMission2(?id,?title,?pid,?name,?ts):-Mission2(?id,?title,?pid,?name,?ts), latestMission2(?id,?ts)."
		 * ))
		 * 
		 * .start();
		 */

		// Durchlauf für Add points + Get Player 1
		/*
		 * for (int i = 0; i< inputSplitAdd.length; i++){ rulesAdd.addAll((new
		 * ParserQueryToDatalogToJava(new
		 * StringReader(inputSplitAdd[i]))).getJavaRules()); }
		 * 
		 * TopDownExecutionNew lazyAdd = new TopDownExecutionNew(facts,
		 * rulesAdd, goalAdd ,unificationMapAdd); ArrayList<Fact> answersAdd =
		 * lazyAdd.getAnswers(); System.out.println(answersAdd.toString());
		 */

		// Durchlauf für Add points + Move name + get Player 1
		Database db = new Database("src/data/EDBLazy.json","src/data/Schema.json");
		for (int i = 0; i < inputSplitAddMove.length; i++) {
			rulesAddMove.addAll((new ParserQueryToDatalogToJava(
					new StringReader(inputSplitAddMove[i]))).getJavaRules(db));
		}
		System.out.println(rulesAddMove.toString());
		
		TopDownExecutionNew lazyAddMove = new TopDownExecutionNew(facts,
				rulesAddMove, goalAddMove, unificationMapAddMove);
		ArrayList<Fact> answersAddMove = lazyAddMove.getAnswers();
		System.out.println(answersAddMove.toString());
	}

}
