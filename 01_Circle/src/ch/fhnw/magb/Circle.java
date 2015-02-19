package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;


public class Circle extends GLEventListenerAdapter {

	/**
	 * Buffer, to which the circle is drawn.
	 */
	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	/**
	 * Displayed frame.
	 */
	private GLFrame frame;
	/**
	 * Variables which are used to setup the projection.
	 */
	private float left=-1, right=1, top, bottom, near=-100, far=100;

	public Circle(GLFrame frame) {
		frame.setStdReshape(false);
		frame.setBuffer(buffer);
		this.frame = frame;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		//Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);
		float aspect = (float)height/width;
		bottom = aspect * left;
		top = aspect * right;
		frame.setProjection(gl, left, right, bottom, top, near, far);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
	    buffer.reset();
	    buffer.setColor(0, 0.5f, 0, 1);
	    Utility.drawCircle(gl, buffer, 0.5f, false, 55);
	    buffer.setColor(0, 1, 0, 1);
	    Utility.drawCircle(gl, buffer, 0.5f, true, 55);
	}

	public static void main(String[] args) {
		//Init frame
		GLFrame frame = new GLFrame("Circle");
		//Create circle and add listener
		Circle circle = new Circle(frame);
		frame.addGLEventListener(circle);
		frame.start();
	}

}
