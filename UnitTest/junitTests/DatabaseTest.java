package junitTests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import parserPutToDatalog.ParseException;
import database.Database;

public class DatabaseTest {
	Database db;
	String initialEDB;
	
	@Before
	public void setUp() throws JsonParseException, JsonMappingException, IOException{
		db = new Database("/data/TestEDB.json", "/data/TestSchema.json");
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
	public void testPut() throws IOException, ParseException{	
		int lastTimestamp = db.getLastTimestamp();
		db.putToDatabase("Player1(3,'Maggie',23)");
		assertEquals(initialEDB + "Player1(3,'Maggie',23," + String.valueOf(lastTimestamp + 1) + ").\n",db.getEDB());
		db.resetDatabaseState();
	}
	
	
	
}
