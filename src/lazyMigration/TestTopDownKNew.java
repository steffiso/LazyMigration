package lazyMigration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import datalog.Fact;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;

public class TestTopDownKNew {

	public static void main(String[] args) throws ParseException,
	 parserRuletoJava.ParseException{
		// TODO Auto-generated method stub
		Fact ff1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',1).")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa.s',2).")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Player1(2,'Homer',2).")).start();
		Fact ff4 = new ParserforDatalogToJava(new StringReader(
				"M1(2,2).")).start();
		Fact ff5 = new ParserforDatalogToJava(new StringReader(
				"M1(3,55).")).start();
		Fact ff6 = new ParserforDatalogToJava(new StringReader(
				"M1(3,33).")).start();
		Fact ff7 = new ParserforDatalogToJava(new StringReader(
				"A1(2,4445).")).start();
		Fact ff8 = new ParserforDatalogToJava(new StringReader(
				"A1(1,3333).")).start();
		ArrayList<Fact> ff = new ArrayList<Fact>();
		ff.add(ff1);
		ff.add(ff2);
		ff.add(ff3);
		ff.add(ff4);
		ff.add(ff5);
		ff.add(ff6);
		ff.add(ff7);
		ff.add(ff8);


		ArrayList<Rule> qq = new ParserRuleToJava(
				new StringReader(
						"Player2(?id, ?name,?points,?ts,?j):-Player1(?id, ?name,?ts),  A1(?id,?points), not M1(?id,?score),Player1(?id,?j,?k), ?ts=2."))
				.start();
		TopDownExecutionNew tp = new TopDownExecutionNew(ff);
		List<ArrayList<String>> maps = tp.getAnswer(qq.get(0));
		if (maps.isEmpty())
			System.out.println("Empty");
		else
			for (ArrayList<String> map : maps)
					System.out.println(map);
	}
	
	}


