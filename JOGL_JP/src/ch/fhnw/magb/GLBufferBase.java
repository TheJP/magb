package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;

public abstract class GLBufferBase {

	public final static int SIZEOF_4C_Float_Array = 4 * Float.BYTES;

	/**
	 * Vertex Array Object Id
	 */
	private int vaoId = 0;
	/**
	 * VertexBuffer
	 */
	private int vertexBufferId = 0;

	public abstract int getBufferSize();

	public void setup(GL2GL3 gl, GLFrame glFrame){
		//Vertex Array Object
		int[] tmp = new int[1];
		gl.glGenVertexArrays(1, tmp, 0);
		vaoId = tmp[0];
		gl.glBindVertexArray(vaoId);
		//Vertex Buffer
		gl.glGenBuffers(1, tmp, 0);
		vertexBufferId = tmp[0];
		gl.glBindBuffer(GL2GL3.GL_ARRAY_BUFFER, vertexBufferId);
		//Allocate memory
		gl.glBufferData(GL2GL3.GL_ARRAY_BUFFER, getBufferSize(), null, GL2GL3.GL_STATIC_DRAW);
	}
}
