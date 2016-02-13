package database;

import java.util.ArrayList;

public class TestDatabase {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Database db = new Database();
		//String edb = db.getEDB();
		//System.out.println(edb);
		//int latestVersion = db.getLatestSchemaVersion("Mission");
		//String latestSchema = db.getSchema("Mission", latestVersion);
		//String latestSchema = db.getLatestSchema("Player");
		//System.out.println(latestSchema);
		//System.out.println(latestVersion + ":" + latestSchema);
//		ArrayList<String> newSchema = new ArrayList<String>();
//		newSchema.add("?test1");
//		newSchema.add("?test2");
//		newSchema.add("?test3");
		//db.saveCurrentSchema("Player", newSchema);
		db.putToDatabase("Player1(3,'Maggie',23)");
	}

}
