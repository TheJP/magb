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

	public static void drawIkosaeder(GL2GL3 gl, GLBufferBase buffer){
		float x = 0.525731112119133606f;
		float z = 0.850650808352039932f;
		float[][] data = new float[][]{
			{-x, 0, z}, {x, 0, z}, {-x, 0, -z}, {x, 0, -z},
			{0, z, x}, {0, z, -x}, {0, -z, x}, {0, -z, -x},
			{z, x, 0}, {-z, x, 0}, {z, -x, 0}, {-z, -x, 0}
		};
		int[][] indicies = new int[][]{
			{1,4,0}, {4,9,0}, {4,5,9}, {8,5,4}, {1,8,4},
			{1,10,8}, {10,3,8}, {8,3,5}, {3,2,5}, {3,7,2},
			{3,10,7}, {10,6,7}, {6,11,7}, {6,0,11}, {6,1,0},
			{10,1,6}, {11,0,9}, {2,11,9}, {5,2,9}, {11,2,7}	
		};
		float c = 0;
		float step = 1f / (indicies.length * 3f);
		for(int i = 0; i < indicies.length; ++i){
			((DefaultGLBuffer)buffer).setColor(0, c, 0, 1);
			buffer.addVertex(data[indicies[i][0]]);
			((DefaultGLBuffer)buffer).setColor(0, c + step, 0, 1);
			buffer.addVertex(data[indicies[i][1]]);
			((DefaultGLBuffer)buffer).setColor(0, c + 2*step, 0, 1);
			buffer.addVertex(data[indicies[i][2]]);
			c += 3*step;
		}
		//Draw
	    buffer.copy(gl);
	    buffer.draw(gl, GL2GL3.GL_TRIANGLES);
	}

}
