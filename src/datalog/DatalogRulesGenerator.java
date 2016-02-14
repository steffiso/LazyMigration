package datalog;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;
import database.Database;
import parserFunctionsToDatalog.ParserForFunctions;
import parserGetToDatalog.ParserForGet;
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
	
	public String getEDBFacts() {

		Database parseToEDB = new Database();
		String edbFacts = "";
		edbFacts = parseToEDB.getEDB();
		return edbFacts;

	}
}
