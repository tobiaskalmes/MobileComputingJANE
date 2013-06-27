package de.uni_trier.jane.sgui;

import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.Vector4D;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * provides the interface to the transformations any 
 * visualizing component should know about 
 * @author Klaus Sausen
 */
public interface IVisualization {
	
	public static final int TRANSFORM_ZOOM = 0;
	public static final int TRANSFORM_ROTATE = 1;
	public static final int TRANSFORM_TRANSLATE = 2;
	public static final int TRANSFORM_SLIDE = 3;

	public static final int VISUALIZE_NUM = 3;
	public static final int VISUALIZE_PLAN = 0;
	public static final int VISUALIZE_PROJECT_PARALLEL = 1;
	public static final int VISUALIZE_PROJECT_PERSPECTIVE = 2;

	/**
	 * switch the visualization mode
	 * VISUALIZE_PLAN VISUALIZE_PERSPECTIVE 
	 * @param vmode
	 */
	public void setVisualizationMode(int vmode);

	/**
	 * actualize the transformation matrix
	 * (e.g. dependent on mouse movement) 
	 */
	public void actualizeTransformationMatrix();
	
	/**
	 * explicitly set the transformation matrix
	 * @param transformation
	 */
	public void setTransformationMatrix(Matrix transformation);

	/**
	 * get the inverse transformation
	 * for point and click
	 * @return inverse transform
	 */
	public Matrix getInverseTransformationMatrix();

	/**
	 * set the worldspace associated with the visualization
	 * @param worldspace
	 */
	public void setWorldspace(Worldspace worldspace);
	/**
	 * get the worldspace associated with the visualization
	 * @return worldspace
	 */
	public Worldspace getWorldspace();

	/**
	 * access to the transformation parameters
	 * @return TransformationParameters instance
	 */
	public TransformationParameters getTransformationParameters();
	
	/**
	 * this is a multi-purpose shape, e.g. to visualize mouse selections
	 * @param shape
	 * @param addressPositionMap
	 */
	public void setShape(de.uni_trier.jane.visualization.shapes.Shape shape, DeviceIDPositionMap addressPositionMap);
	
	
	/** entirely enable/disable the renderer */
	public void setRendererEnabled(boolean p);
	/** check enabled state */
	public boolean isRendererEnabled();
	
	/**
	 * wraps the transformations in a clean way
	 * @author Klaus Sausen
	 */
	public class TransformationParameters {
		protected double zoom;
		protected Vector4D rotation;
        protected Vector4D simpleTranslation;
		protected Vector4D translation;

		public TransformationParameters() {
			zoom = 1.0;
			rotation = new Vector4D();
			translation = new Vector4D();
            simpleTranslation=new Vector4D();
		}

		public TransformationParameters(TransformationParameters copy) {
			zoom = copy.zoom;
			rotation = new Vector4D(copy.rotation);
			translation = new Vector4D(copy.translation);
            simpleTranslation= new Vector4D(copy.simpleTranslation);
		}
		
		/**
		 * create the transformation matrix of the given values
		 * @return the matrix
		 */
		public Matrix getTransformationMatrix() {
			Matrix matrix = Matrix.identity3d()
			.mul(
				Matrix.translation3d(translation))
			.mul(
				Matrix.rotation3d(new Vector4D(1.0,0.0,0.0), rotation.x))
			.mul(
				Matrix.rotation3d(new Vector4D(0.0,1.0,0.0), rotation.y))
			.mul(
				Matrix.rotation3d(new Vector4D(0.0,0.0,1.0), rotation.z))
			.mul(
				Matrix.scaling3d(new Vector4D(zoom,zoom,zoom,1.0)));
			/*System.out.println("rot("
					+rotation.x+","+rotation.y+","+rotation.z+") trans:("
					+translation.x+","+translation.y+","+translation.z+") zoom:"+zoom);
			*/
			return matrix;
		}

		/**
		 * create the inverse transformation matrix to the given values
		 * @return the inverse matrix
		 */
		public Matrix getInverseTransformationMatrix() {
			double invZoom = 1.0/zoom;	//inverse zoom
			Vector4D invTranslation = new Vector4D(-translation.x,-translation.y,-translation.z);
			Matrix matrix = Matrix.identity3d()
			.mul(
				Matrix.scaling3d(new Vector4D(invZoom,invZoom,invZoom,1.0)))
			.mul(
				Matrix.rotation3d(new Vector4D(0.0,0.0,1.0),-rotation.z))
			.mul(
				Matrix.rotation3d(new Vector4D(0.0,1.0,0.0),-rotation.y))
			.mul(
				Matrix.rotation3d(new Vector4D(1.0,0.0,0.0),-rotation.x))
			.mul(
				Matrix.translation3d(invTranslation));
			/*System.out.println("rot("
					+rotation.x+","+rotation.y+","+rotation.z+") trans:("
					+translation.x+","+translation.y+","+translation.z+") zoom:"+zoom);
			*/
			return matrix;
		}
	}

}
