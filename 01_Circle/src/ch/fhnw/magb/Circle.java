package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;


public class Circle extends GLEventListenerAdapter {

	private DefaultGLBuffer buffer = new DefaultGLBuffer();
	private GLFrame frame;
	private float left=-1, right=1, top, bottom, near=-100, far=100;

	public Circle(GLFrame frame) {
		frame.setStdReshape(false);
		frame.setBuffer(buffer);
		this.frame = frame;
	}

	/**
	 * Draws a circle at the current center.
	 * @param gl OpenGl context
	 * @param radius Radius of the circle
	 * @param fill Filled or only border
	 * @param points Number of points to use 
	 */
	public static void drawCircle(GL2GL3 gl, GLBufferBase buffer, float radius, boolean fill, int points){
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		//Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);
		float aspect = (float)height/width;
		bottom = aspect * left;
		top = aspect * right;
		setProjection(gl, left, right, bottom, top, near, far);
	}

    public void setProjection(GL2GL3 gl, float left, float right, float bottom, float top, float near, float far) {
		float m00 = 2.0f / (right-left);;
		float m11 = 2.0f / (top-bottom);
		float m22 = -2.0f / (far-near);
		float m03 = - (right + left) / (right-left);
		float m13 = - (top + bottom) / (top-bottom);
		float m23 = - (far + near) / (far-near);
		float m33 = 1;
		float[] projMatrix = {m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33 };
		gl.glUniformMatrix4fv(frame.getProjMatrixLoc(), 1, false, projMatrix, 0);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
	    buffer.reset();
	    buffer.setColor(0, 1, 0, 1);
	    buffer.addVertex(-0.5f,-0.5f,0);
	    buffer.addVertex(0.5f,-0.5f,0);
	    buffer.addVertex(0,0.5f,0);
	    buffer.copy(gl);
	    buffer.draw(gl, GL2GL3.GL_TRIANGLES);
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
