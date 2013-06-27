/*****************************************************************************
 * 
 * NetconfigGUI.java
 * 
 * $Id: NetconfigGUI.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer;

import java.awt.Frame;
import java.awt.Dialog;
import java.awt.TextField;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.Choice;
import java.awt.Button;
import java.awt.event.*;
import java.net.*;
import java.util.*;
/**
 * TODO: comment class  
 * @author daniel
 **/

public class NetconfigGUI extends Dialog{

	private ArrayList list;
    private int maxPacketSize;
    private double pendingPacketDelta;
    
    /**
     * 
     * Constructor for class <code>NetconfigGUI</code>
     */
    public NetconfigGUI() {
        this(40000,60); 
    }
    
    /**
     * 
     * Constructor for class <code>NetconfigGUI</code>
     * @param maxPacketSize
     */
    public NetconfigGUI(int maxPacketSize) {
        this(maxPacketSize,60);
    }
    
    /**
     * 
     * Constructor for class <code>NetconfigGUI</code>
     * @param maxPacketSize
     * @param pendingPacketDelta
     */
    public NetconfigGUI(int maxPacketSize, double pendingPacketDelta) {
        super(new Frame());
        list=new ArrayList();
        initDialog();
        this.maxPacketSize=maxPacketSize;
        this.pendingPacketDelta=pendingPacketDelta;
        
    }
    
	private TextField receivePortText = null;
	private Label ReceivePort = null;
	private Label sendPort = null;
	private TextField sendPortText = null;
	private Label interfaceSelect = null;
	private Choice selectInterfaceChoise = null;  //  @jve:decl-index=0:visual-constraint="143,1"
	private Button button = null;  //  @jve:decl-index=0:visual-constraint="47,236"
    private Label addressLabel;
	/**
	 * This method initializes dialog	
	 * 	
	 * @return java.awt.Dialog	
	 */    
	private void initDialog() {
		
			interfaceSelect = new Label();
			sendPort = new Label();
			GridLayout gridLayout1 = new GridLayout();
			ReceivePort = new Label();
			this.setLayout(gridLayout1);
			this.setSize(240, 180);
			ReceivePort.setText("Receive Port");
			sendPort.setText("Send Port");
			interfaceSelect.setText("Select Interface");
			
			this.add(sendPort, java.awt.BorderLayout.SOUTH);
			this.add(getSendPortText(), null);
			
			this.add(ReceivePort, java.awt.BorderLayout.WEST);
			this.add(getReceivePortText(), java.awt.BorderLayout.CENTER);
			addressLabel=new Label();
			this.add(interfaceSelect, null);
			this.add(getSelectInterfaceChoise(), null);
			gridLayout1.setRows(5);
			gridLayout1.setColumns(2);
			this.addWindowListener(new WindowListener() {
                public void windowActivated(WindowEvent e) {
                    // TODO Auto-generated method stub

                }

                public void windowClosed(WindowEvent e) {
                    synchronized(NetconfigGUI.this){
                        
                        NetconfigGUI.this.notify();
                        //NetconfigGUI.this.dispose();
                    }

                }

                public void windowClosing(WindowEvent e) {
                    // TODO Auto-generated method stub

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
			this.add(new Label("IP Address"));
			this.add(addressLabel);
			this.add(getButton());
		
		
	}
	/**
	 * This method initializes textField	
	 * 	
	 * @return java.awt.TextField	
	 */    
	private TextField getReceivePortText() {
		if (receivePortText == null) {
			receivePortText = new TextField("4242");
		}
		return receivePortText;
	}
	/**
	 * This method initializes textField1	
	 * 	
	 * @return java.awt.TextField	
	 */    
	private TextField getSendPortText() {
		if (sendPortText == null) {
			sendPortText = new TextField("4343");
		}
		return sendPortText;
	}
	public NetworkConfiguration open(){
	    show();
	    try {
	        synchronized(this){
	            wait();
	        }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return new NetworkConfiguration(Integer.parseInt(receivePortText.getText()),
	            					Integer.parseInt(sendPortText.getText()),
                                    getInterfaceAddress(),
                                    maxPacketSize,pendingPacketDelta);
	            					//(InetAddress)((NetworkInterface)list.get(selectInterfaceChoise.getSelectedIndex())).getInetAddresses().nextElement());
	}
	/**
	 * This method initializes choice	
	 * 	
	 * @return java.awt.Choice	
	 */    
	private Choice getSelectInterfaceChoise() {
		if (selectInterfaceChoise == null) {
			selectInterfaceChoise = new Choice();
		}
		Enumeration enumeration=null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while(enumeration.hasMoreElements()){
		    NetworkInterface netInterface=(NetworkInterface)enumeration.nextElement();
		    list.add(netInterface);
		    selectInterfaceChoise.add(netInterface.getName());
		}
        selectInterfaceChoise.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                addressLabel.setText(getInterfaceAddress().toString());;

            }
        });
        addressLabel.setText(getInterfaceAddress().toString());
		return selectInterfaceChoise;
	}
	/**
     * TODO: comment method 
     * 
     */
    private InetAddress getInterfaceAddress() {
        NetworkInterface nif=(NetworkInterface)list.get(selectInterfaceChoise.getSelectedIndex());
        Enumeration it = nif.getInetAddresses();
        InetAddress element=null;
        while (it.hasMoreElements()){
            element=(InetAddress)it.nextElement();
        }
        return  element;
        
    }
    /**
	 * This method initializes button	
	 * 	
	 * @return java.awt.Button	
	 */    
	private Button getButton() {
		if (button == null) {
			button = new Button("OK");
		}
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(NetconfigGUI.this){
                    NetconfigGUI.this.dispose();
                    NetconfigGUI.this.notify();
                }

            }
        });
		return button;
	}
      }
