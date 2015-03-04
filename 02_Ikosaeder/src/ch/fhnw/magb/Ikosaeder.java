package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import org.la4j.V;
import org.la4j.Vector;

import ch.fhnw.magb.GLFrame.ReshapeType;

public class Ikosaeder extends GLEventListenerAdapter {

	/**
	 * Buffer, to which the circle is drawn.
	 */
	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	/**
	 * Reference to the used GLFrame.
	 */
	private GLFrame frame;
	/**
	 * Stores the direction in which the sun is. (Directional light)
	 */
	private Vector toLight = V.v(-1, 2, 1);

	public Ikosaeder(GLFrame frame) {
		frame.setStdReshape(ReshapeType.PROPORTIONAL);
		frame.setBuffer(buffer);
		this.frame = frame;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
		frame.setCameraSystem(gl, frame.getR(), frame.getElevation(), frame.getAzimut());
	    buffer.reset();
	    buffer.setColor(0, 1, 0, 1);
	    Utility.drawIkosaeder(gl, buffer, toLight);
	}

	public static void main(String[] args) {
		//Init frame
		GLFrame frame = new GLFrame("Ikosaeder");
		frame.setR(10);
		frame.setElevation(14);
		frame.setAzimut(10);
		frame.setLeft(-3);
		frame.setRight(3);
		frame.setNear(-100);
		frame.setFar(100);
		//Add key listener
		frame.addKeyListener(new DefaultMovingKeyListener(frame));
		//Create circle and add listener
		Ikosaeder circle = new Ikosaeder(frame);
		frame.addGLEventListener(circle);
		frame.start();
	}

}
