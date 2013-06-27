/*******************************************************************************
 * 
 * SimulationMainFrame.java
 * 
 * $Id: SimulationMainFrame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/

/*
 * Created on 15.06.2005
 *
 */
package de.uni_trier.jane.sgui;

import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.uni_trier.jane.basetypes.DefaultWorldspace;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
// !!! disabled SWT stuff !!!
// import de.uni_trier.jane.sgui.swt.DetachedSWTVisualizationFrame;
// import de.uni_trier.jane.sgui.swt.SWTVisualization;
import de.uni_trier.jane.simulation.ShutdownListener;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.gui.SimulationFrame;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;
import de.uni_trier.jane.simulation.visualization.Frame;
import de.uni_trier.jane.simulation.visualization.PngRenderCanvas;
import de.uni_trier.jane.simulation.visualization.PostScript2DRenderCanvas;
import de.uni_trier.jane.simulation.visualization.RenderQueue;
import de.uni_trier.jane.simulation.visualization.XMLRenderCanvas;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.awt.SWT_AWT;
//import org.eclipse.swt.events.ControlAdapter;
//import org.eclipse.swt.events.ControlEvent;
//import org.eclipse.swt.events.DisposeEvent;
//import org.eclipse.swt.events.DisposeListener;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.events.PaintListener;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.layout.FillLayout;

//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;

/**
 * @author Klaus Sausen
 * The mainframe of the simulation gui
 */
public class SimulationMainFrame extends JFrame
	implements ActionListener, SimulationFrame {

	private JFrame jFrame = null;

	/** activate this if you want the eps screenshot option as well */
	private static final boolean HAVE_EPS = false;
	
	//renderqueue
	protected RenderQueue frameSource;

	/** a buffer that holds all/most recent that have already been visualized */
	protected FrameBuffer frameBuffer;
	
	//timer
	private javax.swing.Timer repaintTimer;
	private double fps;
	
	/**
	 * access to the default functions, 
	 * either points to visualizationPanel
	 * or to swtCanvas
	 */
	private IVisualization visualization;
	/**
	 * visualizes the simulation in a jpanel 
	 **/
	private IVisualization visualizationPanel;
	/**
	 * visualizes the simulation in an swt canvas
	 */
// !!! disabled SWT stuff !!!
//	private SWTVisualization swtCanvas;
	
	/**
	 * controls the simulation
	 */
	protected SimulationControlPanel simulationControlPanel;  //  @jve:decl-index=0:visual-constraint="632,351"
	
	//shutdown
	private ShutdownAnnouncer globalShutdownAnnouncer;
    private ShutdownListener myShutdownListener;
	
	private javax.swing.JPanel jContentPane = null;

	//these are awt for the sake of swt..
	private JMenuBar jMenuBar = null;

	private JMenu jSimulationMenu = null;
	private JMenuItem jSimuQuitMenuItem = null;
	private JMenuItem jSimuStartMenuItem = null;

	private JMenu jVisualizationMenu = null;
	private JMenuItem jVisuDetachAttachMenuItem = null;
	private JMenuItem jVisuFrameCacheMenuItem = null;
	private JMenuItem jVisuSwingMenuItem = null;
	private JMenuItem jVisuSWTMenuItem = null;
	
	private JMenu jScreenshotMenu = null;
	private JMenuItem jScreenSaveAsMenuItem = null;
	private JMenuItem jScreenSavePsMenuItem = null;
	private JMenuItem jScreenSaveEpsMenuItem = null;
	private JMenuItem jScreenSaveXmlMenuItem = null;
	private JMenuItem jScreenSavePngMenuItem = null;
	
	private JPanel jControlPanel = null;
	private JPanel jSimulationPanel = null;
	private JPanel jSimulationControlPanel = null;
	private VisualizationPanel jVisualizationPanel = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JPanel jViewSelectPanel = null;
	private JPanel jViewPanel = null;
	private PlanViewPanel jPlanViewPanel = null;
	private PerspectiveViewPanel jPerspectiveViewPanel = null;  //  @jve:decl-index=0:visual-constraint="632,10"
	
	private JRadioButton jDefaultViewRadioButton = null;
	private JRadioButton jPerspectiveViewRadioButton = null;

    protected Frame frame;

    protected DeviceID markDevice;
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jMenuBar == null) {
			jMenuBar = new JMenuBar();
			jMenuBar.add(getJSimulationMenu());
			jMenuBar.add(getJVisualizationMenu());
			jMenuBar.add(getJScreenshotMenu());
		}
		return jMenuBar;
	}
	/**
	 * This method initializes jSimulationMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJSimulationMenu() {
		if (jSimulationMenu == null) {
			jSimulationMenu = new JMenu();
			jSimulationMenu.setText("Simulation");
			jSimulationMenu.add(getJFileStartMenuItem());
			jSimulationMenu.add(getJFileQuitMenuItem());
		}
		return jSimulationMenu;
	}
	/**
	 * This method initializes jFileStartMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJFileStartMenuItem() {
		if (jSimuStartMenuItem == null) {
			jSimuStartMenuItem = new JMenuItem();
			jSimuStartMenuItem.setText("Start");
			jSimuStartMenuItem.setEnabled(false);
			jSimuStartMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//(TODO)
				}
			});
		}
		return jSimuStartMenuItem;
	}
	/**
	 * This method initializes jFileQuitMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJFileQuitMenuItem() {
		if (jSimuQuitMenuItem == null) {
			jSimuQuitMenuItem = new JMenuItem();
			jSimuQuitMenuItem.setText("Quit");
			jSimuQuitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					shutdown();
				}
			});
		}
		return jSimuQuitMenuItem;
	}

	/**
	 * This method initializes jVisualizationMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJVisualizationMenu() {
		if (jVisualizationMenu == null) {
			jVisualizationMenu = new JMenu();
			jVisualizationMenu.setText("Visualization");
			jVisualizationMenu.add(getJVisuDetachAttachMenuItem());
			jVisualizationMenu.add(getJVisuFrameCacheMenuItem());
// !!! disabled SWT stuff !!!
//			if (this.visualizationUseSWT) {
//				jVisualizationMenu.add(getJVisuSwingMenuItem());
//				jVisualizationMenu.add(getJVisuSWTMenuItem());
//			}
		}
		return jVisualizationMenu;
	}

	/**
	 * This method initializes jVisuDetachAttachMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJVisuDetachAttachMenuItem() {
		if (jVisuDetachAttachMenuItem == null) {
			jVisuDetachAttachMenuItem = new JMenuItem();
			jVisuDetachAttachMenuItem.setText("Detach");
			jVisuDetachAttachMenuItem.setEnabled(true);
			jVisuDetachAttachMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (visualizationAttached)
						detachVisualization();
					else
						attachVisualization();
				}
			});
		}
		return jVisuDetachAttachMenuItem;
	}

	private JMenuItem getJVisuFrameCacheMenuItem() {
		if (jVisuFrameCacheMenuItem == null) {
			jVisuFrameCacheMenuItem = new JMenuItem();
			jVisuFrameCacheMenuItem.setText("Frame cache");
			jVisuFrameCacheMenuItem.setEnabled(true);
			jVisuFrameCacheMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JMenuItem item = (JMenuItem)e.getSource();
					
					CacheSizeDlg dlg = 
						new CacheSizeDlg(item.getParent(), frameBuffer.getCacheSize());
					dlg.setVisible(true);
					frameBuffer.setCacheSize(dlg.getCacheSize());
					System.out.println(frameBuffer.getCacheSize());
				}
			});
		}
		return jVisuFrameCacheMenuItem;
	}
	
	/**
	 * This method initializes jVisuSwingMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJVisuSwingMenuItem() {
		if (jVisuSwingMenuItem == null) {
			jVisuSwingMenuItem = new JMenuItem();
			jVisuSwingMenuItem.setText("Swing");
			jVisuSwingMenuItem.setEnabled(false);
			jVisuSwingMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//(TODO)
					System.out.println("switching back to swing");
//					jVisuSWTMenuItem.setEnabled(true);
//					jVisuSwingMenuItem.setEnabled(false);
					setVisualizationPanel(visualizationPanel);
//					useSwingVisualization();
				}
			});
		}
		return jVisuSwingMenuItem;
	}
	/**
	 * This method initializes jVisuSWTMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
// !!! disabled SWT stuff !!!
//	private JMenuItem getJVisuSWTMenuItem() {
//		if (jVisuSWTMenuItem == null) {
//			jVisuSWTMenuItem = new JMenuItem();
//			jVisuSWTMenuItem.setText("SWT");
//			jVisuSWTMenuItem.setEnabled(true);
//			jVisuSWTMenuItem.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent arg0) {
//					//(TODO)
//					System.out.println("switching to SWT");
////					jVisuSwingMenuItem.setEnabled(true);
////					jVisuSWTMenuItem.setEnabled(false);
//					setVisualizationPanel(visualizationPanel);
//					useSWTVisualization();
//				}
//			});
//		}
//		return jVisuSWTMenuItem;
//	}
	
	/**
	 * icon resource for the screenshotmenu
	 */
	private final ImageIcon optionIconSelected = 
		new ImageIcon(SimulationMainFrame.class.getResource("camera16x16.gif"));
	/**
	 * icon resource for the screenshotmenu
	 */
	private final ImageIcon optionIconNotSelected = 
		new ImageIcon(SimulationMainFrame.class.getResource("empty.gif"));
		
	public static final int SAVE_PNG = 0;
	public static final int SAVE_PS  = 1;
	public static final int SAVE_EPS = 2;
	public static final int SAVE_XML = 3;

	/**
	 * what type of screenshot action to perform
	 */
	int screenShotSelection = SimulationMainFrame.SAVE_PNG;
	
	/**
	 * @return SAVE_PNG|SAVE_PS|SAVE_EPS|SAVE_XML
	 */
	public int getScreenshotSelection() {
		return screenShotSelection;
	}
	
	/**
	 * @param selection SAVE_PNG|SAVE_PS|SAVE_EPS|SAVE_XML
	 */
	private void setScreenshotSelection(int selection) {
		screenShotSelection = selection;
		updateScreenshotMenu();
	}
	
	private void updateScreenshotMenu() {
		getJScreenSavePngMenuItem().setIcon(optionIconNotSelected);
		getJScreenSavePsMenuItem().setIcon(optionIconNotSelected);
		getJScreenSaveEpsMenuItem().setIcon(optionIconNotSelected);
		getJScreenSaveXmlMenuItem().setIcon(optionIconNotSelected);
		switch (getScreenshotSelection()) {
		case SAVE_PNG:
			getJScreenSavePngMenuItem().setIcon(optionIconSelected);
			break;
		case SAVE_PS:
			getJScreenSavePsMenuItem().setIcon(optionIconSelected);
			break;
		case SAVE_EPS:
			getJScreenSaveEpsMenuItem().setIcon(optionIconSelected);
			break;
		case SAVE_XML:
			getJScreenSaveXmlMenuItem().setIcon(optionIconSelected);
			break;
		}
	}
	
	/**
	 * This method initializes jVisualizationMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJScreenshotMenu() {
		if (jScreenshotMenu == null) {
			jScreenshotMenu = new JMenu();
			jScreenshotMenu.setText("Screenshot");
			jScreenshotMenu.add(getJScreenSaveAsMenuItem());
			jScreenshotMenu.add(getJScreenSavePngMenuItem());
			jScreenshotMenu.add(getJScreenSavePsMenuItem());
			if (HAVE_EPS)
				jScreenshotMenu.add(getJScreenSaveEpsMenuItem());
			jScreenshotMenu.add(getJScreenSaveXmlMenuItem());
		}
		updateScreenshotMenu();
		return jScreenshotMenu;
	}

	/**
	 * This method initializes jScreenSaveAsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJScreenSaveAsMenuItem() {
		if (jScreenSaveAsMenuItem == null) {
			jScreenSaveAsMenuItem = new JMenuItem();
			jScreenSaveAsMenuItem.setText("save as:");
			jScreenSaveAsMenuItem.setEnabled(false);
		}
		return jScreenSaveAsMenuItem;
	}

	/**
	 * This method initializes jScreenSavePngMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJScreenSavePngMenuItem() {
		if (jScreenSavePngMenuItem == null) {
			jScreenSavePngMenuItem = new JMenuItem();
			jScreenSavePngMenuItem.setText("png");
			jScreenSavePngMenuItem.setEnabled(true);
			jScreenSavePngMenuItem.setToolTipText("save as portable network graphics (.png)");
			jScreenSavePngMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setScreenshotSelection(SAVE_PNG);
				}
			});
		}
		return jScreenSavePngMenuItem;
	}

	/**
	 * This method initializes jScreenSavePsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJScreenSavePsMenuItem() {
		if (jScreenSavePsMenuItem == null) {
			jScreenSavePsMenuItem = new JMenuItem();
			jScreenSavePsMenuItem.setText("postscript");
			jScreenSavePsMenuItem.setEnabled(true);
			jScreenSavePsMenuItem.setIcon(optionIconSelected);
			jScreenSavePsMenuItem.setToolTipText("save as postscript (.ps)");
			jScreenSavePsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setScreenshotSelection(SAVE_PS);
				}
			});
		}
		return jScreenSavePsMenuItem;
	}

	/**
	 * This method initializes jScreenSaveEpsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJScreenSaveEpsMenuItem() {
		if (jScreenSaveEpsMenuItem == null) {
			jScreenSaveEpsMenuItem = new JMenuItem();
			jScreenSaveEpsMenuItem.setText("eps");
			jScreenSaveEpsMenuItem.setEnabled(true);
			jScreenSaveEpsMenuItem.setIcon(optionIconSelected);
			jScreenSaveEpsMenuItem.setToolTipText("save as encapsulated postscript (.eps)");
			jScreenSaveEpsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setScreenshotSelection(SAVE_EPS);
				}
			});
		}
		return jScreenSaveEpsMenuItem;
	}
	
	
	/**
	 * This method initializes jScreenSaveXmlMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJScreenSaveXmlMenuItem() {
		if (jScreenSaveXmlMenuItem == null) {
			jScreenSaveXmlMenuItem = new JMenuItem();
			jScreenSaveXmlMenuItem.setText("xml");
			jScreenSaveXmlMenuItem.setEnabled(true);
			jScreenSaveXmlMenuItem.setSelected(false);
			jScreenSaveXmlMenuItem.setToolTipText("save as xml dump");
			jScreenSaveXmlMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setScreenshotSelection(SAVE_XML);
				}
			});
		}
		return jScreenSaveXmlMenuItem;
	}

	
	/**
	 * flag indicating wether the visualization is currently attached
	 * or not
	 */
	private boolean visualizationAttached = true;
	
	
	/**
	 * wether to use the SWT/OpenGL extension or not
	 */
// !!! disabled SWT stuff !!!
//	private boolean visualizationUseSWT = false;
	
	/**
	 * the frame visualization a detached simulation
	 */
	private VisualizationFrame detachedVisualizationFrame;

// !!! disabled SWT stuff !!!
//	public void useSWTVisualization() {
//		
//		//detach, but do not create the default detached swing implementation
//		if (visualizationAttached) {
//			visualizationAttached = false;
//			jContentPane.setVisible(false);
//			jContentPane.remove(getJSimulationPanel());
//			getJSimulationPanel().remove(getJSimulationControlPanel()); //also works component tree is traversed> jContentPane.remove(getJSimulationControlPanel());
//			jContentPane.add(getJSimulationControlPanel(), BorderLayout.CENTER);
//			simulationControlPanel.showConsole();
//			//resize the main window...
//			jFrame.setSize(SIZEX_DETACHED, SIZEY_DETACHED);
////			detachedVisualizationFrame = 
////				new DetachedVisualizationFrame(
////						this.getTitle(), 
////						getJSimulationPanel());
//			
////			detachedVisualizationFrame.setVisible(true);
//			
//			jContentPane.setVisible(true);
//		} else {
//			// remove the detached swing visualization
//			detachedVisualizationFrame.dispose();
//			detachedVisualizationFrame = null;
//		}
//		//instanciate the swt implementation
//
//		detachedVisualizationFrame =
//			new DetachedSWTVisualizationFrame(
//					jFrame.getTitle());
//		detachedVisualizationFrame.start(); //run();
//		
//		visualization = detachedVisualizationFrame.getVisualization();
//		swtCanvas = (SWTVisualization)visualization;
//        visualizationPanel=swtCanvas;
//        
//	}
	
	/** 
	 * detaches the visualization of the simulation
	 */
	public void detachVisualization() {
		if (visualizationAttached) {
			visualizationAttached = false;
			jVisuDetachAttachMenuItem.setText("Attach");
			jContentPane.setVisible(false);
			jContentPane.remove(getJSimulationPanel());
			getJSimulationPanel().remove(getJSimulationControlPanel()); //also works component tree is traversed> jContentPane.remove(getJSimulationControlPanel());
			jContentPane.add(getJSimulationControlPanel(), BorderLayout.CENTER);
			simulationControlPanel.showConsole();
			//resize the main window...
			jFrame.setSize(SIZEX_DETACHED, SIZEY_DETACHED);
			detachedVisualizationFrame = 
				new DetachedVisualizationFrame(
						jFrame.getTitle(), 
						this);
			detachedVisualizationFrame.setVisible(true);
			
			jContentPane.setVisible(true);
		}
	}
	
	/**
	 * attaches the visualization of the simulation
	 */
	public void attachVisualization() {
		if (visualizationAttached)
			return;

		if (detachedVisualizationFrame!=null) {
			detachedVisualizationFrame.setVisible(false);
			//detachedVisualizationFrame.remove(getJSimulationPanel());
			detachedVisualizationFrame.dispose();
			detachedVisualizationFrame=null;
		}
		visualizationAttached = true;
		jVisuDetachAttachMenuItem.setText("Detach");
		jContentPane.setVisible(false);
		jContentPane.remove(getJSimulationControlPanel());
		jContentPane.add(getJSimulationPanel(), BorderLayout.CENTER);
		
		getJSimulationPanel().add(getJVisualizationPanel(), BorderLayout.CENTER);
		getJSimulationPanel().add(getJSimulationControlPanel(), BorderLayout.SOUTH);
		//this looks nice as well:
		//jContentPane.add(getJSimulationControlPanel(), BorderLayout.SOUTH);
		
		//resize the main window (maybe save original values)
		jFrame.setSize(SIZEX_ATTACHED, SIZEY_ATTACHED);
		
		jContentPane.setVisible(true);
	}
	
	/**
	 * set this to true to embed the swing components into an SWT window
	 * UNSTABLE, non working yet
	 */
	private boolean useNativeSWT = false;

	
	private GuiScripter guiScripter;
//	Display display = null;
//    Shell shell = null;

	/**
	 * This is the default constructor
	 */
	public SimulationMainFrame() {

		//super();
        //Composite composite = new Composite(getContainer(), SWT.NONE);
        //final Display display = Display.getCurrent();

        //	jFrame = new java.awt.Frame();
        //jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
    
    public void setGuiScripter(GuiScripter guiScripter){
        this.guiScripter=guiScripter;
    }
	
	
	
/*	public SimulationMainFrame(Simulation simulation, DefaultSimulationParameters parameters) {
		super();
		//initialize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		//set the MainFrame visible
		parameters.useVisualisation(this);

		//calls initialize:
		parameters.getSimulationFrame().show(parameters); //this.show(parameters);
		//if (parameters.isUseVisualisationOn()){
        	//parameters.getSimulationFrame().show(parameters);
		//}
        ApplicationSimulation applicationsimulation=new ApplicationSimulation(parameters,simulation);
        applicationsimulation.run();

	}*/
	
	/**
	 * paint actualizes the scrollbars
	 * @see java.awt.Component#paint(Graphics)
	 * FIXME this is not necessary anymore and was overridden when
	 * the class was inherited from JFrame
	 */
	public void paint(Graphics g) {
		if (visualizationPanel != null) {
			//Dimension pref = visualizationPanel.getPreferredSize();
			//Dimension size = visualizationPanel.getSize();
			/*int vExtent = (int) (size.height * (1 / visualizationPanel.getZoom()));
			int vMax = pref.height;
			int vValue = Math.max(0, Math.min(vsb.getValue(), vMax - vExtent));
			vsb.setVisible(vExtent < vMax);
			vsb.setValues(vValue, vExtent, 0, vMax);
			int hExtent = (int) (size.width * (1 / myPanel.getZoom()));
			int hMax = pref.width;
			int hValue = Math.max(0, Math.min(hsb.getValue(), hMax - hExtent));
			hsb.setVisible(hExtent < hMax);		
			hsb.setValues(hValue, hExtent, 0, hMax);*/
		}
		jFrame.paint(g);
		//super.paint(g);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		//'frame' has only changed if simulation is playing
		if (this.isPlaying()) {
			frame 
				= frameSource.getFrame();

			frameBuffer.addFrame(frame);
			int sz = frameBuffer.size();

			simulationControlPanel.getTimelinePanel().setMaximum(sz);
			simulationControlPanel.getTimelinePanel().setValue(sz);

			//in case a frame is added the messages have to be updated
			simulationControlPanel.getConsolePanel()
	    		.updateMessages();
		} else {
			frame 
				= frameBuffer.getFrameAt(
						simulationControlPanel.getTimelinePanel().getValue()-1);
			//in case cached frame is shown the corresponding debug messages are highlighted
			simulationControlPanel.getConsolePanel()
				.updateHighlight(frame);
		}

		

        
        String time = frame.getTimeString();        
        simulationControlPanel.setTimeLabelText("time " +time);
        
        if (guiScripter!=null){
            guiScripter.nextFrame(frame);
        }

        
        
		if (visualizationPanel != null) {
            Shape shape=buildShape(frame.getShape());
			visualizationPanel.setShape(shape, frame.getAddressPositionMap());
		}
	}
	
	/**
     * 
     * TODO Comment method
     * @param frameShape
     * @return
	 */
    protected Shape buildShape(Shape frameShape) {
        Shape shape;
        if (markDevice!=null){
            ShapeCollection shapeCollection=new ShapeCollection();
            shapeCollection.addShape(frameShape);
            shapeCollection.addShape(new RectangleShape(markDevice,new Extent(10,10),Color.BLUE,false));
            
            shape=shapeCollection;
        }else{
            shape=frameShape;
        }
        return shape;
    }

    protected boolean playing = true;

    private JPanel searchPanel;
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * pause rendering
	 */
	public void pause() {
		playing = false;
		frameSource.setPlayPauseRender(playing);
		simulationControlPanel.setPlaying(playing);
	}

	/**
	 * resume rendering
	 */
	public void play() {
		playing = true;
		frameSource.setPlayPauseRender(playing);
		simulationControlPanel.setPlaying(playing);
	}
	
	protected void setupPlayPauseAction() {
		JButton ppButt = simulationControlPanel.getJPlayPauseButton();
		ppButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playing = !playing;
				//switch the renderqueue.
				frameSource.setPlayPauseRender(playing);
				//switch the icon
				simulationControlPanel.setPlaying(playing);
			}
		});
	}

    public class ScreenshotAction extends AbstractAction {
        private final JFileChooser fdialog;
        private final JFrame frame;
    
        ScreenshotAction(JFrame frame) {
            super("Save As...");
            this.fdialog = new JFileChooser();
            this.frame = frame;
        }
    
        private boolean wasPlaying;
        
        class PngFilter extends javax.swing.filechooser.FileFilter {
            public static final String SUFFIX=".png";
        	public boolean accept(File file) {
                String filename = file.getName();
                return filename.endsWith(SUFFIX);
            }
            public String getDescription() {
                return "*.png";
            }
        }
        class EpsFilter extends javax.swing.filechooser.FileFilter {
            public static final String SUFFIX=".eps";
        	public boolean accept(File file) {
                String filename = file.getName();
                return filename.endsWith(SUFFIX);
            }
            public String getDescription() {
                return "*.eps";
            }
        }
        class PsFilter extends javax.swing.filechooser.FileFilter {
            public static final String SUFFIX=".ps";
        	public boolean accept(File file) {
                String filename = file.getName();
                return filename.endsWith(SUFFIX);
            }
            public String getDescription() {
                return "*.ps";
            }
        }
        class XmlFilter extends javax.swing.filechooser.FileFilter {
            public static final String SUFFIX=".xml";
        	public boolean accept(File file) {
                String filename = file.getName();
                return filename.endsWith(SUFFIX);
            }
            public String getDescription() {
                return "*.xml";
            }
        }
        
        public void actionPerformed(ActionEvent evt) {
        	int ret;
			String filename;
			String fileSuffix;
        	//stop rendering the simulation (halt queue)
        	if (wasPlaying = playing) // if not already paused
        		pause();
        	
			FileFilter filefilter;
			switch (getScreenshotSelection()) {
			case SAVE_EPS:	filefilter = new EpsFilter();	
							fileSuffix = EpsFilter.SUFFIX;
							break;
			case SAVE_PS:	filefilter = new PsFilter();	
							fileSuffix = PsFilter.SUFFIX;
							break;
			case SAVE_XML:	filefilter = new XmlFilter();	
							fileSuffix = XmlFilter.SUFFIX;
							break;
			case SAVE_PNG:
				default:	filefilter = new PngFilter();
							fileSuffix = PngFilter.SUFFIX;
							break;
			}
        	fdialog.setFileFilter(filefilter);
			ret = fdialog.showSaveDialog(frame);
			if (ret == JFileChooser.CANCEL_OPTION) {
				//System.out.println("cancelled");
			} else {
	            // Get the selected file
	            File file = fdialog.getSelectedFile();
			
	            filename = file.getAbsoluteFile().toString();
	            //optionally append a suffix 
	            if (!filename.endsWith(fileSuffix)) {
	            	filename+=fileSuffix;
	            }
	            render(filename);
			}
			//start again
			if (wasPlaying)
				play();
        }
    };

    public void render(String filename) {
    	VisualizationPanel visualizationPanel =
    		this.getJVisualizationPanel();
    	
    	de.uni_trier.jane.visualization.Canvas shotCanvas = null;
    	XMLRenderCanvas xmlCanvas = null;
    	PostScript2DRenderCanvas psCanvas = null;
    	PngRenderCanvas pngCanvas = null;

    	//save the reference to the visualizing worldspace 
    	Worldspace origWorldspace = 
    		visualizationPanel.getWorldspace();

    	switch(this.getScreenshotSelection()) {
    	case SAVE_EPS:
    			shotCanvas = 
    			psCanvas = new PostScript2DRenderCanvas(filename,"Jane - "+simulationParameters.getSimulationName(),
    													PostScript2DRenderCanvas.SAVE_EPS);
       			psCanvas.setWidth(origWorldspace.getCanvas().getVisibleWidth());
    			psCanvas.setHeight(origWorldspace.getCanvas().getVisibleHeight());
    		break;
    	case SAVE_PS:
    			shotCanvas = 
    			psCanvas = new PostScript2DRenderCanvas(filename,"Jane - "+simulationParameters.getSimulationName(),
    													PostScript2DRenderCanvas.SAVE_PS);
    		break;
    	case SAVE_XML:
    			shotCanvas = 
    			xmlCanvas  = new XMLRenderCanvas(filename);
    			xmlCanvas.beginRendering();
    		break;
    	case SAVE_PNG:default:
    			shotCanvas =
    			pngCanvas  = new PngRenderCanvas(filename);
    			//this must be called before setting the worldspace!
    			//(otherwise graphics is null)
    			pngCanvas.setWidth(origWorldspace.getCanvas().getVisibleWidth());
    			pngCanvas.setHeight(origWorldspace.getCanvas().getVisibleHeight());
    			pngCanvas.beginRendering();
    		break;
    	}
    	
    	Worldspace shotWorldspace =
    		new DefaultWorldspace(shotCanvas);
    	
    	shotWorldspace.setTransformation(
    			origWorldspace.getTransformation());
    	
    	visualizationPanel.setWorldspace(shotWorldspace);
    	
    	//render the scene!
    	visualizationPanel.renderScene();
    	
    	if (psCanvas!=null)		//flush buffer to disk
    		psCanvas.writePSFile();
    	else
    	if (xmlCanvas!=null)	//write end tag 
    		xmlCanvas.endRendering();
    	else
    	if (pngCanvas!=null) {
    		pngCanvas.writePngFile();
    		pngCanvas.endRendering();
    	}
    	//restore old render engine
    	visualizationPanel.setWorldspace(origWorldspace);
    }
    
	/**
	 * screenShotAction
	 * this can be accomplished in a nicer way (e.g. numbered image sequences)
	 */
	protected void setupScreenshotAction() {
		JButton scButt = simulationControlPanel.getJScreenshotButton();
		ScreenshotAction screenshotAction
			= new ScreenshotAction(jFrame);
		scButt.addActionListener(screenshotAction);
	}
	
	private class TimeSliderChangeListener implements ChangeListener {
		final SimulationMainFrame simulationMainFrame;
		public TimeSliderChangeListener(
				SimulationMainFrame simulationMainFrame) {
			this.simulationMainFrame = simulationMainFrame;
		}
		public void stateChanged(ChangeEvent e) {
			//JSlider timeSlider = 
			//	(JSlider)e.getSource();
			//System.out.println("time action:"+timeSlider.getValue());
		}
	}
	
	private TimeSliderChangeListener timeSliderChangeListener; 
	
	protected void setupTimeSlider() {
		JSlider timeSlider;
		TimelinePanel timelinePanel;
		timelinePanel = simulationControlPanel.getTimelinePanel();

		timeSlider = timelinePanel.getJTimeSlider();
		timeSliderChangeListener = new TimeSliderChangeListener(this);
		timeSlider.addChangeListener(timeSliderChangeListener);
	}
	
	/**
	 * add the necessary change listeners to the speedSlider component instance
	 */
	protected void setupSpeedSlider() {
		JSlider speedSlider;
		SpeedPanel speedPanel;
		speedPanel = simulationControlPanel.getSpeedPanel();
		speedSlider = speedPanel.getSpeedSlider();
		if (speedPanel.isLinear()) {
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					double val = s.getValue();
					double factor;
					if (val < 0.0) {
						factor = 1.0 / (1.0 + (val * -0.5));
					} else {
						factor = val + 1.0;
					}
					if (frameSource!=null) {
						frameSource.setFrameInterval((factor / fps));
					}
				}
			});
		} else 
		if (speedPanel.isExponential()) {
			speedSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					int val = s.getValue();
					double factor;
					if (val < 0.0) {
						val=-val;
						int speedup=(int)Math.pow(2,val/10);
						factor=speedup+(val%10)*speedup/10.0;
						
						factor = 1.0 / factor;// (1.0 + (val * -0.5));
					} else {
						
						int speedup=(int)Math.pow(2,val/10);
						factor=speedup+(val%10)*speedup/10.0;
					}
					if (frameSource!=null) {
                        
						frameSource.setFrameInterval((factor / fps));
					}
				}
			});
		}
	}
	
	//public void dispose() {
	//	jFrame.dispose();
	//}
	
	Composite composite = null;
	
	/**
	 * reference to the simulation parameters
	 */
	SimulationParameters simulationParameters = null;
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.SimulationFrame#show(de.uni_trier.jane.simulation.SimulationParameters)
	 */
	public void show(SimulationParameters parameters) {
		this.simulationParameters = parameters;
		
		
		//this.visualizationParameters = parameters.getVisualizationParameters();
        if (useNativeSWT) {/*
    		display = new Display( );
            shell = new Shell(display);
        	shell.setText("Jane - "+parameters.getSimulationName()+" (swt)");
        	shell.setSize(SIZEX_ATTACHED, SIZEY_ATTACHED);
        	
        	composite = new Composite(shell, SWT.EMBEDDED);
        	Rectangle area = shell.getClientArea();
        	composite.setBounds(0,0,area.width, area.height);
        	composite.setLayout(new FillLayout( ));
        	
        	awtFrame = SWT_AWT.new_Frame(composite);
        	//	awtFrame = SWT_AWT.new_Frame(composite);

    		composite.addControlListener(new ControlAdapter() {
    			public void controlResized(ControlEvent e) {
    				//resizeScene();
    				Rectangle area;
    				area = composite.getClientArea();
    				System.out.println("resize"+area.width+" "+area.height);
    				
    			}
    		});
    		
    		composite.addDisposeListener(new DisposeListener() {
    			public void widgetDisposed(DisposeEvent e) {
    				System.out.println("widgetDisposed");
    				//SimulationMainFrame.this.
    			//SWTSimulationFrame.this.onDispose(e);
    			}
    		});
    		composite.addPaintListener(new PaintListener() {
    			public void paintControl(PaintEvent e) {
    				System.out.println("paintControl");
    				//SWTSimulationFrame.this.paintControl(e);
    			}
    		});		
        	
        	*/
        } else {
        	jFrame = new JFrame();
        	jFrame.setTitle("Jane - "+simulationParameters.getSimulationName());
        	}

        globalShutdownAnnouncer=simulationParameters.getGlobalShutdownAnnouncer();
        myShutdownListener=new ShutdownListener() {
            public void shutdown() {
            	System.out.println("SimulationMainFrame: shutdown()");
                jFrame.dispose();
                dispose();
                repaintTimer.stop();
            }
        };
        globalShutdownAnnouncer.addShutdownListener(myShutdownListener);

        
                
        
		RenderQueue queue = 
			new RenderQueue(simulationParameters);
		frameSource=queue;
		frameBuffer = new FrameBuffer(simulationParameters.getVisualizationParameters().getFrameCacheSize());
		
		simulationParameters.setFrameRenderer(queue);

		this.showSplashScreen();
        initialize();
        
        simulationControlPanel.getConsolePanel()
        	.setFrameBuffer(frameBuffer);

        /*simulationControlPanel.getTimelinePanel()
        	.getJTimeSlider().addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					repaint();
				}
        		
        	});*/
        
		/*
    	globalShutdownAnnouncer=parameters.getGlobalShutdownAnnouncer();
        myShutdownListener=new ShutdownListener() {
            public void shutdown() {
            	System.out.println("SimulationMainFrame: shutdown()");
                jFrame.dispose();
            	//dispose();
            }
        };
        globalShutdownAnnouncer.addShutdownListener(myShutdownListener);

		RenderQueue queue = 
			new RenderQueue(parameters);
		frameSource=queue;
		
		parameters.setFrameRenderer(queue);

		this.showSplashScreen();
		
		initialize();
		*/
		fps = simulationParameters.getSimulationFrameFPS();
double frameInterval = 1000.0 / fps;
		setupPlayPauseAction();
		setupScreenshotAction();
		
		setupSpeedSlider();
		setupTimeSlider();
		actualizeVisualizationMode();
		
		repaintTimer = new javax.swing.Timer((int) frameInterval, this);
		repaintTimer.start();
		jFrame.setVisible(true);

	
        if (this.useNativeSWT) {
        	/*shell.open( );
        	while(!shell.isDisposed( )) {
        		if (!display.readAndDispatch( )) display.sleep( );
        	}
		
        	display.dispose( );	*/
        }
        /*while (mainFrameThread != null)
        		try {
        			Thread.sleep(100);
        		} catch (InterruptedException ie) {}
        */
        //System.out.println("Thread SimulationMainFrame exited.");
	}
	
	protected void shutdown() {
		System.out.println("Shutdown");
		globalShutdownAnnouncer.removeShutdownListener(myShutdownListener);
		frameSource.stopRender();
		globalShutdownAnnouncer.shutdown();
		
        jFrame.dispose();
        dispose();
        repaintTimer.removeActionListener(this);
        repaintTimer.stop();
        repaintTimer=null;
        
		//System.exit(0);
	}

	/**
	 * switches between the default/plan and the perspective view of the simulationvisualization
	 *
	 */
	protected void actualizeVisualizationMode() {
		JRadioButton radio = getJDefaultViewRadioButton();
		JPanel panel = getJViewPanel();
		panel.setVisible(false);
		if (radio.isSelected()) {
			panel.remove(getJPerspectiveViewPanel());
			panel.add(getJPlanViewPanel(), BorderLayout.WEST);
			visualizationPanel.setVisualizationMode(
					VisualizationPanel.VISUALIZE_PLAN);
			jPlanViewPanel.actualizeUserControlledTransformMode();
		} else {
			panel.remove(getJPlanViewPanel());
			panel.add(getJPerspectiveViewPanel(), BorderLayout.WEST); 
			visualizationPanel.setVisualizationMode(
					VisualizationPanel.VISUALIZE_PROJECT_PARALLEL);
			jPerspectiveViewPanel.actualizeUserControlledTransformMode();
		}
		panel.setVisible(true);
	}

	/**
	 * show the greetings window
	 */
	private void showSplashScreen() {
		final JWindow splashScreen = new JWindow();

		JLabel label = new JLabel(new ImageIcon(SimulationMainFrame.class.getResource("splash.jpg")));

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
	}	
	
	/**
	 * default x size in attached window mode
	 */
	private final static int SIZEX_ATTACHED = 800;
	/**
	 * default y size in attached window mode
	 */
	private final static int SIZEY_ATTACHED = 600;
	/**
	 * default x size in detached window mode
	 */
	private final static int SIZEX_DETACHED = 800;
	/**
	 * default y size in detached window mode 
	 */
	private final static int SIZEY_DETACHED = 300;
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		jFrame.setJMenuBar(getJJMenuBar());
		//jFrame.getRootPane().setMenuBar(getJJMenuBar);
		//jFrame.setMenuBar(getJJMenuBar());
		
		jFrame.setSize(SIZEX_ATTACHED, SIZEY_ATTACHED);
		//jFrame.setContentPane(getJContentPane());
		jFrame.getContentPane().add(getJContentPane());
		
		
		jFrame.addComponentListener(new ComponentListener() {
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
				//showVisualization.setEnabled(true);							
			}
		}); 
		jFrame.addWindowListener(new WindowListener(){

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
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJControlPanel(), java.awt.BorderLayout.EAST);
			jContentPane.add(getJSimulationPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jControlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJControlPanel() {
		if (jControlPanel == null) {
			jControlPanel = new JPanel();
//			jControlPanel.setBounds(0,0,50,50);
			//jControlPanel.set
			jControlPanel.setLayout(new java.awt.BorderLayout());
            jControlPanel.add(getSearchPanel(), java.awt.BorderLayout.NORTH);
            
			jControlPanel.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jControlPanel;
	}
	
	private JPanel getSearchPanel() {
        if (searchPanel==null){
            searchPanel=new JPanel();
            final JTextField text = new JTextField("",4);
            searchPanel.add(text,null);
            JButton button=new JButton("Search");
            button.addChangeListener(new ChangeListener() {
            
                public void stateChanged(ChangeEvent e) {
                    try{
                        int id=Integer.parseInt(text.getText());
                        SimulationDeviceID deviceID=new SimulationDeviceID(id);
                        if (frame.getAddressPositionMap().hasPosition(deviceID)){
                            markDevice=deviceID;
                        }else{
                            markDevice=null;
                        }
                    }catch (NumberFormatException ex) {
                        markDevice=null;
                    }
            
                }
            
            });
            searchPanel.add(button,null);
        }
        
        return searchPanel;
    }
    /**
	 * This method initializes jSimulationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJSimulationPanel() {
		if (jSimulationPanel == null) {
			jSimulationPanel = new JPanel();
			jSimulationPanel.setLayout(new java.awt.BorderLayout());
			jSimulationPanel.add(getJVisualizationPanel(), java.awt.BorderLayout.CENTER);
			jSimulationPanel.add(getJSimulationControlPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jSimulationPanel;
	}
	/**
	 * This method initializes jSimulationSpeedPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJSimulationControlPanel() {
		if (jSimulationControlPanel == null) {
			simulationControlPanel = new SimulationControlPanel();
			jSimulationControlPanel = simulationControlPanel;
		}
		return jSimulationControlPanel;
	}
	/**
	 * This method initializes jVisualizationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	protected VisualizationPanel getJVisualizationPanel() {
		if (jVisualizationPanel == null) {
			jVisualizationPanel = new VisualizationPanel(
					frameSource.getRectangle(), 
					frameSource.getFrame().getShape()
			);
		}
		visualizationPanel=(VisualizationPanel)jVisualizationPanel;
		visualization = visualizationPanel;
		return jVisualizationPanel;
	}
	/**
	 * get the VisualizationPanel component; 
	 * is instanciated if not yet constructed
	 * @return VisualizationPanel 
	 */
//	public VisualizationPanel getVisualizationPanel() {
//		getJVisualizationPanel();
//		return visualizationPanel;
//	}
	
	/**
	 * replaces the current visualizationPanel instance by another one
	 * this is useful to switch between swing/swt-gl 
	 * @param visualizationPanel
	 */
	public void setVisualizationPanel(IVisualization visualizationPanel) {
		if (visualizationPanel==null) // ignore silly calls
			return;
		System.out.println("setVisualizationPanel: switching VisualizationPanel");
		
		jSimulationPanel.setVisible(false);
		//remove the old reference within the gui...
		jSimulationPanel.remove(this.jVisualizationPanel);

		//replace with new, given instance
        this.visualizationPanel = visualizationPanel;
        if (visualizationPanel instanceof JPanel){
            jVisualizationPanel = (VisualizationPanel) visualizationPanel;
		
            jSimulationPanel.add(getJVisualizationPanel(), java.awt.BorderLayout.CENTER);

            jSimulationPanel.setVisible(true);
            //update the references for the controllers (they set the matrix)
            jPlanViewPanel.setVisualizationPanel(jVisualizationPanel);
            jPerspectiveViewPanel.setVisualizationPanel(jVisualizationPanel);
        }
	}
	
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */    
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Views", null, getJPanel(), null);
			jTabbedPane.addTab("Stats", null, getJPanel1(), null);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			//jPanel.setBounds(0,0,100,50);
			jPanel.setLayout(new java.awt.BorderLayout());
			jPanel.add(getJViewSelectPanel(), java.awt.BorderLayout.NORTH);
			jPanel.add(getJViewPanel(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}
	/**
	 * This method initializes jViewSelectPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJViewSelectPanel() {
		if (jViewSelectPanel == null) {
			ButtonGroup group = new ButtonGroup();
			jViewSelectPanel = new JPanel();
            jViewSelectPanel.setLayout(new BorderLayout());
			jViewSelectPanel.add(getJDefaultViewRadioButton(), BorderLayout.NORTH);
			jViewSelectPanel.add(getJPerspectiveViewRadioButton(), BorderLayout.SOUTH);
			group.add(getJDefaultViewRadioButton());
			group.add(getJPerspectiveViewRadioButton());
		}
		return jViewSelectPanel;
	}
	/**
	 * This method initializes jViewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJViewPanel() {
		if (jViewPanel == null) {
			jViewPanel = new JPanel();
			//default to PlanViewPanel
			jViewPanel.add(getJPlanViewPanel(), BorderLayout.WEST);
			//instanciate PerspectiveViewPanel
			getJPerspectiveViewPanel();
		}
		return jViewPanel;
	}
	/**
	 * This method initializes jPlanViewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPlanViewPanel() {
		if (jPlanViewPanel == null) {
			jPlanViewPanel = new PlanViewPanel();
			//necessary for the visualization modes triggered by the events:
			this.getJVisualizationPanel();//instanciate visualizationPanel
			jPlanViewPanel.setVisualizationPanel(jVisualizationPanel);
		}
		return jPlanViewPanel;
	}
	
	/**
	 * This method initializes jPerspectiveViewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPerspectiveViewPanel() {
		if (jPerspectiveViewPanel == null) {
			jPerspectiveViewPanel = new PerspectiveViewPanel();
			jPerspectiveViewPanel.setSize(185, 335);
			//necessary for the visualization modes triggered by the events:
			this.getJVisualizationPanel();//instanciate visualizationPanel
			jPerspectiveViewPanel.setVisualizationPanel(jVisualizationPanel);

		}
		return jPerspectiveViewPanel;
	}
	/**
	 * This method initializes jDefaultViewRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJDefaultViewRadioButton() {
		if (jDefaultViewRadioButton == null) {
			jDefaultViewRadioButton = new JRadioButton();
			jDefaultViewRadioButton.setText("Default/Plan");
			jDefaultViewRadioButton.setSelected(true);
			jDefaultViewRadioButton.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					if (jDefaultViewRadioButton.isSelected())
						actualizeVisualizationMode();
				}
			});
		}
		return jDefaultViewRadioButton;
	}
	/**
	 * This method initializes jPerspectiveViewRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJPerspectiveViewRadioButton() {
		if (jPerspectiveViewRadioButton == null) {
			jPerspectiveViewRadioButton = new JRadioButton();
			jPerspectiveViewRadioButton.setText("Perspective");
			//jPerspectiveViewRadioButton.
			jPerspectiveViewRadioButton.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					if (jPerspectiveViewRadioButton.isSelected())
						actualizeVisualizationMode();
				}
			});
		}
		return jPerspectiveViewRadioButton;
	}
    /**
     * TODO: comment method 
     * @param speed
     */
    public void setSpeed(int speed) {
        simulationControlPanel.setSpeed(speed);
        
    }
      }  //  @jve:decl-index=0:visual-constraint="10,10"
