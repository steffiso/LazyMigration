package junitTests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lazyMigration.LazyMigration;
import lazyMigration.TopDownExecution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserQueryToDatalogToJava.ParserQueryToDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import database.Database;
import datalog.Condition;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import datalog.RuleBody;
import eagerMigration.EagerMigration;

public class TopDownTest {

	ArrayList<Fact> facts = new ArrayList<Fact>();
	Database dbLazy = new Database("/data/EDBLazy.json", "/data/Schema.json");
	Database dbEager = new Database("/data/EDBEager.json", "/data/Schema.json");
	Set<Rule> rulesForTD;
	String kind;

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

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"Join1(1,?name,?title,?ts,10):-Player1(1, ?name,?score, ?ts),Mission1(?id1, ?title,1,?ts1),?ts>1."))
				.start();

		TopDownExecution execute = new TopDownExecution(facts, rules);
		List<ArrayList<String>> answers = execute.getAnswer(rules.get(0));
		// Testfall
		assertEquals("[[1, 'Lisa. M', 'find the ring', 5, 10]]",
				answers.toString());
	}

	@Test
	public void testNot() throws parserRuletoJava.ParseException {

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"Join1(?id,?name):-Player1(?id, ?name,?score, ?ts), not Mission1(?id1,'find the ring',?pid,?ts1), ?id=?pid."))
				.start();
		TopDownExecution execute = new TopDownExecution(facts, rules);
		List<ArrayList<String>> answers = execute.getAnswer(rules.get(0));

		// Testfall
		assertEquals("[[2, 'Homer']]", answers.toString());
	}

	@Test
	public void testForGet() throws parserRuletoJava.ParseException,
			parserPutToDatalog.ParseException, IOException, URISyntaxException {

		// test für get
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "1");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Player");
		ArrayList<Map<String, String>> unificationMap = new ArrayList<Map<String, String>>();
		unificationMap.add(attributeMap);

		ArrayList<String> schema = new ArrayList<String>();
		schema.add("1");
		schema.add("?name");
		schema.add("?score");
		schema.add("?ts");
		Predicate goal = new Predicate("getPlayer1", 4, schema);

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts."
								+ "latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)."
								+ "getPlayer1(?id,?name,?score,?ts):-Player1(?id, ?name,?score,?ts), latestPlayer1(?id,?ts)."))

		.start();

		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,
				unificationMap);
		assertEquals("[getPlayer1(1,'Lisa. M',20,5).]", lazy.getAnswers()
				.toString());

	}

	@Test
	public void testForOnePredicate() throws parserRuletoJava.ParseException,
			parserPutToDatalog.ParseException, IOException, URISyntaxException {

		// test für get
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "1");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Player");
		ArrayList<Map<String, String>> unificationMap = new ArrayList<Map<String, String>>();
		unificationMap.add(attributeMap);

		ArrayList<String> schema = new ArrayList<String>();
		schema.add("1");
		schema.add("?ts");
		Predicate goal = new Predicate("getPlayer1", 2, schema);

		ArrayList<Rule> rules = new ParserRuleToJava(new StringReader(
				"latestPlayer1(?id,?ts):- Player1(?id,?name,?score,?ts), ?ts=5."
						+ "getPlayer1(?id,?ts):- latestPlayer1(?id,?ts)."))

		.start();

		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,
				unificationMap);
		assertEquals("[getPlayer1(1,5).]", lazy.getAnswers().toString());

	}

	@Test
	public void testForAddandCopy() throws parserRuletoJava.ParseException,
			parserPutToDatalog.ParseException, IOException, URISyntaxException {

		// test für add, copy and get
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("value", "2");
		attributeMap.put("position", "0");
		attributeMap.put("kind", "Mission");
		ArrayList<Map<String, String>> unificationMap = new ArrayList<Map<String, String>>();
		unificationMap.add(attributeMap);

		ArrayList<String> schema = new ArrayList<String>();
		schema.add("2");
		schema.add("?title");
		schema.add("?pid");
		schema.add("?points");
		schema.add("?ts");

		Predicate goal = new Predicate("getMission2", 5, schema);

		ArrayList<Rule> rules = new ParserRuleToJava(
				new StringReader(
						"legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts."
								+ "latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts)."
								+ "latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)."
								+ "Player2(?id,?name,?score,100,?ts):-Player1(?id,?name,?score,?ts), latestPlayer1(?id,?ts)."
								+ "legacyPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts),Player2(?id,?name2,?score2,?points,?nts), ?ts < ?nts."
								+ "latestPlayer2(?id,?ts):-Player2(?id,?name,?score,?points,?ts), not legacyPlayer2(?id,?ts)."
								+ "legacyMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts),Mission1(?id,?title2,?pid2,?nts), ?ts < ?nts."
								+ "latestMission1(?id,?ts):-Mission1(?id,?title,?pid,?ts), not legacyMission1(?id,?ts)."
								+ "Mission2(?id1,?title,?pid,?points,13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1),Player2(?id2,?name,?score,?points,?ts2), latestPlayer1(?id2, ?ts2),?id2 = ?pid."
								+ "Mission2(?id1,?title,?pid,'',13):-Mission1(?id1,?title,?pid,?ts1),latestMission1(?id1, ?ts1), not Player2(?id2,?name,?score,?points,?ts2),?id2 = ?pid."
								+ "legacyMission2(?id,?ts):-Mission2(?id,?title,?pid,?points,?ts),Mission2(?id,?title2,?pid2,?score2,?nts), ?ts < ?nts."
								+ "latestMission2(?id,?ts):-Mission2(?id,?title,?pid,?points,?ts), not legacyMission2(?id,?ts)."
								+ "getMission2(?id,?title,?pid,?points,?ts):-Mission2(?id,?title,?pid,?points, ?ts), latestMission2(?id,?ts)."))

		.start();

		TopDownExecution lazy = new TopDownExecution(facts, rules, goal,
				unificationMap);
		assertEquals("[getMission2(2,'collect',2,100,13).]", lazy.getAnswers()
				.toString());

	}

	@Test
	public void testComplexExample() throws parserRuletoJava.ParseException,
			parserPutToDatalog.ParseException, IOException, URISyntaxException {

		List<String> inputs = new ArrayList<String>();
		inputs.add("add Player.points=100");
		inputs.add("put Mission(4,'go to school',1,1)");
		inputs.add("copy Player.score to Mission where Player.id=Mission.pid");
		inputs.add("get Mission.id=1");
		inputs.add("put P(1,100,1)");
		inputs.add("move Player.name to Mission where Player.id=Mission.pid");
		inputs.add("get Player.id=2");
		inputs.add("move Mission.priority to P where Mission.id=P.mid");
		inputs.add("get Mission.id=1");
		inputs.add("get Mission.id=3");
		inputs.add("get Player.id=3");
		inputs.add("get P.id=1");

		for (String uiInput : inputs)
			if (uiInput.startsWith("get")) {
				try {
					executeGetCommand(uiInput);
				} catch (parserPutToDatalog.ParseException | IOException
						| URISyntaxException | InputMismatchException
						| parserRuletoJava.ParseException e) {
					e.printStackTrace();
					return;
				}
			} else if (uiInput.startsWith("add")
					|| uiInput.startsWith("delete")
					|| uiInput.startsWith("move") || uiInput.startsWith("copy")) {
				try {
					executeCommand(uiInput);
				} catch (InputMismatchException | IOException
						| parserRuletoJava.ParseException e) {
					e.printStackTrace();
					return;
				}
			} else if (uiInput.startsWith("put")) {
				executePutCommand(uiInput);
			}

		assertEquals("Player1(1,'Lisa',20,1).\n" + "Player1(2,'Bart',20,2).\n"
				+ "Player1(3,'Homer',20,3).\n"
				+ "Mission1(1,'go to library',1,1,4).\n"
				+ "Mission1(2,'go to work',0,20,5).\n"
				+ "Mission1(3,'visit Moe',1,2,6).\n"
				+ "Player1(1,'Lisa S.',40,7).\n"
				+ "Player2(2,'Bart',20,100,8).\n"
				+ "Player2(3,'Homer',20,100,8).\n"
				+ "Player2(1,'Lisa S.',40,100,8).\n"
				+ "Mission1(4,'go to school',1,1,9).\n"
				+ "Mission2(1,'go to library',1,1,40,10).\n"
				+ "Mission2(3,'visit Moe',1,2,20,10).\n"
				+ "Mission2(4,'go to school',1,1,40,10).\n"
				+ "Mission2(2,'go to work',0,20,null,10).\n"
				+ "Player3(2,'Bart',20,100,10).\n"
				+ "Player3(3,'Homer',20,100,10).\n"
				+ "Player3(1,'Lisa S.',40,100,10).\n" + "P1(1,100,1,12).\n"
				+ "Mission3(1,'go to library',1,1,40,'Lisa S.',13).\n"
				+ "Mission3(3,'visit Moe',1,2,20,'Bart',13).\n"
				+ "Mission3(4,'go to school',1,1,40,'Lisa S.',13).\n"
				+ "Mission3(2,'go to work',0,20,null,null,13).\n"
				+ "Player4(2,20,100,13).\n" + "Player4(3,20,100,13).\n"
				+ "Player4(1,40,100,13).\n" + "P2(1,100,1,1,15).\n"
				+ "Mission4(1,'go to library',1,40,'Lisa S.',15).\n"
				+ "Mission4(3,'visit Moe',2,20,'Bart',15).\n"
				+ "Mission4(4,'go to school',1,40,'Lisa S.',15).\n"
				+ "Mission4(2,'go to work',20,null,null,15).\n",
				dbEager.getEDB());

		assertEquals("Player1(1,'Lisa',20,1).\n" + "Player1(2,'Bart',20,2).\n"
				+ "Player1(3,'Homer',20,3).\n"
				+ "Mission1(1,'go to library',1,1,4).\n"
				+ "Mission1(2,'go to work',0,20,5).\n"
				+ "Mission1(3,'visit Moe',1,2,6).\n"
				+ "Player1(1,'Lisa S.',40,7).\n"
				+ "Mission1(4,'go to school',1,1,9).\n"
				+ "Player2(1,'Lisa S.',40,100,8).\n"
				+ "Mission2(1,'go to library',1,1,40,10).\n"
				+ "P1(1,100,1,12).\n" + "Player2(2,'Bart',20,100,8).\n"
				+ "Player3(2,'Bart',20,100,10).\n" + "Player4(2,20,100,13).\n"
				+ "Player3(1,'Lisa S.',40,100,10).\n"
				+ "Mission3(1,'go to library',1,1,40,'Lisa S.',13).\n"
				+ "Mission4(1,'go to library',1,40,'Lisa S.',15).\n"
				+ "Mission2(3,'visit Moe',1,2,20,10).\n"
				+ "Mission3(3,'visit Moe',1,2,20,'Bart',13).\n"
				+ "Mission4(3,'visit Moe',2,20,'Bart',15).\n"
				+ "Player2(3,'Homer',20,100,8).\n"
				+ "Player3(3,'Homer',20,100,10).\n" + "Player4(3,20,100,13).\n"
				+ "P2(1,100,1,1,15).\n", dbLazy.getEDB());

	}

	@Test
	public void testNewCopy() throws parserRuletoJava.ParseException,
			parserPutToDatalog.ParseException, IOException, URISyntaxException {

		List<String> inputs = new ArrayList<String>();
		inputs.add("add Player.points=100");
		inputs.add("copy Player.points to Mission where Player.id=Mission.pid");
		inputs.add("put Player(1,\"Lisa.S\",20,200)");
		inputs.add("get Mission.id=1");

		for (String uiInput : inputs)
			if (uiInput.startsWith("get")) {
				try {
					executeGetCommand(uiInput);
				} catch (parserPutToDatalog.ParseException | IOException
						| URISyntaxException | InputMismatchException
						| parserRuletoJava.ParseException e) {
					e.printStackTrace();
					return;
				}
			} else if (uiInput.startsWith("add")
					|| uiInput.startsWith("delete")
					|| uiInput.startsWith("move") || uiInput.startsWith("copy")) {
				try {
					executeCommand(uiInput);
				} catch (InputMismatchException | IOException
						| parserRuletoJava.ParseException e) {
					e.printStackTrace();
					return;
				}
			} else if (uiInput.startsWith("put")) {
				executePutCommand(uiInput);
			}

		assertEquals("Player1(1,'Lisa',20,1).\n" + "Player1(2,'Bart',20,2).\n"
				+ "Player1(3,'Homer',20,3).\n"
				+ "Mission1(1,'go to library',1,1,4).\n"
				+ "Mission1(2,'go to work',0,20,5).\n"
				+ "Mission1(3,'visit Moe',1,2,6).\n"
				+ "Player1(1,'Lisa S.',40,7).\n"
				+ "Player2(2,'Bart',20,100,8).\n"
				+ "Player2(3,'Homer',20,100,8).\n"
				+ "Player2(1,'Lisa S.',40,100,8).\n"
				+ "Mission2(1,'go to library',1,1,100,9).\n"
				+ "Mission2(3,'visit Moe',1,2,100,9).\n"
				+ "Mission2(2,'go to work',0,20,null,9).\n"
				+ "Player3(2,'Bart',20,100,9).\n"
				+ "Player3(3,'Homer',20,100,9).\n"
				+ "Player3(1,'Lisa S.',40,100,9).\n"
				+ "Player3(1,'Lisa.S',20,200,11).\n", dbEager.getEDB());

		assertEquals("Player1(1,'Lisa',20,1).\n" + "Player1(2,'Bart',20,2).\n"
				+ "Player1(3,'Homer',20,3).\n"
				+ "Mission1(1,'go to library',1,1,4).\n"
				+ "Mission1(2,'go to work',0,20,5).\n"
				+ "Mission1(3,'visit Moe',1,2,6).\n"
				+ "Player1(1,'Lisa S.',40,7).\n"
				+ "Player3(1,'Lisa.S',20,200,11).\n"
				+ "Player2(1,'Lisa S.',40,100,8).\n"
				+ "Mission2(1,'go to library',1,1,100,9).\n", dbLazy.getEDB());

	}

	private void executeCommand(String uiInput) throws InputMismatchException,
			IOException, parserRuletoJava.ParseException {
		ArrayList<Rule> rulesTemp = null;
		String rulesStr = "";
		if (rulesForTD == null)
			rulesForTD = new HashSet<Rule>();
		try {
			ParserQueryToDatalogToJava parserQuery = new ParserQueryToDatalogToJava(
					new StringReader(uiInput));
			rulesTemp = parserQuery.getJavaRules(dbLazy);
			rulesForTD.addAll(rulesTemp);
			rulesStr = parserQuery.getRules();

		} catch (parserQueryToDatalogToJava.ParseException e) {
			e.printStackTrace();
			return;
		}
		executeQueryBU(rulesStr, uiInput);

	}

	private void executeGetCommand(String uiInput)
			throws parserPutToDatalog.ParseException, IOException,
			URISyntaxException, InputMismatchException,
			parserRuletoJava.ParseException {
		String id = "";
		ParserQueryToDatalogToJava parserget = new ParserQueryToDatalogToJava(
				new StringReader(uiInput));
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		if (rulesForTD != null)
			rulesTemp = copyRules();

		try {
			rulesTemp.addAll(parserget.getJavaRules(dbLazy));
		} catch (parserQueryToDatalogToJava.ParseException e) {
			e.printStackTrace();
			return;
		}
		kind = parserget.getKind();
		id = parserget.getId();
		// start lazy migration
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("kind", kind);
		attributeMap.put("position", "0");
		attributeMap.put("value", id);
		ArrayList<Map<String, String>> uniMap = new ArrayList<Map<String, String>>();
		uniMap.add(attributeMap);
		ArrayList<String> schema;
		Predicate goal;
		try {
			schema = dbLazy.getLatestSchema(kind).getAttributes();

			schema.add("?ts");

			goal = new Predicate("get" + kind
					+ dbLazy.getLatestSchemaVersion(kind), schema.size(),
					schema);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		executeQueryTD(rulesTemp, goal, uniMap);
		executeQueryBU(parserget.getRules(), uiInput);
	}

	private ArrayList<Rule> copyRules() {
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		for (Rule r : rulesForTD) {
			@SuppressWarnings("unchecked")
			ArrayList<String> scheme = (ArrayList<String>) r.getHead()
					.getScheme().clone();
			Predicate newHead = new Predicate(r.getHead().getKind(), r
					.getHead().getNumberSchemeEntries(), scheme);
			newHead.setHead(r.getHead().isHead());
			ArrayList<Predicate> predicates = new ArrayList<Predicate>();
			for (Predicate p : r.getPredicates()) {
				@SuppressWarnings("unchecked")
				ArrayList<String> scheme2 = (ArrayList<String>) p.getScheme()
						.clone();
				Predicate p2 = new Predicate(p.getKind(),
						p.getNumberSchemeEntries(), scheme2);
				p2.setNot(p.isNot());
				predicates.add(p2);
			}
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			for (Condition c : r.getConditions())
				conditions.add(new Condition(c.getLeftOperand(), c
						.getRightOperand(), c.getOperator()));
			RuleBody newRuleBody = new RuleBody(predicates, conditions);
			Rule rNew = new Rule(newHead, newRuleBody);

			rulesTemp.add(rNew);
		}
		return rulesTemp;
	}

	private void executeQueryBU(String rulesStr, String uiInput)
			throws InputMismatchException, JsonParseException,
			JsonMappingException, IOException {

		String[] edbFacts;
		try {
			edbFacts = dbEager.getEDB().split("\n");

		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		String query = uiInput;
		ArrayList<Rule> rules = null;
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (String factString : edbFacts) {
			try {
				facts.add(new ParserforDatalogToJava(new StringReader(
						factString)).start());
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			rules = new ParserRuleToJava(new StringReader(rulesStr)).start();
		} catch (parserRuletoJava.ParseException e) {
			e.printStackTrace();
			return;
		}

		EagerMigration migrate = new EagerMigration(facts, rules, query);

		try {
			migrate.writeAnswersInDatabase();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

	}

	private void executeQueryTD(ArrayList<Rule> rulesTemp, Predicate goal,
			ArrayList<Map<String, String>> uniMap)
			throws parserPutToDatalog.ParseException, IOException,
			URISyntaxException {

		String[] edbFacts;
		try {
			edbFacts = dbLazy.getEDB().split("\n");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (String factString : edbFacts) {
			try {
				facts.add(new ParserforDatalogToJava(new StringReader(
						factString)).start());
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
		}
		LazyMigration migrate = new LazyMigration(facts, rulesTemp, goal,
				uniMap);
		migrate.writeAnswersInDatabase();
	}

	private void executePutCommand(String uiInput) {

		// put to database
		try {
			dbEager.putToDatabase(uiInput);
			dbLazy.putToDatabase(uiInput);
		} catch (parserPutToDatalog.ParseException | IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@After
	public void resetDB() {
		try {
			dbLazy.resetDatabaseState();
			dbEager.resetDatabaseState();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
