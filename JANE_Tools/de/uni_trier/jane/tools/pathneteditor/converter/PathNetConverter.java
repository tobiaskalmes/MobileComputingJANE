/*
 * Created on May 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.converter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import de.uni_trier.jane.tools.pathneteditor.constants.XMLConstants;
import de.uni_trier.jane.tools.pathneteditor.export.ModelImportExportFilter;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.PathNetLoader;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PathNetConverter extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Private classes
	 */
	
	// scaling all values of the input stream with an specified factor
	private class ScalingInputStream extends InputStream implements XMLConstants {
		private InputStream in;
		private double factor;
		private int index = 0;
		
		private boolean tagOpen = false;
		private boolean textValue = false;
		private boolean noTag = false;
		private StringBuffer sb = new StringBuffer();
		
		public ScalingInputStream(InputStream in, double factor) throws IOException{			
			this.in = in;
			this.factor = factor;
			
			int b;
			while((b = in.read()) != -1) {
				sb.append((char)b);
			}

			System.out.println("Starting to convert... " + (new Date()).toString());
			
			// factorize all widths
			factorize(XML_ATT_WIDTH, sb);
			
			// factorize all positions
			factorize(XML_ATT_POS_X, sb);
			factorize(XML_ATT_POS_Y, sb);
			factorize(XML_ATT_POS_Z, sb);
			
			// factorize all area values
			factorize(XML_ATT_AREA_DEPTH, sb);
			factorize(XML_ATT_AREA_HEIGHT, sb);
			factorize(XML_ATT_AREA_WIDTH, sb);
			
			System.out.println("Converting has finished... " + (new Date()).toString());
		}
		
		public int read() throws IOException {
			return index < sb.length() ? sb.charAt(index++) : -1;
		}

		public int available() {
			return sb.length() - index + 1;
		}
		
		private void factorize(String tagname, StringBuffer sb) {
			int i = -1, offset = 0;
			while ((i=sb.toString().indexOf(tagname+"=\"", offset)) != -1) {
				offset = i + tagname.length() + 2;
				i = sb.toString().indexOf("\"", offset);
				sb.replace(offset, i, applyFactor(sb.substring(offset, i)));
			}	
		}
		
		private String applyFactor(String s) {
			// buffer has form xxxxx.xxxxxx						
			try {
				double v = Double.parseDouble(s) * factor;
				return "" + v;
			} catch (NumberFormatException e) {
				return s;
			}
		}
	};
	
	// constants
	public final static ModelImportExportFilter 	DEFAULT_MODEL_FILTER = PathNetLoader.DEFAULT_MODEL_FILTER;
	public final static ModelImportExportFilter 	DEFAULT_COLLECTION_FILTER = PathNetLoader.DEFAULT_COLLECTION_FILTER;
	protected final static FileFilter XML_FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.toString().toLowerCase().endsWith(".xml");
		}
		public String getDescription() { return "XML File (*.xml)"; }					
	};
	
	private JSplitPane	factorSP		= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JPanel	originalP 	= new JPanel();
	private JPanel	modifiedP 	= new JPanel();
	private JPanel	buttonP		= new JPanel();
	private JButton	okB			= new JButton("Save");
	private JButton cancelB		= new JButton("Cancel");
	
	protected 	JTextField	collection_orig_TF = new JTextField();
	protected 	JButton		collection_orig_B = new JButton("...");		
	protected 	JTextField	model_orig_TF = new JTextField();
	protected	JButton		model_orig_B = new JButton("...");
	protected	JButton		preview_orig_B = new JButton("Show Preview");
	
	protected 	JTextField	collection_mod_TF = new JTextField();
	protected 	JButton		collection_mod_B = new JButton("...");		
	protected 	JTextField	model_mod_TF = new JTextField();
	protected	JButton		model_mod_B = new JButton("...");	
	protected	JSpinner	factor_mod_SP = new JSpinner();
		
	private		GraphicsPanel	preview_orig_panel = new GraphicsPanel(new DefaultPathNetModel());	
	private		GraphicsPanel	preview_mod_panel = new GraphicsPanel(new DefaultPathNetModel());
	
	// export/import filter
	protected	Vector		filter = new Vector();	
	
	// gui
	protected 	JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
	
	public PathNetConverter(Frame parent) {
		super(parent);
		
		// add new filter here
		filter.add(DEFAULT_MODEL_FILTER);
		filter.add(DEFAULT_COLLECTION_FILTER);
		
		fc.setAcceptAllFileFilterUsed(true);
		fc.setFileFilter(XML_FILTER);
		
		init();
		setSize(640, 480);
	}

	private void init() {
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		c.add(factorSP, BorderLayout.CENTER);
		c.add(buttonP, BorderLayout.SOUTH);
				
		factorSP.setLeftComponent(originalP);
		factorSP.setRightComponent(modifiedP);		
						
		buttonP.setLayout(new GridLayout(1, 5, 5, 0));
		buttonP.add(new JPanel());
		buttonP.add(okB);
		buttonP.add(new JPanel());
		buttonP.add(cancelB);
		buttonP.add(new JPanel());
	
		/*
		 * Set left (original) panel
		 */
		originalP.setBackground(Color.WHITE);
		preview_orig_panel.showCoordinateSystem(false);
		preview_orig_panel.showMouseMeter(false);
		preview_orig_panel.showMeterRule(false);
		preview_orig_panel.showZeroSource(false);
		preview_orig_panel.showOverviewMap(false);
		preview_orig_panel.setBorder(new TitledBorder("Original:"));
		initPanel(
				originalP,
				model_orig_TF, model_orig_B,
				collection_orig_TF, collection_orig_B,
				preview_orig_B,
				preview_orig_panel
		);
		
		/*
		 * Set right (modified) panel
		 */
		modifiedP.setBackground(Color.WHITE);
		preview_mod_panel.showCoordinateSystem(false);
		preview_mod_panel.showMouseMeter(false);
		preview_mod_panel.showMeterRule(false);
		preview_mod_panel.showZeroSource(false);
		preview_mod_panel.showOverviewMap(false);
		preview_mod_panel.setBorder(new TitledBorder("Preview:"));
		
		factor_mod_SP.setModel(new SpinnerNumberModel(1.0, .0001, 1000, 1));
		
		JPanel factor_wrapper = new JPanel();
		factor_wrapper.setBackground(getBackground());
		factor_wrapper.setLayout(new BoxLayout(factor_wrapper, BoxLayout.X_AXIS));
		factor_wrapper.add(new JLabel("Apply factor: "));
		factor_wrapper.add(factor_mod_SP);
		factor_wrapper.setPreferredSize(new Dimension(200, preview_orig_B.getPreferredSize().height));
		
		initPanel(
				modifiedP,
				model_mod_TF, model_mod_B,
				collection_mod_TF, collection_mod_B,
				factor_wrapper,
				preview_mod_panel
		);		
	
		/*
		 * The button listeners
		 */
		ActionListener loadListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = 0;
				
				result = fc.showOpenDialog(PathNetConverter.this);
				
				switch(result) {
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.APPROVE_OPTION:
						String file = fc.getSelectedFile().toString();
						if ( e.getSource() == collection_orig_B )
							collection_orig_TF.setText(file);
						else
							model_orig_TF.setText(file);
						break;
				}
			}
		};		
		collection_orig_B.addActionListener(loadListener);		
		model_orig_B.addActionListener(loadListener);
		
		ActionListener saveListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = 0;
				
				result = fc.showSaveDialog(PathNetConverter.this);
				
				switch(result) {
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.APPROVE_OPTION:
						String file = fc.getSelectedFile().toString();
						if ( e.getSource() == collection_mod_B )
							collection_mod_TF.setText(file);
						else
							model_mod_TF.setText(file);
						break;
				}
			}
		};		
		collection_mod_B.addActionListener(saveListener);		
		model_mod_B.addActionListener(saveListener);
		
		cancelB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		okB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveModifiedModel();
				setVisible(false);
			}
		});
		
		preview_orig_B.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOriginalModel();
				showModifiedModel();
			}
		});
	}
	
	private void initPanel(
			JPanel field_P, JTextField model_TF, JButton model_B, JTextField collection_TF, JButton collection_B,
			JComponent previewOrFactorC, GraphicsPanel previewP) {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		field_P.setLayout(gbl);
		
		gbc.insets = new Insets(5, 5, 10, 10);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;		
		gbl.addLayoutComponent(model_TF, gbc);		
		field_P.add(model_TF);
		
		gbc.weightx = 0.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;;
		gbl.addLayoutComponent(model_B, gbc);
		field_P.add(model_B);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridwidth = 1;
		gbl.addLayoutComponent(collection_TF, gbc);		
		field_P.add(collection_TF);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;		
		gbl.addLayoutComponent(collection_B, gbc);
		field_P.add(collection_B);
		
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.weightx = 1.0;
//		gbc.gridwidth = 1;
//		gbl.addLayoutComponent(factorSP, gbc);
//		field_P.add(factorSP);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;		
		gbl.addLayoutComponent(previewOrFactorC, gbc);
		field_P.add(previewOrFactorC);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbl.addLayoutComponent(previewP, gbc);
		field_P.add(previewP);
		
		model_TF.setBorder(new TitledBorder("PathNetModel File:"));
		collection_TF.setBorder(new TitledBorder("RoomCollection File:"));
		
	}
	
	public void setVisible(boolean setVisible) {
		super.setVisible(setVisible);
		
		factorSP.setDividerLocation(.5);
	}
	
	private void saveModifiedModel() {
		File out_model = new File(model_mod_TF.getText());
		File out_collection = new File(collection_mod_TF.getText());
		
		try {
		
			if (!out_model.exists() || showFileExistsDialog(out_model)) {
				// save model
				if (!model_mod_TF.getText().trim().equals("")) {
					OutputStream model_out = new BufferedOutputStream(new FileOutputStream(out_model));
					ModelImportExportFilter model_filter = DEFAULT_MODEL_FILTER;
					
					model_filter.saveModelData(model_out, preview_mod_panel.getPathnetModel());			
					model_out.close();
				}
			}
			
			if (!out_collection.exists() || showFileExistsDialog(out_collection)) {
				// load collections			
				if (!collection_mod_TF.getText().trim().equals("")) {									
					OutputStream collection_out = new BufferedOutputStream(new FileOutputStream(out_collection));
					ModelImportExportFilter collection_filter = DEFAULT_COLLECTION_FILTER;
					
					// collection_filter.setSettings(probs);
					collection_filter.saveModelData(collection_out, preview_mod_panel.getPathnetModel());			
					collection_out.close();
				}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showOriginalModel() {
		String model_file = model_orig_TF.getText();
		String collection_file = collection_orig_TF.getText();
		
		try {
		
			InputStream model_in = model_file.trim().equals("")
				? null
			    : new BufferedInputStream(new FileInputStream(model_file));
			
			InputStream collection_in = collection_file.trim().equals("")
				? null
				: new BufferedInputStream(new FileInputStream(collection_file));
			
			loadModel(getFileFilter(model_file), model_in, getFileFilter(collection_file), collection_in, preview_orig_panel.getPathnetModel());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showModifiedModel() {
		String model_file = model_orig_TF.getText();
		String collection_file = collection_orig_TF.getText();
		
		double factor = Double.parseDouble(factor_mod_SP.getValue().toString().replaceAll(",",""));
		
		try {
		
			InputStream model_in = model_file.trim().equals("")
				? null
			    : new ScalingInputStream(new BufferedInputStream(new FileInputStream(model_file)), factor);
			
			InputStream collection_in = collection_file.trim().equals("")
				? null
				: new ScalingInputStream(new BufferedInputStream(new FileInputStream(collection_file)), factor);
			
			loadModel(getFileFilter(model_file), model_in, getFileFilter(collection_file), collection_in, preview_mod_panel.getPathnetModel());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadModel(
			ModelImportExportFilter model_filter, InputStream model_in,
			ModelImportExportFilter collection_filter, InputStream collection_in, PathNetModel model) {
		
		try {
			
			// disable model events
			if (model instanceof DefaultPathNetModel)
				((DefaultPathNetModel)model).enableModelEvents(false);
			
			// reset model
			model.clearModelData();
			
			if (model_in != null) {							
				// load model								
				model_filter.loadModelData(model_in, model);			
				model_in.close();
			}
			
			if (collection_in != null) {
				// load collection
				collection_filter.loadModelData(collection_in, model);			
				collection_in.close();
			}
			
		} catch (IOException e) {
			// enable model events
			if (model instanceof DefaultPathNetModel)
				((DefaultPathNetModel)model).enableModelEvents(true);
			return;			
		}
		
		// enable model events
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(true);
	}
	
	private boolean showFileExistsDialog(File file) {
		System.err.println("WARNING: Overwriting file "+file);
		return true;
	}
	
	protected ModelImportExportFilter getFileFilter(String filename) {
		for (int i=0; i<filter.size(); i++) {
			ModelImportExportFilter f = (ModelImportExportFilter)(filter.get(i));
			
			try {
				InputStream stream = new FileInputStream(filename);
								
				if ( f.acceptsStream(stream) )
					return f;
				
				stream.close();
			} catch(FileNotFoundException e) {
				continue;
			} catch (IOException e) {
				continue;
			}
		}
		
		System.out.println(filename + ": No filter found for file: "+filename);
		
		return null;
	}
	
	public static void main(String[] args) {
		Frame f = new Frame();
		PathNetConverter main = new PathNetConverter(f);

		main.setVisible(true);
		main.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
	}
}
