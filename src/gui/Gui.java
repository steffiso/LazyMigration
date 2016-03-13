package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JCheckBox;

import java.awt.GridLayout;

import javax.swing.border.MatteBorder;

import java.awt.SystemColor;

public class Gui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField commandTextField;
	JTextArea rulesTextArea;
	JPanel panelCommand;
	JScrollPane scrollPane;
	Database databaseBU = new Database("data/EDBEager.json", "data/Schema.json");
	Database databaseTD = new Database("data/EDBLazy.json", "data/Schema.json");
	String factsBU = "";
	String factsTD = "";
	int totalNumberBU = 0;
	int totalNumberTD = 0;
	int totalDBEntriesBU = 0;
	int totalDBEntriesTD = 0;
	private JTextField textFieldNrDBEager;
	private JTextField textFieldNrDBLazy;
	JLabel lblRule;
	JTextArea factsTextAreaTD;
	JTextArea factsTextAreaBU;
	JTabbedPane tabbedPane;
	JPanel panelMigration;
	private JTextField textFieldNrPrevDBEager;
	private JTextField textFieldNrPrevDBLazy;
	ArrayList<Rule> rulesForTD = null;
	private JTextField textFieldResultsBU;
	private JTextField textFieldResultsTD;
	private JTextField textFieldNrDBEntriesBU;
	private JTextField textFieldNrDBEntriesTD;
	String kind = "";

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("DatalogMigration");
		setBounds(100, 10, 817, 647);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 2));

		panelCommand = new JPanel();
		getContentPane().add(panelCommand);
		panelCommand.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelCommand.setLayout(new GridLayout(0, 1, 0, 1));

		JPanel panelCommandPrompt = new JPanel();
		panelCommandPrompt.setBorder(new MatteBorder(2, 2, 0, 2,
				(Color) Color.DARK_GRAY));
		FlowLayout fl_panelCommandPrompt = (FlowLayout) panelCommandPrompt
				.getLayout();
		fl_panelCommandPrompt.setAlignment(FlowLayout.LEFT);
		fl_panelCommandPrompt.setVgap(20);
		panelCommand.add(panelCommandPrompt);

		JLabel lblCommandoPrompt = new JLabel(" Command Prompt:");
		panelCommandPrompt.add(lblCommandoPrompt);
		lblCommandoPrompt.setFont(new Font("Tahoma", Font.PLAIN, 13));

		commandTextField = new JTextField();
		panelCommandPrompt.add(commandTextField);
		commandTextField.setColumns(35);

		JButton execCommandBtnBU = new JButton("Execute Command");
		panelCommandPrompt.add(execCommandBtnBU);
		execCommandBtnBU.setFont(new Font("Tahoma", Font.PLAIN, 13));

		JButton btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
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
		btnInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelCommandPrompt.add(btnInfo);
		execCommandBtnBU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String uiInput = commandTextField.getText();
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
					rulesTextArea.setText("No valid query");
				}

			}

		});

		JPanel panelDatalogView = new JPanel();
		panelDatalogView.setBorder(new MatteBorder(1, 2, 2, 2,
				(Color) Color.DARK_GRAY));
		FlowLayout fl_panelDatalogView = (FlowLayout) panelDatalogView
				.getLayout();
		fl_panelDatalogView.setAlignment(FlowLayout.LEFT);
		fl_panelDatalogView.setVgap(10);
		panelCommand.add(panelDatalogView);

		JLabel lblDatalogView = new JLabel("        Datalog View:");
		lblDatalogView.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panelDatalogView.add(lblDatalogView);

		JCheckBox chckbxNewCheckBox = new JCheckBox("view generated rules");
		panelDatalogView.add(chckbxNewCheckBox);

		lblRule = new JLabel("   Rules:");
		lblRule.setVisible(false);
		panelDatalogView.add(lblRule);

		rulesTextArea = new JTextArea();
		rulesTextArea.setEditable(false);
		rulesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		rulesTextArea.setRows(3);
		rulesTextArea.setColumns(65);
		rulesTextArea.setVisible(false);
		scrollPane = new JScrollPane(rulesTextArea);
		panelDatalogView.add(scrollPane);
		scrollPane.setVisible(false);

		chckbxNewCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rulesTextArea.setVisible(!rulesTextArea.isVisible());
				lblRule.setVisible(!lblRule.isVisible());
				scrollPane.setVisible(!scrollPane.isVisible());

			}

		});

		JPanel panelEmpty = new JPanel();
		panelEmpty.setBackground(SystemColor.controlHighlight);
		panelEmpty.setForeground(Color.LIGHT_GRAY);
		panelEmpty.setBorder(null);
		panelCommand.add(panelEmpty);

		JPanel panelViewFactsLabel = new JPanel();
		panelViewFactsLabel.setBorder(new MatteBorder(2, 2, 0, 2,
				(Color) new Color(64, 64, 64)));
		FlowLayout fl_panelViewFactsLabel = (FlowLayout) panelViewFactsLabel
				.getLayout();
		fl_panelViewFactsLabel.setVgap(48);
		panelCommand.add(panelViewFactsLabel);

		JLabel lblViewAllFacts = new JLabel("View all DB entries and Results:");
		lblViewAllFacts.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panelViewFactsLabel.add(lblViewAllFacts);

		JPanel panelTabbedPane = new JPanel();
		panelTabbedPane.setBorder(new MatteBorder(0, 2, 2, 2,
				(Color) new Color(64, 64, 64)));
		getContentPane().add(panelTabbedPane);
		panelTabbedPane.setLayout(new GridLayout(0, 1, 0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelTabbedPane.add(tabbedPane);
		panelMigration = new JPanel();
		tabbedPane.addTab("Migration", null, panelMigration, null);
		panelMigration.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(0.5);

		panelMigration.add(splitPane, BorderLayout.CENTER);
		JPanel panelLabels = new JPanel();
		panelMigration.add(panelLabels, BorderLayout.NORTH);
		panelLabels.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel lblEagermigration = new JLabel("  EagerMigration");
		lblEagermigration.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panelLabels.add(lblEagermigration);
		JPanel panelResults = new JPanel();
		panelMigration.add(panelResults, BorderLayout.SOUTH);
		panelResults.setLayout(new GridLayout(2, 2, 2, 0));

		JLabel lblResultseager = new JLabel("   Results Eager");
		panelResults.add(lblResultseager);

		JLabel lblRresultslazy = new JLabel("   Results Lazy");
		panelResults.add(lblRresultslazy);

		textFieldResultsBU = new JTextField();
		textFieldResultsBU.setEditable(false);
		panelResults.add(textFieldResultsBU);
		textFieldResultsBU.setColumns(10);

		textFieldResultsTD = new JTextField();
		textFieldResultsTD.setEditable(false);
		panelResults.add(textFieldResultsTD);
		textFieldResultsTD.setColumns(10);
		JLabel lblLazymigration = new JLabel("   LazyMigration");
		lblLazymigration.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panelLabels.add(lblLazymigration);
		factsTextAreaBU = new JTextArea();
		factsTextAreaBU.setEditable(false);
		JScrollPane scrollFactsBU = new JScrollPane(factsTextAreaBU);
		scrollFactsBU.setBounds(10, 25, 330, 226);
		splitPane.add(scrollFactsBU);
		factsTextAreaTD = new JTextArea();
		factsTextAreaTD.setEditable(false);
		JScrollPane scrollFactsTD = new JScrollPane(factsTextAreaTD);
		scrollFactsTD.setBounds(368, 25, 330, 226);
		splitPane.add(scrollFactsTD);

		factsBU = databaseBU.getJson();
		factsTD = databaseTD.getJson();

		factsTextAreaBU.setText(factsBU);
		factsTextAreaTD.setText(factsTD);

		JPanel panelComparison = new JPanel();
		tabbedPane.addTab("Comparison", null, panelComparison, null);
		panelComparison.setLayout(null);

		JPanel panelComparisonBU = new JPanel();
		panelComparisonBU.setBounds(10, 11, 270, 227);
		panelComparison.add(panelComparisonBU);
		panelComparisonBU.setLayout(null);

		JLabel lblBottomUpSummary = new JLabel("EagerMigration Summary");
		lblBottomUpSummary.setBounds(10, 11, 186, 14);
		panelComparisonBU.add(lblBottomUpSummary);

		textFieldNrDBEager = new JTextField();
		textFieldNrDBEager.setEditable(false);
		textFieldNrDBEager.setBounds(10, 125, 86, 20);
		panelComparisonBU.add(textFieldNrDBEager);
		textFieldNrDBEager.setColumns(10);

		JLabel lblNumberOfDb = new JLabel("Number of total DB puts");
		lblNumberOfDb.setBounds(10, 100, 186, 14);
		panelComparisonBU.add(lblNumberOfDb);

		JLabel lblNumberOfPreviousBU = new JLabel("Number of previous DB puts");
		lblNumberOfPreviousBU.setBounds(10, 156, 186, 14);
		panelComparisonBU.add(lblNumberOfPreviousBU);

		textFieldNrPrevDBEager = new JTextField();
		textFieldNrPrevDBEager.setEditable(false);
		textFieldNrPrevDBEager.setColumns(10);
		textFieldNrPrevDBEager.setBounds(10, 180, 86, 20);
		panelComparisonBU.add(textFieldNrPrevDBEager);

		JLabel lblNumberOfDbBU = new JLabel("Number of DB entries");
		lblNumberOfDbBU.setBounds(10, 44, 167, 14);
		panelComparisonBU.add(lblNumberOfDbBU);

		textFieldNrDBEntriesBU = new JTextField();
		textFieldNrDBEntriesBU.setEditable(false);
		textFieldNrDBEntriesBU.setColumns(10);
		textFieldNrDBEntriesBU.setBounds(10, 69, 86, 20);
		panelComparisonBU.add(textFieldNrDBEntriesBU);

		totalDBEntriesBU = factsBU.split("\n").length;
		textFieldNrDBEntriesBU.setText(String.valueOf(totalDBEntriesBU));

		JPanel panelComparisonTD = new JPanel();
		panelComparisonTD.setLayout(null);
		panelComparisonTD.setBounds(372, 11, 270, 227);
		panelComparison.add(panelComparisonTD);

		JLabel lblTopDownSummary = new JLabel("LazyMigration Summary");
		lblTopDownSummary.setBounds(10, 11, 186, 14);
		panelComparisonTD.add(lblTopDownSummary);

		textFieldNrDBLazy = new JTextField();
		textFieldNrDBLazy.setEditable(false);
		textFieldNrDBLazy.setColumns(10);
		textFieldNrDBLazy.setBounds(10, 124, 86, 20);
		panelComparisonTD.add(textFieldNrDBLazy);

		JLabel lblNumberOfTotal = new JLabel("Number of total DB puts");
		lblNumberOfTotal.setBounds(10, 99, 186, 14);
		panelComparisonTD.add(lblNumberOfTotal);

		textFieldNrPrevDBLazy = new JTextField();
		textFieldNrPrevDBLazy.setEditable(false);
		textFieldNrPrevDBLazy.setColumns(10);
		textFieldNrPrevDBLazy.setBounds(10, 180, 86, 20);
		panelComparisonTD.add(textFieldNrPrevDBLazy);

		JLabel lblNumberOfPreviousTD = new JLabel("Number of previous DB puts");
		lblNumberOfPreviousTD.setBounds(10, 155, 186, 14);
		panelComparisonTD.add(lblNumberOfPreviousTD);

		JLabel label = new JLabel("Number of DB entries");
		label.setBounds(10, 43, 167, 14);
		panelComparisonTD.add(label);

		textFieldNrDBEntriesTD = new JTextField();
		textFieldNrDBEntriesTD.setEditable(false);
		textFieldNrDBEntriesTD.setColumns(10);
		textFieldNrDBEntriesTD.setBounds(10, 68, 86, 20);
		panelComparisonTD.add(textFieldNrDBEntriesTD);

		totalDBEntriesTD = factsTD.split("\n").length;
		textFieldNrDBEntriesTD.setText(String.valueOf(totalDBEntriesTD));
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
		rulesTextArea.setText(parserget.rulesStr);
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
		rulesTextArea.setText(rulesStr);
		textFieldNrPrevDBLazy.setText(String.valueOf(0));
		textFieldNrDBLazy.setText(String.valueOf(totalNumberTD));
		executeQueryBU(rulesStr);
		JOptionPane.showMessageDialog(new JFrame(),
				"successfully added schema change", "dialog",
				JOptionPane.INFORMATION_MESSAGE);
		textFieldResultsTD.setText("");
	}

	private void executeQueryBU(String rulesStr) {
		// set edb-facts in gui
		String[] edbFacts = databaseBU.getEDB().split("\n");
		String query = commandTextField.getText();
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
		textFieldNrPrevDBEager.setText(String.valueOf(migrate.getNumber()));
		totalNumberBU = totalNumberBU + migrate.getNumber();

		textFieldNrDBEager.setText(String.valueOf(totalNumberBU));
		totalDBEntriesBU = totalDBEntriesBU + totalNumberBU;
		factsBU = databaseBU.getJson();
		factsTextAreaBU.setText(factsBU);
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
			textFieldResultsBU.setText(json);
		} else
			textFieldResultsBU.setText("");
		textFieldNrDBEntriesBU
				.setText(String.valueOf(factsBU.split("\n").length));

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
		textFieldNrPrevDBLazy
				.setText(String.valueOf(migrate.getNumberOfPuts()));
		totalNumberTD = totalNumberTD + migrate.getNumberOfPuts();

		textFieldNrDBLazy.setText(String.valueOf(totalNumberTD));
		totalDBEntriesTD = totalDBEntriesTD + totalNumberTD;
		factsTD = databaseTD.getJson();
		factsTextAreaTD.setText(factsTD);
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
		textFieldResultsTD.setText(json);
		textFieldNrDBEntriesTD
				.setText(String.valueOf(factsTD.split("\n").length));
	}
}
