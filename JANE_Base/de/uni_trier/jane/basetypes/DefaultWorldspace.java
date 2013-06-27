package de.uni_trier.jane.basetypes;

import java.awt.Image;

import javax.swing.ImageIcon;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.Canvas;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * implementation of a default worldspace <-> canvas indirection layer
 * @author Klaus Sausen
 */
public class DefaultWorldspace extends Worldspace {

	public DefaultWorldspace(Canvas canvas) {
		super(canvas);

		//allocate stuff on heap
		initBasicObjects();
	}
    
    public void drawLine(PositionBase from, PositionBase to, Color color) {
        drawLine(from,to,color,1);
    }
	public void drawLine(PositionBase from, PositionBase to, Color color,int width) {
		Matrix matrix = this.getTransformation();
		MutablePosition f,t;
		(f=mutable_points[0]).set(from).transform(matrix);
		(t=mutable_points[1]).set(to).transform(matrix);
		canvas.drawLine(f,t,color, width);
	}

	/**
	 * draw a face of not-yet transformed points
	 * @param face
	 * @param points 
	 * @param color
	 * do not use this method to draw objects consisting of several faces
	 * as the points are likely to be transformed more than once (sub-optimal)
	 */
	public void drawSingleFace(Face face, PositionBase[] points, Color color) {
		Matrix matrix = this.getTransformation();
		MutablePosition a,b,c;
		(a=mutable_points[0]).set(points[face.getIndex(0)]).transform(matrix);
		(b=mutable_points[1]).set(points[face.getIndex(1)]).transform(matrix);
		(c=mutable_points[2]).set(points[face.getIndex(2)]).transform(matrix);
		canvas.drawLine(a,b,color, 1);
		canvas.drawLine(b,c,color, 1);
		canvas.drawLine(c,a,color, 1);
	}
	
	/**
	 * 
	 * @param face
	 * @param points
	 * @param color
	 */
	public void drawFace(Face face, PositionBase[] points, Color color) {
		//TODO
		this.drawSingleFace(face,points,color);
	}
	
	/**
	 * draw several faces 
	 * yet to be optimized
	 * @param face
	 * @param points
	 * @param color
	 */
	public void drawFaces(Face[] face, PositionBase[] points, Color color) {
		for (int a=0;a<face.length;a++)
			this.drawFace(face[a], points, color);
	}
	
	/**
	 * draw a rectangle, heap-optimized (no new operator)
	 * @param center its midpoint 
	 * @param width its width 
	 * @param height its height
	 * @param color its color
	 * @param filled is ignored in this implementation
	 */
	public void drawXYRectangle(PositionBase center, double width, double height, Color color, boolean filled, float lineWidth) {
		Matrix matrix = this.getTransformation();
		MutablePosition p0,p1,p2,p3,p4,p5;
		//this uses the middle of the rectangle as initial position
		double widh = 0.5*width;
		double high = 0.5*height;
		(p0=mutable_points[0]).set(center);
		(p1=mutable_points[1]).set(center);
		(p2=mutable_points[2]).set(center);
		(p3=mutable_points[3]).set(center);

		(p4=mutable_points[4]).set(widh,high);
		(p5=mutable_points[5]).set(-widh,high);
		
		p0.sub(p4);
		p1.sub(p5);
		p2.add(p4);
		p3.add(p5);

		p0.transform(matrix);
		p1.transform(matrix);
		p2.transform(matrix);
		p3.transform(matrix);
		
		canvas.drawLine(p0, p1, color, lineWidth);
		canvas.drawLine(p1, p2, color, lineWidth);
		canvas.drawLine(p2, p3, color, lineWidth);
		canvas.drawLine(p3, p0, color, lineWidth);
	}

	/**
	 * draw an approximated ellipse heap-optimized (no new operator)
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled ignored in this implememtation
	 */
	public void drawXYEllipse(final PositionBase center, final double r0, final double r1, Color color, boolean filled) {
//		final Matrix matrix = this.getTransformation();
		
//		MutablePosition last;
//		MutablePosition current;
//
//		last = mutable_points[NUMELLIPSEPOINTS-1];
//		last.set(circlePoints[NUMELLIPSEPOINTS-1]);
//		last
//			.mul(r0, r1)
//			.add(center)	
//			.transform(matrix);

/*		MutablePosition ctr   = mutable_points[0];
		MutablePosition r0Vec = mutable_points[1];
		MutablePosition r1Vec = mutable_points[2];
		MutablePosition r2Vec = mutable_points[3];
		
		Matrix matrix = getTransformation();
		
		ctr.set(center)
			.transform(matrix);
		
		//since radii are relative to center
		matrix = Matrix.dropTranslation(matrix);
		
		r0Vec.set(1.0,0.0,0.0)
			.transform(matrix);
		r1Vec.set(0.0,1.0,0.0)
			.transform(matrix);
		
		//r0 *= r0Vec.getX();//r0Vec.length();//Math.sqrt(r0Vec.getX()*r0Vec.getX() + r0Vec.getY()*r0Vec.getY());
		//r1 *= r1Vec.getY();//r1Vec.length();//Math.sqrt(r1Vec.getX()*r1Vec.getX() + r1Vec.getY()*r1Vec.getY());
		
		r0 *= Math.sqrt(r0Vec.getX()*r0Vec.getX() + r0Vec.getY()*r0Vec.getY());//
		r1 *= Math.sqrt(r1Vec.getX()*r1Vec.getX() + r1Vec.getY()*r1Vec.getY());//

		//r0 *= r0Vec.length();
		//r1 *= r1Vec.length();
		//r0 *= Math.max(r0Vec.length(), r1Vec.length());
		//r1 *= Math.max(r0Vec.length(), r1Vec.length());
		
		Rectangle rect = new Rectangle(
			ctr.getX()-r0, ctr.getY()-r1,
			ctr.getX()+r0, ctr.getY()+r1);
		
		canvas.drawEllipse(rect,color,filled);
*/	
		

		canvas.drawPolygon(new PositionIterator(){
		    private int a=0;
            public Position next() {
                MutablePosition current=mutable_points[a];
                current.set(circlePoints[a]);
                current
                    .mul(r0,r1)
                    .add(center)    
                    .transform(getTransformation());
                a++;
                return current.getPosition();
            }
        
            public boolean hasNext() {
                
                return a<NUMELLIPSEPOINTS;
            }
        
        },color,filled);
        
        
        
//		for (int a=0;a<NUMELLIPSEPOINTS;a++) {
//			current = mutable_points[a];
//			current.set(circlePoints[a]);
//			current
//				.mul(r0,r1)
//				.add(center)	
//				.transform(matrix);
//			
//			canvas.drawLine(last, current, color);
//			last = current;
//		}
	}
	
	/**
	 * draw an approximated ellipse heap-optimized (no new operator)
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled ignored in this implememtation
	 */
	public void drawXZEllipse(PositionBase center, double r0, double r1, Color color, boolean filled) {
		Matrix matrix = this.getTransformation();
		
		MutablePosition last;
		MutablePosition current;

		last = mutable_points[NUMELLIPSEPOINTS-1];
		last.set(circleXZPoints[NUMELLIPSEPOINTS-1]);
		last
			.mulXZ(r0, r1)
			.add(center)	
			.transform(matrix);

		for (int a=0;a<NUMELLIPSEPOINTS;a++) {
			current = mutable_points[a];
			current.set(circleXZPoints[a]);
			current
				.mulXZ(r0,r1)
				.add(center)	
				.transform(matrix);
			
			canvas.drawLine(last, current, color, 1);
			last = current;
		}
	}

	/**
	 * draw an approximated ellipse heap-optimized (no new operator)
	 * @param center its center
	 * @param r0 its radius (height)
	 * @param r1 its radius (width)
	 * @param color its color
	 * @param filled ignored in this implememtation
	 */
	public void drawYZEllipse(PositionBase center, double r0, double r1, Color color, boolean filled) {
		Matrix matrix = this.getTransformation();
		
		MutablePosition last;
		MutablePosition current;

		last = mutable_points[NUMELLIPSEPOINTS-1];
		last.set(circleYZPoints[NUMELLIPSEPOINTS-1]);
		last
			.mulYZ(r0, r1)
			.add(center)	
			.transform(matrix);

		for (int a=0;a<NUMELLIPSEPOINTS;a++) {
			current = mutable_points[a];
			current.set(circleYZPoints[a]);
			current
				.mulYZ(r0,r1)
				.add(center)	
				.transform(matrix);
			
			canvas.drawLine(last, current, color, 1);
			last = current;
		}
	}
	
	public void drawPolygon(PositionIterator positionIterator, Color color,
			boolean filled) {
		canvas.drawPolygon(
				new MatrixPositionIterator(this.getTransformation(), positionIterator)
				, color, filled);
	}

	private static class MatrixPositionIterator implements PositionIterator {
		private Matrix matrix;
		private PositionIterator positionIterator;
		public MatrixPositionIterator(Matrix matrix, PositionIterator positionIterator) {
			this.matrix = matrix;
			this.positionIterator = positionIterator;
		}
		public boolean hasNext() {
			return positionIterator.hasNext();
		}
		public Position next() {
			return positionIterator.next().transform(matrix);
		}
	}


	public void drawCross(PositionBase center, double size, Color color, boolean diagonal) {
		Matrix matrix = this.getTransformation();
		Position[] p =
			diagonal?diagonalCrossPoints:crossPoints;

		(mutable_points[4])
			.set(center)
			.transform(matrix);

		for (int x=0;x<4;x++) {
			(mutable_points[x])
			.set(p[x])
			.scale(size)
			.add(mutable_points[4]);
		}
		canvas.drawLine(mutable_points[0], mutable_points[1], color, 1);
		canvas.drawLine(mutable_points[2], mutable_points[3], color, 1);
	}
	
	/**
	 * draw a {diagonal,regular} cross shape in the z-plane
	 * @param center center position
	 * @param size size of the cross
	 * @param color color of the cross
	 * @param diagonal 
	 */
	public void drawXYCross(PositionBase center, double size, Color color, boolean diagonal) {
		Matrix matrix = this.getTransformation();
		Position[] p =
			diagonal?diagonalCrossPoints:crossPoints;
		for (int x=0;x<4;x++) {
			(mutable_points[x])
			.set(p[x])
			.scale(size)
			.add(center)
			.transform(matrix);
		}
		canvas.drawLine(mutable_points[0], mutable_points[1], color, 1);
		canvas.drawLine(mutable_points[2], mutable_points[3], color, 1);
	}

	public void drawEllipse(Rectangle rectangle, Color color, boolean filled) {
        Position pos=rectangle.getCenter();
        //pos=pos.transform(getTransformation());
        //rectangle=new Rectangle(pos,rectangle.getExtent());
       rectangle= new Rectangle(rectangle.getBottomLeft().transform(getTransformation()),
                rectangle.getTopRight().transform(getTransformation()));
        canvas.drawEllipse(rectangle,color,filled);
	}
		
	public void drawText(String text, Rectangle rectangle, Color color) {
		Matrix matrix = this.getTransformation();

		//why on earth does drawText get a rect? :)
		Position bottomleft = rectangle.getBottomLeft().transform(matrix);
		Position topright   = rectangle.getTopRight().transform(matrix);
		
		canvas.drawText(text,new Rectangle(bottomleft, topright),color);
	}

	public void drawImage(Image image, PositionBase position) {
		Matrix matrix = this.getTransformation();
		(mutable_points[0])
			.set(position)
			.transform(matrix);
		
		canvas.drawImage(image, mutable_points[0], matrix);
	}

	/**
	 * draw a regular tetrahedron
	 * @param center
	 * @param radius
	 * @param color
	 */
	public void drawTetrahedron(PositionBase center, double radius, Color color) {
		Matrix matrix = this.getTransformation();
		MutablePosition a,b,c;
		(mutable_points[0])
			.set(tetraPoints[0])
			.scale(radius)
			.add(center)
			.transform(matrix);
		(mutable_points[1])
			.set(tetraPoints[1])
			.scale(radius)
			.add(center)
			.transform(matrix);
		(mutable_points[2])
			.set(tetraPoints[2])
			.scale(radius)
			.add(center)
			.transform(matrix);
		(mutable_points[3])
			.set(tetraPoints[3])
			.scale(radius)
			.add(center)
			.transform(matrix);

		
		for (int z=0;z<4;z++) {
			a = mutable_points[tetraFaces[z].getIndex(0)];
			b = mutable_points[tetraFaces[z].getIndex(1)];
			c = mutable_points[tetraFaces[z].getIndex(2)];
		
			canvas.drawLine(a,b,color, 1);
			canvas.drawLine(b,c,color, 1);
			canvas.drawLine(c,a,color, 1);
		}
	}
	
	public void drawCube(PositionBase center, double edgelen, Color color) {
		Matrix matrix = this.getTransformation();
		MutablePosition a,b,c,d;

		for (int z=0;z<8;z++) {
			(mutable_points[z])
				.set(cubePoints[z])
				.scale(edgelen)
				.add(center)
				.transform(matrix);
		}
		
		for (int z=0;z<6;z++) {
			a = mutable_points[cubeFaces[z].getIndex(0)];
			b = mutable_points[cubeFaces[z].getIndex(1)];
			c = mutable_points[cubeFaces[z].getIndex(2)];
			d = mutable_points[cubeFaces[z].getIndex(3)];
			canvas.drawLine(a,b,color, 1);
			canvas.drawLine(b,c,color, 1);
			canvas.drawLine(c,d,color, 1);
			canvas.drawLine(d,a,color, 1);
		}
	}

	public void drawRectangularBox(PositionBase center, double width, double height, double depth, Color color, boolean filled) {
		Matrix matrix = this.getTransformation();
		Matrix xfm = Matrix.scaling3d(new Vector4D(width,height,depth));//scale the unit cube
		MutablePosition a,b,c,d;

		for (int z=0;z<8;z++) {
			(mutable_points[z])
				.set(cubePoints[z])
				.transform(xfm)
				.add(center)
				.transform(matrix);
		}
		
		for (int z=0;z<6;z++) {
			a = mutable_points[cubeFaces[z].getIndex(0)];
			b = mutable_points[cubeFaces[z].getIndex(1)];
			c = mutable_points[cubeFaces[z].getIndex(2)];
			d = mutable_points[cubeFaces[z].getIndex(3)];
			canvas.drawLine(a,b,color, 1);
			canvas.drawLine(b,c,color, 1);
			canvas.drawLine(c,d,color, 1);
			canvas.drawLine(d,a,color, 1);
		}
	}

	
	public void drawDiamond(PositionBase center, double edgelen, Color color) {
		Matrix matrix = this.getTransformation();
		MutablePosition a,b,c;

		for (int z=0;z<6;z++) {
			(mutable_points[z])
				.set(diamondPoints[z])
				.scale(edgelen)
				.add(center)
				.transform(matrix);
		}
		
		for (int z=0;z<8;z++) {
			a = mutable_points[diamondFaces[z].getIndex(0)];
			b = mutable_points[diamondFaces[z].getIndex(1)];
			c = mutable_points[diamondFaces[z].getIndex(2)];
			canvas.drawLine(a,b,color, 1);
			canvas.drawLine(b,c,color, 1);
			canvas.drawLine(c,a,color, 1);
		}
	}
	
	
	public void drawSphere(PositionBase center, double radius, Color color) {
		drawXYEllipse(center,radius,radius,color,false);
		drawXZEllipse(center,radius,radius,color,false);
		drawYZEllipse(center,radius,radius,color,false);
	}
	
	protected void initBasicObjects() {
		for (int a=0;a<NUMELLIPSEPOINTS;a++) {
			mutable_points[a] = new MutablePosition(.0,.0,.0);
		}
	}
	
	private final static int NUMELLIPSEPOINTS=30;
	
	/** for heap-optimal visualization */
	private final static MutablePosition mutable_points[] = 
		new MutablePosition[NUMELLIPSEPOINTS];

	/** 
	 * a precalced circle (radius 1.0)
	 */
	private final static Position circlePoints[] = {
			new Position(0.0,1.0),
			new Position(0.20791169081775931,0.9781476007338057),
			new Position(0.40673664307580015,0.9135454576426009),
			new Position(0.5877852522924731,0.8090169943749475),
			new Position(0.7431448254773942,0.6691306063588582),
			new Position(0.8660254037844386,0.5000000000000001),
			new Position(0.9510565162951535,0.30901699437494745),
			new Position(0.9945218953682733,0.10452846326765368),
			new Position(0.9945218953682734,-0.10452846326765333),
			new Position(0.9510565162951536,-0.30901699437494734),
			new Position(0.8660254037844387,-0.4999999999999998),
			new Position(0.7431448254773945,-0.6691306063588579),
			new Position(0.5877852522924732,-0.8090169943749473),
			new Position(0.40673664307580043,-0.9135454576426008),
			new Position(0.20791169081775973,-0.9781476007338056),
			new Position(5.66553889764798E-16,-1.0),
			new Position(-0.20791169081775907,-0.9781476007338057),
			new Position(-0.4067366430757998,-0.9135454576426011),
			new Position(-0.587785252292473,-0.8090169943749475),
			new Position(-0.743144825477394,-0.6691306063588585),
			new Position(-0.8660254037844385,-0.5000000000000004),
			new Position(-0.9510565162951535,-0.30901699437494756),
			new Position(-0.9945218953682733,-0.10452846326765423),
			new Position(-0.9945218953682734,0.10452846326765299),
			new Position(-0.9510565162951536,0.30901699437494723),
			new Position(-0.866025403784439,0.49999999999999933),
			new Position(-0.7431448254773946,0.6691306063588578),
			new Position(-0.587785252292474,0.8090169943749468),
			new Position(-0.40673664307580093,0.9135454576426005),
			new Position(-0.20791169081775987,0.9781476007338056)		
	};

	/** 
	 * a precalced circle in X/Z plane (radius 1.0)
	 */
	private final static Position circleXZPoints[] = {
			new Position(0.0,0.0,1.0),
			new Position(0.20791169081775931,0.0,0.9781476007338057),
			new Position(0.40673664307580015,0.0,0.9135454576426009),
			new Position(0.5877852522924731,0.0,0.8090169943749475),
			new Position(0.7431448254773942,0.0,0.6691306063588582),
			new Position(0.8660254037844386,0.0,0.5000000000000001),
			new Position(0.9510565162951535,0.0,0.30901699437494745),
			new Position(0.9945218953682733,0.0,0.10452846326765368),
			new Position(0.9945218953682734,0.0,-0.10452846326765333),
			new Position(0.9510565162951536,0.0,-0.30901699437494734),
			new Position(0.8660254037844387,0.0,-0.4999999999999998),
			new Position(0.7431448254773945,0.0,-0.6691306063588579),
			new Position(0.5877852522924732,0.0,-0.8090169943749473),
			new Position(0.40673664307580043,0.0,-0.9135454576426008),
			new Position(0.20791169081775973,0.0,-0.9781476007338056),
			new Position(5.66553889764798E-16,0.0,-1.0),
			new Position(-0.20791169081775907,0.0,-0.9781476007338057),
			new Position(-0.4067366430757998,0.0,-0.9135454576426011),
			new Position(-0.587785252292473,0.0,-0.8090169943749475),
			new Position(-0.743144825477394,0.0,-0.6691306063588585),
			new Position(-0.8660254037844385,0.0,-0.5000000000000004),
			new Position(-0.9510565162951535,0.0,-0.30901699437494756),
			new Position(-0.9945218953682733,0.0,-0.10452846326765423),
			new Position(-0.9945218953682734,0.0,0.10452846326765299),
			new Position(-0.9510565162951536,0.0,0.30901699437494723),
			new Position(-0.866025403784439,0.0,0.49999999999999933),
			new Position(-0.7431448254773946,0.0,0.6691306063588578),
			new Position(-0.587785252292474,0.0,0.8090169943749468),
			new Position(-0.40673664307580093,0.0,0.9135454576426005),
			new Position(-0.20791169081775987,0.0,0.9781476007338056)		
	};

	/** 
	 * a precalced circle (radius 1.0)
	 */
	private final static Position circleYZPoints[] = {
			new Position(0.,0.0,1.0),
			new Position(0.,0.20791169081775931,0.9781476007338057),
			new Position(0.,0.40673664307580015,0.9135454576426009),
			new Position(0.,0.5877852522924731,0.8090169943749475),
			new Position(0.,0.7431448254773942,0.6691306063588582),
			new Position(0.,0.8660254037844386,0.5000000000000001),
			new Position(0.,0.9510565162951535,0.30901699437494745),
			new Position(0.,0.9945218953682733,0.10452846326765368),
			new Position(0.,0.9945218953682734,-0.10452846326765333),
			new Position(0.,0.9510565162951536,-0.30901699437494734),
			new Position(0.,0.8660254037844387,-0.4999999999999998),
			new Position(0.,0.7431448254773945,-0.6691306063588579),
			new Position(0.,0.5877852522924732,-0.8090169943749473),
			new Position(0.,0.40673664307580043,-0.9135454576426008),
			new Position(0.,0.20791169081775973,-0.9781476007338056),
			new Position(0.,5.66553889764798E-16,-1.0),
			new Position(0.,-0.20791169081775907,-0.9781476007338057),
			new Position(0.,-0.4067366430757998,-0.9135454576426011),
			new Position(0.,-0.587785252292473,-0.8090169943749475),
			new Position(0.,-0.743144825477394,-0.6691306063588585),
			new Position(0.,-0.8660254037844385,-0.5000000000000004),
			new Position(0.,-0.9510565162951535,-0.30901699437494756),
			new Position(0.,-0.9945218953682733,-0.10452846326765423),
			new Position(0.,-0.9945218953682734,0.10452846326765299),
			new Position(0.,-0.9510565162951536,0.30901699437494723),
			new Position(0.,-0.866025403784439,0.49999999999999933),
			new Position(0.,-0.7431448254773946,0.6691306063588578),
			new Position(0.,-0.587785252292474,0.8090169943749468),
			new Position(0.,-0.40673664307580093,0.9135454576426005),
			new Position(0.,-0.20791169081775987,0.9781476007338056)		
	};
	

	/**
	 * a precalced diamond (radius 1.0)
	 */
	private final static Position diamondPoints[] = {
		new Position(-.5, .5, .0),
		new Position( .5, .5, .0),
		new Position( .5,-.5, .0),
		new Position(-.5,-.5, .0),
		new Position( .0, .0, .5),
		new Position( .0, .0,-.5)
	};
	
	private final static Face diamondFaces[] = {
		new Face(0,1,4),
		new Face(1,2,4),
		new Face(2,3,4),
		new Face(3,4,0),
		new Face(5,2,1),
		new Face(5,3,2),
		new Face(5,0,3),
		new Face(5,1,0)
	};

	private final static Position crossPoints[] = {
		new Position(-.5, .0, .0),
		new Position( .5, .0, .0),
		new Position( .0,-.5, .0),
		new Position( .0, .5, .0)
	};
	
	private final static Position diagonalCrossPoints[] = {
		new Position(diamondPoints[3]),
		new Position(diamondPoints[1]),
		new Position(diamondPoints[2]),
		new Position(diamondPoints[0])
	};
	
	/**
	 * the points of a regular tetrahedron
	 */
	private final static Position tetraPoints[] = {
			new Position(-.5,-Math.sqrt(3./2.)/6., Math.sqrt(3.)/6.),
			new Position( .5,-Math.sqrt(3./2.)/6.,-Math.sqrt(3.)/6.),
			new Position( .0,-Math.sqrt(3./2.)/6., Math.sqrt(3.)/3.),
			new Position( .0, Math.sqrt(2./3.)
							 -Math.sqrt(3./2.)/6.,.0)
	};
	
	private final static Face tetraFaces[] = {
			new Face(0,1,2),
			new Face(1,0,3),
			new Face(2,3,0),
			new Face(3,2,1)
	};
	
	private final static Position cubePoints[] = {
			new Position( .5, .5, .5),
			new Position( .5, .5,-.5),
			new Position( .5,-.5,-.5),
			new Position( .5,-.5, .5),
			new Position(-.5, .5, .5),
			new Position(-.5, .5,-.5),
			new Position(-.5,-.5,-.5),
			new Position(-.5,-.5, .5)
	};
	
	private final static Face cubeFaces[] = {
		new Face(0,1,2,3),
		new Face(2,6,7,3),
		new Face(0,3,7,4),
		new Face(1,5,6,2),
		new Face(0,4,5,1),
		new Face(4,7,6,5)
	};
	
	//helper to make circle final static 
	/*points = new Position[NUMPOINTS];
	double fracdt = 1./(double)NUMPOINTS;
	double frac = .0;
	System.out.println("----------------------------");
	for (int a=0;a<NUMPOINTS;a++) {
		points[a]=new Position(
				0.0 + 1.0 * Math.sin(2.0*Math.PI*frac),
				0.0 + 1.0 * Math.cos(2.0*Math.PI*frac)
		);
		frac += fracdt;
		System.out.println("new Position"+points[a]+",");
	}*/

}
