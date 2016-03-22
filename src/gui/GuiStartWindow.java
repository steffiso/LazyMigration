package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Color;

import javax.swing.border.MatteBorder;

import java.awt.GridLayout;

public class GuiStartWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiStartWindow frame = new GuiStartWindow();
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
	public GuiStartWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 90, 780, 539);
		setTitle("SchemaMigrator");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDatalogmigrator = new JLabel("A Datalog-based Tool");
		lblDatalogmigrator.setForeground(new Color(0, 0, 128));
		lblDatalogmigrator.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblDatalogmigrator.setBounds(254, 59, 241, 84);
		contentPane.add(lblDatalogmigrator);

		JButton btnNewButton = new JButton("Start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
					GuiMigration gui = new GuiMigration();
					gui.setVisible(true);
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnNewButton.setBounds(237, 198, 268, 98);
		contentPane.add(btnNewButton);

		JPanel panelChooseFile = new JPanel();
		panelChooseFile.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		panelChooseFile.setBounds(134, 357, 475, 70);
		contentPane.add(panelChooseFile);
		panelChooseFile.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panelDemoFiles = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelDemoFiles.getLayout();
		flowLayout_1.setVgap(20);
		panelChooseFile.add(panelDemoFiles);
		
		JLabel lblNewLabel = new JLabel("This application uses demo JSON files");
		panelDemoFiles.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 13));
		
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
		
		JLabel lblInNosqlDatabases_1 = new JLabel("for Schema Evolution in NoSQL Databases");
		lblInNosqlDatabases_1.setForeground(new Color(0, 0, 128));
		lblInNosqlDatabases_1.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblInNosqlDatabases_1.setBounds(152, 104, 492, 84);
		contentPane.add(lblInNosqlDatabases_1);
	}
}
