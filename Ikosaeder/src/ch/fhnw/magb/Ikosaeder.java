package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import ch.fhnw.magb.GLFrame.ReshapeType;

public class Ikosaeder extends GLEventListenerAdapter {

	/**
	 * Buffer, to which the circle is drawn.
	 */
	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	private GLFrame frame;

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
	    buffer.setColor(0, 0.5f, 0, 1);
	    Utility.drawIkosaeder(gl, buffer);
	}

	public static void main(String[] args) {
		//Init frame
		GLFrame frame = new GLFrame("Circle");
		/*frame.setR(10);
		frame.setElevation(14);
		frame.setAzimut(10);
		frame.setLeft(-3);
		frame.setRight(3);
		frame.setNear(-10);
		frame.setFar(100);*/
		//Create circle and add listener
		Ikosaeder circle = new Ikosaeder(frame);
		frame.addGLEventListener(circle);
		frame.start();
	}

}
