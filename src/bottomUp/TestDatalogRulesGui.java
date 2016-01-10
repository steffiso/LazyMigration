package bottomUp;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import lazyMigration.DatalogRulesGenerator;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.*;
public class TestDatalogRulesGui {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame();
			}
		});

	}

	/**
	 * The main application window
	 */
	public static class MainFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JButton startBottomUp = new JButton("BottomUp Ausführung");
		JTextArea edbTextArea = new JTextArea();
		JTextArea queryTextArea = new JTextArea();
		JTextArea idbTextArea = new JTextArea();
		JTextArea answerTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(edbTextArea);
		JScrollPane scroll2 = new JScrollPane(idbTextArea);
		JScrollPane scroll3 = new JScrollPane(queryTextArea);
		JScrollPane scroll4 = new JScrollPane(answerTextArea);
		JPanel panel = new JPanel();
		ArrayList<Fact> facts = null;
		ArrayList<Rule> rules = null;
		DatalogRulesGenerator datalogGenerator = new DatalogRulesGenerator();

		public MainFrame() {
			initComponents();
		}

		private void initComponents() {
			setTitle("TestGui");
			setLayout(new GridLayout(4, 1));

			String edb = datalogGenerator.getEDBFacts();

			edbTextArea.setText(edb);
			idbTextArea
					.setText("legacyPlayer(?id,?ts):-Player(?id, ?name,?score, ?ts),Player(?id, ?name2,?score2,?nts), ?ts < ?nts.\n"
							+ "latestPlayer(?id,?ts):-Player(?id, ?name,?score,?ts), not legacyPlayer(?id,?ts).\n"
							+ "legacyMission(?id,?ts):-Mission(?id, ?title,?pid, ?ts),Mission(?id, ?title2,?pid2,?nts), ?ts < ?nts.\n"
							+ "latestMission(?id,?ts):-Mission(?id, ?title,?pid,?ts), not legacyMission(?id,?ts).\n"
							+ "Mission2(?id1, ?title,?pid,?score,'2016-01-08 01:49:14.608'):-Mission(?id1, ?title,?pid,?ts1),latestMission(?id1, ?ts1),Player(?id2, ?name,?score,?ts2), latestPlayer(?id2, ?ts2),?pid = ?id2.\n"
							+ "Mission2(?id1, ?title,?pid,'','2016-01-08 01:49:14.62'):-Mission(?id1, ?title,?pid,?ts1),latestMission(?id1, ?ts1), not Player(?id2, ?name,?score,?ts2), ?pid = ?id2.");

			add(scroll);
			add(scroll2);
			add(startBottomUp);
			add(scroll4);
			pack();
			setSize(700, 600);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			startBottomUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {

					if (!edbTextArea.getText().equals("")
							&& !idbTextArea.getText().equals(""))
						executeQuery();
					else
						answerTextArea.setText("No EDB or IDB found!");
				}
			});

		}

		private void executeQuery() {
			// set edb-facts in gui
			edbTextArea.setText(datalogGenerator.getEDBFacts());
			String[] edbFacts = edbTextArea.getText().split("\n");
			String answerString = "";

			facts = new ArrayList<Fact>();
			for (String factString : edbFacts) {
				try {
					facts.add(new ParserforDatalogToJava(new StringReader(
							factString)).start());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				rules = new ParserRuleToJava(new StringReader(
						idbTextArea.getText())).start();
			} catch (parserRuletoJava.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			BottomUpExecution bottomup = new BottomUpExecution(facts);
			bottomup.generateQueries(rules);
			for (Rule rule : rules) {
				ArrayList<ArrayList<String>> answers = bottomup.getFact(rule
						.getHead().getKind(), rule.getHead().getAnz());
				answerString = answerString + "Results for IDB Fact '"
						+ rule.getHead().getKind() + "'\n";
				for (ArrayList<String> answer : answers)
					answerString = answerString + answer.toString() + "\n";
			}

			answerTextArea.setText(answerString);
		}

	}

}
