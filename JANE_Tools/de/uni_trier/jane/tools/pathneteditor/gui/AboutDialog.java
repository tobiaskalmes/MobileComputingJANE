package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	final private static String imageURL = (new File("docs/AboutBG.jpg")).getAbsolutePath();
	final private static Image image = new ImageIcon(imageURL).getImage();
	final private static String infoText = "<html>" +			
			"<p align=center><font color=white>" +
			"<font size=4>This Programm was developed by<br><br></font>" +
			"<font size=8>Jakob Weidlich<br></font>" +
			"<font size=4>( J.Weidlich@e406.net )<br><br></font>" +
			"<font size=4>and<br><br></font>" +
			"<font size=8>Steffen Salewski<br></font>" +
			"<font size=4>( SSalewski@Gmx.net )<br><br></font>" +
			"<font size=4>2005, University of Trier<br></font>" +
			"</font></p></html>";
	
	private class BGPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private Image img ;
		
		public BGPanel(Image img) {
			setLayout( new BorderLayout() ) ;
		
			this.img = img;
			
			setBackground(Color.BLACK);
			
			if( img == null ) {
				System.out.println( "Image is null" );				
			}
		
			else if( img.getHeight(this) <= 0 || img.getWidth( this ) <= 0 ) {
				System.out.println( "Image width or height must be +ve" );
				this.img = null;
			}			
			
		}
		
		public void drawBackground( Graphics g ) {
			int w = getWidth() ;
			int h = getHeight() ;
			int iw = img.getWidth( this ) ;
			int ih = img.getHeight( this ) ;
			for( int i = 0 ; i < w ; i+=iw ) {
				for( int j = 0 ; j < h ; j+= ih ) {
					g.drawImage( img , i , j , this ) ;
				}
			}
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null)
				drawBackground( g ) ;
		}		
	};
	
	public AboutDialog() {
		
		setSize(new Dimension(400,300));
		setTitle("About");
		
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((sd.width - getSize().width)/2, (sd.height - getSize().height)/2);
		
		setModal(true);
		
		init();		
	}
	
	public AboutDialog(Frame f) {
		super(f);
		
		setSize(new Dimension(400, 300));
		setTitle("About");
		
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((sd.width - getSize().width)/2, (sd.height - getSize().height)/2);
		
		setModal(true);
		
		init();
	}
	
	private void init() {
		JLabel label = new JLabel(infoText);
		label.setOpaque(false);
						
		BGPanel panel = new BGPanel(image);		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AboutDialog main = new AboutDialog();
		main.setVisible(true);
		
		main.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
	}

}
