package lazyMigration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import datalog.Fact;
import datalog.Rule;

public class TestTopDown {
	public static void main(String[] args) throws ParseException,
			parserRuletoJava.ParseException {

		Fact ff1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',1).")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',2).")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Player1(2,'Homer',1).")).start();
		Fact ff4 = new ParserforDatalogToJava(new StringReader(
				"M1(3,2).")).start();
		Fact ff5 = new ParserforDatalogToJava(new StringReader(
				"M1(2,55).")).start();
		Fact ff6 = new ParserforDatalogToJava(new StringReader(
				"M1(2,33).")).start();
		ArrayList<Fact> ff = new ArrayList<Fact>();
		ff.add(ff1);
		ff.add(ff2);
		ff.add(ff3);
		ff.add(ff4);
		ff.add(ff5);
		ff.add(ff6);

		ArrayList<Rule> qq = new ParserRuleToJava(
				new StringReader(
						"Player2(?id~1, ?name,?score,?ts):-Player1(?id~1, ?name,?ts), not M1(?id, ?score),?ts=2."))
				.start();
		TopDownExecution tp = new TopDownExecution(ff);
		List<Map<String, String>> maps = tp.getAnswer(qq.get(0));
		if (maps.isEmpty())
			System.out.println("Empty");
		else
			for (Map<String, String> map : maps)
				for (Entry<String, String> e : map.entrySet())
					System.out.println(e.getKey() + " " + e.getValue());

	}
}
