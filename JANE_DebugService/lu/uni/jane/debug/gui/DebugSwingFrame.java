/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import sun.java2d.pipe.TextPipe;

import lu.uni.jane.debug.service.DebugGlobalService;
import lu.uni.jane.debug.util.Debug;
import lu.uni.jane.debug.util.DebugMessage;

/**
 * @author Adrian Andronache
 */
public class DebugSwingFrame extends JFrame implements ItemListener, PropertyChangeListener
{
	// the debug service
	DebugGlobalService debugService;
	
	// the log table
	protected ConsoleTablePane consoleTablePane;
	// auto scroll to see the last message
	boolean autoscroll = true;
	// the autoscroll checkbox
	JCheckBox autoScrollButton;
	
	// Fields for the log level
    private JFormattedTextField startLevelField;
    private JFormattedTextField endLevelField;

	// the class filter table pane
	protected ClassFilterTablePane classFilterTablePane;
	// the class filter table pane
	protected PackageFilterTablePane packageFilterTablePane;
	 
	/**
	 * @throws HeadlessException
	 */
	public DebugSwingFrame( DebugGlobalService debugService ) throws HeadlessException 
	{
		super();
		this.debugService = debugService;
		this.create();
	}

	/**
	 * @param gc
	 */
	public DebugSwingFrame( DebugGlobalService debugService, GraphicsConfiguration gc ) 
	{
		super( gc );
		this.debugService = debugService;
		this.create();
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public DebugSwingFrame( DebugGlobalService debugService, String title ) throws HeadlessException 
	{
		super( title );
		this.debugService = debugService;
		this.create();
	}

	/**
	 * @param title
	 * @param gc
	 */
	public DebugSwingFrame( DebugGlobalService debugService, String title, GraphicsConfiguration gc ) 
	{
		super( title, gc );
		this.debugService = debugService;
		this.create();
	}

	
	/**
	 * Create and add components to the debug frame
	 */
	public void create()
	{
		// Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated( true );
		this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		
		JTabbedPane tabbedPane = new JTabbedPane();		
		
		// add the log text area tab
		JComponent panel1 = makeConsole();
		tabbedPane.addTab( "Console", null, panel1, "The debug console" );
		tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 );

		// package and class filter
		JComponent panel2 = makePackageClassFilter();
		tabbedPane.addTab( "Package/Class Filter", null, panel2, "The package/class debug filter" );
		tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 );
		
		// add the log level filter tab
		JComponent panel3 = makeLevelFilter();
		tabbedPane.addTab( "Level Filter", null, panel3, "The log level debug filter" );
		tabbedPane.setMnemonicAt( 2, KeyEvent.VK_2 );
		
		// add the tabbed pane to the frame
		this.getContentPane().add( tabbedPane );
		
        //Display the frame.
        this.pack();
        this.setVisible( true );
	}
	
	/**
	 * Returns a JSplitPane wich contains the package and the class filter.
	 * 
	 * @return JSplitPane
	 */
	private JComponent makePackageClassFilter() 
	{
		// class filter pane
		classFilterTablePane = new ClassFilterTablePane( debugService );
		// package filter pane
		packageFilterTablePane = new PackageFilterTablePane( debugService, classFilterTablePane.getClassFilterTableModel() );
		
		// Create a split pane with the two components in it.
		JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, packageFilterTablePane, classFilterTablePane );
		splitPane.setOneTouchExpandable( false );
		splitPane.setDividerLocation( 100 );
		
		return splitPane;
	}

	/**
	 * Lame method for testing ... :P
	 */
    protected JComponent makeTextPanel( String text ) 
	{
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
	
	/**
	 * Returns the console panel wich contains 
	 * the text area and the autoscroll checkbox.
	 * @return JSplitPane
	 */
	protected JComponent makeConsole()
	{
		// create the log table
		this.consoleTablePane = new ConsoleTablePane();
		
		// the autoscroll checkbox
		this.autoScrollButton = new JCheckBox( "Autoscroll console" );
		this.autoScrollButton.setMnemonic( KeyEvent.VK_A );
		this.autoScrollButton.setSelected( true );
		this.autoScrollButton.addItemListener(this);
		
		// create the console panel
		JPanel panel = new JPanel( false );
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		// add the autoscroll button
		panel.add( autoScrollButton );
		// add the table
		panel.add( consoleTablePane );
		
		return panel;
	}
	
	/**
	 * Returns the level filter panel.
	 * @return JPanel
	 */
	protected JComponent makeLevelFilter()
	{
		// the start log level field
		startLevelField = new JFormattedTextField();
		startLevelField.setValue( new Integer( 0 ) );
		startLevelField.setColumns( 3 );
		startLevelField.addPropertyChangeListener( "value", this );
		// the end log level field
		endLevelField = new JFormattedTextField();
		endLevelField.setValue( new Integer( -1 ) );
		endLevelField.setColumns( 3 );
		endLevelField.addPropertyChangeListener( "value", this );
		endLevelField.setPreferredSize( new Dimension( 50, 30 ) );
		
		JPanel panel1 = new JPanel( false );
		panel1.setLayout( new BoxLayout( panel1, BoxLayout.X_AXIS ) );
		panel1.add( new JLabel( " Log from level: " ) );
        panel1.add( startLevelField );
		panel1.add( new JLabel( " to level: " ) );
		panel1.add( endLevelField );
	
		return panel1;
	}
	
	
	/**
	 * If autoscroll is true, the console will always
	 * show the last log line.
	 */
	public void setAutoscroll( boolean auto )
	{
		this.autoscroll = auto;
	}
	
	/**
	 * Used to notify the filter pane that a new class 
	 * was added to the filter.
	 */
	public void addedClassRow( int index )
	{
		classFilterTablePane.addedRow( index );
	}
	
	/**
	 * Used to notify the filter pane that a new package 
	 * was added to the filter.
	 */
	public void addedPackageRow( int index )
	{
		packageFilterTablePane.addedRow( index );
	}
	
	/**
	 * Add the message to the models vector.
	 */
	public void log( DebugMessage message )
	{		
		// add the message to the pane
		consoleTablePane.addMessage( message );
		
		// Make sure the last line is always visible
		// if autoscroll is true.
		if( this.autoscroll )
		{			
			// show the last log line
			consoleTablePane.scrollDown();
		}
	}

	/**
	 * Used to clear the message table.
	 */
	public void clearMessages()
	{
		consoleTablePane.clearMessages();
	}
	
	//
	// #################### ItemListener methods ####################
	//
	/**
	 * Listens to the check boxes.
	 */
	public void itemStateChanged( ItemEvent e ) 
	{
		Object source = e.getItemSelectable();

		// check which box was changed
        if( source == this.autoScrollButton ) 
		{
			if( e.getStateChange() == ItemEvent.DESELECTED )
			{
				this.autoscroll = false;
			}
			else
			{
				this.autoscroll = true;
			}
        }
	}
	
	//
	// #################### PropertyChangeListener methods ####################
	//
	public void propertyChange( PropertyChangeEvent e ) 
	{
		int start = ((Number)startLevelField.getValue()).intValue();
		if( start < -1 ) start = -1;
		
		int end = ((Number)endLevelField.getValue()).intValue();
		if( (end < start) || (end < -1) ) end = -1;

		//System.out.println( "LOG SPAN: " + start + " - " + end );
		
		debugService.setLogLevelSpan( start, end );
	}
}