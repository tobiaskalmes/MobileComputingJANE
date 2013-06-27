package de.uni_trier.jane.sgui;

import java.util.Vector;

import de.uni_trier.jane.simulation.visualization.Frame;

/**
 * a buffer that wraps all frames that have already been simulated
 * @author Klaus Sausen
 */
public class FrameBuffer {

	Vector frameBuffer = new Vector();

	int frameMax = 0;
	
	/**
	 * c'tor
	 */
	public FrameBuffer(int bufferSize) {
		frameBuffer = new Vector();
//		frameMax = 500;
		frameMax = bufferSize;
	}

	public void setCacheSize(int maxframes) {
		frameMax = maxframes;
	}
	
	public int getCacheSize() {
		return frameMax;
	}
	
	/**
	 * FIFO throw out any old frames
	 * TODO
	 */
	public void shrink() {
		if (frameMax>0) {
			//TODO
			while (frameBuffer.size()>frameMax) {
				frameBuffer.removeElementAt(0);
			}
		}
	}
	
	public void addFrame(Frame frame) {
		frameBuffer.add(frame);
		shrink();
	}

	public Frame getFrameAt(int idx) {
		Frame f;
        if (idx<0) idx=0;
       // if (frameBuffer.size()>=idx)
		   f = (Frame)frameBuffer.elementAt(idx);
		return f;
	}
	
	public int size() {
		return frameBuffer.size();
	}
}
