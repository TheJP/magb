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

import org.la4j.Matrix;
import org.la4j.matrix.DenseMatrix;

public class GLFrame extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 2228610266884853555L;

	//** Standard constants **//

	public final static int STD_WIDTH = 800;
	public final static int STD_HEIGHT = 600;
	public final static String STD_VERTEX_SHADER_RESPATH = "/vertex.glsl";
	public final static String STD_FRAGMENT_SHADER_RESPATH = "/fragment.glsl";
	public final static String STD_SHADERVAR_PROJ_MATRIX = "projMatrix";
	public final static String STD_SHADERVAR_VIEW_MATRIX = "viewMatrix";
	public final static String STD_SHADERVAR_VERTEX_POS = "vertexPosition";
	public final static String STD_SHADERVAR_VERTEX_COLOR = "vertexColor";

	//** Constants **//

	private final static int BUFFER_SIZE = 2048;

	//** Fields **//

	/**
	 * Internal GL canvas for drawing.
	 */
	private GLCanvas canvas;
	/**
	 * Defines if shaders should be loaded by GLFrame.
	 */
	private boolean stdShaders = true;
	/**
	 * Defines if shaders should be parameterized by GLFrame.
	 */
	private boolean stdShaderVars = true;
	/**
	 * Defines if the standard reshape should be used.
	 */
	private boolean stdReshape = true;
	/**
	 * Id of the currently used shader program.
	 */
	private int programId = 0;
	/**
	 * Buffer of vertex data.
	 */
	private GLBufferBase buffer = new DefaultGLBuffer();

	//** Shader variable ids **//

	private int projMatrixLoc;
	private int viewMatrixLoc;
	private int vPositionLocation;
	private int vColorLocation;

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
	 * Sets projection cuboid. The parameters define the shape of the cuboid-
	 * @param gl OpenGl context.
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 */
    public void setProjection(GL2GL3 gl, float left, float right, float bottom, float top, float near, float far) {
    	Matrix m = Matrix.from2DArray(new double[][]{
    		{2.0f / (right-left),                   0,                  0, - (right + left) / (right-left)},
    		{                  0, 2.0f / (top-bottom),                  0, - (top + bottom) / (top-bottom)},
    		{                  0,                   0, -2.0f / (far-near),     - (far + near) / (far-near)},
    		{                  0,                   0,                  0,                               1}
    	});
		gl.glUniformMatrix4fv(getProjMatrixLoc(), 1, false, Utility.matrixToArray(m), 0);
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
		}
		//Load shader to OpenGL
		gl.glShaderSource(shaderId, 1, new String[] { shaderSource.toString() }, null);
		//Compile shader
		gl.glCompileShader(shaderId);
		return shaderId;
	}

	/**
	 * Adds the standard shaders to the given GL context.
	 * @param gl
	 */
	private void addStdShaders(GL2GL3 gl){
		try {
			//Create shaders
			int vertexShader = addShader(gl, GL2GL3.GL_VERTEX_SHADER, GLFrame.class.getResource(STD_VERTEX_SHADER_RESPATH).openStream());
			int fragmentShader = addShader(gl, GL2GL3.GL_FRAGMENT_SHADER, GLFrame.class.getResource(STD_FRAGMENT_SHADER_RESPATH).openStream());
			//Create program and attach shaders
			programId = gl.glCreateProgram();
			gl.glAttachShader(programId, vertexShader);
			gl.glAttachShader(programId, fragmentShader);
			gl.glLinkProgram(programId);
			gl.glUseProgram(programId);
		} catch (IOException e) {
			System.err.println("Failed to create or use standard shaders");
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2GL3 gl = drawable.getGL().getGL2GL3();
		gl.glClearColor(1, 1, 1, 1); //White background
		gl.glEnable(GL2GL3.GL_DEPTH_TEST);
		//Add standard shaders if enabled
		if(isStdShaders()){
			addStdShaders(gl);
		}
		//Add shader variables
		if(isStdShaderVars()){
			//Get shader variable id
			projMatrixLoc = gl.glGetUniformLocation(getProgramId(), STD_SHADERVAR_PROJ_MATRIX);
			viewMatrixLoc = gl.glGetUniformLocation(getProgramId(), STD_SHADERVAR_VIEW_MATRIX);
			vPositionLocation = gl.glGetAttribLocation(getProgramId(), STD_SHADERVAR_VERTEX_POS);
			vColorLocation = gl.glGetAttribLocation(getProgramId(), STD_SHADERVAR_VERTEX_COLOR);
			//Set identity matrix as initial values
			float[] identityMatrix = Utility.matrixToArray(DenseMatrix.identity(4));
			gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
			gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
		}
		buffer.setup(gl, this);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		//This method has to be implemented by the clients of the GLFrame
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		//This method has to be implemented by the clients of the GLFrame
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		if(stdReshape){
			GL2GL3 gl = drawable.getGL().getGL2GL3();
			//Set the viewport to be the entire window
			gl.glViewport(0, 0, width, height);
		}
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

	/**
	 * Defines if shaders should be parameterized by GLFrame.
	 * @return
	 */
	public boolean isStdShaderVars() {
		return stdShaderVars;
	}

	/**
	 * Defines if shaders should be parameterized by GLFrame.
	 * @param stdShaderVars
	 */
	public void setStdShaderVars(boolean stdShaderVars) {
		this.stdShaderVars = stdShaderVars;
	}

	/**
	 * Id of the currently used shader program.
	 * @return
	 */
	public int getProgramId() {
		return programId;
	}

	/**
	 * Id of the currently used shader program.
	 * @param programId
	 */
	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public int getProjMatrixLoc() {
		return projMatrixLoc;
	}

	public int getViewMatrixLoc() {
		return viewMatrixLoc;
	}

	public int getvPositionLocation() {
		return vPositionLocation;
	}

	public int getvColorLocation() {
		return vColorLocation;
	}

	/**
	 * Buffer of vertex data.
	 */
	public GLBufferBase getBuffer() {
		return buffer;
	}

	/**
	 * Buffer of vertex data.
	 */
	public void setBuffer(GLBufferBase buffer) {
		this.buffer = buffer;
	}

	/**
	 * Defines if the standard reshape should be used.
	 * @return
	 */
	public boolean isStdReshape() {
		return stdReshape;
	}

	/**
	 * Defines if the standard reshape should be used.
	 * @param stdReshape
	 */
	public void setStdReshape(boolean stdReshape) {
		this.stdReshape = stdReshape;
	}
}
