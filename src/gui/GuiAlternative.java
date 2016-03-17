package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;

import javax.swing.border.LineBorder;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import lazyMigration.LazyMigration;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserPutToDatalog.ParserForPut;
import parserQueryToDatalogToJava.ParserQueryToDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import database.Database;
import datalog.Condition;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import datalog.RuleBody;
import eagerMigration.EagerMigration;

import java.awt.Font;

import javax.swing.JCheckBox;

import java.awt.GridLayout;

import javax.swing.border.MatteBorder;

import java.awt.SystemColor;
import javax.swing.SwingConstants;

public class GuiAlternative extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panelCommand;
	Database databaseBU = new Database("data/EDBEager.json", "data/Schema.json");
	Database databaseTD = new Database("data/EDBLazy.json", "data/Schema.json");
	String factsEager = "";
	String factsLazy = "";
	int totalPutsBU = 0;
	int totalPutsTD = 0;
	int totalDBEntriesBU = 0;
	int totalDBEntriesTD = 0;
	ArrayList<Rule> rulesForTD = null;
	String kind = "";
	private JTextField txtFieldCommand;
	private JTextField txtFieldDBEntriesEager;
	private JTextField txtFieldTotalPutsEager;
	private JTextField txtFieldPrevPutsEager;
	private JTextField txtFieldDBEntriesLazy;
	private JTextField txtFieldTotalPutsLazy;
	private JTextField txtFieldNrPrevDBLazy;
	private JTextArea txtAreaRules;
	private JTextArea txtAreaResultsEager;
	private JTextArea txtAreaResultsLazy;
	private JScrollPane scrollPaneRules;
	private JTextArea txtAreaFactsLazy;
	private JTextArea txtAreaFactsEager;

	/**
	 * Create the application.
	 */
	public GuiAlternative() {
		setResizable(false);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("DatalogMigration");
		setBounds(100, 80, 805, 594);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 2));
		
		panelCommand = new JPanel();
		getContentPane().add(panelCommand);
		panelCommand.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelCommand.setLayout(new GridLayout(0, 1, 0, 1));

		JPanel panelEmpty = new JPanel();
		panelEmpty.setBackground(SystemColor.control);
		panelEmpty.setForeground(Color.LIGHT_GRAY);
		panelEmpty.setBorder(null);
		panelCommand.add(panelEmpty);
		panelEmpty.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel.setBounds(0, 0, 797, 44);
		panelEmpty.add(panel);
		panel.setLayout(null);
		
		txtFieldCommand = new JTextField();
		txtFieldCommand.setBounds(140, 11, 414, 20);
		panel.add(txtFieldCommand);
		txtFieldCommand.setColumns(35);
		
		JButton btCommand = new JButton("Execute Command");
		btCommand.setBounds(566, 11, 144, 25);
		panel.add(btCommand);
		btCommand.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JButton btInfo = new JButton("Info");
		btInfo.setBounds(720, 13, 62, 23);
		panel.add(btInfo);
		btInfo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"Commands:\n\nadd kind.attribute=value\n e.g. add Player.points=100 or add Player.name=\"100\"\n\ndelete kind.attribute\n e.g. delete Player.points\n\n"
										+ "copy kind1.attribute to kind2 where kind1.attribute1=kind2.attribute2\n e.g. copy Player.score to Mission where Player.id=Mission.pid\n\n"
										+ "move kind1.attribute to kind2 where kind1.attribute1=kind2.attribute2\n e.g. move Player.score to Mission where Player.id=Mission.pid\n\n"
										+ "get kind.id=value\n e.g. get Player.id=1\n\nput kind(value1,value2,...) e.g. put Player(1,'Lisa',...)",
								"info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblCommand = new JLabel(" Command Prompt:");
		lblCommand.setBounds(10, 11, 122, 16);
		panel.add(lblCommand);
		lblCommand.setFont(new Font("Tahoma", Font.BOLD, 13));
		btCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String uiInput = txtFieldCommand.getText();
				if (uiInput.startsWith("get")) {
					executeGetCommand(uiInput);
				} else if (uiInput.startsWith("add")
						|| uiInput.startsWith("delete")
						|| uiInput.startsWith("move")
						|| uiInput.startsWith("copy")) {
					executeCommand(uiInput);
				} else if (uiInput.startsWith("put")) {
					// To do : eager migration
				} else {
					txtAreaRules.setText("No valid query");
				}

			}

		});		
		
		JLabel lbPrevPutsEager = new JLabel("Number of previous DB puts");
		lbPrevPutsEager.setBounds(107, 519, 215, 14);
		panelEmpty.add(lbPrevPutsEager);
		
		JLabel lbDBEntriesEager = new JLabel("Number of DB entries");
		lbDBEntriesEager.setBounds(107, 457, 215, 14);
		panelEmpty.add(lbDBEntriesEager);
		
		JLabel lbTotalPutsEager = new JLabel("Number of total DB puts");
		lbTotalPutsEager.setBounds(107, 488, 215, 14);
		panelEmpty.add(lbTotalPutsEager);
		
		txtFieldDBEntriesEager = new JTextField();
		txtFieldDBEntriesEager.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFieldDBEntriesEager.setText("0");
		txtFieldDBEntriesEager.setEditable(false);
		txtFieldDBEntriesEager.setColumns(10);
		txtFieldDBEntriesEager.setBounds(382, 457, 49, 20);
		panelEmpty.add(txtFieldDBEntriesEager);
		
		txtFieldTotalPutsEager = new JTextField();
		txtFieldTotalPutsEager.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFieldTotalPutsEager.setText("0");
		txtFieldTotalPutsEager.setEditable(false);
		txtFieldTotalPutsEager.setColumns(10);
		txtFieldTotalPutsEager.setBounds(382, 488, 49, 20);
		panelEmpty.add(txtFieldTotalPutsEager);
		
		txtFieldPrevPutsEager = new JTextField();
		txtFieldPrevPutsEager.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFieldPrevPutsEager.setText("0");
		txtFieldPrevPutsEager.setEditable(false);
		txtFieldPrevPutsEager.setColumns(10);
		txtFieldPrevPutsEager.setBounds(382, 519, 49, 20);
		panelEmpty.add(txtFieldPrevPutsEager);
		
		txtFieldDBEntriesLazy = new JTextField();
		txtFieldDBEntriesLazy.setText("0");
		txtFieldDBEntriesLazy.setEditable(false);
		txtFieldDBEntriesLazy.setColumns(10);
		txtFieldDBEntriesLazy.setBounds(448, 457, 49, 20);
		panelEmpty.add(txtFieldDBEntriesLazy);
		
		txtFieldTotalPutsLazy = new JTextField();
		txtFieldTotalPutsLazy.setText("0");
		txtFieldTotalPutsLazy.setEditable(false);
		txtFieldTotalPutsLazy.setColumns(10);
		txtFieldTotalPutsLazy.setBounds(448, 488, 49, 20);
		panelEmpty.add(txtFieldTotalPutsLazy);
		
		txtFieldNrPrevDBLazy = new JTextField();
		txtFieldNrPrevDBLazy.setText("0");
		txtFieldNrPrevDBLazy.setEditable(false);
		txtFieldNrPrevDBLazy.setColumns(10);
		txtFieldNrPrevDBLazy.setBounds(448, 519, 49, 20);
		panelEmpty.add(txtFieldNrPrevDBLazy);
		
		JCheckBox cbDatalogRules = new JCheckBox("view generated rules");
		cbDatalogRules.setBackground(SystemColor.control);
		cbDatalogRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				txtAreaRules.setVisible(!txtAreaRules.isVisible());
				scrollPaneRules.setVisible(!scrollPaneRules.isVisible());

			}

		});
		cbDatalogRules.setBounds(107, 58, 145, 23);
		panelEmpty.add(cbDatalogRules);
		
		JLabel lblDatalogView = new JLabel("DATALOG VIEW");
		lblDatalogView.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDatalogView.setBounds(10, 62, 91, 14);
		panelEmpty.add(lblDatalogView);
		
		JLabel lblSummary = new JLabel("SUMMARY");
		lblSummary.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSummary.setBounds(10, 458, 66, 14);
		panelEmpty.add(lblSummary);
		
		JLabel lblDbEntries = new JLabel("DB ENTRIES");
		lblDbEntries.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDbEntries.setBounds(10, 199, 66, 14);
		panelEmpty.add(lblDbEntries);
		
		JLabel lblEagerMigration = new JLabel("Eager Migration");
		lblEagerMigration.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEagerMigration.setBounds(214, 167, 123, 23);
		panelEmpty.add(lblEagerMigration);
		
		JLabel lblLazyMigration = new JLabel("Lazy Migration");
		lblLazyMigration.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblLazyMigration.setBounds(566, 166, 107, 25);
		panelEmpty.add(lblLazyMigration);
		
		factsEager = databaseBU.getJson();
		factsLazy = databaseTD.getJson();
		
		txtAreaFactsLazy = new JTextArea();
		txtAreaFactsLazy.setEditable(false);
		txtAreaFactsLazy.setText(factsLazy);
		
		txtAreaFactsEager = new JTextArea();
		txtAreaFactsEager.setEditable(false);
		txtAreaFactsEager.setText(factsEager);

		JScrollPane scrollPaneEager = new JScrollPane(txtAreaFactsEager);
		scrollPaneEager.setBounds(107, 193, 324, 190);
		scrollPaneEager.setViewportView(txtAreaFactsEager);	
		panelEmpty.add(scrollPaneEager);
		
		JScrollPane scrollPaneLazy = new JScrollPane(txtAreaFactsLazy);
		scrollPaneLazy.setBounds(448, 193, 324, 190);
		panelEmpty.add(scrollPaneLazy);
		
		txtAreaRules = new JTextArea();
		txtAreaRules.setEditable(false);
		txtAreaRules.setBounds(20, 11, 796, 66);
		txtAreaRules.setVisible(false);
				
		scrollPaneRules = new JScrollPane(txtAreaRules);
		scrollPaneRules.setEnabled(false);
		scrollPaneRules.setBounds(107, 88, 665, 68);
		scrollPaneRules.setVisible(false);
		panelEmpty.add(scrollPaneRules);
				
				
		
		totalDBEntriesBU = factsEager.split("\n").length;

		totalDBEntriesTD = factsLazy.split("\n").length;
		
		txtFieldDBEntriesEager.setText(String.valueOf(totalDBEntriesBU));
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));
				
		txtAreaResultsLazy = new JTextArea();
		txtAreaResultsLazy.setEditable(false);
		
		txtAreaResultsEager = new JTextArea();
		txtAreaResultsEager.setEditable(false);
		
		JScrollPane scrollPaneResultsEager = new JScrollPane();
		scrollPaneResultsEager.setBounds(107, 402, 324, 44);
		scrollPaneResultsEager.setViewportView(txtAreaResultsEager);	
		panelEmpty.add(scrollPaneResultsEager);
				
		JScrollPane scrollPaneResultsLazy = new JScrollPane();
		scrollPaneResultsLazy.setBounds(448, 402, 324, 44);
		scrollPaneResultsLazy.setViewportView(txtAreaResultsLazy);	
		panelEmpty.add(scrollPaneResultsLazy);

				
		JLabel lblResults = new JLabel("RESULTS");
		lblResults.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblResults.setBounds(10, 402, 49, 25);
		panelEmpty.add(lblResults);
		
	}

	private void executeGetCommand(String uiInput) {
		String id = "";
		ParserQueryToDatalogToJava parserget = new ParserQueryToDatalogToJava(
				new StringReader(uiInput));
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		if (rulesForTD != null)
			rulesTemp = copyRules();

		try {
			rulesTemp.addAll(parserget.getJavaRules());
		} catch (parserQueryToDatalogToJava.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		kind = parserget.kindStr;
		id = parserget.idStr;
		// start lazy migration

		Database db = new Database();
		// test für add, copy and get
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("kind", kind);
		attributeMap.put("position", "0");
		attributeMap.put("value", id);
		ArrayList<Map<String, String>> uniMap = new ArrayList<Map<String, String>>();
		uniMap.add(attributeMap);
		ArrayList<String> schema = db.getLatestSchema(kind).getAttributes();
		schema.add("?ts");

		Predicate goal = new Predicate("get" + kind
				+ db.getLatestSchemaVersion(kind), schema.size(), schema);
		executeQueryTD(rulesTemp, goal, uniMap);
		txtAreaRules.setText(parserget.rulesStr);
		executeQueryBU(parserget.rulesStr);
	}

	private ArrayList<Rule> copyRules() {
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		for (Rule r : rulesForTD) {
			@SuppressWarnings("unchecked")
			ArrayList<String> scheme = (ArrayList<String>) r.getHead()
					.getScheme().clone();
			Predicate newHead = new Predicate(r.getHead().getKind(), r
					.getHead().getAnz(), scheme);
			newHead.setHead(r.getHead().isHead());
			ArrayList<Predicate> predicates = new ArrayList<Predicate>();
			for (Predicate p : r.getPredicates()) {
				@SuppressWarnings("unchecked")
				ArrayList<String> scheme2 = (ArrayList<String>) p.getScheme()
						.clone();
				Predicate p2 = new Predicate(p.getKind(), p.getAnz(), scheme2);
				p2.setNot(p.isNot());
				predicates.add(p2);
			}
			ArrayList<Condition> conditions = new ArrayList<Condition>();
			for (Condition c : r.getConditions())
				conditions.add(new Condition(c.getLeftOperand(), c
						.getRightOperand(), c.getOperator()));
			RuleBody newRuleBody = new RuleBody(predicates, conditions);
			Rule rNew = new Rule(newHead, newRuleBody);

			rulesTemp.add(rNew);
		}
		return rulesTemp;
	}

	private void executeCommand(String uiInput) {
		ArrayList<Rule> rulesTemp = null;
		String rulesStr = "";
		if (rulesForTD == null)
			rulesForTD = new ArrayList<Rule>();
		try {
			ParserQueryToDatalogToJava parserQuery = new ParserQueryToDatalogToJava(
					new StringReader(uiInput));
			rulesTemp = parserQuery.getJavaRules();
			rulesForTD.addAll(rulesTemp);
			rulesStr = parserQuery.rulesStr;

		} catch (parserQueryToDatalogToJava.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txtAreaRules.setText(rulesStr);
		txtFieldNrPrevDBLazy.setText(String.valueOf(0));
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));
		executeQueryBU(rulesStr);
		JOptionPane.showMessageDialog(new JFrame(),
				"successfully added schema change", "dialog",
				JOptionPane.INFORMATION_MESSAGE);
		txtAreaResultsLazy.setText("");
		txtAreaResultsEager.setText("");
	}

	private void executeQueryBU(String rulesStr) {
		// set edb-facts in gui
		String[] edbFacts = databaseBU.getEDB().split("\n");
		String query = txtFieldCommand.getText();
		ArrayList<Rule> rules = null;
		ArrayList<Fact> facts = new ArrayList<Fact>();
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
			rules = new ParserRuleToJava(new StringReader(rulesStr)).start();
		} catch (parserRuletoJava.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EagerMigration migrate = new EagerMigration(facts, rules, query);
		String answerString = migrate.writeAnswersInDatabase();
		txtFieldPrevPutsEager.setText(String.valueOf(migrate.getNumber()));
		
		totalPutsBU = totalPutsBU + migrate.getNumber();
		txtFieldTotalPutsEager.setText(String.valueOf(totalPutsBU));
		
		totalDBEntriesBU = totalDBEntriesBU + totalPutsBU;
		txtFieldDBEntriesEager.setText(String.valueOf(totalPutsBU));
		
		factsEager = databaseBU.getJson();
		txtAreaFactsEager.setText(factsEager);
		if (query.startsWith("get")) {
			int nr = databaseBU.getLatestSchemaVersion(kind);
			String json = null;
			try {
				json = new ParserForPut(new StringReader(kind + nr + "("
						+ answerString.replace("[", "").replace("]", "") + ")"))
						.start2();
			} catch (parserPutToDatalog.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			txtAreaResultsEager.setText(json);
		} else
			txtAreaResultsEager.setText("");
		txtFieldDBEntriesEager
				.setText(String.valueOf(factsEager.split("\n").length));

	}

	private void executeQueryTD(ArrayList<Rule> rulesTemp, Predicate goal,
			ArrayList<Map<String, String>> uniMap) {
		// set edb-facts in gui
		String[] edbFacts = databaseTD.getEDB().split("\n");
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (String factString : edbFacts) {
			try {
				facts.add(new ParserforDatalogToJava(new StringReader(
						factString)).start());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LazyMigration migrate = new LazyMigration(facts, rulesTemp, goal,
				uniMap);
		String answerString = migrate.writeAnswersInDatabase();
		txtFieldNrPrevDBLazy
				.setText(String.valueOf(migrate.getNumberOfPuts()));
		totalPutsTD = totalPutsTD + migrate.getNumberOfPuts();
		txtFieldTotalPutsLazy.setText(String.valueOf(totalPutsTD));
		
		totalDBEntriesTD = totalDBEntriesTD + totalPutsTD;
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));
		
		factsLazy = databaseTD.getJson();
		txtAreaFactsLazy.setText(factsLazy);
		int nr = databaseTD.getLatestSchemaVersion(kind);
		String json = null;
		try {
			json = new ParserForPut(new StringReader(kind
					+ nr
					+ "("
					+ answerString.replace("[", "").replace("]", "")
							.replace(".", "")
							.substring(answerString.indexOf("(")))).start2();
		} catch (parserPutToDatalog.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(json);
		txtAreaResultsLazy.setText(json);
		txtFieldDBEntriesLazy
				.setText(String.valueOf(factsLazy.split("\n").length));
	}
}
