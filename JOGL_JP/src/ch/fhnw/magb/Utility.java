package ch.fhnw.magb;

import javax.media.opengl.GL2GL3;

import org.la4j.Matrix;
import org.la4j.V;
import org.la4j.Vector;

import ch.fhnw.magb.figure.Ikosaeder;
import ch.fhnw.magb.figure.Sphere;

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

	/**
	 * Implementation of the cross product: a x b.
	 * @param a
	 * @param b
	 * @return
	 */
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
	 * @param buffer
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

	/**
	 * Ambient light
	 */
	private final static float ambient = 0.5f;
	/**
	 * Intensity factor, which is applied to directional lights
	 */
	private final static float lightFactor = 0.9f - ambient;

	/**
	 * Calculates the light, which should be drawn for the given Triangle (defines by 3 Vectors).
	 * @param a
	 * @param b
	 * @param c
	 * @param toLight Defines the directional Light, for which this calculation should be made.
	 * @return Light level in the interval [0, 1]
	 */
	public static float calculateLight(Vector a, Vector b, Vector c, Vector toLight){
		//Get triangle normal
		Vector n = cross(b.subtract(a), (c.subtract(a)));
		//Normalize
		n = n.divide(n.norm());
		return ambient + lightFactor * (float)toLight.innerProduct(n);
	}

	/**
	 * Projects the given vector to the given plane and adds the result to the buffer.
	 * @param buffer
	 * @param a Position vector of the point, which has to be projection vector.
	 * @param toLight Normalized direction towards the directional light. 
	 * @param nE Normal of the projection plane.
	 * @param d Result of the projection planes equation. (dot(n,p) = d)
	 */
	public static void drawShadowVector(GLBufferBase buffer, Vector a, Vector toLight, Vector nE, double d){
		double t = (d - a.innerProduct(nE)) / nE.innerProduct(toLight);
		Vector aa = a.add(toLight.multiply(t));
		buffer.addVertex(aa);
	}

	/**
	 * Draws an Ikosaeder at the current center using the current color.
	 * The lighting and shadows are drawn using one directional light source, which is given as parameter.
	 * @param gl OpenGl context
	 * @param buffer
	 * @param toLight Normalized direction towards the directional light. 
	 */
	public static void drawIkosaeder(GL2GL3 gl, GLColorBufferBase buffer, Vector toLight){
		Ikosaeder.draw(gl, buffer, toLight);
	}

	/**
	 * Draws a Sphere at the current center using the current color.
	 * The lighting and shadows are drawn using one directional light source, which is given as parameter.
	 * @param gl
	 * @param buffer
	 * @param toLight Normalized direction towards the directional light. 
	 * @param precision The sphere is drawn as a subdivided Ikosaeder. This parameter defines the number of subdivisions.
	 */
	public static void drawSphere(GL2GL3 gl, DefaultGLBuffer buffer, Vector toLight, int precision) {
		Sphere.draw(gl, buffer, toLight, precision);
	}

}
