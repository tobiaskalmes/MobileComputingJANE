package de.uni_trier.jane.sgui;

/**
 * an interface to the frame of visualized simulation 
 * @author Klaus Sausen
 */
public interface VisualizationFrame {
	public void start();
	//public void run();
	public void dispose();
	public void setVisible(boolean visibility);
	
	public IVisualization getVisualization();
	
	public class Test {
		public Test() {
			System.out.println("hello world");
		}
	}
}
