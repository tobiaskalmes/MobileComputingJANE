package de.uni_trier.jane.simulation.visualization;

import java.awt.Image;
import java.io.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;



/**
 * @author Roland Schwarz
 * This Class is for transforming the Visualizationshapes of the 
 * Simulation into PostScript-Shapes. 
 * 
 */
public class PostScript2DRenderCanvas implements Canvas {
	
	private String psOutFileName = "";
	private java.awt.Color BGColor ;
	private java.awt.Rectangle boundingbox;	
	private Matrix transformation = Matrix.identity3d();
	
	private final String title;
	
	//private String PSCommands_workingSet = "";
	private StringBuffer PSCommands_workingSet = new StringBuffer();
	private String PSContent = "" ;

	private String temp_cmd = "";

	// PS-Header
		
	private final String PS_h_Version 	= "%!PS-Adobe-2.1";
	private final String PS_h_Title		= "%%Title: ";
	private final String PS_h_Creator 	= "%%Creator: ";
	private final String PS_h_BoundingBox	= "%%BoundingBox: ";
	private final String PS_h_Pages 		= "%%Pages: ";
	private final String PS_h_EndComment	= "%%EndComments\n";
	private final String PS_t_Trailer		= "showpage\n";

	//EPS modif
	private final String EPS_h_Version = "%!PS-Adobe-3.0 EPSF-3.0";
	
	/**
	 * Constructor
	 * 
	 * 
	 **/

	private int width  = 0;
	private int height = 0; 
	
	public void setWidth(int w) {
		this.width = w;
	}
	
	public void setHeight(int h) {
		this.height = h;
	}
	
	
	public PostScript2DRenderCanvas(String FName, String title, java.awt.Color BGColor, java.awt.Rectangle bounds) {
		this.psOutFileName = FName;
		this.BGColor = BGColor;
		this.boundingbox = bounds;
		this.title = title;
	}

	
	public PostScript2DRenderCanvas(String FName, String title, boolean saveAs) {
		this.psOutFileName = FName;
		this.title = title;
		this.saveAs = saveAs;
		System.out.println("Rendering to PS/EPS...");
	}
	
	private boolean saveAs = SAVE_PS;
	public final static boolean SAVE_EPS = true;
	public final static boolean SAVE_PS = false;

	public static String colorConvert(Color color) {
		final double reciprocal = 1.0/256.0;
		
		return 
		(reciprocal*(double)color.getRed())+" "+
		(reciprocal*(double)color.getGreen())+" "+
		(reciprocal*(double)color.getBlue())+
		" setrgbcolor ";
	}
	
	public static void moveTo(StringBuffer sb, PositionBase position) {
		sb.append(position.getX()+" "+position.getY()+" moveto\n");
	}
	
	public static void lineTo(StringBuffer sb, PositionBase position) {
		sb.append(position.getX()+" "+position.getY()+" lineto\n");
	}
	
	public int writePSFile() {
		int opResult = 0;
		// generate PS-Header 
		if (saveAs == SAVE_PS) {	//postscript 
			PSContent = PSContent + PS_h_Version + '\n';
			PSContent = PSContent + PS_h_Title + title +'\n';
			PSContent = PSContent + PS_h_Creator + System.getProperty("user.name") + '\n';
//			PSContent = PSContent + PS_h_BoundingBox  + boundingbox.getX() + " " + boundingbox.getY() + " " + (boundingbox.getX()+boundingbox.getWidth()) + " " + (boundingbox.getY()+boundingbox.getHeight()) + " \n";
//			PSContent = PSContent + PS_h_Pages + " 1\n";
			PSContent = PSContent + PS_h_EndComment + "\n";
		} else {					//encapsulated postscript
			//http://www.geocities.com/SiliconValley/5682/ps2eps.html
			PSContent = PSContent + EPS_h_Version + "\n";
			PSContent = PSContent + PS_h_Title + title +'\n';
			PSContent = PSContent + PS_h_Creator + System.getProperty("user.name") + "\n";
			PSContent = PSContent + PS_h_BoundingBox + "0 0 "+width+" "+height+ '\n';
//			PSContent = PSContent + PS_h_BoundingBox  + boundingbox.getX() + " " + boundingbox.getY() + " " + (boundingbox.getX()+boundingbox.getWidth()) + " " + (boundingbox.getY()+boundingbox.getHeight()) + " \n";
//			PSContent = PSContent + PS_h_Pages + " 1\n";
			PSContent = PSContent + PS_h_EndComment + "\n";
		}

		temp_cmd = "";		
		// BGColor etc ..
/**		temp_cmd = temp_cmd + BGColor.getRed()/255 + " " + BGColor.getGreen()/255 + " " + BGColor.getBlue()/255 + " setrgbcolor ";
		// konstruiere Pfad entlang der bounds mit lineto & fill 
		temp_cmd = temp_cmd + boundingbox.getX() + " " + boundingbox.getY() +" moveto ";
		// lineto x=0 , y=max
		temp_cmd = temp_cmd + (boundingbox.getX()+boundingbox.getWidth()) + " " + boundingbox.getY() + " lineto ";
		// lineto x=max, y=max
		temp_cmd = temp_cmd + (boundingbox.getX()+boundingbox.getWidth()) + " " + (boundingbox.getY()+boundingbox.getHeight()) + " lineto ";
		// lineto x=max, y=0
		temp_cmd = temp_cmd + (boundingbox.getX()+boundingbox.getWidth()) + " " + boundingbox.getY() + " lineto ";
		// lineto x=0, y=0		
		temp_cmd = temp_cmd + boundingbox.getX() + " " + boundingbox.getY() +" lineto ";
		temp_cmd = temp_cmd + "fill \n";
**/
		PSContent = PSContent + PSCommands_workingSet.toString() + "\n" + PS_t_Trailer;
		
		try {
			java.io.FileOutputStream fout = new java.io.FileOutputStream(psOutFileName);

			byte[] bout = new byte[PSContent.length()];
			for (int i=0;i<PSContent.length();i++) {
				bout[i] = (byte) PSContent.charAt(i);
			}
			fout.write(bout);
			fout.flush();
			fout.close();
			// cleanup stuff ..
			fout = null;
			bout = null;
			opResult = 0;
		}
		catch( IOException ioex) {
			System.out.println(ioex.toString())	;
			opResult = -1;
		}
		return opResult;	
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawLine(Position, Position, Color, float)
	 */
	public void drawLine(PositionBase from, PositionBase to, Color color, float lineWidth) {
		StringBuffer temp = PSCommands_workingSet;
		// object-comment ?
		// newpath
		// temp_cmd = temp_cmd + " newpath ";
		temp.append(" newpath "); 
		// moveto from		
		//temp_cmd = temp_cmd + from.getX() + " " + from.getY() + " moveto ";
		moveTo(temp, from);
		lineTo(temp, to);
		// set line color
		temp.append(colorConvert(color));
		
		// draw the line
		//temp_cmd = temp_cmd + "stroke ";
		temp.append("stroke ");
		// closepath
		//temp_cmd = temp_cmd + "closepath \n" ;
		temp.append("closepath \n");
		// fill , color settings ?
		//PSCommands_workingSet = PSCommands_workingSet + temp_cmd + "\n";
		//temp_cmd = "";
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawRectangle(Rectangle, Color, boolean, float)
	 */
	public void drawRectangle(Rectangle rectangle, Color color, boolean filled, float lineWidth) {
		StringBuffer temp = PSCommands_workingSet;
		//temp_cmd = "";
		// " schwarze" bg-füllung kommt wohl von dieser methode ..
		// newpath
		//temp_cmd = temp_cmd + " newpath ";		
		temp.append(" newpath ");		
		// moveto begin-> rectangle top-left
		moveTo(temp, rectangle.getBottomLeft());
		lineTo(temp, rectangle.getBottomRight());
		lineTo(temp, rectangle.getTopRight());
		lineTo(temp, rectangle.getTopLeft());
		lineTo(temp, rectangle.getBottomLeft());
		
		temp.append(colorConvert(color));
		temp.append(" closepath \n");
		if ( filled ) {
//			temp_cmd = temp_cmd + " fill  "; 	// fill ist hier seltsamerweise fuer komplett schwarzen
												// bg verantwortlich ... :-(
		}
		if ( !filled ) {
			//temp_cmd = temp_cmd + " stroke ";
			temp.append(" stroke");
		}
		temp.append("\n");
		//PSCommands_workingSet = PSCommands_workingSet + temp_cmd + "\n";
		//temp_cmd = "";	
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawEllipse(Rectangle, Color, boolean)
	 */
	public void drawEllipse(Rectangle rectangle, Color color, boolean filled) {
		StringBuffer temp = PSCommands_workingSet;
		// newpath hier erst nach moveto um horizontale linien in shape zu vermeiden
		//temp_cmd = "";
		Position centerPoint = new Position(rectangle.getCenter());
		temp.append(centerPoint.getX() + " " + centerPoint.getY() + " moveto \n");
		temp.append(" newpath ");
		// arc : centerx centery radius angle1 angle2 arc /// angle  0 360  radius ...
		double t_radius = (Math.abs((rectangle.getBottomLeft().getX()-rectangle.getBottomRight().getX())))/2;
		temp.append(centerPoint.getX() + " " + centerPoint.getY() + " " + t_radius + " 0 360 arc ");
		// setcolor 
		temp.append(colorConvert(color));
		// stroke || fill
		temp.append(" closepath \n");
		if ( filled ) {
			temp.append(" fill\n"); // fill
		}
		if ( !filled ) {
			temp.append(" stroke\n");
		}
		//PSCommands_workingSet = PSCommands_workingSet + temp_cmd + "\n";
		//temp_cmd = "";
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawText(String, Rectangle, Color)
	 */
	public void drawText(String text, Rectangle rectangle, Color color) {
		StringBuffer temp = PSCommands_workingSet;
		// moveto Position, setfont, set color print font
		//temp_cmd = "";
		// setfont , fontsize mal beachten und abfragen !!!!
		temp.append("/Times-Roman findfont \n " + " 15 scalefont \n setfont \n ");
		moveTo(temp,rectangle.getBottomLeft());
		//		
//		temp.append(" newpath ");
		// moveto rect. lower_left
		temp.append(colorConvert(color));
		temp.append("("+text+") show ");
//		temp.append("closepath\n");
	
		//PSCommands_workingSet = PSCommands_workingSet + temp_cmd + "\n";
		//temp_cmd = "";
	}
	
	public void drawImage(String fileName, Rectangle rectangle) {
		drawRectangle(rectangle, Color.BLACK, false, 1);
		drawText(fileName, rectangle, Color.BLACK);
		System.err.println("Warning> PostScript2DRenderCanvas does not support drawImage");
	}
	public void drawImage(Image image, PositionBase position, Matrix matrix) {
		System.err.println("Warning> PostScript2DRenderCanvas does not support drawImage");
	}

	/**
	 * draws a polygon through approximating with lines
	 * 	FIXME draw filled polygon as well.. this one simply draws lines
	 */
	public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled) {
		StringBuffer temp = PSCommands_workingSet;
		//this can be heap optimized (see DefaultWorldspace as an example)
		MutablePosition p0 = new MutablePosition(), 
						p1 = new MutablePosition();

		// object-comment ?
		// newpath
		// set line color
		temp.append(" newpath ");

		if (positionIterator.hasNext())
			p0.set(positionIterator.next());
		// moveto from		
		//temp.append(p0.getX() + " " + p0.getY() + " moveto\n");
		moveTo(temp, p0);
		while(positionIterator.hasNext()) {
			p1.set(positionIterator.next());
			// lineto to
			//temp.append(p1.getX() + " " + p1.getY() + " lineto ");
			lineTo(temp, p1);
		}
		// close poly
		//temp.append(p0.getX() + " " + p0.getY() + " lineto\n");
		lineTo(temp, p0);
		
		temp.append(colorConvert(color));
		if (filled) {
			temp.append("fill ");
		}
		temp.append("stroke ");
		// closepath
		temp.append("closepath \n");

		//PSCommands_workingSet = PSCommands_workingSet + temp_cmd + "\n";
		//temp_cmd = "";
	}

	public void setTransformation(Matrix transformation) {
		this.transformation = transformation;
	}
	public Matrix getTransformation() {
		return transformation; 
	}

	public int getVisibleWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getVisibleHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
}
