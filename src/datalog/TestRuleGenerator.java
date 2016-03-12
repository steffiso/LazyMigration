package datalog;

import java.util.ArrayList;

public class TestRuleGenerator {

	public static void main(String[] args) {		
		
		DatalogRulesGenerator edb = new DatalogRulesGenerator ();	

		String datalogFacts = edb.getEDBFacts();
		String datalogRules = "";
		//datalogRules = edb.getRules("get Mission.id=\"3\"");
		//datalogRules = edb.getRules("add Player.scoret=\"333\"");
		//datalogRules = edb.getRules("delete Player.score");

		datalogRules = edb.getRules("copy Player.score to Mission where Player.id=Mission.pid");
		System.out.println(datalogFacts + datalogRules);
		//datalogRules = edb.getRules("move Player.score to Mission where Player.id=Mission.pid");
		ArrayList<Rule> rules = edb.getJavaRules("copy Player.score to Mission where Player.id=Mission.pid");
		System.out.println(rules.toString());
		


	}
}