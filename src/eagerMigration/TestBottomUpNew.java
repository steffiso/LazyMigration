package eagerMigration;

import java.io.StringReader;
import java.util.ArrayList;

import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;

public class TestBottomUpNew {
	public static void main(String[] args) throws ParseException,
			parserRuletoJava.ParseException {
		/*
		 * Fact f1 = new ParserforDatalogToJava(new StringReader(
		 * "Player(1,'Lisa',40).")).start(); Fact fr1 = new
		 * ParserforDatalogToJava(new StringReader(
		 * "Player(2,'Bart',20).")).start(); Fact f2 = new
		 * ParserforDatalogToJava(new StringReader(
		 * "Mission(1,'Find',1).")).start(); Fact fr2 = new
		 * ParserforDatalogToJava(new StringReader(
		 * "Mission(2,'Find Ring',2).")).start(); Fact f3 = new
		 * ParserforDatalogToJava(new StringReader(
		 * "New('Hallo','Lisa',1).")).start(); Fact fr3 = new
		 * ParserforDatalogToJava(new StringReader(
		 * "New('Hallo','Bart',2).")).start(); ArrayList<Fact> f = new
		 * ArrayList<Fact>(); f.add(f1); f.add(f2); f.add(f3); f.add(fr1);
		 * f.add(fr2); f.add(fr3); ArrayList<Query> q = new
		 * ParserIDBQueryToJava( new StringReader(
		 * "Get(?id,?name,?title,?id3,?name3):-Player(?id,?name,?ts), Mission(?id2,?title,?id),New(?id3,?name3,?id).GetLL(?name,?title,?id3,?name3):-Get(?id,?name,?title,?id3,?name3)."
		 * )) .start(); BottomUpExecution mm = new BottomUpExecution(f); for
		 * (Query qq : q) System.out.println(mm.getAnswer(qq).toString());
		 */
		/*
		 * Fact ff1 = new ParserforDatalogToJava(new StringReader(
		 * "a(1).")).start(); Fact ff2 = new ParserforDatalogToJava(new
		 * StringReader( "b(1).")).start(); Fact ff3 = new
		 * ParserforDatalogToJava(new StringReader( "a(1).")).start();
		 * ArrayList<Fact> ff = new ArrayList<Fact>(); ff.add(ff1); ff.add(ff2);
		 * ff.add(ff3); ArrayList<Query> qq = new ParserIDBQueryToJava( new
		 * StringReader( "m(?x):-a(?x),not b(?x).")) .start(); BottomUpExecution
		 * mmm = new BottomUpExecution(ff); for (Query qqq : qq)
		 * System.out.println(mmm.getAnswer(qqq).toString());
		 * 
		 * mmm.orderStratum(qq); for (Query qqq : qq) {
		 * System.out.println(qqq.getIdbRelation().getKind() + " " +
		 * qqq.getIdbRelation().isNot() + " " +
		 * qqq.getIdbRelation().getStratum()); for (Relation r :
		 * qqq.getRelations()) System.out.println(r.getKind() + " " + r.isNot()
		 * + " " + r.getStratum()); }
		 */

		Fact ff1 = new ParserforDatalogToJava(new StringReader(
				"Player1(1,'Lisa',20).")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player1(2,'Homer',20).")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Mission1(1,'find the ring',1).")).start();
		Fact ff4 = new ParserforDatalogToJava(new StringReader(
				"Mission1(2,2,2).")).start();
		ArrayList<Fact> ff = new ArrayList<Fact>();
		ff.add(ff1);
		ff.add(ff2);
		ff.add(ff3);
		ff.add(ff4);
		ArrayList<Rule> qq = new ParserRuleToJava(
				new StringReader(
						"latestPlayer1(2,?name,2):-Player1(2, ?name,?score),Mission1(?id1, 2,?id)."))
				.start();

		BottomUpExecutionNew mmm = new BottomUpExecutionNew(ff);
		for (Rule r : qq) {
			System.out.println(r.toString());
			System.out.println(r.getHead().getKind() + " "
					+ r.getHead().getScheme());
			for (Predicate p : r.getPredicates())
				System.out.println(p.getKind() + " " + p.getScheme());
		}
		mmm.generateAllRules(qq);

		System.out.println(mmm.getFact("latestPlayer1", 3));

	}

}
