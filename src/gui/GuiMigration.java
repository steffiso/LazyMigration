package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.ComponentOrientation;

import javax.swing.border.LineBorder;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class GuiMigration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panelMigration;
	Database databaseBU = new Database("/data/EDBEager.json",
			"/data/Schema.json");
	Database databaseTD = new Database("/data/EDBLazy.json",
			"/data/Schema.json");
	String factsEager = "";
	String factsLazy = "";
	int totalPutsBU = 0;
	int totalPutsTD = 0;
	int totalDBEntriesBU = 0;
	int totalDBEntriesTD = 0;
	Set<Rule> rulesForTD = null;
	String kind = "";
	private JTextField txtFieldCommand;
	private JTextField txtFieldDBEntriesEager;
	private JTextField txtFieldDBEntriesLazy;
	private JTextArea txtAreaRules;
	private JTextArea txtAreaResultsEager;
	private JTextArea txtAreaResultsLazy;
	private JScrollPane scrollPaneRules;
	private JTextArea txtAreaFactsLazy;
	private JTextArea txtAreaFactsEager;
	JScrollPane scrollPaneResultsEager;
	JScrollPane scrollPaneResultsLazy;

	/**
	 * Create the application.
	 */
	public GuiMigration() {
		setResizable(false);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	private void initialize() {
		setTitle("Datalution");
		setBounds(200, 80, 959, 609);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 2));

		// reset database to initial state
		try {
			databaseBU.resetDatabaseState();
			databaseTD.resetDatabaseState();
		} catch (IOException e) {
			showErrorLog(e.getMessage());
			return;
		}

		panelMigration = new JPanel();
		getContentPane().add(panelMigration);
		panelMigration.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMigration.setLayout(new GridLayout(0, 1, 0, 1));

		JPanel panelMain = new JPanel();
		panelMain.setBackground(SystemColor.control);
		panelMain.setForeground(Color.LIGHT_GRAY);
		panelMain.setBorder(null);
		panelMigration.add(panelMain);
		panelMain.setLayout(null);

		JPanel panelCommand = new JPanel();
		panelCommand.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0,
				0, 0)));
		panelCommand.setBounds(0, 0, 951, 51);
		panelMain.add(panelCommand);
		panelCommand.setLayout(null);

		txtFieldCommand = new JTextField();
		txtFieldCommand.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtFieldCommand.setBounds(153, 12, 518, 23);
		panelCommand.add(txtFieldCommand);
		txtFieldCommand.setColumns(50);

		JButton btCommand = new JButton("Execute Command");
		btCommand.setBounds(681, 11, 144, 25);
		panelCommand.add(btCommand);
		btCommand.setFont(new Font("Tahoma", Font.PLAIN, 13));

		JButton btInfo = new JButton("Info");
		btInfo.setBounds(835, 13, 62, 23);
		panelCommand.add(btInfo);
		btInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"Commands:\n\nadd kind.attribute=value\n e.g. add Player.points=100 or add Player.name=\"Lisa\"\n\ndelete kind.attribute\n e.g. delete Player.points\n\n"
										+ "copy kind1.attribute to kind2 where kind1.attribute1=kind2.attribute2\n e.g. copy Player.score to Mission where Player.id=Mission.pid\n\n"
										+ "move kind1.attribute to kind2 where kind1.attribute1=kind2.attribute2\n e.g. move Player.score to Mission where Player.id=Mission.pid\n\n"
										+ "get kind.id=value\n e.g. get Player.id=1\n\nput kind(value1,value2,...) e.g. put Player(1,'Lisa',...)",
								"info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btInfo.setFont(new Font("Tahoma", Font.BOLD, 11));

		JLabel lblCommand = new JLabel(" Command Prompt:");
		lblCommand.setBounds(10, 15, 122, 16);
		panelCommand.add(lblCommand);
		lblCommand.setFont(new Font("Tahoma", Font.BOLD, 13));
		btCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String uiInput = txtFieldCommand.getText();
				if (uiInput.startsWith("get")) {
					try {
						executeGetCommand(uiInput);
					} catch (parserPutToDatalog.ParseException | IOException
							| URISyntaxException | InputMismatchException | parserRuletoJava.ParseException e) {
						showErrorLog(e.getMessage());
						return;
					}
				} else if (uiInput.startsWith("add")
						|| uiInput.startsWith("delete")
						|| uiInput.startsWith("move")
						|| uiInput.startsWith("copy")) {
					try {
						executeCommand(uiInput);
					} catch (InputMismatchException | IOException
							| parserRuletoJava.ParseException e) {
						showErrorLog(e.getMessage());
						return;
					}
				} else if (uiInput.startsWith("put")) {
					try {
						executePutCommand(uiInput);
					} catch (InputMismatchException | parserPutToDatalog.ParseException | IOException e) {
						showErrorLog(e.getMessage());
					}
				} else {
					txtAreaRules.setText("No valid query");
				}

			}

		});

		JLabel lbDBEntriesEager = new JLabel("Number of DB entries");
		lbDBEntriesEager.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbDBEntriesEager.setBounds(107, 538, 215, 14);
		panelMain.add(lbDBEntriesEager);

		txtFieldDBEntriesEager = new JTextField();
		txtFieldDBEntriesEager.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFieldDBEntriesEager.setText("0");
		txtFieldDBEntriesEager.setEditable(false);
		txtFieldDBEntriesEager.setColumns(10);
		txtFieldDBEntriesEager.setBounds(442, 536, 49, 20);
		panelMain.add(txtFieldDBEntriesEager);

		txtFieldDBEntriesLazy = new JTextField();
		txtFieldDBEntriesLazy.setText("0");
		txtFieldDBEntriesLazy.setEditable(false);
		txtFieldDBEntriesLazy.setColumns(10);
		txtFieldDBEntriesLazy.setBounds(512, 536, 49, 20);
		panelMain.add(txtFieldDBEntriesLazy);

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
		panelMain.add(cbDatalogRules);

		JLabel lblDatalogView = new JLabel("DATALOG VIEW");
		lblDatalogView.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDatalogView.setBounds(10, 62, 91, 14);
		panelMain.add(lblDatalogView);

		JLabel lblSummary = new JLabel("SUMMARY");
		lblSummary.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSummary.setBounds(10, 539, 66, 14);
		panelMain.add(lblSummary);

		JLabel lblDbEntries = new JLabel("DB ENTRIES");
		lblDbEntries.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDbEntries.setBounds(10, 199, 66, 14);
		panelMain.add(lblDbEntries);

		JLabel lblEagerMigration = new JLabel("Eager Migration");
		lblEagerMigration.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEagerMigration.setBounds(214, 167, 123, 23);
		panelMain.add(lblEagerMigration);

		JLabel lblLazyMigration = new JLabel("Lazy Migration");
		lblLazyMigration.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblLazyMigration.setBounds(566, 166, 107, 25);
		panelMain.add(lblLazyMigration);

		try {
			factsEager = databaseBU.getJson();
			factsLazy = databaseTD.getJson();
		} catch (JsonParseException e) {
			showErrorLog(e.getMessage());
			factsLazy = "0";
		} catch (JsonMappingException e) {
			showErrorLog(e.getMessage());
			factsLazy = "0";
		} catch (IOException e) {
			showErrorLog(e.getMessage());
			factsLazy = "0";
		}

		txtAreaFactsLazy = new JTextArea();
		txtAreaFactsLazy.setEditable(false);
		txtAreaFactsLazy.setText(factsLazy);

		txtAreaFactsEager = new JTextArea();
		txtAreaFactsEager.setEditable(false);
		txtAreaFactsEager.setText(factsEager);

		JScrollPane scrollPaneEager = new JScrollPane(txtAreaFactsEager);
		scrollPaneEager.setBounds(107, 193, 384, 275);
		scrollPaneEager.setViewportView(txtAreaFactsEager);
		panelMain.add(scrollPaneEager);

		JScrollPane scrollPaneLazy = new JScrollPane(txtAreaFactsLazy);
		scrollPaneLazy.setBounds(512, 193, 384, 275);
		panelMain.add(scrollPaneLazy);

		txtAreaRules = new JTextArea();
		txtAreaRules.setEditable(false);
		txtAreaRules.setBounds(20, 11, 796, 66);
		txtAreaRules.setVisible(false);

		scrollPaneRules = new JScrollPane(txtAreaRules);
		scrollPaneRules.setEnabled(false);
		scrollPaneRules.setBounds(107, 88, 789, 68);
		scrollPaneRules.setVisible(false);
		panelMain.add(scrollPaneRules);

		totalDBEntriesBU = factsEager.split("\n").length;

		totalDBEntriesTD = factsLazy.split("\n").length;

		txtFieldDBEntriesEager.setText(String.valueOf(totalDBEntriesBU));
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));

		txtAreaResultsLazy = new JTextArea();
		txtAreaResultsLazy.setEditable(false);

		txtAreaResultsEager = new JTextArea();
		txtAreaResultsEager.setEditable(false);

		scrollPaneResultsEager = new JScrollPane();
		scrollPaneResultsEager.setBounds(107, 480, 384, 44);
		scrollPaneResultsEager.setViewportView(txtAreaResultsEager);
		panelMain.add(scrollPaneResultsEager);

		scrollPaneResultsLazy = new JScrollPane();
		scrollPaneResultsLazy.setBounds(512, 480, 384, 44);
		scrollPaneResultsLazy.setViewportView(txtAreaResultsLazy);
		panelMain.add(scrollPaneResultsLazy);

		JLabel lblResults = new JLabel("RESULTS");
		lblResults.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblResults.setBounds(10, 481, 49, 25);
		panelMain.add(lblResults);

	}

	private void executeGetCommand(String uiInput) throws parserPutToDatalog.ParseException, IOException, URISyntaxException, InputMismatchException, parserRuletoJava.ParseException {
		String id = "";
		ParserQueryToDatalogToJava parserget = new ParserQueryToDatalogToJava(
				new StringReader(uiInput));
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		if (rulesForTD != null)
			rulesTemp = copyRules();

		try {
			rulesTemp.addAll(parserget.getJavaRules(databaseTD));
		} catch (parserQueryToDatalogToJava.ParseException e) {
			showErrorLog(e.getMessage());
			return;
		}
		kind = parserget.getKind();
		id = parserget.getId();
		// start lazy migration
		Map<String, String> attributeMap = new TreeMap<String, String>();
		attributeMap.put("kind", kind);
		attributeMap.put("position", "0");
		attributeMap.put("value", id);
		ArrayList<Map<String, String>> uniMap = new ArrayList<Map<String, String>>();
		uniMap.add(attributeMap);
		ArrayList<String> schema;
		Predicate goal;
		try {
			schema = databaseTD.getLatestSchema(kind).getAttributes();

			schema.add("?ts");

			goal = new Predicate("get" + kind
					+ databaseTD.getLatestSchemaVersion(kind), schema.size(),
					schema);
		} catch (JsonParseException e) {
			showErrorLog(e.getMessage());
			return;
		} catch (JsonMappingException e) {
			showErrorLog(e.getMessage());
			return;
		} catch (IOException e) {
			showErrorLog(e.getMessage());
			return;
		}
		executeQueryTD(rulesTemp, goal, uniMap);
		txtAreaRules.setText(parserget.getRules());
		executeQueryBU(parserget.getRules());
	}

	private ArrayList<Rule> copyRules() {
		ArrayList<Rule> rulesTemp = new ArrayList<Rule>();
		for (Rule r : rulesForTD) {
			@SuppressWarnings("unchecked")
			ArrayList<String> scheme = (ArrayList<String>) r.getHead()
					.getScheme().clone();
			Predicate newHead = new Predicate(r.getHead().getKind(), r
					.getHead().getNumberSchemeEntries(), scheme);
			newHead.setHead(r.getHead().isHead());
			ArrayList<Predicate> predicates = new ArrayList<Predicate>();
			for (Predicate p : r.getPredicates()) {
				@SuppressWarnings("unchecked")
				ArrayList<String> scheme2 = (ArrayList<String>) p.getScheme()
						.clone();
				Predicate p2 = new Predicate(p.getKind(),
						p.getNumberSchemeEntries(), scheme2);
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

	private void executeCommand(String uiInput) throws InputMismatchException, IOException, parserRuletoJava.ParseException {
		ArrayList<Rule> rulesTemp = null;
		String rulesStr = "";
		if (rulesForTD == null)
			rulesForTD = new HashSet<Rule>();
		try {
			ParserQueryToDatalogToJava parserQuery = new ParserQueryToDatalogToJava(
					new StringReader(uiInput));
			rulesTemp = parserQuery.getJavaRules(databaseTD);
			rulesForTD.addAll(rulesTemp);
			rulesStr = parserQuery.getRules();

		} catch (parserQueryToDatalogToJava.ParseException e) {
			showErrorLog(e.getMessage());
			return;
		}
		txtAreaRules.setText(rulesStr);
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));
		executeQueryBU(rulesStr);
		JOptionPane.showMessageDialog(new JFrame(),
				"successfully added schema change", "dialog",
				JOptionPane.INFORMATION_MESSAGE);
		txtAreaResultsLazy.setText("");
		txtAreaResultsEager.setText("");
	}

	private void executeQueryBU(String rulesStr) throws InputMismatchException, JsonParseException, JsonMappingException, IOException {
		// set edb-facts in gui
		String[] edbFacts;
		try {
			edbFacts = databaseBU.getEDB().split("\n");
		} catch (JsonParseException e2) {
			showErrorLog(e2.getMessage());
			return;
		} catch (JsonMappingException e2) {
			showErrorLog(e2.getMessage());
			return;
		} catch (IOException e2) {
			showErrorLog(e2.getMessage());
			return;
		}
		String query = txtFieldCommand.getText();
		ArrayList<Rule> rules = null;
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (String factString : edbFacts) {
			try {
				facts.add(new ParserforDatalogToJava(new StringReader(
						factString)).start());
			} catch (ParseException e) {
				showErrorLog(e.getMessage());
				return;
			}
		}
		try {
			rules = new ParserRuleToJava(new StringReader(rulesStr)).start();
		} catch (parserRuletoJava.ParseException e) {
			showErrorLog(e.getMessage());
			return;
		}

		EagerMigration migrate = new EagerMigration(facts, rules, query);
		ArrayList<String> answerString = null;
		try {
			answerString = migrate.writeAnswersInDatabase();
		} catch (Exception e1) {
			showErrorLog(e1.getMessage());
			return;
		}

		totalPutsBU = totalPutsBU + migrate.getNumber();

		totalDBEntriesBU = totalDBEntriesBU + totalPutsBU;
		txtFieldDBEntriesEager.setText(String.valueOf(totalPutsBU));

		try {
			factsEager = databaseBU.getJson();
		} catch (JsonParseException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (JsonMappingException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (IOException e1) {
			showErrorLog(e1.getMessage());
			return;
		}
		txtAreaFactsEager.setText(factsEager);
		if (query.startsWith("get")) {
			String getAnswer = "";
			if (answerString != null)
				for (String answer : answerString) {
					int nr;
					try {
						nr = databaseBU.getLatestSchemaVersion(kind);
					} catch (JsonParseException e1) {
						showErrorLog(e1.getMessage());
						return;
					} catch (JsonMappingException e1) {
						showErrorLog(e1.getMessage());
						return;
					} catch (IOException e1) {
						showErrorLog(e1.getMessage());
						return;
					}
					String json = null;
					try {
						json = new ParserForPut(new StringReader(kind + nr
								+ "("
								+ answer.replace("[", "").replace("]", "")
								+ ")")).startJSON(databaseBU);
					} catch (parserPutToDatalog.ParseException e) {
						showErrorLog(e.getMessage());
						return;
					}
					getAnswer = getAnswer + json + "\n";
				}
			txtAreaResultsEager.setText(getAnswer);
			scrollPaneResultsEager
					.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		} else
			txtAreaResultsEager.setText("");
		txtFieldDBEntriesEager
				.setText(String.valueOf(factsEager.split("\n").length));

	}

	private void executeQueryTD(ArrayList<Rule> rulesTemp, Predicate goal,
			ArrayList<Map<String, String>> uniMap) throws parserPutToDatalog.ParseException, IOException, URISyntaxException {
		// set edb-facts in gui
		String[] edbFacts;
		try {
			edbFacts = databaseTD.getEDB().split("\n");
		} catch (JsonParseException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (JsonMappingException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (IOException e1) {
			showErrorLog(e1.getMessage());
			return;
		}
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (String factString : edbFacts) {
			try {
				facts.add(new ParserforDatalogToJava(new StringReader(
						factString)).start());
			} catch (ParseException e) {
				showErrorLog(e.getMessage());
				return;
			}
		}
		LazyMigration migrate = new LazyMigration(facts, rulesTemp, goal,
				uniMap);
		ArrayList<String> answerString = migrate.writeAnswersInDatabase();

		totalPutsTD = totalPutsTD + migrate.getNumberOfPuts();

		totalDBEntriesTD = totalDBEntriesTD + totalPutsTD;
		txtFieldDBEntriesLazy.setText(String.valueOf(totalDBEntriesTD));

		try {
			factsLazy = databaseTD.getJson();
		} catch (JsonParseException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (JsonMappingException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (IOException e1) {
			showErrorLog(e1.getMessage());
			return;
		}
		txtAreaFactsLazy.setText(factsLazy);
		int nr;
		try {
			nr = databaseTD.getLatestSchemaVersion(kind);
		} catch (JsonParseException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (JsonMappingException e1) {
			showErrorLog(e1.getMessage());
			return;
		} catch (IOException e1) {
			showErrorLog(e1.getMessage());
			return;
		}
		String getAnswer = "";
		if (answerString != null)
			for (String answer : answerString) {
				String json = null;
				try {
					json = new ParserForPut(new StringReader(kind
							+ nr
							+ answer.replace("[", "").replace("]", "")
									.replace(".", "")
									.substring(answer.indexOf("("))))
							.startJSON(databaseTD);
				} catch (parserPutToDatalog.ParseException e) {
					showErrorLog(e.getMessage());
					return;
				}
				getAnswer = getAnswer + json + "\n";
			}
		txtAreaResultsLazy.setText(getAnswer);
		scrollPaneResultsLazy
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		txtFieldDBEntriesLazy
				.setText(String.valueOf(factsLazy.split("\n").length));
	}

	private void executePutCommand(String uiInput) throws InputMismatchException, JsonParseException, JsonMappingException, parserPutToDatalog.ParseException, IOException {
		
			// put to database
			databaseTD.putToDatabase(uiInput);
			databaseBU.putToDatabase(uiInput);

			// reset text area facts
			factsEager = databaseBU.getJson();
			factsLazy = databaseTD.getJson();
		
		

		txtAreaFactsEager.setText(factsEager);
		txtAreaFactsLazy.setText(factsLazy);

		// increment db entries counter
		int entriesEager = Integer.parseInt(txtFieldDBEntriesEager.getText());
		int entriesLazy = Integer.parseInt(txtFieldDBEntriesLazy.getText());
		txtFieldDBEntriesEager.setText(String.valueOf(entriesEager + 1));
		txtFieldDBEntriesLazy.setText(String.valueOf(entriesLazy + 1));
		txtAreaResultsLazy.setText("");
		txtAreaResultsEager.setText("");
	}

	private void showErrorLog(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "error",
				JOptionPane.ERROR_MESSAGE);
	}
}
