package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;

import org.la4j.Matrix;
import org.la4j.V;
import org.la4j.Vector;

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
		for(int y = m.rows() - 1; y >= 0; --y){
			for(int x = m.columns() - 1; x >= 0; --x){
				result[--i] = (float)m.get(y, x);
			}
		}
		return result;
	}

	/**
	 * Converts a given double array to a float array.
	 * @param d
	 * @return
	 */
	public static float[] doubleToFloat(double... d){
		float[] result = new float[d.length];
		for(int i = d.length - 1; i >= 0; --i){
			result[i] = (float)d[i];
		}
		return result;
	}

	/**
	 * Converts a given float array to a double array.
	 * @param d
	 * @return
	 */
	public static double[] floatToDouble(float... f){
		double[] result = new double[f.length];
		for(int i = f.length - 1; i >= 0; --i){
			result[i] = f[i];
		}
		return result;
	}

	public static Vector cross(Vector a, Vector b){
		if(a.length() != 3 || b.length() != 3){ throw new IllegalArgumentException("3D Vectors needed!"); }
		return V.v(
			a.get(1)*b.get(2) - a.get(2)*b.get(1),
			a.get(2)*b.get(0) - a.get(0)*b.get(2),
			a.get(0)*b.get(1) - a.get(1)*b.get(0)
		);
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
		//Direction to the sun
		Vector toLight = Vector.fromArray(new double[]{ -1, 2, 1 });
		//Normalize
		toLight = toLight.divide(toLight.norm());
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
		for(int i = 0; i < indicies.length; ++i){
			Vector a = Vector.fromArray(floatToDouble(data[indicies[i][0]]));
			Vector b = Vector.fromArray(floatToDouble(data[indicies[i][1]]));
			Vector c = Vector.fromArray(floatToDouble(data[indicies[i][2]]));
			//Get triangle normal

			Vector n = cross(b.subtract(a), (c.subtract(a)));
			//Normalize
			n = n.divide(n.norm());
			float light = 0.5f + 0.4f * (float)toLight.innerProduct(n);
			//Set light
			((DefaultGLBuffer)buffer).setColor(0, light, 0, 1);
			buffer.addVertex(data[indicies[i][0]]);
			buffer.addVertex(data[indicies[i][1]]);
			buffer.addVertex(data[indicies[i][2]]);
			//Set shadow
			((DefaultGLBuffer)buffer).setColor(0, 0, 0, 1);
			Vector nE = V.v(0, 1, 0);
			double d = nE.innerProduct(V.v(0, -1, 0)); //xz-Ebene
			//a
			double t = (d - a.innerProduct(nE)) / nE.innerProduct(toLight);
			Vector aa = a.add(toLight.multiply(t));
			buffer.addVertex(matrixToArray(aa.toRowMatrix()));
			//b
			t = (d - b.innerProduct(nE)) / nE.innerProduct(toLight);
			Vector bb = b.add(toLight.multiply(t));
			buffer.addVertex(matrixToArray(bb.toRowMatrix()));
			//c
			t = (d - c.innerProduct(nE)) / nE.innerProduct(toLight);
			Vector cc = c.add(toLight.multiply(t));
			buffer.addVertex(matrixToArray(cc.toRowMatrix()));
		}
		//Draw
	    buffer.copy(gl);
	    buffer.draw(gl, GL2GL3.GL_TRIANGLES);
	}

}
