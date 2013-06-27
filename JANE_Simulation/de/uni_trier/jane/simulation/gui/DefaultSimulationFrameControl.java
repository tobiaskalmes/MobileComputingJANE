/*****************************************************************************
 * 
 * SimulationFrame.java
 * 
 * $Id: DefaultSimulationFrameControl.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

/**
 * This is a simple <code>JFrame</code> implementation to visualize the simulation
 * run.
 */
public class DefaultSimulationFrameControl extends JFrame implements ActionListener, SimulationFrame {

	
	// Screenshot varibles
	private static boolean saveScreenshot = false;
	


	private final static String VERSION = "$Id: DefaultSimulationFrameControl.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private JButton showVisualization;
	
	private JButton showConsole;

//	private JScrollBar vsb;
//	private JScrollBar hsb;
//	public SimulationPanel myPanel;
	
	private JLabel simTime;
	
	private FrameSource frameSource;
	private javax.swing.Timer repaintTimer;
	private double fps;
	
//	private BufferedData bufferedData;
	
	private JANEVisualizationFrame visualizationFrame;
	
	private JANEConsoleFrame consoleFrame;
	private JButton screenShot;

	private Font sliderLabelFont = new Font("SansSerif", Font.PLAIN, 10);

	//private double lastSecond;
	//private HashSet shutdownListeners;



    private ShutdownAnnouncer globalShutdownAnnouncer;
    private ShutdownListener myShutdownListener;



	private OutputFrameRenderer screenshotRenderer;
	private OutputFrameRenderer videoRenderer;



	protected boolean makeScreenShot;

    /**
	 * 
	 */
	public DefaultSimulationFrameControl(JANEConsoleFrame consoleFrame, JANEVisualizationFrame visualizationFrame) {
		this(consoleFrame,visualizationFrame,null);
	}
	
	public DefaultSimulationFrameControl(JANEConsoleFrame consoleFrame, JANEVisualizationFrame visualizationFrame,OutputFrameRenderer screenshotRenderer) {
		this(consoleFrame,visualizationFrame,screenshotRenderer,null);
	}
	 /**
	 * 
	 */
	public DefaultSimulationFrameControl(JANEConsoleFrame consoleFrame, JANEVisualizationFrame visualizationFrame,OutputFrameRenderer screenshotRenderer, OutputFrameRenderer videoRenderer) {
		this.consoleFrame=consoleFrame;
		this.visualizationFrame=visualizationFrame;
		this.screenshotRenderer=screenshotRenderer;
		this.videoRenderer=videoRenderer;
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

		RenderQueue queue = new RenderQueue(parameters);//EmptyShape.getInstance());//parameters.getDynamicSource().getShape());
		frameSource=queue;
		parameters.setFrameRenderer(queue);
		visualizationFrame.setRectangle(queue.getRectangle());
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
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		de.uni_trier.jane.simulation.visualization.Frame frame = frameSource.getFrame();
		if (consoleFrame != null&&consoleFrame.isVisible()) {
			ConsoleTextIterator it = frame.getConsoleTextIterator();
			while (it.hasNext()) {
				it.next().println(consoleFrame);
			}
		}
		simTime.setText("time " + (int) frame.getTime());
		if (makeScreenShot&&screenshotRenderer!=null){
			screenshotRenderer.renderFrame(frame);
			makeScreenShot=false;
			screenShot.setEnabled(true);
		}
		if(videoRenderer!=null){
			videoRenderer.renderFrame(frame);
		}
		visualizationFrame.addShape(frame.getShape(), frame.getAddressPositionMap());
		
	}

	private void initComponents() {
		visualizationFrame.addFrameListener(new JANEFrameListener() {
			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.gui.JANEFrameListener#frameHidden()
			 */
			public void frameHidden() {
				showVisualization.setEnabled(true);	

			}

			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.gui.JANEFrameListener#frameClosed()
			 */
			public void frameClosed() {
				// TODO Auto-generated method stub

			}
		});		 		
		consoleFrame.addFrameListener(new JANEFrameListener() {
			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.gui.JANEFrameListener#frameHidden()
			 */
			public void frameHidden() {
				showConsole.setEnabled(true);	

			}

			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.gui.JANEFrameListener#frameClosed()
			 */
			public void frameClosed() {
				// TODO Auto-generated method stub

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
		
		showVisualization.setEnabled(true);
			
		//showVisualization.setEnabled(false);
		visualizationFrame.setVisible(true);
	}

	
	
	/**
	 * 
	 */
	protected void shutdown() {
		System.out.println("Shutdown");
		globalShutdownAnnouncer.removeShutdownListener(myShutdownListener);
		globalShutdownAnnouncer.shutdown();
		
		System.exit(0);
		
		
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
		frameSource.setFrameInterval((1.0 / DefaultSimulationFrameControl.this.fps) * 1);		
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
					DefaultSimulationFrameControl.this.frameSource.setFrameInterval((1.0 / DefaultSimulationFrameControl.this.fps) * factor);
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

					DefaultSimulationFrameControl.this.frameSource.setFrameInterval((1.0 / DefaultSimulationFrameControl.this.fps) * factor);
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
				//showVisualization.setEnabled(false);
				visualizationFrame.setVisible(!visualizationFrame.isVisible());
			}
		});
		buttons.add(showVisualization);
	
		showConsole = new JButton("Show Console");
		showConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showConsole.setEnabled(false);				
				consoleFrame.setVisible(true);
			}
		});
		buttons.add(showConsole);	
		
		screenShot = new JButton("Screenshot");
		screenShot.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				screenShot.setEnabled(false);				
				makeScreenShot=true;
			}
		});
		buttons.add(screenShot);
		if (screenshotRenderer==null){
			screenShot.setEnabled(false);
		}
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		contentPane.add(buttons, gbc);
		pack();
		setVisible(true);		
	}
	
	
	




	
}