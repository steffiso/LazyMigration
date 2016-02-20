package datalog;

import java.io.StringReader;
import database.Database;
import parserFunctionsToDatalog.ParserForFunctions;
import parserGetToDatalog.ParserForGet;
import parserPutToDatalog.ParseException;
import parserPutToDatalog.ParserForPut;

public class DatalogRulesGenerator {
	
	String rules = "";
	
	public String get(String input) {

		try {
			rules = rules
					+ new ParserForGet(new StringReader(input)).getRule();
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}
	
	public String addAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(
							new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;
	}

	public String deleteAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}
	
	public String copyAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;

	}
	
	public String moveAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}
	
	public String putFact(String input){
		String fact = "";
		String jsonString = "";
		try {
			jsonString = new ParserForPut(new StringReader(input))
					.getJSONString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//put to database
		Database db = new Database();
		db.writeInJsonFile("data/EDB.json", jsonString);
		if (input.startsWith("put")){
			fact = input.substring(4, input.length()) + ".";
		}
		else
			fact = input + ".";
		return fact;
	}
	
	public String getEDBFacts() {

		Database parseToEDB = new Database();
		String edbFacts = "";
		edbFacts = parseToEDB.getEDB();
		return edbFacts;

	}
}
