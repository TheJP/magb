package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;

public class DefaultGLBuffer extends GLBufferBase {

	public final static int STD_BUFFER_SIZE = 512;

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

}
