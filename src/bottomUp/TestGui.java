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

import lazyMigration.TestEDB;
import parserDatalogToJava.ParseException;
import parserDatalogToJava.ParserforDatalogToJava;
import parserIDBQuery.ParserIDBQueryToJava;

public class TestGui {
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
		JButton queryJButton = new JButton("Ausführen");
		JTextArea edbTextArea = new JTextArea();
		JTextArea queryTextArea = new JTextArea();
		JTextArea answerTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(edbTextArea);
		JScrollPane scroll2 = new JScrollPane(queryTextArea);
		JScrollPane scroll3 = new JScrollPane(answerTextArea);
		JPanel panel = new JPanel();
		ArrayList<Fact> facts = null;
		ArrayList<Query> queries = null;
		TestEDB newTestEDB = new TestEDB();

		public MainFrame() {
			initComponents();
		}

		private void initComponents() {
			setTitle("TestGui");
			setLayout(new GridLayout(3, 1));

			String rules = newTestEDB.getEDBFacts();

			edbTextArea.setText(rules);

			queryTextArea
					.setText("legacyPlayer(?id,?ts) :- Player(?id, ?name,?score, ?ts),Player(?id, ?name2,?score2,?nts), ?ts < ?nts.\n"
							+ "latestPlayer(?id,?ts) :- Player(?id, ?name,?score,?ts), not legacyPlayer(?id,?ts).\n"
							+ "getPlayer(?id,?name,?score,?ts) :- Player(?id, ?name,?score, ?ts), latestPlayer(?id,?ts).");

			queryJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					executeQuery();
				}
			});
			panel.setLayout(new GridLayout(2, 1));
			panel.add(scroll2);
			panel.add(queryJButton);

			add(scroll);
			add(panel);
			add(scroll3);
			pack();
			setSize(700, 600);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		private void executeQuery() {
			if (!queryTextArea.getText().equals("")) {
				// TODO Auto-generated method stub
				String[] edbFacts = edbTextArea.getText().split("\n");
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
			}
			try {
				queries = new ParserIDBQueryToJava(new StringReader(
						queryTextArea.getText())).start();
			} catch (parserIDBQuery.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BottomUpExecution bottomup = new BottomUpExecution(facts);
			String answerString = "";
			bottomup.generateQueries(queries);
			for (Query query : queries) {
				ArrayList<ArrayList<String>> answers = bottomup.getFact(query
						.getIdbRelation().getKind(), query.getIdbRelation()
						.getAnz());
				answerString = answerString + "Results for IDB Fact '"
						+ query.getIdbRelation().getKind() + "'\n";
				for (ArrayList<String> answer : answers)
					answerString = answerString + answer.toString() + "\n";
			}
			answerTextArea.setText(answerString);
		}

	}

}
