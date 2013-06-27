/*****************************************************************************
 * 
 * ScenarioGenerator.java
 * 
 * $Id: ScenarioGenerator.java,v 1.1 2007/06/25 07:25:25 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.tools.scenarioGenerator;

import java.awt.event.*;
import java.awt.*;
import java.io.*;



import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;






public class ScenarioGenerator extends JFrame {
    
    protected final static FileFilter XML_FILTER = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.toString().toLowerCase().endsWith(".xml");
        }
        public String getDescription() { return "XML File (*.xml)"; }                   
    };


    protected static final int POINT_THICKNESS = 5;
    

    private JPanel jContentPane = null;

    private JMenuBar jJMenuBar = null;

    private JMenu fileMenu = null;

    private JMenuItem exitMenuItem = null;

    private JMenuItem saveMenuItem = null;

    private JToolBar jJToolBarBar = null;

    private JTextField jTextField = null;

    private JTextPane jTextPane = null;

    private JPanel jPanel = null;
    private Set devices;
    private int sendingRange=100;

    private JFileChooser fileChooser;


    protected File file;


    private JMenuItem saveAsMenuItem;


    private JMenuItem newMenuItem;

    /**
     * This is the default constructor
     */
    public ScenarioGenerator() {
        super();
        devices=new HashSet();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(getJJMenuBar());
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("Application");
        intFileChooser();
    }

    private void intFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(XML_FILTER);
        
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJJToolBarBar(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jJMenuBar	
     * 	
     * @return javax.swing.JMenuBar	
     */
    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
        }
        return jJMenuBar;
    }

    /**
     * This method initializes jMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getNewMenuItem());
            fileMenu.add(getSaveMenuItem());
            fileMenu.add(getSaveAsMenuItem());
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    private JMenuItem getNewMenuItem() {
        if (newMenuItem == null) {
            newMenuItem = new JMenuItem();
            newMenuItem.setText("New");
            newMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    devices.clear();
                    jPanel.repaint();
                }
            });
        }
        return newMenuItem;
    }

    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText("Exit");
            exitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return exitMenuItem;
    }
    
    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem();
            saveMenuItem.setText("Save");
            saveMenuItem.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    saveFile();
                }
            
            });
            saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                    Event.CTRL_MASK, true));
        }
        return saveMenuItem;
    }

    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getSaveAsMenuItem() {
        if (saveAsMenuItem == null) {
            saveAsMenuItem = new JMenuItem();
            saveAsMenuItem.setText("Save As");
            
            saveAsMenuItem.addActionListener(new ActionListener() {
            
                public void actionPerformed(ActionEvent e) {
                    file=null;
                    saveFile();
                }
            
            });

        }
        return saveAsMenuItem;
    }

 
    /**
     * This method initializes jJToolBarBar	
     * 	
     * @return javax.swing.JToolBar	
     */
    private JToolBar getJJToolBarBar() {
        if (jJToolBarBar == null) {
            jJToolBarBar = new JToolBar();
            
            jJToolBarBar.add(getJTextPane());
            jJToolBarBar.add(getJTextField());
        }
        return jJToolBarBar;
    }

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new JTextField();
            jTextField.setText(""+sendingRange);
            jTextField.addActionListener(new java.awt.event.ActionListener() {


                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        sendingRange=Integer.parseInt(jTextField.getText());
                        jPanel.repaint();
                        
                    }catch (Exception ex) {
                        jTextField.setText(""+sendingRange);
                    }
                    
                }
            });
        }
        return jTextField;
    }

    /**
     * This method initializes jTextPane	
     * 	
     * @return javax.swing.JTextPane	
     */
    private JTextPane getJTextPane() {
        if (jTextPane == null) {
            jTextPane = new JTextPane();
            jTextPane.setText("SendingRadius");
        }
        return jTextPane;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel(){
                public void paint(Graphics g) {
                 
                    super.paint(g);
                    
                    // Adrian, 20.10.2006, java 1.3
                    //for( Point point : devices )
                    for( Iterator it=devices.iterator(); it.hasNext(); )
                    {
                    	Point point = (Point) it.next();
                    	
                        g.fillOval(point.x-POINT_THICKNESS,point.y-POINT_THICKNESS,POINT_THICKNESS*2,POINT_THICKNESS*2);
                        
                        g.drawOval(point.x-sendingRange,point.y-sendingRange,sendingRange*2,sendingRange*2);
                        
                        //for (Point other : devices)
                        for( Iterator itt=devices.iterator(); it.hasNext(); )
                        {
                        	Point other = (Point) itt.next();
                        	
                            if ( point.distance(other) < sendingRange && !other.equals(point) )
                            {
                                g.drawLine(point.x,point.y,other.x,other.y);
                            }
                        }
                        
                    }
                }
            };
        }
        jPanel.addMouseListener(new MouseListener(){
        
            

            public void mouseExited(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        
            public void mouseEntered(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        
            public void mouseReleased(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        
            public void mousePressed(MouseEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getButton()==MouseEvent.BUTTON3){
                    Iterator iterator = devices.iterator();
                    while (iterator.hasNext()){
                        Point point= (Point) iterator.next();
                        if (point.distance(arg0.getPoint())<=POINT_THICKNESS){
                            iterator.remove();
                        }
                    }
                    jPanel.repaint();
                }else{
                    devices.add(arg0.getPoint());
                    jPanel.repaint();
                }
                
            }
        
        });
        return jPanel;
    }

    /**
     * TODO Comment method
     */
    protected void saveFile() {
        if (file==null){
            fileChooser.showSaveDialog(ScenarioGenerator.this);
        
            file=fileChooser.getSelectedFile();
            if (file==null) return;
        }
        
           try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        PrintWriter pw=null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }
        int id=0;
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<!DOCTYPE fixed_nodes SYSTEM \"fixed_nodes.dtd\">");
        pw.println("<fixed_nodes>");
        // Adrian, 20.10.2006, java 1.3
        //for  (Point point : devices)
        Iterator iterator = devices.iterator();
        while( iterator.hasNext() )
        {
        	Point point = (Point) iterator.next();
        	
            pw.println("<node>");
            pw.println("    <address>"+id++ +"</address>");
            pw.println("    <position><x>"+point.x+"</x><y>"+point.y+"</y></position>");
            pw.println("    <sending_radius>"+sendingRange+"</sending_radius>");
            pw.println("</node>");
            
        }
        pw.println("</fixed_nodes>");
        pw.close();
    }

    /**
     * Launches this application
     */
    public static void main(String[] args) {
        ScenarioGenerator application = new ScenarioGenerator();
        application.show();
    }

}
