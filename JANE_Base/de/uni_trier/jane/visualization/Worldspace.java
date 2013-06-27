package de.uni_trier.jane.visualization;

import java.awt.Image;

import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.PositionBase;
import de.uni_trier.jane.basetypes.PositionIterator;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.Face;

/** provide the drawing routines, transformations and projection functions 2d->3d 
 * @author Klaus Sausen
 **/
public abstract class Worldspace {

	/**
	 * the (2d) canvas that is used to visualize things 
	 */
	protected Canvas canvas;
	
	/**
	 * transformation
	 */
	protected Matrix transformation = Matrix.identity3d();
	
	/**
	 * construct a Worldspace object
	 * @param canvas the canvas to render to
	 */
	public Worldspace(Canvas canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * draw a line
	 * @param from
	 * @param to
	 * @param color
	 */
	abstract public void drawLine(PositionBase from, PositionBase to, Color color);
    
    abstract public void drawLine(PositionBase from, PositionBase to, Color color,int width);
	
	/**
	 * draw a face
	 * @param face
	 * @param points
	 * @param color
	 */
	abstract public void drawFace(Face face, PositionBase[] points, Color color);
	
	/**
	 * draw a rectangle in the X/Y plane
	 * @param center its midpoint 
	 * @param width its width 
	 * @param height its height
	 * @param color its color
	 * @param filled 
	 * @param lineWidth TODO
	 */
	abstract public void drawXYRectangle(PositionBase center, double width, double height, Color color, boolean filled, float lineWidth);
	
	/**
	 * draw an ellipse in the X/Y plane
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled 
	 */
	abstract public void drawXYEllipse(PositionBase center, double r0, double r1, Color color, boolean filled);

	/**
	 * draw an ellipse in the X/Z plane
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled 
	 */
	abstract public void drawXZEllipse(PositionBase center, double r0, double r1, Color color, boolean filled);

	/**
	 * draw an ellipse in the Y/Z plane
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled 
	 */
	abstract public void drawYZEllipse(PositionBase center, double r0, double r1, Color color, boolean filled);
	
	
	abstract public void drawEllipse(Rectangle rectangle, Color color, boolean filled);

	abstract public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled);
	
	/**
	 * draw a text to the specified position
	 * @param text
	 * @param rectangle
	 * @param color
	 */
	abstract public void drawText(String text, Rectangle rectangle, Color color);

	/**
	 * draw a (2d) cross 
	 * @param center
	 * @param size
	 * @param color
	 * @param diagonal
	 */
	abstract public void drawCross(PositionBase center, double size, Color color, boolean diagonal);

	/**
	 * draw a  cross in the z-plane 
	 * @param center
	 * @param size
	 * @param color
	 * @param diagonal
	 */
	abstract public void drawXYCross(PositionBase center, double size, Color color, boolean diagonal);
	
	
	/**
	 * draw an IconImage somewhere
	 * @param image the image
	 * @param position
	 */
	abstract public void drawImage(Image image, PositionBase position);
	
	/**
	 * draws a regular tetrahedron
	 * @param center
	 * @param radius
	 * @param color
	 */

	abstract public void drawTetrahedron(PositionBase center, double radius, Color color);
	/**
	 * draw a cubic object 
	 * @param corner top left front corner of the cube
	 * @param edgelen length of its edges
	 * @param color the color
	 */
	abstract public void drawCube(PositionBase center, double edgelen, Color color);

	/**
	 * draw a rectangular box 
	 * @param center flux space vector	
	 * @param width the with of the box
	 * @param height the height
	 * @param depth the z-extent (name conventions slightly staggered
	 * @param filled 
	 */
	abstract public void drawRectangularBox(PositionBase center, double width, double height, double depth, Color color, boolean filled);
	
	/**
	 * draw a diamond object 
	 * @param corner top left front corner of the cube
	 * @param edgelen length of its edges
	 * @param color the color
	 */
	abstract public void drawDiamond(PositionBase center, double edgelen, Color color);
	
	
	/**
	 * draws a sphere shape
	 * @param center
	 * @param radius
	 * @param color
	 */
	abstract public void drawSphere(PositionBase center, double radius, Color color);
	
	/**
	 * set the world transformation transformation 
	 * @param transformation
	 */
	public void setTransformation(Matrix transformation) {
		this.transformation = transformation;
	}

	/**
	 * get the currently active transformation matrix  
	 * @return transformation matrix
	 */
	public Matrix getTransformation() {
		return transformation;
	}

	/**
	 * @return the canvas set in the constructor
	 */
	public Canvas getCanvas() {
		return canvas;
	}
	
}
