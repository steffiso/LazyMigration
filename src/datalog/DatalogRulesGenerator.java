package datalog;

import java.io.StringReader;
import java.util.ArrayList;

import database.Database;
import parserPutToDatalog.ParseException;
import parserPutToDatalog.ParserForPut;
import parserQueryToDatalogToJava.ParserQueryToDatalogToJava;
import datalog.Rule;

public class DatalogRulesGenerator {

	private Database db;
	
	public DatalogRulesGenerator(Database db){
		this.db = db;
	}
	// return the generated Datalog rules in one String for 
	// get, add, delete, copy or move
	// e.g. String input = "add Player.points=20"
	public String getRules(String input) {
		String rules = "";

		String[] splitString = input.split("\n");
		for (int i = 0; i < splitString.length; i++) {
			try {
				rules = rules
						+ new ParserQueryToDatalogToJava(new StringReader(
								splitString[i])).getDatalogRules(db);
			} catch (parserQueryToDatalogToJava.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return rules;
	}

	// return the generated Datalog rules in ArrayList<Rule> for 
	// get, add, delete, copy or move
	// e.g. String input = "add Player.points=20"
	public ArrayList<Rule> getJavaRules(String input) {
		ArrayList<Rule> rules = new ArrayList<Rule>();

		String[] splitString = input.split("\n");
		for (int i = 0; i < splitString.length; i++) {
			try {
				rules.addAll((new ParserQueryToDatalogToJava(
						new StringReader(splitString[i]))).getJavaRules(db));
			} catch (parserQueryToDatalogToJava.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return rules;
	}

	public String[] getTD(String input) {
		String rules = "";
		String kind = "";
		String id = "";
		ParserQueryToDatalogToJava parserget = new ParserQueryToDatalogToJava(new StringReader(
				input));
		try {
			rules = rules + parserget.getJavaRules(db);
		} catch (parserQueryToDatalogToJava.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		kind = parserget.kindStr;
		id = parserget.idStr;
		return new String[] { rules, kind, id };

	}

	// writes a fact to database
	// input e.g. "put Player(4,'Maggie',30)
	public String putFact(String input) {
		String fact = "";
		String jsonString = "";
		try {
			jsonString = new ParserForPut(new StringReader(input))
					.getJSONString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// put to database
		db.writeInJsonFile(db.getFilenameEDB(), jsonString);
		if (input.startsWith("put")) {
			fact = input.substring(4, input.length()) + ".";
		} else
			fact = input + ".";
		return fact;
	}

	// returns all edb facts from database in one string
	public String getEDBFacts() {
		Database parseToEDB = new Database();
		String edbFacts = "";
		edbFacts = parseToEDB.getEDB();
		return edbFacts;

	}

	// returns all edb facts from database in one json-like string
	public String getJsonFacts() {
		Database parseToEDB = new Database();
		String edbFacts = "";
		edbFacts = parseToEDB.getJson();
		return edbFacts;

	}
}
