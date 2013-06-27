package de.uni_trier.jane.sgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * detached frame for the visualization (swing)
 * @author Klaus Sausen
 */
public class DetachedVisualizationFrame extends JFrame implements
		VisualizationFrame,ActionListener {

	/**
	 * the content pane
	 */
	private javax.swing.JPanel jContentPane = null;

	/**
	 * Title of the detached window (set in the constructor)
	 */
	private final String title;

	/**
	 * visualization interface points to <code>visualizationPanel</code>
	 */
	private final IVisualization visualization;

	/**
	 * visualization panel controlled from elsewhere
	 */
	private final VisualizationPanel visualizationPanel;

	/**
	 * return instance to the swing implementation of IVisualization
	 */
	public IVisualization getVisualization() {
		return visualization;
	}
	
	/**
	 * default x size of detached frame
	 */
	private final static int SIZEX_DEFAULT = 640;
	/**
	 * default y size of detached frame
	 */
	private final static int SIZEY_DEFAULT = 480;
	
	
	/**
	 * this is only needed for the SWT implementation
	 */
	public void run() {
	}
	/**
	 * this is only needed for the SWT implementation
	 * starts the seperate window thread
	 */
	public void start() {
	}

	private SimulationMainFrame simulationMainFrame;

	private boolean windowVisible = true;
	public boolean isWindowVisible() { return windowVisible; }
	
	/**
	 * construct a detached visualization frame
	 * @param title
	 * @param visualizationPanel
	 */
	public DetachedVisualizationFrame(String title, SimulationMainFrame mainframe) {
		this.title = title;
		this.simulationMainFrame = mainframe;
		this.visualization =
		this.visualizationPanel = mainframe.getJVisualizationPanel();
		this.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				//reattach to the mainframe>
				simulationMainFrame.attachVisualization();
			}
			public void windowClosed(WindowEvent e) {}
			/**
			 * this speeds up the simulation e.g. to get to 
			 * to a certain state of the sim. more quickly
			 * -> detach -> iconify!
			 */
			public void windowIconified(WindowEvent e) {
				windowVisible = false;
				visualizationPanel.setRendererEnabled(false);
			}
			public void windowDeiconified(WindowEvent e) {
				windowVisible = false;
				visualizationPanel.setRendererEnabled(true);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});
		initialize();
	}
	
	/**
	 * initialize the components
	 */
	public void initialize() {
		this.setSize(SIZEX_DEFAULT, SIZEY_DEFAULT);
		this.setContentPane(getJContentPane());
		this.setTitle(title+" (visualization)");
	}
	
	public void actionPerformed(ActionEvent arg0) {
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
			jContentPane.add(visualizationPanel, java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	
}
