package database;

import gui.GuiStartWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
	private String filePath;

	public Database(String filenameEDB, String filenameSchema) {
		super();
		try {
			this.filePath = new File(GuiStartWindow.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI()).getParentFile()
					.getPath();
		} catch (URISyntaxException e) {
			filePath = "";
		}
		this.filenameEDB = filePath + filenameEDB;
		this.filenameSchema = filePath + filenameSchema;
	}

	public String getFilenameEDB() {
		return filenameEDB;
	}

	public String getFilenameSchema() {
		return filenameSchema;
	}

	// return all database entries as edb facts in one string
	// e.g.
	// "Player1(1,'Lisa',20,1).\nPlayer1(2,'Bart',20,2).\nPlayer1(3,'Homer',20,3)."
	public String getEDB() throws JsonParseException, JsonMappingException,
			IOException {
		String edb = "";

		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Entity> entities = mapper.readValue(new File(filenameEDB),
				new TypeReference<List<Entity>>() {
				});

		for (Entity e : entities) {
			edb = edb + e.toEDBString();
		}

		return edb;
	}

	// return all database entries from database in a json-like string
	// e.g. "Player1{"id":1,"name":"Lisa","score":20,"ts":1}.\n" +
	// "Player1{"id":2,"name":"Bart","score":20,"ts":2}.\n" +
	// "Player1{"id":3,"name":"Homer","score":20,"ts":3}."
	public String getJson() throws JsonParseException, JsonMappingException,
			IOException {
		String edb = "";

		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Entity> entities = mapper.readValue(new File(filenameEDB),
				new TypeReference<List<Entity>>() {
				});

		for (Entity e : entities) {
			edb = edb + e.toJsonString();
		}

		return edb;
	}

	// return the schema for kind and version
	public Schema getSchema(String inputKind, int inputVersion) throws InputMismatchException
		 {

		Schema schema = null;

		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Schema> schemata;
		try {
			schemata = mapper.readValue(new File(filenameSchema),
					new TypeReference<List<Schema>>() {
					});
		} catch ( IOException e) {
			throw new InputMismatchException("no info for schema " + inputKind
					+ " found");
		}

		for (Schema s : schemata) {
			if (s.getKind().equals(inputKind)
					&& s.getSchemaversion() == inputVersion) {
				schema = s;
			}
		}

		return schema;
	}

	// returns the latest schema version number for the given kind
	public int getLatestSchemaVersion(String inputKind)
			throws JsonParseException, JsonMappingException, IOException {

		int latestSchemaVersion = 0;
		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Schema> schemata = mapper.readValue(new File(filenameSchema),
				new TypeReference<List<Schema>>() {
				});
		int schemaVersion;
		String kind;

		for (Schema s : schemata) {
			kind = s.getKind();
			schemaVersion = s.getSchemaversion();
			if (kind.equals(inputKind) && schemaVersion > latestSchemaVersion)
				latestSchemaVersion = schemaVersion;

		}

		return latestSchemaVersion;
	}

	// write the datalogFact in json-File
	// input: "Player2(4,'Lisa',40)"
	// timestamp will be added automatically
	public void putToDatabase(String datalogFact) throws InputMismatchException, JsonParseException, JsonMappingException, ParseException, IOException {

		String json = new ParserForPut(new StringReader(datalogFact))
				.start(this);
		writeInJsonFile(filenameEDB, json);

	}

	// returns the latest schema for input kind
	// output: "?name,?score,?points"
	public Schema getLatestSchema(String kind) throws InputMismatchException, JsonParseException, JsonMappingException, IOException{
		int currentSchemaVersion = 0;
		Schema currentSchema = null;

		currentSchemaVersion = getLatestSchemaVersion(kind);
		currentSchema = getSchema(kind, currentSchemaVersion);

		return currentSchema;
	}

	// write a schema to file "Schema.json"
	// input example: newSchema = "?a,?b,?c"
	public void saveCurrentSchema(String kind, ArrayList<String> newSchemaList)
			throws JsonParseException, JsonMappingException, IOException {
		int latestSchemaVersion = getLatestSchemaVersion(kind);
		int newSchemaVersion = latestSchemaVersion + 1;
		String newSchema = "[";
		for (String s : newSchemaList) {
			newSchema = newSchema + "\"" + s + "\",";
		}
		newSchema = newSchema.substring(0, newSchema.length() - 1) + "]";

		writeInJsonFile(filenameSchema, kind, newSchemaVersion, newSchema);
	}

	public int getLastTimestamp() throws JsonParseException,
			JsonMappingException, IOException {
		int ts = 0;

		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Schema> schemata = mapper.readValue(new File(filenameSchema),
				new TypeReference<List<Schema>>() {
				});
		for (Schema s : schemata) {
			if (s.getTimestamp() > ts)
				ts = s.getTimestamp();
		}

		ArrayList<Entity> entities = mapper.readValue(new File(filenameEDB),
				new TypeReference<List<Entity>>() {
				});
		for (Entity e : entities) {
			if (e.getTimestamp() > ts)
				ts = e.getTimestamp();
		}

		return ts;
	}

	public void writeInJsonFile(String filename, String kind,
			int schemaVersion, String values) throws JsonParseException,
			JsonMappingException, IOException {
		int newTS = getLastTimestamp() + 1;
		String jsonString = "{\"kind\":\"" + kind + "\",\n"
				+ "\"schemaversion\":" + Integer.toString(schemaVersion)
				+ ",\n" + "\"attributes\":" + values + ",\n\"ts\":" + newTS
				+ "}";

		writeInJsonFile(filename, jsonString);
	}

	public void writeInJsonFile(String filename, String jsonString)
			throws FileNotFoundException, IOException {
		File inputFile = new File(filename);
		File tempFile = new File(filePath + "/data/temp.json");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String lineToRemove = "]";
		String currentLine;

		while ((currentLine = reader.readLine()) != null) {
			// trim newline when comparing with lineToRemove
			String trimmedLine = currentLine.trim();
			if (!trimmedLine.equals(lineToRemove)) {
				if (currentLine.endsWith(",") || currentLine.equals("["))
					writer.append(currentLine + "\n");
				else
					writer.append(currentLine + ",\n");
			} else
				writer.append(jsonString + "\n" + "]");
		}
		writer.close();
		reader.close();

		if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
			System.out.println("Problem occurs while writing json files");
		}

	}

	// reset database to initial state
	public void resetDatabaseState() throws IOException {
		Path initialState = Paths.get(filePath + "/data/EDBInitial.json");
		Path overwrite = Paths.get(filenameEDB);
		Files.copy(initialState, overwrite, StandardCopyOption.REPLACE_EXISTING);

		initialState = Paths.get(filePath + "/data/SchemaInitial.json");
		overwrite = Paths.get(filenameSchema);
		Files.copy(initialState, overwrite, StandardCopyOption.REPLACE_EXISTING);

	}

}
