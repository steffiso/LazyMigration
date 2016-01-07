package parserPutToDatalog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import lazyMigration.DatalogRulesGenerator;

public class TestPut {

	public static void main(String[] args) {
		DatalogRulesGenerator edb=new DatalogRulesGenerator();
		String datalog=edb.putKind("Player", "1,'Lisa.K', 40");
		String datalog2=edb.putKind("Player", "1,'Lisa.K',40");
		System.out.println(datalog);
		edb.putDatalogToJSON("Player", datalog);
		edb.putDatalogToJSON("Player", datalog2);
	}

}
