package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.*;
import de.uni_trier.jane.tools.pathneteditor.objects.Edge;
import de.uni_trier.jane.tools.pathneteditor.objects.Target;
import de.uni_trier.jane.tools.pathneteditor.objects.Waypoint;


public class ModelSettingsPanel extends JPanel implements PathNetConstants {
	
	static final long serialVersionUID = -3225689961795585460L;
   
    // datastructure variables
	protected PathNetModel	model	=	null; 
	
	// gui variables
	protected ProbTable			main_T;
	protected EdgeProbabilityProperties	edge_P;	
	protected JScrollPane	main_SP	=	new JScrollPane();
	protected JScrollPane	edge_SP	=	new JScrollPane();
	protected JSplitPane	main_edge_SP =new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	// private/protected classes		
	protected class Edge_TM implements TableModel {
		
		private Waypoint	source;
		private Target		target;
		private Edge[]		edgeList;
		
		public Edge_TM(Waypoint source, Target target) {
			this.source = source;
			this.target = target;
			this.edgeList = model.getEdges(source);
		}
		
		public int getColumnCount() {
			// this table is row-oriented now
			return edgeList.length;
		}

		public int getRowCount() {
			// this table is row-oriented now
			return 1;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// indeed, these cells are editable
			return true;
		}

		public Class getColumnClass(int columnIndex) {
			// just Strings
			return new String().getClass();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			// return the probability of the edge
			return "" + model.getProb(source, target, edgeList[columnIndex]);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// change probability of edge and fire event
			double d = 0;
			
			try {
				d = Double.parseDouble(aValue.toString());
			} catch (NumberFormatException e) {
				System.err.println("Invalid probability for cell: " + rowIndex + " / " + columnIndex);
				return;
			}
						
			model.setProb(source, target, edgeList[columnIndex], d);
		}

		public String getColumnName(int columnIndex) {			
			return edgeList[columnIndex].getID();
		}

		public void addTableModelListener(TableModelListener l) {}

		public void removeTableModelListener(TableModelListener l) {}
		
	};
		
	// constructor
	public ModelSettingsPanel(PathNetModel model) {
		this.model = model;
		main_T = new ProbTable(model);
		edge_P = new EdgeProbabilityProperties(model);
		
		init();
	}
	
	// private methods
	private void init() {
		this.setLayout(new BorderLayout());				
		
//		main_SP.setViewportView(main_T);
//		main_SP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
		edge_SP.setViewportView(edge_P);
		
		main_edge_SP.setTopComponent(main_T);		
		main_edge_SP.setBottomComponent(edge_SP);
		main_edge_SP.setDividerLocation(300);
		this.add(main_edge_SP, BorderLayout.CENTER);
		
		main_T.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {				
				if (e.getButton() != MouseEvent.BUTTON1)
					return;
				
				int r = main_T.getSelectedRow();			
				int c = main_T.getSelectedColumn();
				
				edge_P.showProbs(
						model.getTargetsAndWaypoints()[r],
						model.getTargets()[c]
				);
				
				setSelections(r, c);
			}
		});
		
		model.addPathNetListener(new PathNetModelAdapter() {

			public void objectAdded(PathNetModelEvent event) {				
				main_T.setEnabled(true);
			}

			public void modelDataCleared(PathNetModelEvent e) {
				main_T.setEnabled(false);
				edge_P.showProbs(null, null);
			}
			
			public void modelDataLoaded(PathNetModelEvent e) {
				main_T.setEnabled(true);
			}
		
			public void modelEventsReenabled(PathNetModelEvent e) {
				objectAdded(e);
			}
		});
		
		main_T.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				int r = main_T.getSelectedRow();			
				int c = main_T.getSelectedColumn();
				
				edge_P.showProbs(
						model.getTargetsAndWaypoints()[r],
						model.getTargets()[c]
				);
				
				setSelections(r, c);
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
			
		});
	}
	
	private void setSelections(int row, int col) {
		SelectionModel select = model.getSelectionModel();
		
		Waypoint 	source	= model.getTargetsAndWaypoints()[row];
		Target	 	target	= model.getTargets()[col];
		Edge[]		edges	= model.getEdges(source);
		
		select.setEventsEnabled(false);
		
		select.clear();
		select.add(source);
		select.add(target);
		
		for (int i=0; i<edges.length; i++) {
			select.add(edges[i]);
		}
		
		select.setEventsEnabled(true);
	}
}
