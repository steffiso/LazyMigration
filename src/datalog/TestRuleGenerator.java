package datalog;

import java.util.ArrayList;

public class TestRuleGenerator {

	public static void main(String[] args) {		
		
		DatalogRulesGenerator edb = new DatalogRulesGenerator ();	

		String datalogFacts = edb.getEDBFacts();
		String datalogRules = "";
		//datalogRules = edb.get("get \"Mission\".\"id\"=\"3\"");
		//datalogRules = edb.addAttribute("add \"Player\".\"score2\"=\"333\"");
		//datalogRules = edb.deleteAttribute("delete \"Player\".\"score\"");

		//datalogRules = edb.copyAttribute("copy \"Player\".\"score\" to \"Mission\" where \"Player\".\"id\"=\"Mission\".\"pid\"");	
		//datalogRules = edb.moveAttribute("move \"Player\".\"score\" to \"Mission\" where \"Player\".\"id\"=\"Mission\".\"pid\"");
		//datalogRules = edb.moveAttribute("Mission", "Player", "title", "pid", "id");

		//System.out.println(datalogFacts + datalogRules);

		ArrayList<String> values = new ArrayList<String>();
		values.add("1");
		values.add("?name");
		values.add("?ts");
		Fact f = new Fact("Player1", values);
		System.out.println(f.toString());
	}
}