package datalog;

public class TestRuleGenerator {

	public static void main(String[] args) {		
		
		DatalogRulesGenerator edb = new DatalogRulesGenerator ();	

		//edb.putDatalogToJSON("Player", "Player(1, 'Marge', 100, '12345')");
		String datalogFacts = edb.getEDBFacts();
		String datalogRules = "";
		//datalogRules = edb.getAll("Mission");
		//datalogRules = edb.get("Mission",3);
		//datalogRules = edb.getEDBFacts();
		//datalogRules = edb.addAttribute("Mission", "priority", "200");
		//datalogRules = edb.deleteAttribute("Mission", "title");

		datalogRules = edb.copyAttribute("Player", "Mission", "score", "id", "pid");		
		//datalogRules = edb.copyAttribute("Mission", "Player", "title", "pid", "id");		
		//datalogRules = edb.moveAttribute("Player", "Mission", "score", "id", "pid");
		//datalogRules = edb.moveAttribute("Mission", "Player", "title", "pid", "id");

		System.out.println(datalogFacts + datalogRules);

	}
}