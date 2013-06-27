/*
 * Created on 16.06.2005
 *
 */
package de.uni_trier.jane.sgui;

import de.uni_trier.jane.simulation.visualization.FrameSource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JButton;

/**
 * @author Klaus Sausen
 */
public class SimulationControlPanel extends JPanel {

	protected SpeedPanel	speedPanel;
	protected JLabel		timeLabel;
	
	private JPanel jControlPanel = null;
	private JPanel jTimeStatusPanel = null;
	private JToggleButton jConsoleToggleButton = null;
	private ImageIcon playImageIcon = null;
	private ImageIcon pauseImageIcon = null;
	private JButton jPlayPauseButton = null;
	private JButton jScreenshotButton = null;
	private JPanel jSpeedPanel = null;
	
	private JPanel jTimelinePanel = null;
	private TimelinePanel timelinePanel = null;
	
	private JPanel jConsolePanel = null;  //  @jve:decl-index=0:visual-constraint="620,10"
	private ConsolePanel consolePanel = null;
	

	/**
	 * This is the default constructor
	 */
	public SimulationControlPanel() {
		super();
		initialize();
	}
	
	/**
	 * @return the panel containing the speed slider component
	 */
	public SpeedPanel getSpeedPanel() {
		return speedPanel;
	}

	/**
	 * sets the text displayed as the current time
	 * @param timestr
	 */
	public void setTimeLabelText(String timestr) {
        
		timeLabel.setText(timestr);
        
        timeLabel.setSize(timeLabel.getPreferredSize());
        
	}

	/**
	 * see emily play
	 * @param playing
	 */
	public void setPlaying(boolean playing) {
		if (playing) {
			jPlayPauseButton.setIcon(getPauseImageIcon());
			getJTimelinePanel().setVisible(false);
			getJSpeedPanel().setVisible(true);
		} else {
			jPlayPauseButton.setIcon(getPlayImageIcon());
			getJTimelinePanel().setVisible(true);
			getJSpeedPanel().setVisible(false);
		}
	}

	/**
	 * initialize the play image icon for the play/pause button
	 */
	public ImageIcon getPlayImageIcon() {
		if (playImageIcon == null) {
			playImageIcon = 
				new ImageIcon(getClass().getResource("control_play16x16.gif"));
		} 
		return playImageIcon;
	}

	/**
	 * initialize the pause image icon for the play/pause button
	 */
	public ImageIcon getPauseImageIcon() {
		if (pauseImageIcon == null) {
			pauseImageIcon = 
				new ImageIcon(getClass().getResource("control_pause16x16.gif"));
		} 
		return pauseImageIcon;
	}
	
	/**
	 * switches the visibility of the console panel
	 */
	protected void switchConsoleVisibility() {
		JToggleButton butt = this.getJConsoleToggleButton();
		if (butt.isSelected()) {
			this.getJConsolePanel().setVisible(true);
		} else {
			this.getJConsolePanel().setVisible(false);
		}
	}
	
	/**
	 * explicitly show the console
	 */
	public void showConsole() {
		JToggleButton butt = this.getJConsoleToggleButton();
		
		if (butt.isSelected()) //is visible already
			return;
		
//		this.setVisible(false);
		butt.setSelected(true);
		this.getJConsolePanel().setVisible(true);
//		this.add(this.getJConsolePanel(), BorderLayout.CENTER);
//		this.setVisible(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setSize(600, 100);
		this.setLayout(new BorderLayout());
		this.add(getJControlPanel(), BorderLayout.NORTH);
		this.add(this.getJConsolePanel(), BorderLayout.CENTER);
		getJConsolePanel().setVisible(false);
	}
	
	/**
	 * This method initializes jControlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJControlPanel() {
		if (jControlPanel == null) {
			jControlPanel = new JPanel();
			jControlPanel.setBounds(0,0,600,40);
			jControlPanel.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth  = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0;				
			gbc.gridx = 0;
			gbc.gridy = 0;
			jControlPanel.add(getJTimeStatusPanel(), gbc);

			gbc.weightx = 1;				
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			jControlPanel.add(getJSpeedPanel(), gbc);
			jControlPanel.add(getJTimelinePanel(), gbc);
			//by default the timeline is invisible
			getJTimelinePanel().setVisible(false);
			getJSpeedPanel().setVisible(true);

			gbc.gridwidth = 1;
			gbc.weightx = 0;				
			gbc.gridx = 3;
			jControlPanel.add(getJPlayPauseButton(), gbc);
			gbc.gridwidth = 1;
			gbc.weightx = 0;				
			gbc.gridx = 4;
			jControlPanel.add(getJScreenshotButton(), gbc);
			gbc.gridwidth = 1;
			gbc.weightx = 0;				
			gbc.gridx = 5;
			jControlPanel.add(getJConsoleToggleButton(), gbc);
		}
		return jControlPanel;
	}
	/**
	 * This method initializes jTimeStatusPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJTimeStatusPanel() {
		if (jTimeStatusPanel == null) {
			timeLabel = new JLabel();
			jTimeStatusPanel = new JPanel();
			timeLabel.setText("time         ");
			jTimeStatusPanel.add(timeLabel, null);
		}
		return jTimeStatusPanel;
	}
	/**
	 * This method initializes jConsoleToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	private JToggleButton getJConsoleToggleButton() {
		if (jConsoleToggleButton == null) {
			jConsoleToggleButton = new JToggleButton();
			jConsoleToggleButton.setText("Console");
			jConsoleToggleButton.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					switchConsoleVisibility();
				}
			});
		}
		return jConsoleToggleButton;
	}
	/**
	 * This method initializes jPlayPauseToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	public JButton getJPlayPauseButton() {
		if (jPlayPauseButton == null) {
			jPlayPauseButton = new JButton();
			jPlayPauseButton.setIcon(
					getPauseImageIcon());
			jPlayPauseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}});
		}
		return jPlayPauseButton;
	}

	/**
	 * This method initializes jScreenshotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	public JButton getJScreenshotButton() {
		if (jScreenshotButton == null) {
			jScreenshotButton = new JButton();
			jScreenshotButton.setIcon(
					new ImageIcon(getClass().getResource("camera16x16.gif")));
		}
		return jScreenshotButton;
	}
	/**
	 * This method initializes jSpeedPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJSpeedPanel() {
		if (jSpeedPanel == null) {
			speedPanel = new SpeedPanel();
			jSpeedPanel = speedPanel;
		}
		return speedPanel;
	}
	/**
	 * This method initializes jTimelinePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJTimelinePanel() {
		if (jTimelinePanel == null) {
			jTimelinePanel =
			timelinePanel  = new TimelinePanel();
		}
		return jTimelinePanel;
	}
	
	/**
	 * public to allow SimulationMainFrame accessing it
	 * @return TimelinePanel
	 */
	public TimelinePanel getTimelinePanel() {
		getJTimelinePanel();
		return timelinePanel;
	}
	
	/**
	 * This method initializes jConsolePanel	
	 * and consolePanel
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJConsolePanel() {
		if (jConsolePanel == null) {
			jConsolePanel =
			consolePanel  = new ConsolePanel();
		}
		return jConsolePanel;
	}
	
	/**
	 * public to allow SimulationMainFrame accessing it
	 * @return the Console
	 */
	public ConsolePanel getConsolePanel() {
		if (jConsolePanel == null) {
			jConsolePanel =
			consolePanel  = new ConsolePanel();
		}
		return consolePanel;
	}

    /**
     * TODO: comment method 
     * @param speed
     */
    public void setSpeed(int speed) {
        speedPanel.setSpeed(speed);
        
    }
	
 }  //  @jve:decl-index=0:visual-constraint="10,10"
