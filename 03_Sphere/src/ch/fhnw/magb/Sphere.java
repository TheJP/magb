package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import org.la4j.V;
import org.la4j.Vector;

import ch.fhnw.magb.GLFrame.ReshapeType;

public class Sphere extends GLEventListenerAdapter {
	/**
	 * Buffer, to which the circle is drawn.
	 */
	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	/**
	 * Stores the direction in which the sun is. (Directional light)
	 */
	private Vector toLight = V.v(-1, 2, 1);

	public Sphere(GLFrame frame) {
		frame.setStdReshape(ReshapeType.PROPORTIONAL);
		frame.setBuffer(buffer);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
	    buffer.reset();
	    buffer.setColor(0, 1, 0, 1);
	    Utility.drawSphere(gl, buffer, toLight, 3);
	}

	public static void main(String[] args) {
		//Init frame
		GLFrame frame = new GLFrame("Sphere");
		frame.setCamera(new Camera(10, 14, 10));
		frame.setProjection(new ProjectionCuboid(-3, 3, -100, 100));
		//Add key listener
		frame.addKeyListener(new DefaultMovingKeyListener(frame));
		//Create circle and add listener
		Sphere circle = new Sphere(frame);
		frame.addGLEventListener(circle);
		frame.start();
	}

}
