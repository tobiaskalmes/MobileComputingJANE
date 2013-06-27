package de.uni_trier.jane.simulation.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

import sun.print.PeekGraphics;

import de.uni_trier.jane.sgui.PNGEncoder;

public class PngRenderCanvas extends DefaultGraphicsCanvas {

	private final PNGEncoder pngEncoder;
	
	private final String filename;
	
	//TODO this should be controllable from the GUI!
	
	private int width=800; 
	private int height=600;
	
	DefaultGraphicsCanvas defaultGraphicsCanvas;
	
	public PngRenderCanvas(String filename) {
		super();
		this.filename = filename;
		pngEncoder = new PNGEncoder();
	}

	public PngRenderCanvas(String filename, double scale) {
		super(scale);
		this.filename = filename;
		pngEncoder = new PNGEncoder();
	}

	
	/** the image to render into */
	private Image renderImage; 
	/** the pixeldepth (set to 8 to change to indexed mode) */
	private int pixelDepth = 32;
	
	public void beginRendering() {
		this.
		renderImage = new BufferedImage(width, height, 
				(pixelDepth == 8) ? BufferedImage.TYPE_BYTE_INDEXED :
				BufferedImage.TYPE_4BYTE_ABGR );
		if (renderImage.getGraphics() instanceof Graphics2D) {
			System.out.println("luckily we have a graphics2d canvas");
			//Graphics2D g2 = new Graphics2D(renderImage.getGraphics());
		}
		System.out.println("renderImage Graphics> "+renderImage.getGraphics());
		this.setGraphics(renderImage.getGraphics());

		renderImage.getGraphics().setColor(Color.WHITE);
		renderImage.getGraphics().fillRect(0,0,width,height);
		
		pngEncoder.setCompressionLevel(9);
		pngEncoder.setEncodeAlpha(false);
		
	}
	
	public void endRendering() {
		
	}
	
	
	public int writePngFile() {

		pngEncoder.setImage(renderImage);

		byte[] pngBytes  = pngEncoder.pngEncode();
	
		try {
		FileOutputStream fout = new FileOutputStream(filename);
		
			fout.write(pngBytes);
			fout.close();
			
		} 
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		System.out
		.println("wrote "+width+"x"+height+" png image to '"+filename+"'");
		return 0;
	}

	/** must be called before <code>beginRendering()</code> */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/** must be called before <code>beginRendering()</code> */
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
