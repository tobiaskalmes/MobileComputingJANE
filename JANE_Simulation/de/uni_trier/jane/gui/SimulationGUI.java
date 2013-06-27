/*****************************************************************************
* 
* $Id: SimulationGUI.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.console.Console;
import de.uni_trier.jane.console.JTextAreaConsole;
import de.uni_trier.jane.simulation.ShutdownListener;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.gui.OutputFrameRenderer;
import de.uni_trier.jane.simulation.gui.SimulationFrame;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;
import de.uni_trier.jane.simulation.visualization.Frame;
import de.uni_trier.jane.simulation.visualization.FrameSource;
import de.uni_trier.jane.simulation.visualization.RenderQueue;
import de.uni_trier.jane.simulationl.visualization.console.ConsoleTextIterator;

/**
 * Simple <code>JFrame</code> implementation for the visualization of the 
 * simulation environment.
 * 
 * @author ulf.wehling
 * @version Jul 2, 2005
 */
public class SimulationGUI 
extends JFrame 
implements ActionListener, SimulationFrame {

	// ID is used for serialization.
	private static final long serialVersionUID = 3689912877192459832L;

	/**
	 * Drawing panel for the devices.
	 */
    protected VisualizationPanel visualizationPanel;
    
	// GUI staff.
	private JButton exitSimulation;
	private JButton showStatistics;	
	private JButton showConsole;
	private JButton screenShot;
	
	private JLabel simTime;
	
	private FrameSource frameSource;
	private javax.swing.Timer repaintTimer;
	private double fps;
	private Console console;

	private JFrame visualizationFrame;
	private JFrame statisticsFrame;
	private JFrame consoleFrame;
	
	private JPanel mainPanel;
	private JPanel scrollPanel;
	private JPanel speedPanel;
	private JPanel zoomPanel;
	
	private JScrollPane scrollPane;
	
	private Font sliderLabelFont = new Font("SansSerif", Font.PLAIN, 10);

    private ShutdownAnnouncer globalShutdownAnnouncer;
    private ShutdownListener shutdownListener;
    private Extent extent;
    private OutputFrameRenderer screenshotRenderer;
    private OutputFrameRenderer videoRenderer;
    private boolean makeScreenshot;
    private boolean disableSplashScreen;
    
  
    /**
     * Constructor of the class <code>SimulationGUI</code>.
     * @param extent Extent of the simulation.
     */
    public SimulationGUI(Extent extent) {
    	this(extent, null, null);
    }
    
    /**
     * Constructor of the the class <code>SimulationGUI</code>.
     * @param extent Extent of the simulation.
     * @param screenshotRenderer Renderer which is used for making screenshots.
     */
    public SimulationGUI(
    		Extent extent,
    		OutputFrameRenderer screenshotRenderer
    		) {
    	this(extent, screenshotRenderer, null);
    }
    
    /**
     * Constructor of the class <code>SimulationGUI</code>.
     * @param extent Extent of the simulation.
     * @param screenshotRenderer Renderer which is used for making screenshots.
     * @param videoRenderer Renderer which is used for making videos.
     */
    public SimulationGUI(
    		Extent extent, 
    		OutputFrameRenderer screenshotRenderer,
    		OutputFrameRenderer videoRenderer
    		) {
    	this.extent = extent;
    	this.screenshotRenderer = screenshotRenderer;
    	this.videoRenderer = videoRenderer;
    	this.makeScreenshot = false;
    	this.disableSplashScreen = false;
    	this.mainPanel = new JPanel(new BorderLayout());
    	this.scrollPanel = new JPanel(new BorderLayout());
    	this.speedPanel = new JPanel(new BorderLayout());
    	this.zoomPanel = new JPanel(new BorderLayout());
    	this.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			shutdown();
    		}
    	});
    }
    
    /**
     * Disables the splash screen at the startup of the simulation GUI.
     * By default the splashscreen is enabled.
     */
    public void disableSplashScreen() {
    	this.disableSplashScreen = true;
    }
    
    /**
     * Implementation of the Interface <code>SimulationFrame</code>.
     * @see de.uni_trier.jane.simulation.gui.SimulationFrame#
     * 		show(SimulationParameters)
     */
    public void show(SimulationParameters parameters) {
        globalShutdownAnnouncer = parameters.getGlobalShutdownAnnouncer();
        shutdownListener = new ShutdownListener() {
			public void shutdown() {
				dispose();
			}
        };

 		RenderQueue queue = new RenderQueue(parameters);
		frameSource = queue;
		parameters.setFrameRenderer(queue);
		this.fps = parameters.getSimulationFrameFPS();

		initControlFrame(parameters.getSimulationName());
		if (!disableSplashScreen) {
			// show the splash screen, if the corresponding image is found ;-)
			try {
				showSplashScreen();
			}
			catch (NullPointerException e) {
				// the corresponding image does not exist, therefore we do 
				// nothing ;-)
			}
		}
		// initialize the frames for the simulation environment
		this.visualizationFrame = createVisualizationFrame();
		this.statisticsFrame = createStatisticsFrame();
		this.consoleFrame = createConsoleFrame();	
		
		double frameInterval = 1000.0 / fps;
		repaintTimer = new javax.swing.Timer((int) frameInterval, this);
		repaintTimer.start();
        super.setVisible(true);
    }
    
	/**
	 * Implementation of the Interface ActionListener.
	 * This method is called whenever a button is pressed or the simulation
	 * time increases, so all action which happens if a button is pressed
	 * is implemented here.
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		de.uni_trier.jane.simulation.visualization.Frame frame = 
			frameSource.getFrame();
		if (console != null) {
			ConsoleTextIterator it = frame.getConsoleTextIterator();
			while (it.hasNext()) {
				it.next().println(console);
			}
		}
		simTime.setText("Simulation Time: " + (int) frame.getTime());
		// render the screenshot
		if (makeScreenshot && screenshotRenderer != null) {
			screenshotRenderer.renderFrame(frame);
			makeScreenshot = false;
			screenShot.setEnabled(true);
		}
		// make a screenshot for the video :-)
		if (videoRenderer != null) {
			videoRenderer.renderFrame(frame);
		}
		if (visualizationPanel != null) {
			// this method can be overriden in subclasses, this allows the
			// user to change the shape collection of the devices, e.g. to
			// mark a selected device
			setShape(frame);
		}
	}
	
	/**
	 * Sets the shape for the visualization panel.
	 * @param frame Current frame.
	 */
	protected void setShape(Frame frame) {
		visualizationPanel.setShape(
				frame.getShape(), 
				frame.getAddressPositionMap()
				);
	}
	
	/**
	 * Returns the simulation frame.
	 * @return Simulation frame of the current simulation.
	 */
	protected JFrame createVisualizationFrame() {
		return this;
	}
	
	/**
	 * Simulation environment shuts down.
	 */
	protected void shutdown() {
		System.out.println("Simulation shuts down");
		globalShutdownAnnouncer.removeShutdownListener(shutdownListener);
		frameSource.stopRender();
		globalShutdownAnnouncer.shutdown();
		System.exit(0);
	}
	
	/**
	 * Creates a slider label for the speed slider bar.
	 * @param text Text of the label.
	 * @return A JLabel which contains the text.
	 */	
	private JLabel createSliderLabel(String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(sliderLabelFont);
		return label;
	}
	
	/**
	 * Initializes and shows the GUI for the simulation environment.
	 * @param title Text which is shown in the header bar.
	 */
	private void initControlFrame(String title) {
		// is used for the zoom slider
		boolean linear = false; 
		
		setSize(660, 660);		
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		// panel on which the devices are drawn
		visualizationPanel = new VisualizationPanel(
				frameSource.getFrame().getShape()
				);
		visualizationPanel.setPreferredSize(
				new Dimension(
						(int) extent.getHeight(),
						(int) extent.getWidth()
						)
				);
		
		// scroll pane
		scrollPane = new JScrollPane(visualizationPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		// zoom panel
		zoomPanel.add(new JLabel("Zoom", JLabel.CENTER), BorderLayout.NORTH);
		zoomPanel.setPreferredSize(new Dimension(70, zoomPanel.getHeight()));
		
		// zoom slider
		JSlider zoomSlider = new JSlider(0, 500, 100);
		zoomSlider.setOrientation(JSlider.VERTICAL);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setMajorTickSpacing(25);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				int value = s.getValue();
				visualizationPanel.setZoom(value == 0 ? 0.01 : (value/100.0));
				double height = (extent.getHeight()+100)*visualizationPanel.getZoom();
				double width = (extent.getWidth()+225)*visualizationPanel.getZoom();
				visualizationPanel.setPreferredSize(
						new Dimension((int) width, (int) height)
						);
				visualizationPanel.revalidate();
				repaint();
			}
		});
		zoomPanel.add(zoomSlider, BorderLayout.CENTER);	
		mainPanel.add(zoomPanel, BorderLayout.EAST);
		
		// speed slider
		JSlider speedSlider;
		frameSource.setFrameInterval((1.0/SimulationGUI.this.fps) * 1);		
		if (linear) {
			speedSlider = new JSlider(-39, 99, 0);
			Dictionary labelTable = new Hashtable();
			for (int i = -39; i < 0; i = i + 8) {
				labelTable.put(
						new Integer(i), 
						createSliderLabel("1/" + (-(i/2) + 1) + "x")
						);			
			} 
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i = 9; i < 100; i = i + 10){
				labelTable.put(
						new Integer(i), 
						createSliderLabel((i + 1) + "x")
						);
			
			}
			speedSlider.setLabelTable(labelTable);
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					double val = s.getValue();
					double factor;
					if (val < 0.0) {
						factor = 1 / (1.0 + (val * -0.5));
					} 
					else {
						factor = val + 1.0;
					}
					SimulationGUI.this.frameSource.setFrameInterval(
							(1.0 / SimulationGUI.this.fps) * factor
							);
				}
			});
		} 
		else {
			speedSlider = new JSlider(-50, 100, 0);
			Dictionary labelTable = new Hashtable();
			for (int i = -50; i < 0; i += 10) {
				labelTable.put(
						new Integer(i), 
						createSliderLabel(
								"1/" + (int) Math.pow(2, ((-i)/10)) + "x"
								)
						);			
			}
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i = 0; i <= 100; i = i + 10){
				labelTable.put(
						new Integer(i), 
						createSliderLabel((int) Math.pow(2, i/10) + "x")
						);
			
			}
			speedSlider.setLabelTable(labelTable);
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					int val = s.getValue();
					double factor;
					if (val < 0.0) {
						val = -val;
						int speedup = (int) Math.pow(2, val/10);
						factor = speedup + (val%10)*speedup/10.0;
						factor = 1/factor;
					} 
					else {	
						int speedup = (int) Math.pow(2, val/10);
						factor = speedup + (val%10)*speedup/10.0;
					}
					SimulationGUI.this.frameSource.setFrameInterval(
							(1.0 / SimulationGUI.this.fps) * factor
							);
				}
			});	
		}
		speedSlider.setOrientation(JSlider.HORIZONTAL);
		speedSlider.setPaintLabels(true);
		speedSlider.setPaintTicks(true);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setSnapToTicks(true);

		// the speed panel contains the speed slider in the "center",
		// the simulation time label in the "south" and the logo of
		// the University of Luxembourg in the "east" (if it is accessible,
		// if not we create an empty label)
		speedPanel.add(speedSlider, BorderLayout.CENTER);
		
		simTime = new JLabel("Simulation Time: 0", JLabel.CENTER);
		speedPanel.add(simTime, BorderLayout.SOUTH);
		
		JLabel label = new JLabel();
		// if the logo of the University of Luxembourg is accessible we use it
		try {
			label = new JLabel(new ImageIcon(
					getClass().getResource("logoUL150X66.gif"))
					);
		}
		// if it is not accessible we do nothing except setting the preferred
		// size of the label
		catch (NullPointerException exception) {
			label.setPreferredSize(new Dimension(70, 50));
		}

		speedPanel.add(label, BorderLayout.EAST);
		
		mainPanel.add(speedPanel, BorderLayout.SOUTH);
		
		contentPane.add(mainPanel, BorderLayout.CENTER);
		
		// buttons are placed in the "south" of the content pane
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		
		exitSimulation = new JButton("Exit Simulation");
		exitSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shutdown();
			}
		});
		buttons.add(exitSimulation);
		
		showStatistics = new JButton("Show Statistics");
		showStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Show Statistics")) {
					statisticsFrame.setVisible(true);
					showStatistics.setText("Hide Statistics");
				}
				else {
					statisticsFrame.setVisible(false);
					showStatistics.setText("Show Statistics");
				}
			}
		});
		buttons.add(showStatistics);
		showStatistics.setEnabled(false); // no functionality at the moment
		
		showConsole = new JButton("Show Console");
		showConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Show Console")) {
					consoleFrame.setVisible(true);
					showConsole.setPreferredSize(showConsole.getSize());
					showConsole.setText("Hide Console");
				}
				else {
					consoleFrame.setVisible(false);
					showConsole.setText("Show Console");
				}
			}
		});
		buttons.add(showConsole);	
		
		screenShot = new JButton("Screenshot");
		screenShot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aex) {
				screenShot.setEnabled(false);
				makeScreenshot = true;
			}
		});
		buttons.add(screenShot);
		// if no screenshot renderer is added we disable this button :-)
		if (screenshotRenderer == null) {
			screenShot.setEnabled(false);
		}
				
		contentPane.add(buttons, BorderLayout.SOUTH);

		setVisible(true);		
	}
	
	/**
	 * Shows the splash screen. If the specified image is not found this method
	 * throws a NullPointerException.
	 */
	private void showSplashScreen() {
		final JWindow splashScreen = new JWindow();

		JLabel label = new JLabel(
				new ImageIcon(SimulationGUI.class.getResource("splash.jpg"))
				);

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
        new javax.swing.Timer(
        		10000, 
        		new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						splashScreen.setVisible(false);
						splashScreen.dispose();
					}
        		}
        		).start();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		splashScreen.setLocation(
				screenSize.width/2 - labelSize.width/2, 
				screenSize.height/2 - labelSize.height/2
				);
		splashScreen.pack();
		splashScreen.setVisible(true);
		splashScreen.toFront();
	}
	
	/**
	 * Creates a new console frame and returns it.
	 * @return New console frame.
	 */
	private JFrame createConsoleFrame() {
		JFrame frame = new JFrame("Console");
		frame.setSize(600, 200);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				showConsole.setText("Show Console");
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
	
	/* (non-Javadoc)
	 * Creates a new statistics frame and returns it.
	 * @return New statistics frame.
	 */
	private JFrame createStatisticsFrame() {
		JFrame frame = new JFrame("Statistics");
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				showStatistics.setText("Show Statistics");
			}
		}); 

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		frame.setVisible(false);
		
		return frame;
	}
}