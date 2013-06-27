package de.uni_trier.jane.sgui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class CacheSizeDlg extends JDialog {

	private int origSize;
	private int cacheSize;
	
	public CacheSizeDlg(Container parent, int cacheSize) throws HeadlessException {
		super();
		this.origSize = 
		this.cacheSize = cacheSize;
		this.setSize(320,49);
		this.setTitle("Number of frames to cache");
		this.setModal(true);
		setLocationRelativeTo(parent);
		initialize();
	}

	private JButton okButt = new JButton();
	private JTextField tField = new JTextField();
	
	void initialize() {
		
		
		tField.setText(Integer.toString(cacheSize));
		okButt.setText("ok");

		okButt.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				try {
					cacheSize = Integer.parseInt(tField.getText());
				} catch (NumberFormatException nfe) {
					cacheSize = origSize;
				}
				setVisible(false);
			}
		});
		getContentPane().add(tField, BorderLayout.CENTER);
		getContentPane().add(okButt, BorderLayout.EAST);
	}
	
	public int getCacheSize() {
		return cacheSize;
	}
	
}
