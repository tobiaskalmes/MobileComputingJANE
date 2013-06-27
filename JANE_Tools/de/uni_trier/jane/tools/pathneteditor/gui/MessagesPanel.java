package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;


public class MessagesPanel extends JScrollPane {
		
	static final long serialVersionUID = 95414857848657320L;	
	
	final public static PrintStream	DEFAULT_OUT = System.out;
	final public static PrintStream	DEFAULT_ERR = System.err;
	
	private PathNetModel model;
	private JTextPane	message_TP = new JTextPane();		
	
	private class MessageOutStream extends OutputStream {
		private Color color;
		private StringBuffer sb = new StringBuffer();
		
		public MessageOutStream(Color color) {
			this.color = color;			
			Style style = message_TP.addStyle(color.toString(), null);
			StyleConstants.setForeground(style, color);
		}
		
		public void write(int b) throws IOException {
			sb.append((char)b);						
			
			if (b == '\n') {
				String oldText = message_TP.getText();				

				try {
					message_TP.getDocument().insertString(
							oldText.length(),
							sb.toString(),
							message_TP.getStyle(color.toString()));
				} catch (BadLocationException e) {
					System.setErr(DEFAULT_ERR);
					e.printStackTrace();
				}
		
				// Scroll to bottom of text
				MessagesPanel.this.getViewport().setViewPosition(new Point(0, message_TP.getHeight()));
				
				sb.setLength(0);			
			}		
		}		
	};
	
	public MessagesPanel(PathNetModel model) {
		super();
		this.model = model;
				
		message_TP.setEditable(false);
		message_TP.setFont(new Font("monospaced", Font.PLAIN, 10));	
		message_TP.setText(" ");
		this.setViewportView(message_TP);
		
		setPreferredSize(new Dimension(200, 200));
		setBorder(new TitledBorder("Messages"));				
	}
	
	public PrintStream getPrintStream(Color c) {
		return new PrintStream(new MessageOutStream(c));
	}
	
	public void writeMessage(String message) {
		writeMessage(message, Color.BLACK);
	}
	
	public void writeMessage(String message, Color color) {
		Style style = message_TP.addStyle(color.toString(), null);
		StyleConstants.setForeground(style, color);
		
		String oldText = message_TP.getText();				
		//DEFAULT_ERR.println(oldText.length());
		try {
			message_TP.getDocument().insertString(
					oldText.length(),
					message,
					message_TP.getStyle(color.toString()));
		} catch (BadLocationException e) {
			System.setErr(DEFAULT_ERR);
			e.printStackTrace();
		}
		
		//Scroll to bottom of text
		this.getViewport().setViewPosition(new Point(0, message_TP.getHeight()));
	}
}
