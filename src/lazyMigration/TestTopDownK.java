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

public class TestTopDownK {

	public static void main(String[] args) throws ParseException,
	 parserRuletoJava.ParseException{
		// TODO Auto-generated method stub
		Fact ff1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',1).")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',11).")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Homer',2).")).start();
		ArrayList<Fact> ff = new ArrayList<Fact>();
		ff.add(ff1);
		ff.add(ff2);
		ff.add(ff3);


		ArrayList<Rule> qq = new ParserRuleToJava(
				new StringReader(
						"Player2(?id, ?name,?score,?ts):-Player1(?id, ?name,?ts), Player1(?id,?j,?nts), ?ts<?nts."))
				.start();
	/*	TopDownExecution tp = new TopDownExecution(ff);
		List<Map<String, String>> maps = tp.getAnswer(qq.get(0));
		if (maps.isEmpty())
			System.out.println("Empty");
		else
			for (Map<String, String> map : maps)
				for (Entry<String, String> e : map.entrySet())
					System.out.println(e.getKey() + " " + e.getValue());*/
	}
	
	}


