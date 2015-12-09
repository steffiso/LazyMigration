package lazyMigration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;

import parserEDBFacts.JSONtoDatalogParser;
import parserEDBFacts.ParseException;
import parserFunctionParser.ParserForFunctions;
import parserGet.ParserForGet;
import putParser.LengthException;
import putParser.ParserForPut;

public class TestEDB {

	public String addAttribute(String kind, String attribute, String value) {

		String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(
							new StringReader("add \"" + kind + "\"." + "\""
									+ attribute + "\"=\"" + value + "\""))
							.getFunctionRule();
		} catch (parserFunctionParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String deleteAttribute(String kind, String attribute) {

		String rules = getEDBFacts();

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader("delete \""
							+ kind + "\"." + "\"" + attribute + "\""))
							.getFunctionRule();
		} catch (parserFunctionParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String copyAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		String rules = getEDBFacts();

		try {
			String temp = "copy \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String moveAttribute(String kindFrom, String kindTo,
			String attribute, String conditionAttributeFrom,
			String conditionAttributeTo) {

		String rules = getEDBFacts();

		try {
			String temp = "move \"" + kindFrom + "\"." + "\"" + attribute
					+ "\" to \"" + kindTo + "\" where \"" + kindFrom + "\"."
					+ "\"" + conditionAttributeFrom + "\"=\"" + kindTo + "\"."
					+ "\"" + conditionAttributeTo + "\"";
			rules = rules
					+ new ParserForFunctions(new StringReader(temp))
							.getFunctionRule();
		} catch (parserFunctionParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String get(String kind, int id) {

		String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).get(kind, id);
		} catch (parserGet.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String getAll(String kind) {

		String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGet(new StringReader("get")).getAll(kind);
		} catch (parserGet.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String putKind(String kind, String attributes) {

		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String time = ts.toString();
		return kind + "(" + attributes + ",'" + time + "')";

	}

	public void putDatalogToJSON(String kind,String datalog) {
		String json=null;
		try {
			json=
			new ParserForPut(new StringReader(datalog)).start();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/"+kind, true)));
		    out.append(String.format("%n")+json);
		    out.close();
		} catch  ( LengthException | putParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
