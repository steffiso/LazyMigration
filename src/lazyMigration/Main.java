package lazyMigration;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {		
		
		TestEDB edb = new TestEDB ();
		
		//String datalogRules = edb.getAll("Mission");
		//String datalogRules = edb.get("Mission",3);
		//String datalogRules = edb.getEDBFacts();
		//String datalogRules = edb.addAttribute("Mission", "priority", "2");
		//String datalogRules = edb.deleteAttribute("Player", "name");

		//!!! move/copyAttribute funktioniert aktuell nur für where player.id = mission.pid !!!
		String datalogRules = edb.copyAttribute("Player", "Mission", "score", "id", "pid");		
		//String datalogRules = edb.moveAttribute("Player", "Mission", "score", "id", "pid");
		System.out.println(datalogRules);

	}
}