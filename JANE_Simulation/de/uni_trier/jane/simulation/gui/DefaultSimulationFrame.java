/*****************************************************************************
 * 
 * SimulationFrame.java
 * 
 * $Id: DefaultSimulationFrame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * This is a simple <code>JFrame</code> implementation to visualize the simulation
 * run.
 */
public class DefaultSimulationFrame extends JFrame implements ActionListener, SimulationFrame {

	
	// Screenshot varibles
	private static boolean saveScreenshot = false;
	


	private final static String VERSION = "$Id: DefaultSimulationFrame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private JButton showVisualization;
	private JButton showStatistics;	
	private JButton showConsole;

	private JScrollBar vsb;
	private JScrollBar hsb;
	public SimulationPanel myPanel;
	
	protected JLabel simTime;
	
	protected FrameSource frameSource;
	private javax.swing.Timer repaintTimer;
	private double fps;
	protected Console console;
//	private BufferedData bufferedData;
	
	private JFrame visualizationFrame;
	private JFrame statisticsFrame;
	private JFrame consoleFrame;
	private JButton screenShot;

	private Font sliderLabelFont = new Font("SansSerif", Font.PLAIN, 10);

	//private double lastSecond;
	//private HashSet shutdownListeners;



    private ShutdownAnnouncer globalShutdownAnnouncer;
    private ShutdownListener myShutdownListener;
    


    /**
     * @deprecated use de.jane.simulation.sgui.SimulationMainFrame instead
     *
     */
    public DefaultSimulationFrame() {
    }
    
    /**
     *
     */

    public void show(SimulationParameters parameters) {
        globalShutdownAnnouncer=parameters.getGlobalShutdownAnnouncer();
        myShutdownListener=new ShutdownListener() {
            /**
             *
             */

            public void shutdown() {
                dispose();

            }
        };

		RenderQueue queue = new RenderQueue(parameters);
		frameSource=queue;
		parameters.setFrameRenderer(queue);
		this.fps = parameters.getSimulationFrameFPS();

		initControlFrame(parameters.getSimulationName());
		showSplashScreen();
		initComponents();
		double frameInterval = 1000.0 / fps;
		repaintTimer = new javax.swing.Timer((int) frameInterval, this);
		repaintTimer.start();
        globalShutdownAnnouncer.addShutdownListener(myShutdownListener);
        super.show();
    }
	

   

	private void showSplashScreen() {
		final JWindow splashScreen = new JWindow();

		JLabel label = new JLabel(new ImageIcon(getClass().getResource("splash.jpg")));

		label.setBorder(BorderFactory.createRaisedBevelBorder());
		Container contentPane = splashScreen.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(label, BorderLayout.CENTER);
		splashScreen.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                splashScreen.setVisible(false);
                splashScreen.dispose();
            }
        });
        new javax.swing.Timer(10000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				splashScreen.setVisible(false);
				splashScreen.dispose();
			}
		}).start();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		splashScreen.setLocation(screenSize.width/2 - labelSize.width/2, screenSize.height/2 - labelSize.height/2);
		splashScreen.pack();
		splashScreen.setVisible(true);
		splashScreen.toFront();
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

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		de.uni_trier.jane.simulation.visualization.Frame frame = frameSource.getFrame();
		if (console != null) {
			ConsoleTextIterator it = frame.getConsoleTextIterator();
			while (it.hasNext()) {
				it.next().println(console);
			}
		}
		simTime.setText("time " + (int) frame.getTime());
		if (myPanel != null) {
			myPanel.setShape(frame.getShape(), frame.getAddressPositionMap());
		}
//TODO remove Statistics...
	//	while(frame.getTime() > lastSecond + STATISTICS_UPDATE_DELTA) {
//			lastSecond += STATISTICS_UPDATE_DELTA;
	//		bufferedData.add(frame.getKeyVisualizedValueMap());
	//	}
	}

	private void initComponents() {
		visualizationFrame = createVisualizationFrame();
		statisticsFrame = createStatisticsFrame();
		consoleFrame = createConsoleFrame();		
		showVisualization.setEnabled(true);
		showStatistics.setEnabled(true);		
		showVisualization.setEnabled(false);
		visualizationFrame.setVisible(true);
	}

	protected JFrame createVisualizationFrame() {
		JFrame frame = new JFrame("Visualization");
		frame.setSize(700, 600);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(new ComponentListener() {
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
				showVisualization.setEnabled(true);							
			}
		}); 
		this.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowClosing(WindowEvent e) {
				shutdown();
				
			}

			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		myPanel = new SimulationPanel(frameSource.getRectangle(), frameSource.getFrame().getShape());
		vsb = new JScrollBar(JScrollBar.VERTICAL);
		hsb = new JScrollBar(JScrollBar.HORIZONTAL);
		JPanel mainPanel = new JPanel();
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
		screenShot = new JButton("Screenshot");
		screenShot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aex) {
				saveScreenshot = true;	
			}
		});
		
		gbc.ipadx = 20;
		gbc.ipady = 20;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		
		contentPane.add(screenShot,gbc);		
		frame.setVisible(false);
		return frame;
	}
	
	/**
	 * 
	 */
	protected void shutdown() {
		System.out.println("Shutdown");
		globalShutdownAnnouncer.removeShutdownListener(myShutdownListener);
		frameSource.stopRender();
		globalShutdownAnnouncer.shutdown();
		
		
		//System.exit(0);
		
		
	}

	private JLabel createSliderLabel(String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(sliderLabelFont);
		return label;
	}
	
	private void initControlFrame(String title) {
		boolean linear = false;
		
		
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		JSlider speedSlider;
		frameSource.setFrameInterval((1.0 / DefaultSimulationFrame.this.fps) * 1);		
		if (linear){
			speedSlider= new JSlider(-39, 99, 0);
			Dictionary labelTable = new Hashtable();
			for (int i=-39;i<0;i=i+8){
				labelTable.put(new Integer(i), createSliderLabel("1/"+(-(i/2)+1)+"x"));			
			}
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i=9;i<100;i=i+10){
				labelTable.put(new Integer(i), createSliderLabel((i+1)+"x"));
			
			}
//		labelTable.put(new Integer(-9), createSliderLabel("1/10x"));
//		labelTable.put(new Integer(-8), createSliderLabel("1/9x"));
//		labelTable.put(new Integer(-7), createSliderLabel("1/8x"));
//		labelTable.put(new Integer(-6), createSliderLabel("1/7x"));
//		labelTable.put(new Integer(-5), createSliderLabel("1/6x"));
//		labelTable.put(new Integer(-4), createSliderLabel("1/5x"));
//		labelTable.put(new Integer(-3), createSliderLabel("1/4x"));
//		labelTable.put(new Integer(-2), createSliderLabel("1/3x"));
//		labelTable.put(new Integer(-1), createSliderLabel("1/2x"));
//		labelTable.put(new Integer(0), createSliderLabel("1x"));
//		labelTable.put(new Integer(1), createSliderLabel("2x"));
//		labelTable.put(new Integer(2), createSliderLabel("3x"));
//		labelTable.put(new Integer(3), createSliderLabel("4x"));
//		labelTable.put(new Integer(4), createSliderLabel("5x"));
//		labelTable.put(new Integer(5), createSliderLabel("6x"));
//		labelTable.put(new Integer(6), createSliderLabel("7x"));
//		labelTable.put(new Integer(7), createSliderLabel("8x"));
//		labelTable.put(new Integer(8), createSliderLabel("9x"));
//		labelTable.put(new Integer(9), createSliderLabel("10x"));
			speedSlider.setLabelTable(labelTable);
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					double val = s.getValue();
					double factor;
					if (val < 0.0) {
						factor = 1 / (1.0 + (val * -0.5));
					} else {
						factor = val + 1.0;
					}
//					factor*=2;
					DefaultSimulationFrame.this.frameSource.setFrameInterval((1.0 / DefaultSimulationFrame.this.fps) * factor);
				}
			});
		}else{
			speedSlider= new JSlider(-50, 100, 0);

			Dictionary labelTable = new Hashtable();
			for (int i=-50;i<0;i+=10){
				labelTable.put(new Integer(i), createSliderLabel("1/"+(int)Math.pow(2,((-i)/10))+"x"));			
			}
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i=0;i<=100;i=i+10){
				labelTable.put(new Integer(i), createSliderLabel((int)Math.pow(2,i/10)+"x"));
			
			}

			speedSlider.setLabelTable(labelTable);
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					int val = s.getValue();
					double factor;
					if (val < 0.0) {
						val=-val;
						int speedup=(int)Math.pow(2,val/10);
						factor=speedup+(val%10)*speedup/10.0;
						
						factor = 1 / factor;// (1.0 + (val * -0.5));
					} else {
						
						int speedup=(int)Math.pow(2,val/10);
						factor=speedup+(val%10)*speedup/10.0;
					}

					DefaultSimulationFrame.this.frameSource.setFrameInterval((1.0 / DefaultSimulationFrame.this.fps) * factor);
				}
			});
			
		}
		speedSlider.setOrientation(JSlider.HORIZONTAL);
		speedSlider.setPaintLabels(true);
		speedSlider.setPaintTicks(true);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setSnapToTicks(true);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;				
		contentPane.add(speedSlider, gbc);
		simTime = new JLabel("0", JLabel.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		contentPane.add(simTime, gbc);		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		showVisualization = new JButton("Show Visualization");
		showVisualization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showVisualization.setEnabled(false);
				visualizationFrame.setVisible(true);
			}
		});
		buttons.add(showVisualization);
		showStatistics = new JButton("Show Statistics");
		showStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStatistics.setEnabled(false);				
				statisticsFrame.setVisible(true);
			}
		});
		buttons.add(showStatistics);		
		showConsole = new JButton("Show Console");
		showConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showConsole.setEnabled(false);				
				consoleFrame.setVisible(true);
			}
		});
		buttons.add(showConsole);		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		contentPane.add(buttons, gbc);
		pack();
		setVisible(true);		
	}
	
	private JFrame createConsoleFrame() {
		JFrame frame = new JFrame("Console");
		frame.setSize(600, 200);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(new ComponentListener() {
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
				showConsole.setEnabled(true);			
			}
		}); 
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(4, 80);
		textArea.setEditable(false);
		console = new JTextAreaConsole(textArea);
		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(false);		
		return frame;
	}
	
	private JFrame createStatisticsFrame() {
		JFrame frame = new JFrame("Statistics");
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(new ComponentListener() {
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
				showStatistics.setEnabled(true);			
			}
		}); 

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
//		XYDataset data = bufferedData;
//		JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, data, true);
//		chart.setAntiAlias(false);
//		graphPanel = new ChartPanel(chart);
	//	contentPane.add(graphPanel, BorderLayout.CENTER);

		frame.setVisible(false);
		return frame;
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

//		private MyPngWriter pngWriter; // TODO: screen shots ohne jcommon
		

		public SimulationPanel(
			de.uni_trier.jane.basetypes.Rectangle rectangle,
			de.uni_trier.jane.visualization.shapes.Shape shape) {
			this.rectangle = rectangle;
			this.shape = shape;
			this.zoom = 1;
			setBackground(java.awt.Color.white);
			painter = new Graphics2DCanvas();
		
			worldspace = new DefaultWorldspace(painter);
			
//			pngWriter = new MyPngWriter(rectangle, "c:\\temp\\pngs\\"); // TODO: screen shots ohne jcommon
			
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).scale(zoom, zoom);
			((Graphics2D) g).translate(-leftPixelValue, -topPixelValue);
			painter.setGraphics((Graphics2D) g);
			shape.visualize(new Position(0, 0), worldspace, addressPositionMap);

//			de.uni_trier.ubi.appsim.kernel.visualization.Shape xmlShape = new XMLRenderShape("d:\\screenshot.xml");
//			xmlShape.visualize(new Position(0,0), painter);
			
			if (saveScreenshot) {
//				epsPainter = new PostScript2DRenderCanvas(OutFileName);
//				shape.visualize(new Position(0,0), epsPainter);
//				epsPainter.writePSFile();

//				XMLRenderCanvas canvas = new XMLRenderCanvas("d:\\screenshot.xml");
//				canvas.beginRendering();
//				shape.visualize(new Position(0,0), canvas);
//				canvas.endRendering();
				
//			    pngWriter.saveShape(shape); // TODO: screen shots ohne jcommon
			    
				saveScreenshot = false;
			}
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
	}

	/* // TODO: screen shots ohne jcommon
	private static class MyPngWriter {
	    private Image image;
	    private Graphics2DCanvas canvas;
	    private Graphics2D graphics;
	    private PngEncoder encoder;
	    private Position zero; 
	    private int count;
	    private NumberFormat format;
	    private String outputDir;
	    private de.uni_trier.ubi.appsim.kernel.basetype.Rectangle rectangle;
	    
	    public MyPngWriter(de.uni_trier.ubi.appsim.kernel.basetype.Rectangle rectangle, String outputDir) {
	        this.rectangle = rectangle;
	        image = new BufferedImage((int)rectangle.getWidth(), (int)rectangle.getHeight(), BufferedImage.TYPE_INT_RGB);
	        graphics = (Graphics2D) image.getGraphics();        
	        graphics.translate(-rectangle.getBottomLeft().getX(), -rectangle.getBottomRight().getY());	        
	        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        canvas = new Graphics2DCanvas();
	        encoder = new PngEncoder(image, false, 0, 9);
	        zero = new Position(0,0);	
	        format = NumberFormat.getIntegerInstance();
			format.setMinimumIntegerDigits(7);
			format.setGroupingUsed(false);
			this.outputDir = outputDir;
	    }
	    
	    private void saveShape(de.uni_trier.jane.visualization.shapes.Shape shape) {
            graphics.setBackground(java.awt.Color.WHITE);
            graphics.clearRect(0, 0, (int)rectangle.getWidth(), (int)rectangle.getHeight());
            canvas.setGraphics(graphics);
	        shape.visualize(zero, canvas);
            byte[] data = encoder.pngEncode();
            try {
                FileOutputStream out = new FileOutputStream(outputDir+"pic"+format.format(count)+".png");
                out.write(data);
                out.close();
                count++;
            } catch (FileNotFoundException e) {
                e.printStackTrace(); 
            } catch (IOException e) {
                e.printStackTrace();
            }                        
	    }	    
	}
	*/
}