/*
 * Created on 15.06.2005
 *
 */
package de.uni_trier.jane.sgui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import de.uni_trier.jane.basetypes.DefaultWorldspace;
import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Vector4D;
import de.uni_trier.jane.simulation.visualization.DefaultGraphicsCanvas;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author Klaus Sausen
 * The visualization of the simulation
 * @see de.un_trier.jane.sgui.VisualizationPanel#getInverseTransformation() 
 */
public class VisualizationPanel extends JPanel implements IVisualization {
	
	private Worldspace worldspace;
	private de.uni_trier.jane.visualization.shapes.Shape shape;
	private de.uni_trier.jane.basetypes.Rectangle rectangle;

	private DeviceIDPositionMap addressPositionMap;
	//private DefaultGraphicsCanvas painter;
	//private Canvas painter;
	
	private Matrix transformation = Matrix.identity3d();

	private TransformationParameters transformParmArray[];
	protected TransformationParameters transformParm;
	private TransformationParameters transformOnMouseDown;
	
	private boolean mouseIsDown = false;
	private int mouseDownStartX, mouseDownStartY;

	protected int transformationMode = TRANSFORM_ZOOM;//default to zoom;
	protected int visualizationMode = VISUALIZE_PLAN;//default plan view
	
    protected Dimension dimension;
	
	/**
	 * get reference to the transformations for matrix update
	 * @return <code>transformParm</code>
	 */
	public TransformationParameters getTransformationParameters() {
		return transformParm;
	}
	
	/**
	 * initializes the visualizationpanel with the default settings
	 * for 3d transformations and default drawing canvas
	 * @param rectangle
	 * @param shape
	 */
	public VisualizationPanel(
			de.uni_trier.jane.basetypes.Rectangle rectangle,
			de.uni_trier.jane.visualization.shapes.Shape shape) {
		this(
			new DefaultWorldspace(new DefaultGraphicsCanvas()), 
			rectangle, shape
		);
	}
	
	/**
	 * setup visualizationpanel given a specific Worldspace (that in turn
	 * references to a Canvas)
	 * @param worldspace
	 * @param rectangle
	 * @param shape
	 */
	public VisualizationPanel(
			Worldspace worldspace,
		de.uni_trier.jane.basetypes.Rectangle rectangle,
		de.uni_trier.jane.visualization.shapes.Shape shape) {

		setupTransformations();
		
		this.rectangle = rectangle;
		this.shape = shape;
		setBackground(java.awt.Color.white);

		//this.painter = worldspace.getCanvas();
		setWorldspace(worldspace);
		
		
		this.addMouseListener(new java.awt.event.MouseListener() { 
			public void mousePressed(java.awt.event.MouseEvent e) {    
				mouseIsDown = true;
				mouseDownStartX = e.getX();
				mouseDownStartY = e.getY();
				//save the transformations for delta calculations
				transformOnMouseDown = new TransformationParameters(transformParm);
			}
			public void mouseReleased(java.awt.event.MouseEvent e) {
				mouseIsDown = false;
			} 
			public void mouseClicked(java.awt.event.MouseEvent e) {} 
			public void mouseEntered(java.awt.event.MouseEvent e) {} 
			public void mouseExited(java.awt.event.MouseEvent e) {} 
		});
		
		this.addMouseMotionListener(new java.awt.event.MouseMotionListener() { 
			public void mouseDragged(java.awt.event.MouseEvent e) {    
				int dtx,dty;
				dtx = mouseDownStartX - e.getX();
				dty = mouseDownStartY - e.getY();
				doUserControlledTransform(dtx,dty);
			}
			public void mouseMoved(java.awt.event.MouseEvent e) {} 
		});
		this.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }
            public void componentShown(ComponentEvent e) {
            }
            public void componentMoved(ComponentEvent e) {
            }
            public void componentResized(ComponentEvent e) {
                dimension=getSize();
            }
        });
        dimension=getSize();
	}	

	/**
	 * set the default transformations
	 */
	private void setupTransformations() {
		transformParmArray = new TransformationParameters[VISUALIZE_NUM];
		transformParmArray[VISUALIZE_PLAN] = new TransformationParameters();
		transformParmArray[VISUALIZE_PROJECT_PARALLEL] = new TransformationParameters();
		transformParmArray[VISUALIZE_PROJECT_PERSPECTIVE] = new TransformationParameters();
		//set to default mode
		transformParm = transformParmArray[visualizationMode];
		
		transformParmArray[VISUALIZE_PROJECT_PARALLEL]
			.rotation = new Vector4D(-67.0,0.0,-13.0);
		transformParmArray[VISUALIZE_PROJECT_PARALLEL]
			.translation = new Vector4D(11.0,170.0,0.0);
		transformParmArray[VISUALIZE_PROJECT_PARALLEL]
			.zoom = 2.0;
	}
	
	/**
	 * switch the transformation mode
	 * TRANSFORM_ZOOM TRANSFORM_ROTATE TRANSFORM_TRANSLATE
	 * @param tmode
	 */
	public void setUserControlledTransformMode(int tmode) {
		transformationMode = tmode;
	}

	/**
	 * switch the visualization mode
	 * VISUALIZE_PLAN VISUALIZE_PERSPECTIVE 
	 * @param vmode
	 */
	public void setVisualizationMode(int vmode) {
		visualizationMode = vmode;
		transformParm = transformParmArray[vmode];
		this.actualizeTransformationMatrix();
	}
	
	/**
	 * modify the transformation matrix by means of the relative 
	 * mouse coordinates
	 * @param dtx
	 * @param dty
	 */
	protected void doUserControlledTransform(int dtx, int dty){// int xCenter, int yCenter) {
		double z;
		switch (visualizationMode) {
		case VISUALIZE_PLAN:
			switch (transformationMode) {
			case TRANSFORM_ZOOM:
				z = transformOnMouseDown.zoom + 0.01*dty;
                double oldZoom=transformOnMouseDown.zoom;
				transformParm.zoom = z>0.01?z:0.01;
                
				transformParm.translation.x = //transformOnMouseDown.translation.x
                    transformParm.simpleTranslation.x*transformParm.zoom
                    +dimension.getWidth()/2-(dimension.getWidth()/2)*(transformParm.zoom);
                //
                    
                transformParm.translation.y = //transformOnMouseDown.translation.y
                transformParm.simpleTranslation.y*transformParm.zoom
                +dimension.getHeight()/2-(dimension.getHeight()/2)*(transformParm.zoom);
                
                //transformParm.translation.y = transformOnMouseDown.translation.y+transformOnMouseDown.translation.y*transformParm.zoom;
                
				break;
			case TRANSFORM_ROTATE:
				transformParm.rotation.z = transformOnMouseDown.rotation.z - dtx;
				break;
			case TRANSFORM_TRANSLATE:
				transformParm.translation.x = transformOnMouseDown.translation.x - dtx;
				transformParm.translation.y = transformOnMouseDown.translation.y - dty;
                transformParm.simpleTranslation.x=transformOnMouseDown.simpleTranslation.x-dtx/transformParm.zoom;
                transformParm.simpleTranslation.y=transformOnMouseDown.simpleTranslation.y-dty/transformParm.zoom;
				break;
			default:
				System.err.println("doUserControlledTransform: undefined mode passed in setTransformationMode");
				break;
			}
			break;
		case VISUALIZE_PROJECT_PERSPECTIVE://(TODO)
		case VISUALIZE_PROJECT_PARALLEL:
			switch (transformationMode) {
			case TRANSFORM_ZOOM:
				z = transformOnMouseDown.zoom + 0.01*dty;
				transformParm.zoom = z>0.01?z:0.01;
                transformParm.translation.x = 
                    transformParm.simpleTranslation.x*transformParm.zoom;
                    //+dimension.getWidth()/2-(dimension.getWidth()/2)*(transformParm.zoom);
                    
                transformParm.translation.y = 
                transformParm.simpleTranslation.y*transformParm.zoom;
                //+dimension.getHeight()/2-(dimension.getHeight()/2)*(transformParm.zoom);
                transformParm.translation.z = 
                    transformParm.simpleTranslation.z*transformParm.zoom;
				break;
			case TRANSFORM_ROTATE:
				transformParm.rotation.z = transformOnMouseDown.rotation.z - dtx;
				transformParm.rotation.x = transformOnMouseDown.rotation.x - dty;
				break;
			case TRANSFORM_TRANSLATE:
				transformParm.translation.x = transformOnMouseDown.translation.x - dtx;
				transformParm.translation.y = transformOnMouseDown.translation.y - dty;
                transformParm.simpleTranslation.x=transformOnMouseDown.simpleTranslation.x-dtx/transformParm.zoom;
                transformParm.simpleTranslation.y=transformOnMouseDown.simpleTranslation.y-dty/transformParm.zoom;
				break;
			case TRANSFORM_SLIDE:
				transformParm.translation.x = transformOnMouseDown.translation.x - dtx;
				transformParm.translation.z = transformOnMouseDown.translation.z - dty;
                transformParm.simpleTranslation.x=transformOnMouseDown.simpleTranslation.x-dtx/transformParm.zoom;
                transformParm.simpleTranslation.z=transformOnMouseDown.simpleTranslation.z-dty/transformParm.zoom;
				break;
			default:
				System.err.println("doUserControlledTransform: undefined mode passed in setTransformationMode");
				break;
			}
			break;
		default:
			System.err.println("doUserControlledTransform: undefined mode passed in setVisualizationMode");
			break;
		}
		actualizeTransformationMatrix();
	}

	/**
	 * actualize the transformation matrix.
	 * this is called once per frame
	 */
	public void actualizeTransformationMatrix() {
		transformation = transformParm.getTransformationMatrix();
		getWorldspace().setTransformation(transformation);
		//visualizationParameters.setTransformationMatrix(transformation);
	}
	
	
	/**
	 * make the inverse transformation public to e.g. MouseActionListeners that need to 
	 * transform into worldspace. @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
	 * Caveat: the transformations need to be affine
	 * @return the inverse transformation
	 */
	public Matrix getInverseTransformationMatrix() {
		return transformParm.getInverseTransformationMatrix();
	}

	boolean rendererEnabled = true;
	
	/** (de)activate renderer - useful to speed up the simulation */
	public void setRendererEnabled(boolean p) {
		rendererEnabled = p;
	}

	/** returns wether the renderer is active or not */
	public boolean isRendererEnabled() {
		return rendererEnabled;
	}
	
	/**
	 * explicitly render the scene
	 * to the given worldspace
	 */
	public void renderScene() {
		if (isRendererEnabled()) {
			shape.visualize(
				new Position(0.0,0.0),	//new Position(0.5*getWidth(),0.5*getHeight()),
				getWorldspace(), addressPositionMap);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//painter.setGraphics((Graphics2D) g);
		if (getWorldspace().getCanvas() instanceof DefaultGraphicsCanvas) {
			((DefaultGraphicsCanvas)getWorldspace().getCanvas()).setGraphics((Graphics2D) g);
		} else {
			System.out.println("(Warning) VisualizationPanel.paintComponent: Graphics will not be set");
		}
		renderScene();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension((int) rectangle.getWidth(), (int) rectangle.getHeight());
	}

	
	public void setTransformationMatrix(Matrix transformation) {
		getWorldspace().setTransformation(this.transformation=transformation);
	}
	
	public void setShape(de.uni_trier.jane.visualization.shapes.Shape shape, DeviceIDPositionMap addressPositionMap) {
		this.shape = shape;
		this.addressPositionMap = addressPositionMap;
		repaint();
	}

	public void setWorldspace(Worldspace worldspace) {
		this.worldspace = worldspace;
	}

	public Worldspace getWorldspace() {
		return worldspace;
	}
}
