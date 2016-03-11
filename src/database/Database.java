package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import parserPutToDatalog.ParseException;
import parserPutToDatalog.ParserForPut;

public class Database {

	private String filenameEDB;
	private String filenameSchema;
	@SuppressWarnings("unused")
	private String filenameLegacy;
	
	public Database(){
		filenameEDB = "data/EDB.json";
		filenameSchema = "data/Schema.json";
		filenameLegacy = "data/LegacyEntities.txt";
	}

	public Database(String filenameEDB, String filenameSchema) {
		super();
		this.filenameEDB = filenameEDB;
		this.filenameSchema = filenameSchema;
	}

	//return all edb-facts from database in one string
	public String getEDB(){
		String edb = "";
		ArrayList<Entity> entities;
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			entities = mapper.readValue(new File(filenameEDB), new TypeReference<List<Entity>>(){});
			
			for (Entity e: entities){
				edb = edb + e.toString();
			}
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return edb;			
	}
		
	//return all json-facts from database in one string
		public String getJson(){
			String edb = "";
			ArrayList<Entity> entities;
			
			ObjectMapper mapper = new ObjectMapper();

			try {
				entities = mapper.readValue(new File(filenameEDB), new TypeReference<List<Entity>>(){});
				
				for (Entity e: entities){
					edb = edb + e.toJsonString();
				}
				
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return edb;			
		}
			
	
	//return the schema for one version and one kind
	//something like "?id,?name,?score" (without ts)
	public Schema getSchema(String inputKind, int inputVersion){	
		
		Schema schema = null;
		ArrayList<Schema> schemata;
		ObjectMapper mapper = new ObjectMapper();

		try {
			schemata = mapper.readValue(new File(filenameSchema), new TypeReference<List<Schema>>(){});		
			
			for (Schema s: schemata){
				if (s.getKind().equals(inputKind) && s.getSchemaversion() == inputVersion){
					schema = s;
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return schema;
	}
	
	//returns the latest schema version number for the given kind
	public int getLatestSchemaVersion(String inputKind){		

		int latestSchemaVersion = 0;	
		ArrayList<Schema> schemata;
		ObjectMapper mapper = new ObjectMapper();

		try {
			schemata = mapper.readValue(new File(filenameSchema), new TypeReference<List<Schema>>(){});		
			int schemaVersion;
			String kind;
			for (Schema s: schemata){
				kind = s.getKind();
				schemaVersion = s.getSchemaversion();
				if (kind.equals(inputKind) && schemaVersion>latestSchemaVersion) latestSchemaVersion = schemaVersion;
				
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return latestSchemaVersion;
	}
	
	//write the datalogFact in json-File
	//input: "Player2(4,'Lisa',40)"
	//timestamp will be added automatically
	public void putToDatabase(String datalogFact) {
		
		String json=null;
			try {
				json = new ParserForPut(new StringReader(datalogFact)).start();
				writeInJsonFile(filenameEDB, json);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	//returns the latest schema for input kind
	//output: "?name,?score,?points"
	public Schema getLatestSchema(String kind){
		int currentSchemaVersion = 0;
		Schema currentSchema = null;

		currentSchemaVersion = getLatestSchemaVersion(kind);
		currentSchema = getSchema(kind, currentSchemaVersion);
		
		return currentSchema;
	}	
	
	//write a schema to file "Schema.json"
	//input example: newSchema = "?a,?b,?c"
	public void saveCurrentSchema(String kind, ArrayList<String> newSchemaList){
		int latestSchemaVersion = getLatestSchemaVersion(kind);		
		int newSchemaVersion = latestSchemaVersion + 1;
		String newSchema = "[";
		for (String s: newSchemaList){
			newSchema = newSchema + "\"" + s + "\",";
		}
		newSchema = newSchema.substring(0, newSchema.length()-1) + "]";
		
		writeInJsonFile(filenameSchema, kind, newSchemaVersion, newSchema);
	}
	
	public int getLastTimestamp(){
		int ts = 0;
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Schema> schemata;
		ArrayList<Entity> entities;

			try {
				schemata = mapper.readValue(new File(filenameSchema), new TypeReference<List<Schema>>(){});	
				for (Schema s: schemata){
					if (s.getTimestamp()>ts) ts = s.getTimestamp();
				}
				
				entities = mapper.readValue(new File(filenameEDB), new TypeReference<List<Entity>>(){});
				for (Entity e: entities){
					if (e.getTimestamp() > ts) ts = e.getTimestamp();
				}

			} catch (JsonParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
				
		return ts;
	}
	
	
	public void writeInJsonFile(String filename, String kind, int schemaVersion, String values){
		int newTS = getLastTimestamp() + 1;
		String jsonString = "{\"kind\":\"" + kind + "\",\n" + "\"schemaversion\":" + Integer.toString(schemaVersion) + ",\n" + "\"attributes\":" + values + ",\n\"ts\":" + newTS + "}";
		
		writeInJsonFile(filename, jsonString);		
	}
	
	public void writeInJsonFile(String filename, String jsonString){
		
		File inputFile = new File(filename);
		File tempFile = new File("data/temp.json");
		
		BufferedReader reader;
		BufferedWriter writer;
		try {
			reader = new BufferedReader(new FileReader(inputFile));
			writer = new BufferedWriter(new FileWriter(tempFile));
			
			String lineToRemove = "]";
			String currentLine;

			while((currentLine = reader.readLine()) != null) {
			    // trim newline when comparing with lineToRemove
			    String trimmedLine = currentLine.trim();
			    if(!trimmedLine.equals(lineToRemove)) {
			    	if (currentLine.endsWith(",") || currentLine.equals("[")) writer.append(currentLine + "\n");
			    	else writer.append(currentLine + ",\n");
			    }
			    else writer.append(jsonString + "\n" + "]");
			}
			writer.close(); 
			reader.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!inputFile.delete() || !tempFile.renameTo(inputFile)){
			System.out.println("Problem occurs while writing json files");
		};
	}
}
