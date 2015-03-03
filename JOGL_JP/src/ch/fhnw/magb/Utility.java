package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;

import org.la4j.Matrix;

/**
 * Utility which provides useful, not changing functionality.
 * @author JP
 */
public class Utility {

	/**
	 * Converts given matrix to flat array.
	 * @param m Matrix
	 * @return Array
	 */
	public static float[] matrixToArray(Matrix m){
		int i = m.rows()*m.columns();
		float[] result = new float[i];
		for(int y = m.columns() - 1; y >= 0; --y){
			for(int x = m.rows() - 1; x >= 0; --x){
				result[--i] = (float)m.get(y, x);
			}
		}
		return result;
	}

	/**
	 * Draws a circle at the current center using the current color.
	 * @param gl OpenGl context
	 * @param radius Radius of the circle
	 * @param fill Filled or only border
	 * @param points Number of points to use 
	 */
	public static void drawCircle(GL2GL3 gl, GLBufferBase buffer, float radius, boolean fill, int points){
		if(fill){ buffer.addVertex(0, 0, 0); }
		//Add points
		float step = (float)((2*Math.PI) / points);
		for(float i = 0; i - step <= 2 * Math.PI; i += step){
			buffer.addVertex((float)(radius * Math.cos(i)), (float)(radius * Math.sin(i)), 0f);
		}
		//Draw
	    buffer.copy(gl);
	    buffer.draw(gl, fill ? GL2GL3.GL_TRIANGLE_FAN : GL2GL3.GL_LINE_LOOP);
	}

}
