package ch.fhnw.magb;

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

}
