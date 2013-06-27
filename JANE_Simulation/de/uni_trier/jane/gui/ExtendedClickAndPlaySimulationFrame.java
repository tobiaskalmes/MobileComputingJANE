/*
 * Created on Jun 9, 2005
 * File: SyncClickAndPlaySimulationGUI.java
 */
package de.uni_trier.jane.gui;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.Action;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.ThreadActionHandler;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySourceSimple;
import de.uni_trier.jane.simulation.gui.ClickAndPlaySimulationFrame;
import de.uni_trier.jane.simulation.gui.OutputFrameRenderer;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * This GUI class is an extended click and play simulation GUI. It allows the
 * user to add popup menus to the devices, if the user clicks on a device in
 * the simulation environment. This GUI is realized as global service and must
 * therefore started by the user in the 
 * <code>initGlobalServices(ServiceUnit serviceUnit)</code> method. 
 * If the user wants to add popup menus to the devices, the user defined GUI
 * must extend this class.<br>
 * If the user needs a GUI which is synchronized he must add the code line
 * <code>parameters.setSynchronizedWithGUI(true);</code> to the method
 * <code>initSimulation(SimulationParameters parameters)</code>. Now, all
 * signals which are sended from the GUI to the simulation are synchronized.<br>
 * Nevertheless one can use this GUI to create an user defined GUI without
 * setting the synchronized flag to <code>true</code>.
 * 
 * @author ulf.wehling
 * @version Jul 2, 2005
 */
public class ExtendedClickAndPlaySimulationFrame 
extends ClickAndPlaySimulationFrame
implements GlobalService {

	


    /**
     * @author goergen
     *
     * TODO comment class
     */
    public interface MyAction {
        public void handle();

    }

    /**
     * @author goergen
     *
     * TODO comment class
     */
    public class Worker implements Runnable {

        private boolean shuttingDown;
        private Object localActionQueueMon;
        private List localActionQueue;

        /**
         * Constructor for class <code>ExtendedClickAndPlaySimulationFrame.Worker</code>
         */
        public Worker() {
            localActionQueue=new ArrayList();
            localActionQueueMon=new Object();
            shuttingDown=false;
        }
        
        public void run() {
            while (!shuttingDown){
                MyAction action;
                synchronized(localActionQueueMon){
                    try {
                        while (localActionQueue.isEmpty()){
                            localActionQueueMon.wait();
                        }
                    } catch (InterruptedException e) {
                        
                    
                    }
                    action=(MyAction)localActionQueue.remove(0);
                }
                action.handle();
                
                
                
                
                
            }

        }

        /**
         * TODO Comment method
         * @param action
         */
        public void addAction(MyAction action) {
            
            synchronized(localActionQueueMon){
               localActionQueue.add(action);
                localActionQueueMon.notify();
            }
            
        }

        /**
         * TODO Comment method
         */
        public void finish() {
            synchronized(localActionQueueMon){
                shuttingDown=true;
                localActionQueue.clear();
                localActionQueueMon.notify();
            }
            
        }

    }

    private static final long serialVersionUID = -3437784668954721679L;
	private static final ServiceID SERVICE_ID = 
		new EndpointClassID("ExtendedClickAndPlaySimulationGUIService");
	
	/**
	 * This class must be used to send signals from the GUI to one device.
	 */
	protected GlobalOperatingSystem globalOS;
	/**
	 * Contains the device ID of the (virtual) global device.
	 */
	protected DeviceID globalDeviceID;
    /**
     * The ID of the device selected by a right mouse click.
     */
    protected DeviceID selectedPopupMenuDevice;
	/**
     * Popup menu for the visualization panel.
     */
    protected JPopupMenu jPopupMenu_panel;
    /**
     * Popup menu for the current selected device.
     */
    protected JPopupMenu jPopupMenu_device;
	
	private ClickAndPlayMobilitySource clickAndPlayMobilitySource;
    private boolean firstClick;
    private Thread workerThread;
    private Worker worker;
	
   
	/**
	 * Constructor of the class <code>ExtendedClickAndPlaySimulationGUI</code>.
	 * @param clickAndPlayMobilitySource Mobility source for the current GUI.
	 */
	public ExtendedClickAndPlaySimulationFrame(
			ClickAndPlayMobilitySource clickAndPlayMobilitySource) {

		super(clickAndPlayMobilitySource);
		this.clickAndPlayMobilitySource = clickAndPlayMobilitySource;
		firstClick = true;
		
        // create the popup menus
        jPopupMenu_panel = new JPopupMenu("Global Actions");
        jPopupMenu_panel.add(new JLabel("Global Actions"));
        jPopupMenu_device = new JPopupMenu("Actions on device");
        jPopupMenu_device.add(new JLabel("Actions on device"));
        worker=new Worker();
        workerThread=new Thread(worker);
        workerThread.start();
        
	}
	
	/**
	 * Adds a new menu item with the specified <code>text</code> and the 
	 * specified <code>actionListener</code> to the popup menu of a device.
	 * @param text Text which is displayed in the popup menu.
	 * @param listener What happens, if this menu item is selected :-)
	 */
	public void addMenuItemToDevice(String text, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(getWorkerListener(listener));
		jPopupMenu_device.add(menuItem);
	}
	
	/**
     * TODO Comment method
     * @param listener
     * @return
     */
    private ActionListener getWorkerListener(final ActionListener listener) {

        return new ActionListener() {
        
            public void actionPerformed(final ActionEvent e) {
                worker.addAction(new MyAction() {
                
                    public void handle() {
                        listener.actionPerformed(e);
                
                    }
                
                });
        
            }
        
        };
    }

    /**
	 * Adds a new menu item with the specified <code>text</code> and the 
	 * specified <code>actionListener</code> to the popup menu of the 
	 * visualization panel.
	 * @param text Text which is displayed in the popup menu.
	 * @param listener What happens, if this menu item is selected :-)
	 */
	public void addMenuItemToPanel(String text, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(getWorkerListener(listener));
		jPopupMenu_panel.add(menuItem);
	}
	
    public void show(SimulationParameters parameters) {
        // TODO Auto-generated method stub
        super.show(parameters);
        createVisualizationFrame();
    }
    
	/**
     * Overriden. Returns the simulation frame.
	 * @see lu.uni.jane.gui.SimulationGUI#createVisualizationFrame()
	 * @return The simulation frame.
	 */
	final protected JFrame createVisualizationFrame() {
//		System.out.println("Extended create visualization frame");
		visualizationPanel.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#
			 *      mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {

				if (e.isPopupTrigger()) {
					showPopup(e);
				}
			}
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#
			 *      mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopup(e);
				}
			}
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#
			 *      mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopup(e);
				}
			}
			/**
			 * Private helper method. This method opens the popup menu for
			 * the selected device or for the simulation panel.
			 * @param e <code>MouseEvent</code> which has triggered this method 
			 *        call.
			 */
			private void showPopup(MouseEvent e) {
                Matrix matrix = visualizationPanel.getInverseTransformationMatrix();
                Position clickPosition=new Position(e.getX(),e.getY()).transform(matrix);
				DeviceID [] devices = clickAndPlayMobilitySource.getAddress(
						new Rectangle(clickPosition, new Extent(5, 5))
						);
				// if at least one device is selected 
				if (devices.length > 0) {
					// show the popup menu for the selected device
					selectedPopupMenuDevice = devices[0];
					jPopupMenu_device.setLabel(
							"Actions on Device " + selectedPopupMenuDevice
							);
					((JLabel) jPopupMenu_device.getComponent(0)).setText(
							"Actions on Device " + selectedPopupMenuDevice
							);
					jPopupMenu_device.show(
                            visualizationPanel, 
                            e.getX(), 
                            e.getY()
                            );
//					System.out.println(
//							"you have selected device " + devices[0] + 
//							" (position " + clickPosition.toString() + ")"
//							);
				} 
				else {
					// show the popup menu for the visualization panel
					jPopupMenu_panel.show(
							visualizationPanel, 
							e.getX(), 
							e.getY()
							);
//					System.out.println(
//							"you have not selected any device (position " + 
//							clickPosition.toString() + ")"
//							);
				}
			}	
		});

		return this;
	}
	

	

	// ##### Implementation of the GlobalService Interface #####################
	
	public void start(GlobalOperatingSystem globalOperatingSystem) {
		this.globalOS = globalOperatingSystem;
		this.globalDeviceID = globalOperatingSystem.getDeviceID();
	}
	
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}
	
	public void finish() {
	    worker.finish();
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void getParameters(Parameters parameters) {
		// NOP
	}
	
}
