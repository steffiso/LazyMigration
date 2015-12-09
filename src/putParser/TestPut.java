package putParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import lazyMigration.TestEDB;

public class TestPut {

	public static void main(String[] args) {
		TestEDB edb=new TestEDB();
		String datalog=edb.putKind("Player", "1,'Lisa.K',40");
		System.out.println(datalog);
		edb.putDatalogToJSON("Player", datalog);
	}

}
