package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class InfoActionHandler implements ContextActionHandler {
	public void registerListener(Component c) {
		// nothing to be done
	}

	public void removeListener(Component c) {
		// nothing to be done
	}

	public void setEnabled(boolean value) {
		// nothing to be done
	}

	public boolean isEnabled() {
		return true;
	}

	public JToggleButton getButton() {
		JToggleButton info_TB = new JToggleButton();
		
		info_TB.setIcon(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Info.png"));
		info_TB.setSelected(true);
		info_TB.setToolTipText("Shows information about the PathNetObjects without doing any changes");
		
		return info_TB;
	}
}
