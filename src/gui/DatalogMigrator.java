package gui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.border.MatteBorder;

public class DatalogMigrator extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String eagerFileName;
	private String lazyFileName;
	private String schemaFileName;
	private JCheckBox chckbxUseDemoFiles;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatalogMigrator frame = new DatalogMigrator();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DatalogMigrator() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 10, 770, 539);
		setTitle("DatalogMigrator");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDatalogmigrator = new JLabel("DatalogMigrator");
		lblDatalogmigrator.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblDatalogmigrator.setBounds(249, 49, 226, 84);
		contentPane.add(lblDatalogmigrator);

		JLabel lblAToolFor = new JLabel(
				"<html>a tool for migrating agile schema changes in <br>\r\nschema-flexible databases based on datalog rules<html>");
		lblAToolFor.setHorizontalAlignment(SwingConstants.CENTER);
		lblAToolFor.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAToolFor.setBounds(186, 128, 410, 72);
		contentPane.add(lblAToolFor);

		JButton btnNewButton = new JButton("start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!chckbxUseDemoFiles.isSelected()&&(eagerFileName == null || lazyFileName == null
						|| schemaFileName == null))
					JOptionPane.showMessageDialog(new JFrame(),
							"choose all filenames", "dialog",
							JOptionPane.ERROR_MESSAGE);
				else {
					Gui gui = new Gui();
					gui.setVisible(true);
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnNewButton.setBounds(231, 211, 268, 98);
		contentPane.add(btnNewButton);

		JPanel panelChooseFile = new JPanel();
		panelChooseFile.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		FlowLayout fl_panelChooseFile = (FlowLayout) panelChooseFile
				.getLayout();
		fl_panelChooseFile.setVgap(20);
		panelChooseFile.setBounds(114, 339, 483, 123);
		contentPane.add(panelChooseFile);

		JButton btnChooseEager = new JButton("choose eager DB file");
		btnChooseEager.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				eagerFileName=chooseFile();
			}

		});
		panelChooseFile.add(btnChooseEager);

		JButton btnChooseLazy = new JButton("choose lazy DB file");
		btnChooseLazy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				lazyFileName=chooseFile();
			}

		});
		panelChooseFile.add(btnChooseLazy);

		JButton btnChooseSchema = new JButton("choose schema file");
		btnChooseSchema.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				schemaFileName=chooseFile();
			}

		});
		panelChooseFile.add(btnChooseSchema);
		
		chckbxUseDemoFiles = new JCheckBox("use demo files");
		panelChooseFile.add(chckbxUseDemoFiles);
	}

	private String chooseFile() {
		JFileChooser fc = new JFileChooser();
		// fc.setFileFilter(new FileNameExtensionFilter("JSONdateien",
		// ".json"));

		int state = fc.showOpenDialog(null);

		if (state == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile()
					.getAbsolutePath();
		}
		else return null;
	}
}
