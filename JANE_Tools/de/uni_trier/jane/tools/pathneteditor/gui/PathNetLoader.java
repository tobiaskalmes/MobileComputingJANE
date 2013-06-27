package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.export.*;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class PathNetLoader extends JDialog {

	// serializable
	static final long serialVersionUID = 8832215757059401789L;

	// constants
	public final static ModelImportExportFilter 	DEFAULT_MODEL_FILTER = new XMLPathNetFilter_v2();
	public final static ModelImportExportFilter 	DEFAULT_COLLECTION_FILTER = new XMLRoomCollectionFilter_v2();
	
	protected final static String	INVALID_FILES = "<html><font color=RED align=CENTER >Files are not valid</font></html>";
	protected final static String	VALID_FILES = "<html><font color=GREEN align=CENTER>Selected files are valid</font></html>";
	protected final static String	LOAD_BUTTON = "Load";
	protected final static String	SAVE_BUTTON = "Save";
	protected final static String	NOT_LOAD_COLLECTION = "Do not load any collection";
	protected final static String	NOT_SAVE_COLLECTION = "Do not save any collection";
	
	protected final static FileFilter XML_FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.toString().toLowerCase().endsWith(".xml");
		}
		public String getDescription() { return "XML File (*.xml)"; }					
	};
	
	// export/import filter
	protected	Vector		filter = new Vector();
	protected	boolean		import_model_data = false;
	
	// gui
	protected 	JFileChooser fc;
	
	protected 	JTextField	collection_TF = new JTextField();
	protected 	JButton		collection_B = new JButton("Choose...");
	protected 	JCheckBox	collection_not_load_CB = new JCheckBox(NOT_LOAD_COLLECTION);
	
	protected 	JTextField	model_TF = new JTextField();
	protected	JButton		model_B = new JButton("Choose...");
	protected 	JPanel		field_P = new JPanel();
	
	protected 	JButton		approve_B = new JButton(LOAD_BUTTON);
	protected 	JButton		cancel_B = new JButton("Cancel");
	protected	JPanel		button_P = new JPanel();

	protected 	JPanel		input_P = new JPanel();
	
	protected	JLabel		info_L = new JLabel();
		
	public PathNetLoader(Frame parent, PathNetModel model) {
		super(parent);
         fc= new JFileChooser( Settings.getString(PathNetConstants.DEFAULT_PATH));		
		// add new filter here
		filter.add(DEFAULT_MODEL_FILTER);
		filter.add(DEFAULT_COLLECTION_FILTER);
		filter.add(new XMLPathNetFilter());
		filter.add(new XMLRoomCollectionFilter());
		
		// container settings
		setModal(true);
						
		init();
		pack();
		
		setSize(new Dimension(600, getHeight()));
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();		
		setLocation( (screen.width-getWidth())/2, (screen.height-getHeight())/2 );
		
		setTitle("Load PathNet");
	}
	
	private void init() {
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());	
		c.add(input_P, BorderLayout.CENTER);
		
		input_P.setLayout(new BorderLayout());
		input_P.add(field_P, BorderLayout.CENTER);
		input_P.add(button_P, BorderLayout.SOUTH);
		
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
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = GridBagConstraints.REMAINDER;		
		gbc.insets = new Insets(0, 5, 10, 0);		
		gbl.addLayoutComponent(collection_not_load_CB, gbc);
		field_P.add(collection_not_load_CB);
		
		model_TF.setBorder(new TitledBorder("PathNetModel File:"));
		collection_TF.setBorder(new TitledBorder("RoomCollection File:"));
		
		button_P.setLayout(new BoxLayout(button_P, BoxLayout.X_AXIS));		
		button_P.add(info_L);
		button_P.add(Box.createHorizontalGlue());
		button_P.add(approve_B);
		button_P.add(Box.createRigidArea(new Dimension(10,0)));
		button_P.add(cancel_B);		
		button_P.setBorder(new EmptyBorder(5, 5, 10, 10));
		
		info_L.setText(INVALID_FILES);
		
		fc.setAcceptAllFileFilterUsed(true);
		fc.setFileFilter(XML_FILTER);
				
		collection_TF.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if ( filesAreValid() )
					info_L.setText(VALID_FILES);
				else
					info_L.setText(INVALID_FILES);
			}			
		});
		model_TF.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if ( filesAreValid() )
					info_L.setText(VALID_FILES);
				else
					info_L.setText(INVALID_FILES);
			}			
		});
		
		ActionListener changeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = 0;
				
				if (approve_B.getText().equals(LOAD_BUTTON))
                    
					result = fc.showOpenDialog(PathNetLoader.this);
				else
					result = fc.showSaveDialog(PathNetLoader.this);
				
				
                switch(result) {
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.APPROVE_OPTION:
                        File file=fc.getSelectedFile();
                        Settings.set(Settings.DEFAULT_PATH,file.getParent());
						String fileName = file.toString();
                        
						if ( e.getSource() == collection_B )
							collection_TF.setText(fileName);
						else
							model_TF.setText(fileName);
						break;
				}
			}
		};		
		collection_B.addActionListener(changeListener);		
		model_B.addActionListener(changeListener);
		
		cancel_B.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				collection_TF.setText("");
				model_TF.setText("");
				PathNetLoader.this.setVisible(false);
			}
		});
		
		collection_not_load_CB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				collection_TF.setEnabled(!collection_not_load_CB.isSelected());
				collection_B.setEnabled(!collection_not_load_CB.isSelected());
				
				if ( filesAreValid() )
					info_L.setText(VALID_FILES);
				else
					info_L.setText(INVALID_FILES);
				
			}			
		});
		
		approve_B.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PathNetLoader.this.setVisible(false);				
			}
		});		
	}
	
	public String getFilename() {
		return fc.getSelectedFile().toString();
	}
	
	public void loadModel(PathNetModel model) {				
		approve_B.setText( LOAD_BUTTON );
		collection_not_load_CB.setText(NOT_LOAD_COLLECTION);
		setTitle("Load PathNet");
		setVisible(true);
		
		String model_file = model_TF.getText();
		String collection_file = collection_TF.getText();
				
		if (!filesAreValid()) {
			System.err.println("Files are not valid !!!");
			return;
		}

		// Properties probs = new Properties();
		// probs.add("SCALE", "10");
		
		try {
			// disable model events
			if (model instanceof DefaultPathNetModel)
				((DefaultPathNetModel)model).enableModelEvents(false);
			
			// reset model
			if (!import_model_data) model.clearModelData();
						
			// load model			
			InputStream model_in = new BufferedInputStream(new FileInputStream(model_file));
			ModelImportExportFilter model_filter = getFileFilter(model_file);
			
			// model_filter.setSettings(probs);
			model_filter.loadModelData(model_in, model);			
			model_in.close();
			
			// load collections			
			if (collection_not_load_CB.isSelected())
				return;
			
			InputStream collection_in = new BufferedInputStream(new FileInputStream(collection_file));
			ModelImportExportFilter collection_filter = getFileFilter(collection_file);
			
			// collection_filter.setSettings(probs);
			collection_filter.loadModelData(collection_in, model);			
			collection_in.close();
			
		} catch(FileNotFoundException e) {
			// enable model events
			if (model instanceof DefaultPathNetModel)
				((DefaultPathNetModel)model).enableModelEvents(true);
			return;
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
	
	public void saveModel(PathNetModel model) {				
		approve_B.setText(SAVE_BUTTON);
		collection_not_load_CB.setText(NOT_SAVE_COLLECTION);
		setTitle("Save PathNet");
		setVisible(true);
		
		String model_file = model_TF.getText();
		String collection_file = collection_TF.getText();
				
		if (!filesAreValid()) {
			System.err.println("Files are not valid !!!");
			return;
		}

		// Properties probs = new Properties();
		// probs.add("SCALE", "10");
		
		try {			
			// save model			
			OutputStream model_out = new BufferedOutputStream(new FileOutputStream(model_file));
			ModelImportExportFilter model_filter = DEFAULT_MODEL_FILTER;
			
			// model_filter.setSettings(probs);
			model_filter.saveModelData(model_out, model);			
			model_out.close();
			
			// load collections			
			if (collection_not_load_CB.isSelected())
				return;
			
			OutputStream collection_out = new BufferedOutputStream(new FileOutputStream(collection_file));
			ModelImportExportFilter collection_filter = DEFAULT_COLLECTION_FILTER;
			
			// collection_filter.setSettings(probs);
			collection_filter.saveModelData(collection_out, model);			
			collection_out.close();
			
		} catch(FileNotFoundException e) {			
			return;
		} catch (IOException e) {		
			return;			
		}		
	}
	
	public static void main(String[] args) {
		PathNetLoader main = new PathNetLoader(null, null);
		main.setVisible(true);
		main.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
	}
	
	protected boolean filesAreValid() {
		// get filenames
		String model_file = model_TF.getText();
		String collection_file = collection_TF.getText();
		
		// identical files
		if (model_file.equals(collection_file))
			return false;
		
		// check model file
		if (model_file.equals(""))
			return false;
		
		File f = new File(model_file);
		if (approve_B.getText().equals(LOAD_BUTTON)) {
			if ( !f.exists() || !f.canRead() || f.isDirectory() || !isPathNetFile(model_file) )
				return false;
		} else {
			if ( f.isDirectory() ) {
				return false;	
			}				
		}
		
		// check collection file
		if (collection_not_load_CB.isSelected())
			return true;
		
		if (collection_file.equals(""))
			return false;
		
		f = new File(collection_file);
		if (approve_B.getText().equals(LOAD_BUTTON)) {
			if ( !f.exists() || !f.canRead() || f.isDirectory() || !isCollectionFile(collection_file) )
				return false;
		} else {
			if ( f.isDirectory() )
				return false;
		}
		
		// still there? then the files are valid!
		return true;
	}
	
	protected boolean isCollectionFile(String filename) {
		ModelImportExportFilter ff = getFileFilter(filename);
		return ( ff!=null ) && ( ff instanceof RoomCollection );
	}
	
	protected boolean isPathNetFile(String filename) {
		ModelImportExportFilter ff = getFileFilter(filename);
		return ( ff!=null ) && ( ff instanceof PathNet );
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
}
