package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

public class ContextMenu {

	final public static Object	DEFAULT_HANDLER = new Object();
	
	final public static Color	DEFAULT_MENU_BG = new Color(0,0,255,70);
	final public static Color	DEFAULT_ITEM_BG = new Color(0,0,255,30);
	final public static Color	DEFAULT_ITEM_FG = Color.BLACK;
	final public static Font	DEFAULT_ITEM_FONT = new Font("Arial", Font.PLAIN, 10);
	
	private boolean			enableContextMenu = true;
	private Vector			contextHandler = new Vector();
//	private ContextHandler	defaultHandler = new ContextHandler() {
//		public String getLabel(Point p) { return "DEFAULT HANDLER"; }
//		public boolean acceptsPoint(Point p) { return true; }
//		public JMenuItem[] getMenuItems(Point p) {
//			return new JMenuItem[] {
//					new JMenuItem("Unspecified context Menu"),
//					ContextHandler.SEPARATOR,
//					new JMenuItem("Given point: "+p)					
//			};
//		}
//		
//	};			
	private ContextHandler	defaultHandler = null;
	
	private JPopupMenu	popupMenu = new JPopupMenu();
	private JComponent	parent = null;			
	
	// debugging
//	private Stack	enableStack = new Stack();
	
	public ContextMenu(JComponent parent) {
		super();
		this.parent = parent;
		
		//popupMenu.setBackground(DEFAULT_MENU_BG);		
		//popupMenu.setLightWeightPopupEnabled(true);
		popupMenu.setInvoker(parent);
		
		addComponent(parent);				
	}
	
	public void addContextHandler(ContextHandler handler) {
		// null value not allowed here
		if (handler == null) {
			throw new IllegalArgumentException("addContextHandler: null value is not allowed; "+
					"handler: " + handler );
		}
		
		contextHandler.add(handler);
	}
	
	public void showContextMenu(Point p) {
		if (!enableContextMenu) return;
		popupMenu.removeAll();
				
		// retrieve handler for this contextObject; default handler is defaultHandler
		ContextHandler handler = defaultHandler;
		
		// look if any handler accepts the point p
		for (Iterator it = contextHandler.iterator(); it.hasNext();) {
			ContextHandler ch = (ContextHandler)(it.next());
			if ( ch.acceptsPoint(p) ) {
				handler = ch;
				break;
			}
		}
				
		// none found? then exit and show nothing
		if (handler == null) {
//			System.err.println("ContextHandler: no context menu handler found for " + p);
			return;
		}

		// build new context menu
		popupMenu.setLabel(handler.getLabel(p));
		JMenuItem[] items = handler.getMenuItems(p);
		setItemProperties(items);
		
		for (int i=0; i<items.length; i++) {
			if (items[i]!=ContextHandler.SEPARATOR)
				popupMenu.add(items[i]);				
			else
				popupMenu.addSeparator();
		}
		
		// show context menu at specified position p		
		popupMenu.pack();				
		popupMenu.show(parent, p.x, p.y);
	}

	public void setEnabled(boolean doEnable, Object source) {
		if (isEnabled() ^ doEnable) {
			enableContextMenu = doEnable;
		}
		
	}
	
	public boolean isEnabled() {
		return enableContextMenu;
	}
	
	private void setItemProperties(MenuElement[] items) {
		for (int i=0; i<items.length; i++) {
			Component item = items[i].getComponent();
			//items[i].setBackground(DEFAULT_ITEM_BG);
			//items[i].setForeground(DEFAULT_ITEM_FG);
			//items[i].setBorderPainted(false);
			item.setFont(DEFAULT_ITEM_FONT);
		
			if (items[i].getSubElements().length > 0)
				setItemProperties(items[i].getSubElements());
		}
	}
	
	private void addComponent(JComponent parent) {
		parent.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:						
						popupMenu.setVisible(false);
						break;
						
					case MouseEvent.BUTTON3:
						showContextMenu(e.getPoint());
				}
					
				
			}
		});
	}
}
