/**
 * Created on 16.06.2005
 * @author Klaus Sausen
 */
package de.uni_trier.jane.sgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import de.uni_trier.jane.simulation.visualization.Frame;
import de.uni_trier.jane.simulationl.visualization.console.ConsoleTextIterator;

/**
 * a swing console component based on JTable
 * @author Klaus Sausen
 */
public class ConsolePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;

	private FrameBuffer frameBuffer = null;
	
	
	/**
	 * This is the default constructor
	 */
	public ConsolePanel() {
		super();
		initialize();
	}
	
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(),BorderLayout.CENTER);
		this.setSize(600, 26);
	}

	/**
	 * the Messagebuffer
	 * it is accessed by JTableCustomModel
	 * @author Klaus Sausen
	 */
	class Messages {
		/** normally display the messages */
		public static final int MODE_NORMAL = 0;
		/** highlight */
		public static final int MODE_ACTIVE = 1;

		/** cleanly wraps a Message (t is for comparison)*/
		public class Message {
			final double t;
			final String time;
			final String mess;
			int highlight_mode;
			
			public Message(double t, String time, String mess) {
				this.t = t;
				this.time = time;
				this.mess = mess;
				this.highlight_mode = MODE_NORMAL;
			}
			public double getT()    { return t;    }
			public String getTime() { return time; }
			public String getMess() { return mess; }

			public void setFashion(int mode) {
				this.highlight_mode  = mode; 
			}
			public int getFashion() {
				return highlight_mode;
			}
		}
		
		final int maxVisible;
		final ArrayList al;
		
		public Messages(int maxVisible) {
			al = new ArrayList();
			this.maxVisible = maxVisible;
		}

		public void clearHighlighting() {
			Iterator it = al.iterator();
			while (it.hasNext())
				((Message)it.next()).setFashion(MODE_NORMAL);
		}
		
		public void highlightMessages(double time) {
			Message m;
			clearHighlighting();
			for (int a=0;a<al.size();a++) {
				m = (Message)al.get(a);
				if (m.getT() == time) {
					m.setFashion(MODE_ACTIVE);
				}
			}
		}
		
		public void addMessage(double t, String time, String mess) {
			addMessage(new Message(t,time,mess));
		}
		
		public void addMessage(Message message) {
			al.add(0,message);
		}
		
		public Message getVisibleAt(int idx) {
			return getMessageAt(idx);
			/* this is valid if addMessage implements al.add(message):
			int dt = al.size() - maxVisible;
			if (dt<0)
				dt=0;
			int pos = dt + idx;
			
			if (pos<al.size()) 
				return (Message)al.get(pos);
			return null;*/
		}
		public Message getMessageAt(int idx) {
			if (idx<al.size()) 
				return (Message)al.get(idx);
			return null;
		}
	}
	
	class JCustomTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		public JCustomTableCellRenderer() {
		}
		public void setValue(Object aValue) {
			Object result = aValue;
			
			if (aValue != null) { //&& (aValue instanceof Stock) ) {

				if (aValue instanceof Messages.Message) {
					Messages.Message m = (Messages.Message)aValue;
				
					if (m.getFashion()==Messages.MODE_ACTIVE) {
						this.setForeground(Color.WHITE);
						if (this.isFocusOwner())
							this.setBackground(Color.RED);
						else
							this.setBackground(Color.BLUE);
					}
					//if (m.getComment()!=null)
					//setToolTipText(m.getComment());
						
					result = m.getMess();
					//else {
				//}
					super.setValue(result);
					return;
				} else {
					this.setForeground(Color.BLACK);
					this.setBackground(Color.WHITE);
				}
			}
			super.setValue(result);
		}   
	}
	
	class JTableCustomDataModel	extends	AbstractTableModel  
	{

		private static final long serialVersionUID = 1L;
		private final Messages messages;
		private final int maxRows;
		
		public JTableCustomDataModel(Messages messages, int maxRows) {
			this.messages = messages;
			this.maxRows = maxRows;
		}
		
		/**
		 * this is suboptimal formatting (likewise)
		 */
		public Object getValueAt(int iRowIndex, int iColumnIndex)
		{
			String s;
			Messages.Message m;
			switch (iColumnIndex) {
			//the time column
			case 0: s=(m=messages.getVisibleAt(iRowIndex))==null
					?"":m.getTime();						break;
			//the message column
			case 1: return messages.getVisibleAt(iRowIndex);
			default:s="";
			}
			return s;//+" ("+iRowIndex+"/"+iColumnIndex+")";
		}
		
		public void setValueAt(Object aValue, int iRowIndex, int iColumnIndex) 
		{//	nothing to do here
		}
		
		public int getColumnCount()
		{//	return 0 because we handle our own columns
			return 0;
		}	
		
		public int getRowCount()
		{
			return maxRows;
		}	
	}
	
	private final static int MAX_VISIBLE_MESSAGES = 500;
		
	private Messages consoleMessages = null;
	
	private Messages getConsoleMessages() {
		if (consoleMessages == null) {
			consoleMessages = new Messages(MAX_VISIBLE_MESSAGES);
		}
		return consoleMessages;
	}

	/**
	 * update the visible messages
	 */
	public void updateMessages() {
		Frame frame;
		ConsoleTextIterator it;
		double time;
		String timestr;
		//get all the messages out of each frame
		if (frameBuffer!=null) {
			for (int a=0;a<frameBuffer.size();a++) {
				frame = frameBuffer.getFrameAt(a);
				it = frame.getConsoleTextIterator();
				time = frame.getTime();
				timestr = frame.getTimeString();
				while (it.hasNext()) {
					getConsoleMessages()
						.addMessage(time,timestr,it.next().getText());
				}
			}
		}
		repaint();
	}
	
	public void updateHighlight(Frame frame) {
		getConsoleMessages().highlightMessages(frame.getTime());
		repaint();
	}
	
	static int ct = 0;
	
	private JTable getJTable() {
		if (jTable == null) {

			// Create the custom data model
			JTableCustomDataModel customDataModel = 
				new JTableCustomDataModel(getConsoleMessages(), MAX_VISIBLE_MESSAGES);

			JCustomTableCellRenderer customCellRenderer = 
				new JCustomTableCellRenderer();
			
			jTable = new JTable( customDataModel );

			jTable.setDefaultRenderer(Object.class, customCellRenderer);
			
			//configure some properties
			jTable.setShowHorizontalLines(false);
			jTable.setRowSelectionAllowed(true);
			jTable.setColumnSelectionAllowed(false);

			jTable.setSelectionForeground( Color.white );
			jTable.setSelectionBackground( Color.red );		

			TableColumn timeColumn = new TableColumn(0);
			timeColumn.setHeaderValue((Object)"Time");
			timeColumn.setMinWidth(60);
			timeColumn.setMaxWidth(120);

			TableColumn messColumn = new TableColumn(1);
			messColumn.setHeaderValue((Object)"Message");
			messColumn.setMinWidth(200);
			//messColumn.setMaxWidth(120);

			// Add the column to the table
			jTable.addColumn(timeColumn);
			jTable.addColumn(messColumn);
		}
		return jTable;
	}
	
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane( getJTable() );
			jScrollPane.setSize(400,100);
			jScrollPane.setMaximumSize(new Dimension(400,100));
			JScrollBar vScrollB = 
			jScrollPane.createVerticalScrollBar();
			//jScrollPane.setLocation(0,-100000000);
		}
		return jScrollPane;
	}
	
 }  //  @jve:decl-index=0:visual-constraint="10,10"
