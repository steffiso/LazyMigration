package lazyMigration;

/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.demo.ProgramExecutor;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluatorFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;

/**
 * A GUI version of the Demo application.
 */
public class TestIRIS
{
	public static final int FONT_SIZE = 12;
	public static final String NEW_LINE = System.getProperty( "line.separator" );

	/**
	 * Application entry point.
	 * @param args
	 */
	public static void main( String[] args )
	{
		// Set up the native look and feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
		}

		// Create the main window and show it.
		MainFrame mainFrame = new MainFrame();
		mainFrame.setSize( 800, 600 );
		mainFrame.setVisible( true );
	}
	
	/**
	 * The main application window
	 */
	public static class MainFrame extends JFrame implements ActionListener
	{
		/** The serialisation ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         */
		public MainFrame()
		{
			super( "IRIS - new" );
			TestEDB newTestEDB= new TestEDB();
			String edbFacts=newTestEDB.getEDBFacts();

			setup(edbFacts);
		}

		/**
		 * Create all the widgets, lay them out and create listeners.
		 */
		private void setup(String edbRules)
		{
			setLayout( new BorderLayout() );
			
			
			mProgram.setText(
							edbRules
			);
							
			mRun.addActionListener( this );

			mAbort.addActionListener( this );
			mAbort.setEnabled( false );

			JScrollPane programScroller = new JScrollPane( mProgram );
			JScrollPane outputScroller = new JScrollPane( mOutput );
			
			Font f = new Font( "courier", Font.PLAIN, FONT_SIZE );
			mProgram.setFont( f );
			mOutput.setFont( f );

			JSplitPane mainSplitter = new JSplitPane( JSplitPane.VERTICAL_SPLIT, false, programScroller, outputScroller );

			getContentPane().add( mainSplitter, BorderLayout.CENTER );
			
			JPanel panel = new JPanel();
			panel.add( mStrategy );
			panel.add( mUnsafeRules );
			panel.add( mOptimise );
			panel.add( mRun );
			panel.add( mAbort );

			getContentPane().add( panel, BorderLayout.SOUTH );

			// Can't seem to make this happen before showinG, even with:
//			 mainSplitter.putClientProperty( JSplitPane.RESIZE_WEIGHT_PROPERTY, "0.5" );
//			mainSplitter.setDividerLocation( 0.5 );
			
			addWindowListener(
							new WindowAdapter()
							{
								public void windowClosing( WindowEvent e )
								{
									System.exit( 0 );
								}
							}
						);
			
		}

		private final JTextArea mProgram = new JTextArea();
		private final JTextArea mOutput = new JTextArea();
		
//		private final JComboBox mEvaluator = new JComboBox( new String[] { "Naive", "Semi-naive" } );
		private final JComboBox mStrategy = new JComboBox( new String[] { "Stratified (Semi-naive)", "Stratified (Naive)", "Well-founded" } );
		private final JCheckBox mUnsafeRules = new JCheckBox( "Unsafe-rules", false );
		private final JComboBox mOptimise = new JComboBox( new String[] { "none", "Magic Sets" } );
		
		private final JButton mRun = new JButton( "Evaluate" );
		private final JButton mAbort = new JButton( "Abort" );
		
		Thread mExecutionThread;
		
		public void actionPerformed( ActionEvent e )
        {
	        if( e.getSource() == mRun )
	        {
	        	run();
	        }
	        else if( e.getSource() == mAbort )
	        {
	        	abort();
	        }
        }

		/**
		 * Called when evaluation has finished.
		 * @param output The evaluation output 
		 */
		synchronized void setOutput( String output )
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			mOutput.setText( output );
		}
		
		/**
		 * Notifier class that 'hops' the output from the evaluation thread to the UI thread.
		 */
		class NotifyOutput implements Runnable
		{
			NotifyOutput( String output )
			{
				mOutput = output;
			}
			
			public void run()
            {
	            setOutput( mOutput );
            }
			
			final String mOutput;
		}
		
		/**
		 * Starts the evaluation.
		 */
		synchronized void run()
		{
			mOutput.setText( "" );

			mRun.setEnabled( false );
			mAbort.setEnabled( true );
			
			String program = mProgram.getText();
			
			Configuration config = KnowledgeBaseFactory.getDefaultConfiguration();
			
			if( mUnsafeRules.isSelected() )
				config.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
			
			switch( mStrategy.getSelectedIndex() )
			{
			default:
			case 0:
				config.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );
				break;
				
			case 1:
				config.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
				break;
				
			case 2:
				config.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
				config.stratifiers.clear();
				break;
			}
			
			switch( mOptimise.getSelectedIndex() )
			{
			case 0:
				break;
				
			case 1:
				config.programOptmimisers.add( new RuleFilter() );
				config.programOptmimisers.add( new MagicSets() );
				break;
			}

			mExecutionThread = new Thread( new ExecutionTask( program, config ), "Evaluation task" );

			mExecutionThread.setPriority( Thread.MIN_PRIORITY );
			mExecutionThread.start();
		}
		
		/**
		 * Aborts the evaluation.
		 */
		synchronized void abort()
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			// Not very nice, but hey, that's life.
			mExecutionThread.stop();
		}
		
		/**
		 * Runnable task for performing the evaluation.
		 */
		class ExecutionTask implements Runnable
		{
			ExecutionTask( String program, Configuration configuration )
			{
				this.program = program;
				this.configuration = configuration;
			}
			
//			@Override
	        public void run()
	        {
	        	ProgramExecutor executor = new ProgramExecutor( program, configuration );
				SwingUtilities.invokeLater( new NotifyOutput( executor.getOutput() ) );
	        }
			
			private final String program;
			private final Configuration configuration;
		}
	}
}