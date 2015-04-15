import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import ch.fhnw.magb.Camera;
import ch.fhnw.magb.DefaultGLBuffer;
import ch.fhnw.magb.DefaultMovingKeyListener;
import ch.fhnw.magb.GLEventListenerAdapter;
import ch.fhnw.magb.GLFrame;
import ch.fhnw.magb.GLFrame.ReshapeType;
import ch.fhnw.magb.ProjectionCuboid;
import ch.fhnw.magb.Utility;


public class ModelView extends GLEventListenerAdapter {

	/**
	 * Buffer, to which the circle is drawn.
	 */
	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	private GLFrame frame;

	public ModelView(GLFrame frame) {
		frame.setStdReshape(ReshapeType.PROPORTIONAL);
		frame.setBuffer(buffer);
		this.frame = frame;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
	    buffer.reset();
	    //Translation matrix
	    frame.begin();
	    frame.translate(gl, -1, 0, 0);
	    buffer.setColor(0, 1, 0, 1);
	    Utility.drawCircle(gl, buffer, 1, true, 55);
	    frame.end();
	    //Translation matrix
	    frame.begin();
	    frame.translate(gl, 1, 0, 0);
	    buffer.setColor(1, 0, 0, 1);
	    Utility.drawCircle(gl, buffer, 1, true, 55);
	    frame.end();
	}

	public static void main(String[] args) {
		//Init frame
		GLFrame frame = new GLFrame("ModelView");
		frame.setCamera(new Camera(0, 0, 0));
		frame.setProjection(new ProjectionCuboid(-4, 4, -4, 4, -4, 4));
		//Add key listener
		frame.addKeyListener(new DefaultMovingKeyListener(frame));
		//Create circle and add listener
		ModelView mv = new ModelView(frame);
		frame.addGLEventListener(mv);
		frame.start();
	}

}
