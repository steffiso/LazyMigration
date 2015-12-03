package lazyMigration;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {		
		
		TestEDB edb = new TestEDB ();
		
		//String datalogRules = edb.getAll("Mission");
		//String datalogRules = edb.get("Mission",3);
		//String datalogRules = edb.getEDBFacts();
		//String datalogRules = edb.addAttribute("Mission", "priority", "200");
		//String datalogRules = edb.deleteAttribute("Mission", "title");

		//String datalogRules = edb.copyAttribute("Player", "Mission", "score", "id", "pid");		
		//String datalogRules = edb.copyAttribute("Mission", "Player", "title", "pid", "id");		
		//String datalogRules = edb.moveAttribute("Player", "Mission", "score", "id", "pid");
		String datalogRules = edb.moveAttribute("Mission", "Player", "title", "pid", "id");
		System.out.println(datalogRules);

	}
}