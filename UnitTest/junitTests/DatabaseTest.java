package junitTests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import database.Database;

public class DatabaseTest {
	Database db;
	String initialEDB;
	
	@Before
	public void setUp(){
		db = new Database("data/eager/EDB.json", "data/eager/Schema.json");
		initialEDB = db.getEDB();
	}

	@Test
	public void testSchemaChange() throws IOException{	
		int latestVersion = 2;
		String latestSchema = "?test1,?test2,?test3";
		ArrayList<String> newSchema = new ArrayList<String>();
		newSchema.add("?test1");
		newSchema.add("?test2");
		newSchema.add("?test3");
		db.saveCurrentSchema("Player", newSchema);
		assertEquals(latestSchema, db.getLatestSchema("Player").toString());
		assertEquals(latestVersion, db.getLatestSchemaVersion("Player"));
		db.resetDatabaseState();
	}
	
	@Test
	public void testPut() throws IOException{	
		int lastTimestamp = db.getLastTimestamp();
		db.putToDatabase("Player1(3,'Maggie',23)");
		System.out.println(db.getEDB());
		assertEquals(initialEDB + "Player1(3,'Maggie',23," + String.valueOf(lastTimestamp + 1) + ").\n",db.getEDB());
		db.resetDatabaseState();
	}
	
	
	
}
