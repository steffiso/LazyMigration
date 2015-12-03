package lazyMigration;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {		
		
		TestEDB edb = new TestEDB ();
		//String datalogRules = edb.getAll("Player");
		//String datalogRules = edb.get("Mission",2);
		//String datalogRules = edb.getEDBFacts();
		//String datalogRules = edb.addAttribute("Mission", "priority", "2");
		String datalogRules = edb.deleteAttribute("Player", "name");
		System.out.println(datalogRules);
//		
//		EDB test = new EDB();
//		String schema = test.getSchema(1);
//		System.out.println(schema);
	}
}
