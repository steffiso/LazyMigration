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
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TestGui {

	private JFrame frmBottomup;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestGui window = new TestGui();
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
	public TestGui() {
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
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 720, 492);
		frmBottomup.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("BottomUp", null, panel, null);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(10, 11, 695, 231);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 25, 325, 195);
		panel_1.add(textArea);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setBounds(360, 25, 325, 195);
		panel_1.add(textArea_1);
		
		JLabel lblFacts = new JLabel("Facts");
		lblFacts.setBounds(10, 11, 46, 14);
		panel_1.add(lblFacts);
		
		JLabel lblResults = new JLabel("Results");
		lblResults.setBounds(360, 11, 46, 14);
		panel_1.add(lblResults);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(10, 257, 695, 196);
		panel.add(panel_3);
		panel_3.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 28, 322, 23);
		panel_3.add(textField);
		textField.setColumns(10);
		
		JLabel lblCommandoPrompt = new JLabel("Command Prompt");
		lblCommandoPrompt.setBounds(10, 11, 133, 14);
		panel_3.add(lblCommandoPrompt);
		
		JButton btnNewButton = new JButton("Execute Command");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(363, 28, 322, 23);
		panel_3.add(btnNewButton);
		
		JTextArea textArea_4 = new JTextArea();
		textArea_4.setBounds(10, 77, 675, 63);
		panel_3.add(textArea_4);
		
		JLabel lblRules = new JLabel("Rules");
		lblRules.setBounds(10, 62, 46, 14);
		panel_3.add(lblRules);
		
		JButton btnNewButton_1 = new JButton("Generate Rules");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setBounds(10, 150, 675, 35);
		panel_3.add(btnNewButton_1);
		JPanel panel2 = new JPanel();
		tabbedPane.addTab("TopDown", null, panel2, null);
		panel2.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setLayout(null);
		panel_2.setBounds(10, 11, 695, 231);
		panel2.add(panel_2);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setEditable(false);
		textArea_2.setBounds(10, 25, 325, 195);
		panel_2.add(textArea_2);
		
		JTextArea textArea_3 = new JTextArea();
		textArea_3.setEditable(false);
		textArea_3.setBounds(360, 25, 325, 195);
		panel_2.add(textArea_3);
		
		JLabel label = new JLabel("Facts");
		label.setBounds(10, 11, 46, 14);
		panel_2.add(label);
		
		JLabel label_1 = new JLabel("Results");
		label_1.setBounds(360, 11, 46, 14);
		panel_2.add(label_1);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBounds(10, 257, 695, 196);
		panel2.add(panel_4);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(10, 28, 322, 23);
		panel_4.add(textField_1);
		
		JLabel label_2 = new JLabel("Command Prompt");
		label_2.setBounds(10, 11, 133, 14);
		panel_4.add(label_2);
		
		JButton button = new JButton("Execute Command");
		button.setBounds(363, 28, 322, 23);
		panel_4.add(button);
		
		JTextArea textArea_5 = new JTextArea();
		textArea_5.setBounds(10, 77, 675, 63);
		panel_4.add(textArea_5);
		
		JLabel label_3 = new JLabel("Rules");
		label_3.setBounds(10, 62, 46, 14);
		panel_4.add(label_3);
		
		JButton button_1 = new JButton("Generate Rules");
		button_1.setBounds(10, 150, 675, 35);
		panel_4.add(button_1);
		
		
	
	}
}
