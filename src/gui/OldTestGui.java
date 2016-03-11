package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;

import javax.swing.border.LineBorder;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import lazyMigration.LazyMigration;
import parserEDBFactToJava.ParseException;
import parserEDBFactToJava.ParserforDatalogToJava;
import parserRuletoJava.ParserRuleToJava;
import database.Database;
import datalog.DatalogRulesGenerator;
import datalog.Fact;
import datalog.Predicate;
import datalog.Rule;
import eagerMigration.EagerMigration;

public class OldTestGui {

	private JFrame frmBottomup;
	private JTextField commandTextFieldBU;
	private JTextField commandTextFieldTD;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	JTextArea rulesTextAreaBU;
	JTextArea resultsTxtAreaBU;
	JTextArea factsTextAreaBU;
	JTextArea factsTextAreaTD;
	JTextArea resultsTextAreaTD;
	JTextArea rulestextAreaTD;
	JTabbedPane tabbedPane;
	JPanel panelBottomUp;
	JPanel panelBUFacts;
	JPanel panelBUCommand;
	JPanel panelTopDown;
	JPanel panelFactsTD;
	JPanel panelCommandTD;
	ArrayList<Fact> facts = null;
	ArrayList<Rule> rules = null;
	DatalogRulesGenerator datalogGenerator = new DatalogRulesGenerator();
	Predicate goal = null;
	ArrayList<Map<String, String>> uniMap = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OldTestGui window = new OldTestGui();
					window.frmBottomup.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OldTestGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBottomup = new JFrame();
		frmBottomup.setTitle("BottomUp");
		frmBottomup.setBounds(100, 100, 736, 531);
		frmBottomup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBottomup.getContentPane().setLayout(null);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 720, 492);
		frmBottomup.getContentPane().add(tabbedPane);
		String[] functionStrings = { "Get", "Add", "Delete", "Copy",
				"Move", "Put" };
		panelBottomUp = new JPanel();
		tabbedPane.addTab("BottomUp", null, panelBottomUp, null);
		panelBottomUp.setLayout(null);

		panelBUFacts = new JPanel();
		panelBUFacts.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelBUFacts.setBounds(10, 11, 695, 231);
		panelBottomUp.add(panelBUFacts);
		panelBUFacts.setLayout(null);

		factsTextAreaBU = new JTextArea();
		factsTextAreaBU.setEditable(false);
		JScrollPane scrollFactsBU = new JScrollPane(factsTextAreaBU);
		scrollFactsBU.setBounds(10, 25, 325, 195);
		panelBUFacts.add(scrollFactsBU);

		resultsTxtAreaBU = new JTextArea();
		resultsTxtAreaBU.setEditable(false);
		JScrollPane scrollResultsBU = new JScrollPane(resultsTxtAreaBU);
		scrollResultsBU.setBounds(360, 25, 325, 195);
		panelBUFacts.add(scrollResultsBU);

		JLabel lblFacts = new JLabel("Facts");
		lblFacts.setBounds(10, 11, 46, 14);
		panelBUFacts.add(lblFacts);

		JLabel lblResults = new JLabel("Results");
		lblResults.setBounds(360, 11, 46, 14);
		panelBUFacts.add(lblResults);

		panelBUCommand = new JPanel();
		panelBUCommand.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelBUCommand.setBounds(10, 257, 695, 196);
		panelBottomUp.add(panelBUCommand);
		panelBUCommand.setLayout(null);

		commandTextFieldBU = new JTextField();
		commandTextFieldBU.setBounds(138, 28, 289, 23);
		panelBUCommand.add(commandTextFieldBU);
		commandTextFieldBU.setColumns(10);

		JLabel lblCommandoPrompt = new JLabel("Command Prompt");
		lblCommandoPrompt.setBounds(138, 11, 133, 14);
		panelBUCommand.add(lblCommandoPrompt);

		JButton execCommandBtnBU = new JButton("Execute Command");
		execCommandBtnBU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				String uiInput = "";
				String rules = "";
				uiInput = commandTextFieldBU.getText();
				DatalogRulesGenerator drg = new DatalogRulesGenerator();

				if (uiInput.startsWith("get")) {
					rules = drg.getRules(uiInput);
					rulesTextAreaBU.setText(rules);
				} else if (uiInput.startsWith("add")) {
					rules = drg.getRules(uiInput);
					rulesTextAreaBU.setText(rules);
				} else if (uiInput.startsWith("delete")) {
					rules = drg.getRules(uiInput);
					rulesTextAreaBU.setText(rules);
				} else if (uiInput.startsWith("move")) {
					rules = drg.getRules(uiInput);
					rulesTextAreaBU.setText(rules);
				} else if (uiInput.startsWith("copy")) {
					rules = drg.getRules(uiInput);
					rulesTextAreaBU.setText(rules);
				} else if (uiInput.startsWith("put")) {
					// To do : write in json file
				} else {
					rulesTextAreaBU.setText("No valid query");
				}

			}

		});
		execCommandBtnBU.setBounds(437, 28, 248, 23);
		panelBUCommand.add(execCommandBtnBU);

		rulesTextAreaBU = new JTextArea();
		JScrollPane scrollRulesBU = new JScrollPane(rulesTextAreaBU);
		scrollRulesBU.setBounds(10, 77, 675, 63);
		panelBUCommand.add(scrollRulesBU);

		JLabel lblRules = new JLabel("Rules");
		lblRules.setBounds(10, 62, 46, 14);
		panelBUCommand.add(lblRules);

		JButton generateBtnBU = new JButton("Generate Rules");
		generateBtnBU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!factsTextAreaBU.getText().equals("")
						&& !rulesTextAreaBU.getText().equals(""))
					executeQuery();
				else
					resultsTxtAreaBU.setText("No EDB or IDB found!");
			}
		});
		generateBtnBU.setBounds(10, 150, 675, 35);
		panelBUCommand.add(generateBtnBU);

		JLabel lblGenerateCommand = new JLabel("Generate Command");
		lblGenerateCommand.setBounds(10, 11, 118, 14);
		panelBUCommand.add(lblGenerateCommand);

		JComboBox<String> comboBoxBU = new JComboBox<String>(functionStrings);
		comboBoxBU.setBounds(10, 29, 118, 20);
		panelBUCommand.add(comboBoxBU);

		factsTextAreaBU.setText(datalogGenerator.getEDBFacts());

		// Top Down....
		panelTopDown = new JPanel();
		tabbedPane.addTab("TopDown", null, panelTopDown, null);
		panelTopDown.setLayout(null);

		panelFactsTD = new JPanel();
		panelFactsTD.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelFactsTD.setLayout(null);
		panelFactsTD.setBounds(10, 11, 695, 231);
		panelTopDown.add(panelFactsTD);

		factsTextAreaTD = new JTextArea();
		factsTextAreaTD.setEditable(false);
		JScrollPane scrollFactsTD = new JScrollPane(factsTextAreaTD);
		scrollFactsTD.setBounds(10, 25, 325, 195);
		panelFactsTD.add(scrollFactsTD);

		resultsTextAreaTD = new JTextArea();
		resultsTextAreaTD.setEditable(false);
		JScrollPane scrollResultsTD = new JScrollPane(resultsTextAreaTD);
		scrollResultsTD.setBounds(360, 25, 325, 195);
		panelFactsTD.add(scrollResultsTD);

		JLabel label = new JLabel("Facts");
		label.setBounds(10, 11, 46, 14);
		panelFactsTD.add(label);

		JLabel label_1 = new JLabel("Results");
		label_1.setBounds(360, 11, 46, 14);
		panelFactsTD.add(label_1);

		panelCommandTD = new JPanel();
		panelCommandTD.setLayout(null);
		panelCommandTD.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelCommandTD.setBounds(10, 257, 695, 196);
		panelTopDown.add(panelCommandTD);

		commandTextFieldTD = new JTextField();
		commandTextFieldTD.setColumns(10);
		commandTextFieldTD.setBounds(138, 28, 289, 23);
		panelCommandTD.add(commandTextFieldTD);

		JLabel label_2 = new JLabel("Command Prompt");
		label_2.setBounds(138, 11, 133, 14);
		panelCommandTD.add(label_2);

		JButton execCommandBtnTD = new JButton("Execute Command");
		execCommandBtnTD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				String uiInput = "";
				String rules = "";
				uiInput = commandTextFieldTD.getText();
				DatalogRulesGenerator drg = new DatalogRulesGenerator();

				if (uiInput.startsWith("get")) {
					String[] info = drg.getTD(uiInput);
					rules = info[0];
					rulestextAreaTD.append("\n" + rules);
					// start lazy migration
					String kind = info[1];
					String id = info[2];
					Database db = new Database();
					// test für add, copy and get
					Map<String, String> attributeMap = new TreeMap<String, String>();
					attributeMap.put("kind", kind);
					attributeMap.put("position", "0");
					attributeMap.put("value", id);
					uniMap = new ArrayList<Map<String, String>>();
					uniMap.add(attributeMap);
					ArrayList<String> schema = db.getLatestSchema(kind)
							.getAttributes();
					schema.add("?ts");

					goal = new Predicate("get" + kind
							+ db.getLatestSchemaVersion(kind), schema.size(),
							schema);
				} else if (uiInput.startsWith("add")) {
					rules = drg.getRules(uiInput);
					rulestextAreaTD.append("\n" + rules);
				} else if (uiInput.startsWith("delete")) {
					rules = drg.getRules(uiInput);
					rulestextAreaTD.append("\n" + rules);
				} else if (uiInput.startsWith("move")) {
					rules = drg.getRules(uiInput);
					rulestextAreaTD.append("\n" + rules);
				} else if (uiInput.startsWith("copy")) {
					rules = drg.getRules(uiInput);
					rulestextAreaTD.append("\n" + rules);
				} else if (uiInput.startsWith("put")) {
					// To do : lazy migration
				} else {
					rulestextAreaTD.setText("No valid query");
				}

			}
		});
		execCommandBtnTD.setBounds(437, 28, 248, 23);
		panelCommandTD.add(execCommandBtnTD);

		rulestextAreaTD = new JTextArea();
		JScrollPane scrollRulesTD = new JScrollPane(rulestextAreaTD);
		scrollRulesTD.setBounds(10, 77, 675, 63);
		panelCommandTD.add(scrollRulesTD);

		JLabel label_3 = new JLabel("Rules");
		label_3.setBounds(10, 62, 46, 14);
		panelCommandTD.add(label_3);

		JButton generateBtnTD = new JButton("Generate Rules");
		generateBtnTD.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// set edb-facts in gui
				factsTextAreaTD.setText(datalogGenerator.getEDBFacts());
				String[] edbFacts = factsTextAreaTD.getText().split("\n");

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
							rulestextAreaTD.getText())).start();
				} catch (parserRuletoJava.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LazyMigration migrate = new LazyMigration(facts, rules, goal,
						uniMap);
				String answerString = migrate.writeAnswersInDatabase();
				resultsTextAreaTD.setText(answerString);
				factsTextAreaTD.setText(datalogGenerator.getEDBFacts());
			}

		});
		generateBtnTD.setBounds(10, 150, 675, 35);
		panelCommandTD.add(generateBtnTD);

		JLabel label_4 = new JLabel("Generate Command");
		label_4.setBounds(10, 11, 118, 14);
		panelCommandTD.add(label_4);

		JComboBox<String> comboBoxTD = new JComboBox<String>(functionStrings);
		comboBoxTD.setBounds(10, 29, 118, 20);
		panelCommandTD.add(comboBoxTD);
		factsTextAreaTD.setText(datalogGenerator.getEDBFacts());
		
		JPanel panel3 = new JPanel();
		tabbedPane.addTab("Comparison", null, panel3, null);
		panel3.setLayout(null);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(10, 11, 333, 442);
		panel3.add(panel_5);
		panel_5.setLayout(null);

		JLabel lblBottomUpSummary = new JLabel("Bottom Up Summary");
		lblBottomUpSummary.setBounds(10, 11, 186, 14);
		panel_5.add(lblBottomUpSummary);

		textField_2 = new JTextField();
		textField_2.setBounds(10, 54, 86, 20);
		panel_5.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(10, 213, 86, 20);
		panel_5.add(textField_3);

		JLabel lblNumberOfDb = new JLabel("Number of DB puts");
		lblNumberOfDb.setBounds(10, 36, 116, 14);
		panel_5.add(lblNumberOfDb);

		JLabel lblNumberOfAuxiliary = new JLabel("Number of auxiliary facts");
		lblNumberOfAuxiliary.setBounds(10, 194, 155, 14);
		panel_5.add(lblNumberOfAuxiliary);

		JPanel panel_6 = new JPanel();
		panel_6.setLayout(null);
		panel_6.setBounds(372, 11, 333, 442);
		panel3.add(panel_6);

		JLabel lblTopDownSummary = new JLabel("Top Down Summary");
		lblTopDownSummary.setBounds(10, 11, 186, 14);
		panel_6.add(lblTopDownSummary);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(10, 54, 86, 20);
		panel_6.add(textField_4);

		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(10, 213, 86, 20);
		panel_6.add(textField_5);

		JLabel label_6 = new JLabel("Number of DB puts");
		label_6.setBounds(10, 36, 116, 14);
		panel_6.add(label_6);

		JLabel label_7 = new JLabel("Number of auxiliary facts");
		label_7.setBounds(10, 194, 155, 14);
		panel_6.add(label_7);

	}

	private void executeQuery() {
		// set edb-facts in gui
		factsTextAreaTD.setText(datalogGenerator.getEDBFacts());
		String[] edbFacts = factsTextAreaBU.getText().split("\n");
		String query = commandTextFieldBU.getText();

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
					rulesTextAreaBU.getText())).start();
		} catch (parserRuletoJava.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EagerMigration migrate = new EagerMigration(facts, rules, query);
		String answerString = migrate.writeAnswersInDatabase();
		factsTextAreaBU.setText(datalogGenerator.getEDBFacts());
		resultsTxtAreaBU.setText(answerString);
	}

}
