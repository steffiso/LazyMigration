package lazyMigration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import javaCCGrammar.JSONtoDatalogParser;
import javaCCGrammar.ParseException;

public class TestEDB {

	public String getEDBFacts() {
		
		final File filename = new File("data/Players.json");
		String edbFacts=listJSONObjects(filename);
		return edbFacts;
		
	}

	public String listJSONObjects(final File filename) {
		
		String edbFacts = "";
		if(filename.exists()) {
			
				BufferedReader br = null;

				try {

					String sCurrentLine;

					br = new BufferedReader(new FileReader(
							filename));

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
