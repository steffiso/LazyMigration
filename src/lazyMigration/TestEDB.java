package lazyMigration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import parserEDBFacts.JSONtoDatalogParser;
import parserEDBFacts.ParseException;
import parserFunctionParser.ParserForFunctions;
import parserGetPlayer.ParserForGetPlayer;

public class TestEDB {

	public String deleteAttribute(String attribute) {

		String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGetPlayer(new StringReader("get"))
							.getAllPlayer();
		} catch (parserGetPlayer.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			rules = rules
					+ new ParserForFunctions(new StringReader(
							"delete Player."+"\""+attribute+"\"")).getFunctionRule();
		} catch (parserFunctionParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;

	}

	public String getPlayer(int id) {

		String rules = getEDBFacts();
		try {
			rules = rules
					+ new ParserForGetPlayer(new StringReader("get"))
							.getPlayer(1);
		} catch (parserGetPlayer.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String getAllPlayer() {

		String rules = null;
		try {
			rules = new ParserForGetPlayer(new StringReader("get"))
					.getAllPlayer();
		} catch (parserGetPlayer.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;

	}

	public String getEDBFacts() {

		final File filename = new File("data/Players.json");
		String edbFacts = listJSONObjects(filename);
		return edbFacts;

	}

	public String listJSONObjects(final File filename) {

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
								sCurrentLine)).getEDBFacts("Player");
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
