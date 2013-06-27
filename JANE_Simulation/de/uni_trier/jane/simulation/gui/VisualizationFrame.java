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
import javax.swing.event.*;


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.visualization.Worldspace;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class VisualizationFrame extends JFrame implements JANEVisualizationFrame {

	private SimulationPanel myPanel;
	private JScrollBar vsb;
	private JScrollBar hsb;
	
	/**
	 * 
	 */
	public VisualizationFrame() {

		super("Visualization");
		setSize(700, 600);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	
	
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		vsb = new JScrollBar(JScrollBar.VERTICAL);
		 hsb = new JScrollBar(JScrollBar.HORIZONTAL);
		JPanel mainPanel = new JPanel();
		myPanel = new SimulationPanel(new de.uni_trier.jane.basetypes.Rectangle(0,0,600,600), EmptyShape.getInstance());
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(myPanel, gbc);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		mainPanel.add(vsb, gbc);
		vsb.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				myPanel.setTopPixelValue(e.getValue());
				repaint();
			}
		});
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(hsb, gbc);
		hsb.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				myPanel.setLeftPixelValue(e.getValue());
				repaint();
			}
		});
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0.9;
		gbc.weighty = 0.9;
		gbc.fill = GridBagConstraints.BOTH;
		
		contentPane.add(mainPanel, gbc);
		// zoom slider
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(new JLabel("Zoom", JLabel.CENTER), gbc);
		JSlider zoomSlider = new JSlider(10, 1000, 100);
		zoomSlider.setOrientation(JSlider.VERTICAL);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setMajorTickSpacing(50);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				int value = s.getValue();
				myPanel.setZoom(value == 0 ? 0.01 : (value / 100.0));
				repaint();
			}
		});
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		contentPane.add(zoomSlider, gbc);		
		// ScreenShotbutton
	
		
			
		setVisible(false);
	}
	
	/**
	 * @see java.awt.Component#paint(Graphics)
	 */
	public void paint(Graphics g) {
		if (myPanel == null) {
			super.paint(g);
			return;
		}
		Dimension pref = myPanel.getPreferredSize();
		Dimension size = myPanel.getSize();
		int vExtent = (int) (size.height * (1 / myPanel.getZoom()));
		int vMax = pref.height;
		int vValue = Math.max(0, Math.min(vsb.getValue(), vMax - vExtent));
		vsb.setVisible(vExtent < vMax);
		vsb.setValues(vValue, vExtent, 0, vMax);
		int hExtent = (int) (size.width * (1 / myPanel.getZoom()));
		int hMax = pref.width;
		int hValue = Math.max(0, Math.min(hsb.getValue(), hMax - hExtent));
		hsb.setVisible(hExtent < hMax);		
		hsb.setValues(hValue, hExtent, 0, hMax);
		super.paint(g);
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.JANEFrame#addFrameListener(de.uni_trier.jane.simulation.gui.JANEFrameListener)
	 */
	public void addFrameListener(final JANEFrameListener frameListener) {
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				// NOP
			}
			public void componentMoved(ComponentEvent e) {
				// NOP
			}
			public void componentShown(ComponentEvent e) {
				// NOP
			}
			public void componentHidden(ComponentEvent e) {
				frameListener.frameHidden();							
			}
		}); 
		
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.JANEVisualizationFrame#addShape(de.uni_trier.jane.visualization.shapes.Shape, de.uni_trier.jane.basetypes.DeviceIDPositionMap)
	 */
	public void addShape(de.uni_trier.jane.visualization.shapes.Shape shape, DeviceIDPositionMap deviceIDPositionMap) {
		myPanel.setShape(shape,deviceIDPositionMap);

	}
	
	protected static class SimulationPanel extends JPanel {
		private de.uni_trier.jane.basetypes.Rectangle rectangle;
		private int topPixelValue;
		private int leftPixelValue;
		private double zoom;
		private de.uni_trier.jane.visualization.shapes.Shape shape;
		private DeviceIDPositionMap addressPositionMap;
		private Graphics2DCanvas painter;
		private PostScript2DRenderCanvas epsPainter;	

		private Worldspace worldspace;
		
		public SimulationPanel(
			de.uni_trier.jane.basetypes.Rectangle rectangle,
			de.uni_trier.jane.visualization.shapes.Shape shape) {
			this.rectangle = rectangle;
			this.shape = shape;
			this.zoom = 1;
			setBackground(java.awt.Color.white);
			painter = new Graphics2DCanvas();
			worldspace = new DefaultWorldspace(painter);
			
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).scale(zoom, zoom);
			((Graphics2D) g).translate(-leftPixelValue, -topPixelValue);
			painter.setGraphics((Graphics2D) g);
			shape.visualize(new Position(0, 0), worldspace, addressPositionMap);


		}
		public Dimension getPreferredSize() {
			return new Dimension((int) rectangle.getWidth(), (int) rectangle.getHeight());
		}
		public void setTopPixelValue(int pixelValue) {
			topPixelValue = pixelValue;
		}
		public void setLeftPixelValue(int pixelValue) {
			leftPixelValue = pixelValue;
		}
		public void setZoom(double zoom) {
			this.zoom = zoom;
			repaint();
		}
		public double getZoom() {
			return zoom;
		}
		
		/**
		 * @param x
		 * @param y
		 * @return
		 */
		public Position translate(int x, int y) {
			return new Position(x/zoom,y/zoom);
		}
		
		public void setShape(de.uni_trier.jane.visualization.shapes.Shape shape, DeviceIDPositionMap addressPositionMap) {
			this.shape = shape;
			this.addressPositionMap = addressPositionMap;
			repaint();
		}
		/**
		 * @param rectangle2
		 */
		public void setRectangle(de.uni_trier.jane.basetypes.Rectangle rectangle) {
			this.rectangle=rectangle;
			
			
		}
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.JANEVisualizationFrame#setRectangle(de.uni_trier.jane.basetypes.Rectangle)
	 */
	public void setRectangle(de.uni_trier.jane.basetypes.Rectangle rectangle) {
		
		myPanel.setRectangle(rectangle);
		
	}

}
