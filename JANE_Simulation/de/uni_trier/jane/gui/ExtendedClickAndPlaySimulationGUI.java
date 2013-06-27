/*
 * Created on Jun 9, 2005
 * File: SyncClickAndPlaySimulationGUI.java
 */
package de.uni_trier.jane.gui;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
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
public class ExtendedClickAndPlaySimulationGUI 
extends ClickAndPlaySimulationGUI 
implements GlobalService {

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
	
   
	/**
	 * Constructor of the class <code>ExtendedClickAndPlaySimulationGUI</code>.
	 * @param clickAndPlayMobilitySource Mobility source for the current GUI.
	 */
	public ExtendedClickAndPlaySimulationGUI(
			ClickAndPlayMobilitySource clickAndPlayMobilitySource
			) {
		this(clickAndPlayMobilitySource, null, null);
	}
	
	/**
	 * Constructor of the class <code>ExtendedClickAndPlaySimulationGUI</code>.
	 * @param clickAndPlayMobilitySource Mobility source for the current GUI.
	 * @param screenshotRenderer Renderer which is used for making screenshots.
	 */
	public ExtendedClickAndPlaySimulationGUI(
			ClickAndPlayMobilitySource clickAndPlayMobilitySource,
			OutputFrameRenderer screenshotRenderer
			) {
		this(clickAndPlayMobilitySource, screenshotRenderer, null);
	}
	
	/**
	 * Constructor of the class <code>ExtendedClickAndPlaySimulationGUI</code>.
	 * @param clickAndPlayMobilitySource Mobility source for the current GUI.
	 * @param screenshotRenderer Renderer which is used for making screenshots.
	 * @param videoRenderer Renderer which is used for making video
	 *        screenshots :-)
	 */
	public ExtendedClickAndPlaySimulationGUI(
			ClickAndPlayMobilitySource clickAndPlayMobilitySource,
			OutputFrameRenderer screenshotRenderer,
			OutputFrameRenderer videoRenderer
			) {
		super(clickAndPlayMobilitySource, screenshotRenderer, videoRenderer);
		this.clickAndPlayMobilitySource = clickAndPlayMobilitySource;
		firstClick = true;
		
        // create the popup menus
        jPopupMenu_panel = new JPopupMenu("Global Actions");
        jPopupMenu_panel.add(new JLabel("Global Actions"));
        jPopupMenu_device = new JPopupMenu("Actions on device");
        jPopupMenu_device.add(new JLabel("Actions on device"));
	}
	
	/**
	 * Adds a new menu item with the specified <code>text</code> and the 
	 * specified <code>actionListener</code> to the popup menu of a device.
	 * @param text Text which is displayed in the popup menu.
	 * @param listener What happens, if this menu item is selected :-)
	 */
	public void addMenuItemToDevice(String text, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(listener);
		jPopupMenu_device.add(menuItem);
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
		menuItem.addActionListener(listener);
		jPopupMenu_panel.add(menuItem);
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
				Position clickPosition = visualizationPanel.translate(
						e.getX(), 
						e.getY()
						);
				// the left mouse button was pressed
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (firstClick) {
						DeviceID [] devices = 
							clickAndPlayMobilitySource.getAddress(
								new de.uni_trier.jane.basetypes.Rectangle(
										clickPosition, 
										new Extent(5, 5)
										)
								);
						if (devices.length > 0) {
							firstClick = false;
							selectedDevice = devices[0];
						}
					} 
					else {
						firstClick = true;
						clickAndPlayMobilitySource.setPosition(
								selectedDevice, 
								clickPosition
								);
					}
				}
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
				Position clickPosition = visualizationPanel.translate(
						e.getX(), 
						e.getY()
						);
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
	
	/**
	 * Overriden.<br>
	 * Sets the shape for the visualization panel. The current selected device
	 * is marked with a red rectangle.
	 * @param frame Current frame.
	 */
	protected void setShape(
			de.uni_trier.jane.simulation.visualization.Frame frame
			) {
		if (!firstClick) {
			ShapeCollection collection = new ShapeCollection();
			collection.addShape(frame.getShape());
			collection.addShape(
					new RectangleShape(
							selectedDevice,
							new Extent(10, 10),
							Color.RED,
							false	
							)				
					);
			visualizationPanel.setShape(
					collection, 
					frame.getAddressPositionMap()
					);
		}
		else {
			visualizationPanel.setShape(
					frame.getShape(), 
					frame.getAddressPositionMap()
					);
		}
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
		// NOP
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void getParameters(Parameters parameters) {
		// NOP
	}
	
}
