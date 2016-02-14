package eagerMigration;

import java.util.ArrayList;

import database.Database;
import datalog.DatalogRulesGenerator;
import datalog.Fact;
import datalog.Rule;

public class EagerMigration {
	
	private ArrayList<Fact> facts;
	private ArrayList<Rule> rules;
	private String query;
	
	public EagerMigration(ArrayList<Fact> facts, ArrayList<Rule> rules, String query){
		this.facts = facts;
		this.rules = rules;
		this.query = query;
	}
	
	public String writeAnswersInDatabase(){
		
		Database db = new Database();
		String answerString = "";
		BottomUpExecution bottomUp = new BottomUpExecution(facts);
		bottomUp.generateAllRules(rules);
		for (Rule rule : rules) {
			ArrayList<ArrayList<String>> answers = bottomUp.getFact(rule
					.getHead().getKind(), rule.getHead()
					.getAnz());	

			if (rule.getHead().getKind().startsWith("legacy")){
//				PrintWriter out;
//				try {
//					out = new PrintWriter (new BufferedWriter(new FileWriter("data/legacyEntities")));
//					String legacyEntities = "";
//					for (ArrayList<String> answer : answers){
//						//put to legacy file	
//						String valueString = "";
//						for (String s: answer) {
//							valueString = valueString + s + ",";
//						}
//						valueString = valueString.substring(0,valueString.length() - 1);
//						legacyEntities = legacyEntities + rule.getHead().getKind() +"(" + valueString + ")\n";
//		
//					}
//					out.append(legacyEntities);
//					out.close();	
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
			}
			else if(!rule.getHead().getKind().startsWith("latest") && !query.startsWith("get")){
				for (ArrayList<String> answer : answers){
					//put to database file
					
					String values = "";
					for (String s : answer)
					{
					    values += s + ", ";
					}
					
					values = values.substring(0, values.length()-2);
					String tempKind = rule.getHead().getKind();
					//toDo: 
					//String datalogFact = ....
					//db.putToDatabase(datalogFact);
				}
			}
			
			for (ArrayList<String> answer : answers)
					answerString = answerString + rule.getHead().getKind() + answer.toString() + "\n";			
			
		}
		
		return answerString;
	}
}
