package bottomUp;

import java.io.StringReader;
import java.util.ArrayList;
import parserDatalogToJava.ParseException;
import parserDatalogToJava.ParserforDatalogToJava;
import parserIDBQuery.ParserIDBQueryToJava;

public class TestJoin {
	public static void main(String[] args) throws ParseException,
			parserIDBQuery.ParseException {
		Fact f1 = new ParserforDatalogToJava(new StringReader(
				"Player(1,'Lisa',40).")).start();
		Fact fr1 = new ParserforDatalogToJava(new StringReader(
				"Player(2,'Bart',20).")).start();
		Fact f2 = new ParserforDatalogToJava(new StringReader(
				"Mission(1,'Find',1).")).start();
		Fact fr2 = new ParserforDatalogToJava(new StringReader(
				"Mission(2,'Find Ring',2).")).start();
		Fact f3 = new ParserforDatalogToJava(new StringReader(
				"New('Hallo','Lisa',1).")).start();
		Fact fr3 = new ParserforDatalogToJava(new StringReader(
				"New('Hallo','Bart',2).")).start();
		ArrayList<Fact> f = new ArrayList<Fact>();
		f.add(f1);
		f.add(f2);
		f.add(f3);
		f.add(fr1);
		f.add(fr2);
		f.add(fr3);
		ArrayList<Query> q = new ParserIDBQueryToJava(
				new StringReader(
						"Get(?id,?name,?title,?id3,?name3):-Player(?id,?name,?ts),Mission(?id2,?title,?id),New(?id3,?name3,?id).GetLL(?name,?title,?id3,?name3):-Get(?id,?name,?title,?id3,?name3)."))
				.start();
		BottomUpExecution mm = new BottomUpExecution(f);
		for (Query qq : q)
			System.out.println(mm.getAnswer(qq).toString());

	}
}
