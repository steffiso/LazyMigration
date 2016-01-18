package database;

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
		//db.saveCurrentSchema("Player", "\"?id\",\"?test1\",\"?test2\",\"?test3\"");
		db.putToDatabase("Player1(3,'Maggie',23,'12345')");
	}

}
