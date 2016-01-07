package lazyMigration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import parserFunctionsToDatalog.ParserForFunctions;
import parserGetToDatalog.ParserForGet;
import parserJSONToEDBFacts.JSONtoDatalogParser;
import parserJSONToEDBFacts.ParseException;
import parserPutToDatalog.LengthException;
import parserPutToDatalog.ParserForPut;

public class DatalogRulesGenerator {
	
	String rules = "";

	public String addAttribute(String kind, String attribute, String value) {

		try {
			rules = rules
					+ new ParserForFunctions(
							new StringReader("add \"" + kind + "\"." + "\""
									+ attribute + "\"=\"" + value + "\""))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;
	}
	
	public String addAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(
							new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;
	}

	public String deleteAttribute(String kind, String attribute) {

		//String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader("delete \""
							+ kind + "\"." + "\"" + attribute + "\""))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String deleteAttribute(String input) {

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}
	public String copyAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		//String rules = getEDBFacts();

		try {
			String temp = "copy \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;

	}
	
	public String copyAttribute(String input) {

		//String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rules;

	}

	public String moveAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		//String rules = getEDBFacts();

		try {
			String temp = "move \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}
	
	public String moveAttribute(String input) {

		//String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(input))
							.getFunctionRule();
		} catch (parserFunctionsToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String get(String kind, int id) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).get(kind, id);
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}
	
	public String get(String input) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader(input)).getRule();
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}


	public String getAll(String kind) {

		//String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).getAll(kind);
		} catch (parserGetToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String putKind(String kind, String attributes) {

		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String time = ts.toString();
		attributes = attributes.substring(0, attributes.lastIndexOf(","));
		return kind + "(" + attributes + ",'"+ time +"')";

	}

	public void putDatalogToJSON(String kind,String datalog) {
		String json=null;
		try {
			json=
			new ParserForPut(new StringReader(datalog)).start();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/"+kind, true)));
		    out.append(String.format("%n")+json);
		    out.close();
		} catch  ( parserPutToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getOldSchema(String kind){
		String currentSchema = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("data/" + kind + "Schema"));
			currentSchema = null;
			String temp =null;

			while( (temp = in.readLine()) != null)
			{
				currentSchema = temp;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentSchema;
	}
	
	public void saveCurrentSchema(String kind, String newSchema){
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter("data/"+kind + "Schema", true)));
		    out.append(String.format("%n")+newSchema);
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getEDBFacts() {

		final File filename = new File("data/Player");
		String edbFacts = listJSONObjects(filename, "Player");
		final File filename2 = new File("data/Mission");
		edbFacts = edbFacts + listJSONObjects(filename2, "Mission");
		return edbFacts;

	}

	public String listJSONObjects(final File filename, String kind) {

		String edbFacts = "";
		if (filename.exists()) {

			BufferedReader br = null;

			try {

				String sCurrentLine;

				br = new BufferedReader(new FileReader(filename));

				while ((sCurrentLine = br.readLine()) != null) {
					String oneEdbFact = null;
					try {
						oneEdbFact = new JSONtoDatalogParser(new StringReader(
								sCurrentLine)).getEDBFacts(kind);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (oneEdbFact != null)
						edbFacts = edbFacts + oneEdbFact + ".\n";
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return edbFacts;
	}
	
	/*public String getSchema(int timestamp)
	  {	  
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootArray;
			String schema = "";
			
			try {
				rootArray = mapper.readTree(new File("data/Players.json"));
			
				for(JsonNode root : rootArray){
					
					int ts;
					String attribute = null;
					// Get timestamp
					ts = root.path("ts").asInt();
					if (timestamp == ts){
						for ( Iterator<String> names = root.fieldNames(); names.hasNext(); ){
							attribute = names.next();
							if (!attribute.equals("ts")) schema = schema + "?" + attribute + "\n";
						}
						return schema;
					}
					
				}
				return schema;
			
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return schema;		
	  }*/
}
