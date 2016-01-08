package bottomUp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserIDBQueryToJava.ParserIDBQueryToJava;

public class TestJoin {
	public static void main(String[] args) throws ParseException,
			parserIDBQueryToJava.ParseException {
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
				"Player(1,'Lisa',20,'2015-11-22 18:29:50.589').")).start();
		Fact ff2 = new ParserforDatalogToJava(new StringReader(
				"Player(3,'Homer',20,'2015-12-02 18:29:50.589').")).start();
		Fact ff3 = new ParserforDatalogToJava(new StringReader(
				"Mission(1,'find the ring',1,'2015-11-26 18:29:50.589')."))
				.start();
		Fact ff4 = new ParserforDatalogToJava(new StringReader(
				"Mission(2,'find the ring2',2,'2015-12-01 18:29:50.589')."))
				.start();
		ArrayList<Fact> ff = new ArrayList<Fact>();
		ff.add(ff1);
		ff.add(ff2);
		ff.add(ff3);
		ff.add(ff4);
		ArrayList<Query> qq = new ParserIDBQueryToJava(
				new StringReader(
						"legacyPlayer(?id,?ts):-Player(?id, ?name,?score, ?ts),Player(?id, ?name2,?score2,?nts), ?ts < ?nts."
								+ "latestPlayer(?id,?ts):-Player(?id, ?name,?score,?ts), not legacyPlayer(?id,?ts)."
								+ "legacyMission(?id,?ts):-Mission(?id, ?title,?pid, ?ts),Mission(?id, ?title2,?pid2,?nts), ?ts < ?nts."
								+ "latestMission(?id,?ts):-Mission(?id, ?title,?pid,?ts), not legacyMission(?id,?ts)."
								+ "Mission2(?id1, ?title,?pid,?score,'2016-01-08 01:49:14.608'):-Mission(?id1, ?title,?pid,?ts1),latestMission(?id1, ?ts1),Player(?id2, ?name,?score,?ts2), latestPlayer(?id2, ?ts2),?id2 = ?pid."
								+ "Mission2(?id1, ?title,?pid,'','2016-01-08 01:49:14.62'):-Mission(?id1, ?title,?pid,?ts1),latestMission(?id1, ?ts1), not Player(?pid, ?name,?score,?ts2)."))
				.start();
		BottomUpExecution mmm = new BottomUpExecution(ff);
		try {
			mmm.orderStratum(qq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			mmm.generateQueries(qq);
		
		System.out.println(mmm.getFact("Mission2", 5));
		for (Query qqq : qq) {
		System.out.println(qqq.getIdbRelation().getKind()+"  "+qqq.getIdbRelation().getStratum()+ " "+ qqq.getIdbRelation().getWerte());
		}

	}

}
