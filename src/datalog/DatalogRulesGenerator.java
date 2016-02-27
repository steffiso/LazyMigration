package datalog;

import java.io.StringReader;
import database.Database;
import parserGetToDatalog.ParserForGet;
import parserPutToDatalog.ParseException;
import parserPutToDatalog.ParserForPut;
import parserQueryToDatalogToJava.ParserQueryToDatalogToJava;

public class DatalogRulesGenerator {
	
	public String getRules(String input){
		String rules = "";
		
		String[] splitString = input.split("\n");
		for (int i = 0; i < splitString.length; i++){
				try {
					rules = rules
							+ new ParserQueryToDatalogToJava(new StringReader(splitString[i]))
									.getDatalogRules();
				} catch (parserQueryToDatalogToJava.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
		}
		
		return rules;
	}
	
//	public String get(String input) {
//		String rules = "";
//		try {
//			rules = rules
//					+ new ParserForGet(new StringReader(input)).getRule();
//		} catch (parserGetToDatalog.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return rules;
//
//	}
	
	public String[] getTD(String input) {
		String rules = "";
		String kind = "";
		String id = "";
		try {
			ParserForGet parserget=	new ParserForGet(new StringReader(input));
			rules=rules+parserget.getRule();
			kind=parserget.kindStr;
			id=parserget.idStr;
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[]{rules,kind,id};

	}
	
//	public String addAttribute(String input) {
//		String rules = "";
//		try {
//			rules = rules
//					+ new ParserForFunctions(
//							new StringReader(input))
//							.getFunctionRule();
//		} catch (parserFunctionsToDatalog.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return rules;
//	}
//
//	public String deleteAttribute(String input) {
//		String rules = "";
//		try {
//			rules = rules
//					+ new ParserForFunctions(new StringReader(input))
//							.getFunctionRule();
//		} catch (parserFunctionsToDatalog.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return rules;
//
//	}
//	
//	public String copyAttribute(String input) {
//		String rules = "";
//		try {
//			rules = rules
//					+ new ParserForFunctions(new StringReader(input))
//							.getFunctionRule();
//		} catch (parserFunctionsToDatalog.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return rules;
//
//	}
//	
//	public String moveAttribute(String input) {
//		String rules = "";
//		try {
//			rules = rules
//					+ new ParserForFunctions(new StringReader(input))
//							.getFunctionRule();
//		} catch (parserFunctionsToDatalog.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return rules;
//
//	}
	
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
