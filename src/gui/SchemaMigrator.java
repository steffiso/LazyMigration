package gui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
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

import java.awt.GridLayout;

public class SchemaMigrator extends JFrame {

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
					SchemaMigrator frame = new SchemaMigrator();
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
	public SchemaMigrator() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(145, 90, 774, 581);
		setTitle("SchemaMigrator");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDatalogmigrator = new JLabel("SchemaMigrator");
		lblDatalogmigrator.setForeground(new Color(0, 0, 128));
		lblDatalogmigrator.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblDatalogmigrator.setBounds(265, 44, 226, 84);
		contentPane.add(lblDatalogmigrator);

		JLabel lblAToolFor = new JLabel(
				"A Datalog-Based Tool for Schema Evolution");
		lblAToolFor.setHorizontalAlignment(SwingConstants.CENTER);
		lblAToolFor.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAToolFor.setBounds(173, 121, 410, 35);
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
					GuiAlternative gui = new GuiAlternative();
					//Gui gui = new Gui();
					gui.setVisible(true);
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnNewButton.setBounds(237, 216, 268, 98);
		contentPane.add(btnNewButton);

		JPanel panelChooseFile = new JPanel();
		panelChooseFile.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		panelChooseFile.setBounds(122, 340, 475, 127);
		contentPane.add(panelChooseFile);
		panelChooseFile.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panelNewFiles = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelNewFiles.getLayout();
		flowLayout.setVgap(20);
		panelChooseFile.add(panelNewFiles);
		
				JButton btnChooseEager = new JButton("choose eager DB file");
				panelNewFiles.add(btnChooseEager);
				
						JButton btnChooseLazy = new JButton("choose lazy DB file");
						panelNewFiles.add(btnChooseLazy);
						
								JButton btnChooseSchema = new JButton("choose schema file");
								panelNewFiles.add(btnChooseSchema);
								btnChooseSchema.addActionListener(new ActionListener() {

									public void actionPerformed(ActionEvent arg0) {
										schemaFileName=chooseFile();
									}

								});
						btnChooseLazy.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent arg0) {
								lazyFileName=chooseFile();
							}

						});
				btnChooseEager.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						eagerFileName=chooseFile();
					}

				});
		
		JPanel panelDemoFiles = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelDemoFiles.getLayout();
		flowLayout_1.setVgap(20);
		panelChooseFile.add(panelDemoFiles);
		
		JLabel lblNewLabel = new JLabel("         or   ");
		panelDemoFiles.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 13));
		
		chckbxUseDemoFiles = new JCheckBox("use demo files");
		panelDemoFiles.add(chckbxUseDemoFiles);
		
		JButton btnWhatsThis = new JButton("what's this?");
		panelDemoFiles.add(btnWhatsThis);
		btnWhatsThis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
				UIManager.put("OptionPane.messageFont", font);
				JOptionPane.showMessageDialog(new JFrame(),
						"\nour demo files illustrate a gaming app\nwhich is displayed in JSON format and consists of:\n\n"
						+ " Player entities, e.g.:\n"
						+ "      Player\n         {   \"id\":1,\n             \"name\":\"Lisa\",\n             \"score\":20,\n             \"ts\":1   }\n"
						+ " and Mission entities, e.g.:\n"
						+ "      Mission\n         {   \"id\":1,\n             \"title\":\"go to library\",\n             \"priority\":1,\n             \"pid\":1,\n             \"ts\":4   }\n"
						+ "\n", "Info on Demo Files",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		JLabel lblInNosqlDatabases = new JLabel("in NoSQL Databases");
		lblInNosqlDatabases.setHorizontalAlignment(SwingConstants.CENTER);
		lblInNosqlDatabases.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblInNosqlDatabases.setBounds(173, 150, 410, 28);
		contentPane.add(lblInNosqlDatabases);
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
