package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelAdapter;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelEvent;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.SliderManager;


public final class EdgeProbabilityProperties extends JPanel {
	static final long serialVersionUID = 6176189434496979766L;
	
	// constants
	final static private int	REMAINDER = GridBagConstraints.REMAINDER;
	
	// datastructure
	private PathNetModel model;
	
	private Waypoint	currentWaypoint;
	private Target		currentTarget;
	
	private ActionListener	lastActionListener = null;
	private ChangeListener	lastChangeListener = null;
//	private SliderGroup		sliderGroup = new SliderGroup();
	private SliderManager	sliderManager = new SliderManager(0, 100);
	
	// gui
	private JLabel 			noneSelected_L	=	new JLabel("No cell selected");
	private JLabel 			noEdges_L   =	new JLabel("This waypoint has no edges to edit");
			
	public EdgeProbabilityProperties(PathNetModel model) {
		this.model = model;
		init();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		
		noneSelected_L.setHorizontalAlignment(JLabel.CENTER);
		noneSelected_L.setVerticalAlignment(JLabel.CENTER);
		noEdges_L.setHorizontalAlignment(JLabel.CENTER);
		noEdges_L.setVerticalAlignment(JLabel.CENTER);
		
		model.addPathNetListener(new PathNetModelAdapter() {
			public void objectAdded(PathNetModelEvent event) {
				if (((PathNetObject)event.getSource()).getObjectType() == PathNetConstants.EDGE)
					showProbs(currentWaypoint, currentTarget);
			}

			public void objectDeleted(PathNetModelEvent event) {
				if (((PathNetObject)event.getSource()).getObjectType() == PathNetConstants.EDGE)
					showProbs(currentWaypoint, currentTarget);
			}

			public void objectPropertyChanged(ObjectEvent e) {				
				//showProbs(currentWaypoint, currentTarget);
			}
		});
		
		this.add(noneSelected_L, BorderLayout.CENTER);
	}
	
	public void showProbs(final Waypoint w, final Target t) {
		currentWaypoint = w;
		currentTarget = t;
		
		final Edge[] edges = model.getEdges(w);
		this.removeAll();
		this.setLayout(new BorderLayout());
		
		if( w == t || w == null || t == null ) {
			this.add(noneSelected_L, BorderLayout.CENTER);
			revalidate();
			repaint();
			return;
		}
		
		if (edges.length == 0) {
			this.add(noEdges_L, BorderLayout.CENTER);
			revalidate();
			repaint();
			return;
		}
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();			
		this.setLayout(gbl);			
		
		final JSlider[]	edgeSlider = new JSlider[edges.length];
		sliderManager.reset();
		
		for (int i=0; i<edges.length; i++) {
			int prob = (int)(model.getProb(w, t, edges[i])*100);
			
			final JLabel edge_L = new JLabel("Edge["+i+"] "+ edges[i].getID() + ": ");
			final JSlider edge_S = new JSlider(0, 100, prob);
			final JTextField edge_TF = new JTextField(""+model.getProb(w, t, edges[i]));
			final Edge edge = edges[i];
			
			// add slider to slider group
			sliderManager.addSlider(edge_S);
			
			
//			if (edges.length == 1 && keep_sum_CB.isSelected()) {
//				edge_S.setEnabled(false);
//				edge_TF.setEnabled(false);
//				
//				if (lastActionListener != null)
//					keep_sum_CB.removeActionListener(lastActionListener);
//				
//				keep_sum_CB.addActionListener(lastActionListener = new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						edge_S.setEnabled(!keep_sum_CB.isSelected());
//						edge_TF.setEnabled(!keep_sum_CB.isSelected());
//					}
//				});
//			}
			
			edgeSlider[i] = edge_S;
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			set(gbl, gbc, 1, 1, 0.1, 0.0, edge_L, this);								
			set(gbl, gbc, 1, 1, 0.6, 0.0, edge_S, this);
			set(gbl, gbc, 1, 1, 0.0, 0.0, sliderManager.getCheckBox(edge_S), this);
			set(gbl, gbc, REMAINDER, ( i == edges.length - 1 ? REMAINDER : 1 ), 0.3, 0.0, edge_TF, this);			
			
			edge_S.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {						
					edge_TF.setText("" + edge_S.getValue() / 100.0);
					model.setProb(w, t, edge, edge_S.getValue() / 100.0);
				}
			});
			
			edge_TF.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					double d = model.getProb(w, t, edge);
					
					try {
						double old_d = d;
						d = Double.parseDouble(edge_TF.getText());
						if (d<0.0 || d>1.0)
							d = old_d;
					} catch (NumberFormatException ex) {
						Toolkit.getDefaultToolkit().beep();							
					}
					
					edge_TF.setText("" + d);
					edge_S.setValue((int)(d*100));
					model.setProb(w, t, edge, d);
				}
			});
		} // end of for
					
		revalidate();
		repaint();		
	}
	
	private void set(GridBagLayout gbl, GridBagConstraints gbc, int gw, int gh,
			double wx, double wy, Component c, Container cont) {
		gbc.gridwidth  = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}
}
