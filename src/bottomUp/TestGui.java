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
		JButton startBottomUp = new JButton("BottomUp Ausführung");
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

		public MainFrame() {
			initComponents();
		}

		private void initComponents() {
			setTitle("TestGui");
			setLayout(new GridLayout(5, 1));

			String edb = datalogGenerator.getEDBFacts();
			
			edbTextArea.setText(edb);	
			
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
						idbTextArea.setText(rules);
					}					
					else if (uiInput.startsWith("add")){
						rules = drg.addAttribute(uiInput);
						idbTextArea.setText(rules);
					}					
					else if (uiInput.startsWith("delete")){
						rules = drg.deleteAttribute(uiInput);
						idbTextArea.setText(rules);
					}
					else if (uiInput.startsWith("move")){
						rules = drg.moveAttribute(uiInput);
						idbTextArea.setText(rules);
					}
					else if (uiInput.startsWith("copy")){
						rules = drg.copyAttribute(uiInput);
						idbTextArea.setText(rules);
					}
					else if (uiInput.startsWith("put")){
						
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
			bottomup.generateAllRules(rules);
			for (Rule rule : rules) {
				ArrayList<ArrayList<String>> answers = bottomup.getFact(rule
						.getHead().getKind(), rule.getHead()
						.getAnz());				
				answerString = answerString + "Results for IDB Fact '"
						+ rule.getHead().getKind() + "'\n";
			
				for (ArrayList<String> answer : answers)	{				
					//bei add: neu generierte Fakten per put in json schreiben
					if (!rule.getHead().getKind().startsWith("latest") && !rule.getHead().getKind().startsWith("legacy") && !queryTextArea.getText().startsWith("get")){					
						String values = "";
						for (String s : answer)
						{
						    values += s + ", ";
						}
						
						values = values.substring(0, values.length()-2);
						String tempKind = rule.getHead().getKind();
						//entferne die Zahl von kind
						tempKind = tempKind.substring(0, tempKind.length() - 1);
						String datalogFact = datalogGenerator.putKind(tempKind, values);
						datalogGenerator.putDatalogToJSON(tempKind, datalogFact);
					}
						
					answerString = answerString + answer.toString() + "\n";
				}
			}

			answerTextArea.setText(answerString);
		}
		
	}

}
