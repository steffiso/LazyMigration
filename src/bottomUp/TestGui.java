package bottomUp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import lazyMigration.DatalogRulesGenerator;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserFunctionsToDatalog.ParserForFunctions;
import parserGetToDatalog.ParserForGet;
import parserIDBQueryToJava.ParserIDBQueryToJava;

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
		JButton queryJButton2 = new JButton("Datalogregeln");
		JTextArea edbTextArea = new JTextArea();
		JTextArea uiTextArea = new JTextArea();
		JTextArea queryTextArea = new JTextArea();
		JTextArea answerTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(edbTextArea);
		JScrollPane scroll2 = new JScrollPane(queryTextArea);
		JScrollPane scroll3 = new JScrollPane(uiTextArea);
		JScrollPane scroll4 = new JScrollPane(answerTextArea);
		JPanel panel = new JPanel();
		ArrayList<Fact> facts = null;
		ArrayList<Query> queries = null;
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
			panel.add(queryJButton2);
			add(panel);
			add(scroll4);	
			add(queryJButton);

			pack();
			setSize(700, 600);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
			
			queryJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {

					executeQuery();
				}
			});
			
			queryJButton2.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					String uiInput = "";
					String rules = "";
					uiInput = uiTextArea.getText();
					DatalogRulesGenerator drg = new DatalogRulesGenerator();
					
					if (uiInput.startsWith("get")){
						rules = drg.get(uiInput);
						queryTextArea.setText(rules);
					};
					
					if (uiInput.startsWith("add")){
						rules = drg.addAttribute(uiInput);
						queryTextArea.setText(rules);
					};
					
					if (uiInput.startsWith("delete")){
						rules = drg.deleteAttribute(uiInput);
						queryTextArea.setText(rules);
					};
					if (uiInput.startsWith("move")){
						rules = drg.moveAttribute(uiInput);
						queryTextArea.setText(rules);
					};
					if (uiInput.startsWith("copy")){
						rules = drg.copyAttribute(uiInput);
						queryTextArea.setText(rules);
					};
					if (uiInput.startsWith("put"));
				}
				
			});
			
		}


		private void executeQuery() {
			if (!queryTextArea.getText().equals("")) {
				// TODO Auto-generated method stub
				edbTextArea.setText(datalogGenerator.getEDBFacts());
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
			} catch (parserIDBQueryToJava.ParseException e) {
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
			
				for (ArrayList<String> answer : answers)	{				
					//bei add: neu generierte Fakten per put in json schreiben
					if (!query.getIdbRelation().getKind().startsWith("latest") && !query.getIdbRelation().getKind().startsWith("legacy") && !uiTextArea.getText().startsWith("get")){					
						String values = "";
						for (String s : answer)
						{
						    values += s + ", ";
						}
						
						values = values.substring(0, values.length()-2);
						String tempKind = query.getIdbRelation().getKind();
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
