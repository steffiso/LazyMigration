package lazyMigration;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import datalog.DatalogRulesGenerator;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;

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
		JButton startBottomUp = new JButton("TopDown Ausführung");
		JButton generateDatalog = new JButton("Generate IDB");
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
		Predicate goal = null;

		public MainFrame() {
			initComponents();
		}

		private void initComponents() {
			setTitle("TestGui");
			setLayout(new GridLayout(5, 1));

			String edb = datalogGenerator.getEDBFacts();
			
			edbTextArea.setText(edb);	
			idbTextArea.setText("legacyPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts),Player1(?id,?name2,?score2,?nts), ?ts < ?nts.\n" + 
							"latestPlayer1(?id,?ts):-Player1(?id,?name,?score,?ts), not legacyPlayer1(?id,?ts).\n" +
							"getPlayer1(?id,?name,?score,?ts):-Player1(?id, ?name,?score,?ts), latestPlayer1(?id,?ts).\n");
			
			add(scroll);
			add(scroll2);		
			panel.setLayout(new GridLayout(1,2));
			panel.add(scroll3);
			panel.add(generateDatalog);
			add(panel);
			add(scroll4);	
			add(startBottomUp);

			pack();
			setSize(700, 600);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
			
			startBottomUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					if (!edbTextArea.getText().equals("") && !idbTextArea.getText().equals(""))
						executeQuery();
					else answerTextArea.setText("No EDB or IDB found!");
				}
			});
			
			generateDatalog.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					String uiInput = "";
					String rules = "";
					uiInput = queryTextArea.getText();
					DatalogRulesGenerator drg = new DatalogRulesGenerator();
					
					if (uiInput.startsWith("get")){
						rules = drg.get(uiInput);
						idbTextArea.append("\n" + rules);
						//start lazy migration
						//goal = (new ParserRuleToJava(rules)).getRelation();
					}					
					else if (uiInput.startsWith("add")){
						rules = drg.addAttribute(uiInput);
						idbTextArea.append("\n" + rules);
					}					
					else if (uiInput.startsWith("delete")){
						rules = drg.deleteAttribute(uiInput);
						idbTextArea.append("\n" + rules);
					}
					else if (uiInput.startsWith("move")){
						rules = drg.moveAttribute(uiInput);
						idbTextArea.append("\n" + rules);
					}
					else if (uiInput.startsWith("copy")){
						rules = drg.copyAttribute(uiInput);
						idbTextArea.append("\n" + rules);
					}
					else if (uiInput.startsWith("put")){
						//To do : lazy migration
					}
					else {
						answerTextArea.setText("No valid query");
					}
						
				}
				
			});
			
		}


		private void executeQuery() {
			//set edb-facts in gui
			edbTextArea.setText(datalogGenerator.getEDBFacts());
			String[] edbFacts = edbTextArea.getText().split("\n");
			String query = queryTextArea.getText();
			
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
			
			//test für get
			SortedMap <String, String> attributeMap = new TreeMap<String, String>();
			attributeMap.put("?id", "1");
			attributeMap.put("?name", "");
			attributeMap.put("?score", "");
			attributeMap.put("?ts", "");
			
			ArrayList<String> schema = new ArrayList<String>();
			schema.add("?id");
			schema.add("?name");
			schema.add("?score");
			Predicate goal = new Predicate("getPlayer1", 4, schema, attributeMap);
			
				
			TopDownExecution migrate = new TopDownExecution(facts, rules, goal);
			ArrayList<Fact> answerString = migrate.getAnswers();

			answerTextArea.setText(answerString.toString());
		}
		
	}

}
