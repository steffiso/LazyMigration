package datalog;

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

		System.out.println(datalogFacts + datalogRules);

	}
}