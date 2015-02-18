package ch.fhnw.magb;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

public class GLFrame extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 2228610266884853555L;

	//Standard constants
	public final static int STD_WIDTH = 800;
	public final static int STD_HEIGHT = 600;

	/**
	 * 
	 */
	private GLCanvas canvas;

	public GLFrame(String title) {
		this(title, STD_WIDTH, STD_HEIGHT);
	}

	public GLFrame(String title, int width, int height){
		super(title);
		setSize(width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//Create openGL context
		GLProfile glp = GLProfile.get(GLProfile.GL2GL3);
		GLCapabilities glCapabilities = new GLCapabilities(glp);
		canvas = new GLCanvas(glCapabilities);
		canvas.addGLEventListener(this);
		add(canvas);
		//TODO add setVisible in main method
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL().getGL();
		gl.glClearColor(1, 1, 1, 1); //White background
		gl.glEnable(GL.GL_DEPTH_TEST);
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
}
