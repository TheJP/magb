package ch.fhnw.magb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.media.opengl.GL2GL3;
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
	public final static String STD_VERTEX_SHADER_RESPATH = "/vertex.glsl";
	public final static String STD_FRAGMENT_SHADER_RESPATH = "/fragment.glsl";
	//Constants
	private final static int BUFFER_SIZE = 2048;

	/**
	 * Internal GL canvas for drawing.
	 */
	private GLCanvas canvas;
	/**
	 * Defines if shaders should be loaded by GLFrame.
	 */
	private boolean stdShaders = true;

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
	}

	/**
	 * Add OpenGL listener.
	 * @param listener
	 */
	public void addGLEventListener(GLEventListener listener) {
		canvas.addGLEventListener(listener);
	}

	/**
	 * Starts openGL rendering and opens frame.
	 */
	public void start(){
		add(canvas);
		setVisible(true);
	}

	/**
	 * Creates shader and compiles it using the filename.
	 * @param gl OpenGl context.
	 * @param shaderType A GL constant, which defines the shader type
	 * @param shaderInputStream Stream for the shader.
	 * @return Shader id, which is returned by OpenGL.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public int addShader(GL2GL3 gl, int shaderType, InputStream shaderInputStream) throws FileNotFoundException, IOException{
		int shaderId = gl.glCreateShader(shaderType);
		//Read shaders from file
		StringBuilder shaderSource = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(shaderInputStream))){
			char[] buffer = new char[BUFFER_SIZE];
			int read;
			do{
				read = reader.read(buffer);
				if(read > 0){
					shaderSource.append(buffer, 0, read);
				}
			} while(read > 0);
		}System.out.println(shaderSource.toString());
		//Load shader to OpenGL
		gl.glShaderSource(shaderId, 1, new String[] { shaderSource.toString() }, null);
		//Compile shader
		gl.glCompileShader(shaderId);
		return shaderId;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClearColor(1, 1, 1, 1); //White background
		gl.glEnable(GL2GL3.GL_DEPTH_TEST);
		//Add standard shaders if enabled
		if(isStdShaders()){
			try {
				//Create shaders
				int vertexShader = addShader(gl, GL2GL3.GL_VERTEX_SHADER, GLFrame.class.getResource(STD_VERTEX_SHADER_RESPATH).openStream());
				int fragmentShader = addShader(gl, GL2GL3.GL_FRAGMENT_SHADER, GLFrame.class.getResource(STD_FRAGMENT_SHADER_RESPATH).openStream());
				//Create program and attach shaders
				int glProgram = gl.glCreateProgram();
				gl.glAttachShader(glProgram, vertexShader);
				gl.glAttachShader(glProgram, fragmentShader);
				gl.glLinkProgram(glProgram);
				gl.glUseProgram(glProgram);
			} catch (IOException e) {
				System.err.println("Failed to create or use standard shaders");
				e.printStackTrace();
				System.exit(0);
			}
		}
		
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

	/**
	 * Defines if shaders should be loaded by GLFrame.
	 * @return
	 */
	public boolean isStdShaders() {
		return stdShaders;
	}

	/**
	 * Defines if shaders should be loaded by GLFrame.
	 * @param useStdShaders
	 */
	public void setStdShaders(boolean stdShaders) {
		this.stdShaders = stdShaders;
	}
}
