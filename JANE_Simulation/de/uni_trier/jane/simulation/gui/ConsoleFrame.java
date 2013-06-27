/*
 * Created on 18.02.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.simulation.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import de.uni_trier.jane.console.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConsoleFrame extends JFrame implements JANEConsoleFrame {
	private JTextAreaConsole console;

	/**
	 * 
	 */
	public ConsoleFrame() {
		super("Console");
		setSize(600, 200);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(4, 80);
		textArea.setEditable(false);
		console = new JTextAreaConsole(textArea);
		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		setVisible(false);		
		
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.JANEFrame#addFrameListener(de.uni_trier.jane.simulation.gui.JANEFrameListener)
	 */
	public void addFrameListener(final JANEFrameListener frameListener) {
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			public void componentHidden(ComponentEvent e) {
				frameListener.frameHidden();
				

			}
		});
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.console.Console#println(java.lang.String)
	 */
	public void println(String text) {
		console.println(text);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.console.Console#print(java.lang.String)
	 */
	public void print(String text) {
		console.print(text);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.console.Console#println()
	 */
	public void println() {
		console.println();
		
	}

}
