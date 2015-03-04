package ch.fhnw.magb;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import com.jogamp.common.nio.Buffers;

public class DefaultGLBuffer extends GLColorBufferBase {

	public final static int STD_BUFFER_SIZE = 1000000;

	/**
	 * Internal buffer of vertices.
	 */
    private FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(getBufferSize());
    /**
     * Number of vertices in the buffer. 
     */
    private int vertices = 0;

	@Override
	public int getBufferSize() {
		return STD_BUFFER_SIZE * (2 * SIZEOF_4C_Float_Array); //4 floats for location, 4 floats for color 
	}

	@Override
	public void setup(GL2GL3 gl, GLFrame glFrame) {
		super.setup(gl, glFrame);
		if(glFrame.isStdShaderVars()){
			gl.glEnableVertexAttribArray(glFrame.getvPositionLocation());
			gl.glEnableVertexAttribArray(glFrame.getvColorLocation());
			//Define offset and stride
			gl.glVertexAttribPointer(glFrame.getvPositionLocation(), 4, GL2GL3.GL_FLOAT, false, 2 * SIZEOF_4C_Float_Array, 0);
			gl.glVertexAttribPointer(glFrame.getvColorLocation(), 4, GL2GL3.GL_FLOAT, false, 2 * SIZEOF_4C_Float_Array, SIZEOF_4C_Float_Array);
		}
	}

	@Override
	public void copy(GL2GL3 gl) {
		vertexBuffer.rewind();
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, getVertexBufferId());
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices * (2 * SIZEOF_4C_Float_Array), vertexBuffer);
	}

	@Override
	public void reset() {
		vertexBuffer.rewind();
		vertices = 0;
	}

	@Override
	public void addVertex(float... v) {
		//Add vector components
		vertexBuffer.put(v);
		//Add 4th vector component
		vertexBuffer.put(1);
		//Add color components
		vertexBuffer.put(getColor());
		//Increment vertex counter
		++vertices;
	}

	@Override
	public void draw(GL2GL3 gl, int type) {
	    gl.glDrawArrays(type, 0, vertices);
	}
}
