package datalog;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;
import database.Database;
import parserFunctionsToDatalog.ParserForFunctions;
import parserGetToDatalog.ParserForGet;

public class DatalogRulesGenerator {
	
	String rules = "";

	public String addAttribute(String kind, String attribute, String value) {

		try {
			rules = rules
					+ new ParserForFunctions(
							new StringReader("add \"" + kind + "\"." + "\""
									+ attribute + "\"=\"" + value + "\""))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
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

	public String deleteAttribute(String kind, String attribute) {

		//String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader("delete \""
							+ kind + "\"." + "\"" + attribute + "\""))
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
	public String copyAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		//String rules = getEDBFacts();

		try {
			String temp = "copy \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;

	}
	
	public String copyAttribute(String input) {

		//String rules = getEDBFacts();

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

	public String moveAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		//String rules = getEDBFacts();

		try {
			String temp = "move \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}
	
	public String moveAttribute(String input) {

		//String rules = getEDBFacts();

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

	public String get(String kind, int id) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).get(kind, id);
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;
	}
	
	public String get(String input) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader(input)).getRule();
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}


	public String getAll(String kind) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).getAll(kind);
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String putKindToDatalog(String kind, String attributes) {

		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String time = ts.toString();
		attributes = attributes.substring(0, attributes.lastIndexOf(","));
		return kind + "(" + attributes + ",'"+ time +"')";

	}
	
	public void put(String kind, String attributes){
		
	}

	
	public String getEDBFacts() {

		Database parseToEDB = new Database();
		String edbFacts = "";
		edbFacts = parseToEDB.getEDB();
		return edbFacts;

	}
}
