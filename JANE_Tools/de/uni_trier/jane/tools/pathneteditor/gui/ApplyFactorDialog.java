package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;



public class ApplyFactorDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// the type of changes possible
	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;
	private static final int FACTOR = 2;
	

	private PathNetModel model = null;
	private PathNetPanel panel = null;

	private JRadioButton stretch_size = new JRadioButton("to size");
	private JRadioButton stretch_factor = new JRadioButton("by factor");
	private ButtonGroup button_group = new ButtonGroup();
	private JButton ok_button = new JButton("ok");
	private JButton cancel_button = new JButton("cancel");
	private JTextField width_field = new JTextField();
	private JTextField height_field = new JTextField();
	private JTextField factor_field = new JTextField();
	
	public ApplyFactorDialog(Frame parent, PathNetModel model, PathNetPanel panel) {
		super(parent, "apply factor", true);
		if (model==null || panel==null)
			throw new IllegalArgumentException("model and panel must be != null.");
		this.model = model;
		this.panel = panel;
		
		setSize(300, 150);
		setResizable(false);
		button_group.add(stretch_factor);
		button_group.add(stretch_size);
		
		init();
		addListener();
		resetFields();
		stretch_factor.setSelected(true);
		
	}
	
	private void resetFields() {
		factor_field.setText("1.0");
		PathNetObject[] objects = model.getAllObjects();
		int max_x = PathNetTools.getMaxX(objects);
		int max_y = PathNetTools.getMaxY(objects);
		int min_x = PathNetTools.getMinX(objects);
		int min_y = PathNetTools.getMinY(objects);
		width_field.setText((max_x-min_x)+"");
		height_field.setText((max_y-min_y)+"");
	}
	
	public void setVisible(boolean value) {
		if (value)
			resetFields();
		super.setVisible(value);
	}
	
	private void addListener() {
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetFields();
				setVisible(false);
			}
		});
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFactor();
			}
		});
		stretch_factor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateFields();
			}
		});
		width_field.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()==KeyEvent.VK_ENTER)
					updateFieldValues(WIDTH);
			}
		});
		width_field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				updateFieldValues(WIDTH);
			}
		});
		height_field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				updateFieldValues(HEIGHT);
			}
		});
		height_field.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()==KeyEvent.VK_ENTER)
					updateFieldValues(HEIGHT);
			}
		});
		factor_field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				updateFieldValues(FACTOR);
			}
		});
		factor_field.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()==KeyEvent.VK_ENTER)
					updateFieldValues(FACTOR);
			}
		});
	}
	
	private void updateFieldValues(int type) {
		switch (type) {
			case WIDTH:
				int new_width;  
				try {
					new_width = Integer.parseInt(width_field.getText());
				} catch (Exception e) {
					width_field.requestFocus();
					return;
				}
				int old_width = getModelWidth();
				double fw = (double)new_width/(double)old_width;
				factor_field.setText(fw+"");
				height_field.setText(((int)(getModelHeight()*fw))+"");
				break;
			case HEIGHT:
				int new_height;
				try {
					new_height = Integer.parseInt(height_field.getText());
				} catch (Exception e) {
					height_field.requestFocus();
					return;
				}
				int old_height = getModelHeight();
				double fh = (double)new_height/(double)old_height;
				factor_field.setText(fh+"");
				width_field.setText(((int)(getModelWidth()*fh))+"");
				break;
			default: // factor
				double factor;
				try {
					factor = Double.parseDouble(factor_field.getText().replace(',','.'));
				} catch (Exception e) {
					factor_field.requestFocus();
					return;
				}
				width_field.setText(((int)((double)getModelWidth()*factor))+"");
				height_field.setText(((int)((double)getModelHeight()*factor))+"");
				break;
		}
	}
	
	private int getModelWidth() {
		PathNetObject[] objects = model.getAllObjects();
		return PathNetTools.getMaxX(objects)-PathNetTools.getMinX(objects);
	}
	
	private int getModelHeight() {
		PathNetObject[] objects = model.getAllObjects();
		return PathNetTools.getMaxY(objects)-PathNetTools.getMinY(objects);
	}
	
	private void updateFields() {
		boolean factor = stretch_factor.isSelected(); 
		factor_field.setEnabled(factor);
		width_field.setEnabled(!factor);
		height_field.setEnabled(!factor);
	}
	
	
	private void applyFactor() {
		double factor;
		try {
			factor = Double.parseDouble(factor_field.getText().replace(',','.'));
		} catch (Exception e) {
			factor_field.requestFocus();
			return;
		}
		PathNetTools.applyFactorToModelData(model, factor);
		panel.update();
		setVisible(false);
	}
	
	private void init() {
		JPanel c = new JPanel();
		c.setBorder(new EmptyBorder(5,5,5,5));
		GridBagConstraints gbc = new GridBagConstraints();

		c.setLayout(new GridBagLayout());
		
		// add headline
		JLabel heading = new JLabel("stretch model");
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		gbc.weighty = 0.0;
		gbc.weightx = 1.0;
		c.add(heading, gbc);
		resetGBC(gbc);
		
		// add radio buttons
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		c.add(stretch_size, gbc);
		gbc.gridx = 3;
		c.add(stretch_factor, gbc);
		resetGBC(gbc);
		
		// add labels
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		c.add(new JLabel("width"), gbc);
		gbc.gridy = 3;
		c.add(new JLabel("height"), gbc);
		// factor label
		gbc.gridx = 3;
		gbc.gridy = 2;
		c.add(new JLabel("factor"), gbc);
		resetGBC(gbc);
		
		// add textfields
		// size fields
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		c.add(width_field, gbc);
		gbc.gridy = 3;
		c.add(height_field, gbc);
		// factor field
		gbc.gridx = 4;
		gbc.gridy = 2;
		c.add(factor_field, gbc);
		resetGBC(gbc);

		
		// add ok/cancel button
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_END;
		c.add(ok_button, gbc);
		gbc.gridx = 3;
		c.add(cancel_button, gbc);
		resetGBC(gbc);

		// add separator
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridheight = 4;
		JSeparator sep = new JSeparator(JSeparator.VERTICAL);
		gbc.ipadx = 10;
		c.add(sep, gbc);
		
		getContentPane().add(c);

	}
	
	private void resetGBC(GridBagConstraints c) {
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
	}
	
	
	
}
