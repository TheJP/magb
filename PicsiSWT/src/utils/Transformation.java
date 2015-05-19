package utils;

import org.eclipse.swt.graphics.ImageData;

/**
 * class that represents a 3x3 affine transformation
 * @author JP
 *
 */
public class Transformation {
	private final double[][] m;

	private Transformation(double[][] m){
		this.m = m;
	}

	/**
	 * Creates a transformation from the given array.
	 * @param m Has to be a 3x3 affine transformation
	 */
	public static Transformation fromArray(double[][] m){
		if(m.length != 3){ throw new IllegalArgumentException(); }
		double[][] cloned = new double[3][];
		for(int i = 0; i < 3; ++i){
			if(m[i].length != 3){ throw new IllegalArgumentException(); }
			cloned[i] = m[i].clone();
		}
		return new Transformation(cloned);
	}

	/**
	 * Returns a rotation transformation.
	 * @param angle Angle in degrees
	 * @return
	 */
	public static Transformation rotation(double angle){
		double rangle = Math.toRadians(angle);
		return new Transformation(new double[][]{
			{ Math.cos(rangle), -Math.sin(rangle), 0 },
			{ Math.sin(rangle), Math.cos(rangle),  0 },
			{ 0, 0, 1 }
		});
	}

	/**
	 * Returns a scale transformation.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Transformation scale(double x, double y){
		return new Transformation(new double[][]{
			{ x, 0, 0 },
			{ 0, y, 0 },
			{ 0, 0, 1 }	
		});
	}

	/**
	 * Returns a scale transformation.
	 * @param s
	 * @return
	 */
	public static Transformation scale(double s){
		return scale(s, s);
	}

	public static Transformation translate(double x, double y){
		return new Transformation(new double[][]{
			{ 1, 0, x },
			{ 0, 1, y },
			{ 0, 0, 1 }
		});
	}

	/**
	 * Inverts the transformation.
	 * @return
	 */
	public Transformation invert(){
		double den = m[0][0]*m[1][1] - m[0][1]*m[1][0];
		if(den != 0){ throw new IllegalArgumentException(); }
		
		return new Transformation(new double[][] {
			{ m[1][1]/den,-m[0][1]/den, (m[0][1]*m[1][2] - m[0][2]*m[1][1])/den }, 
			{-m[1][0]/den, m[0][0]/den, (m[0][2]*m[1][0] - m[0][0]*m[1][2])/den }, 
			{ 0, 0 , 1 }
		});
	}

	/**
	 * Multiplies the transformation with a vector (result = this * vector).
	 * @param v
	 * @return
	 */
	public double[] multiply(double[] v){
		if(v.length != 3){ throw new IllegalArgumentException(); }
		double[] r = new double[3];
		for(int y = 0; y < 3; ++y){
			for(int i = 0; i < 3; ++i){
				r[y] += m[y][i] * v[i]; 
			}
		}
		return r;
	}

	/**
	 * Combines to transformation (result = this * param).
	 * @param t
	 * @return
	 */
	public Transformation combine(Transformation t){
		double[][] r = new double[3][];
		for(int y = 0; y < 3; ++y){
			r[y] = new double[3];
			for(int x = 0; x < 3; ++x){
				for(int i = 0; i < 3; ++i){
					r[y][x] += m[y][i] * t.m[i][x];
				}
			}
		}
		return new Transformation(r);
	}

	/**
	 * Transforms the given image.
	 * @param inData
	 * @param t
	 * @return
	 */
	public ImageData targetToSourceMapping(ImageData inData){
		final ImageData outData = (ImageData)inData.clone();
		final int halfx = outData.width / 2;
		final int halfy = outData.height / 2;

		Transformation t2 = translate(halfx, halfy) //3
			.combine(this) //2
			.combine(translate(-halfx, -halfy)); //1

		Parallel.For(0, outData.height, v -> {
			for (int u=0; u < outData.width; ++u) {
				double[] p = { u, v, 1 };
				p = t2.multiply(p);
				int x = (int)Math.round(p[0]);
				int y = (int)Math.round(p[1]);
				if(x >= 0 && x < outData.width && y >= 0 && y < outData.height){
					outData.setPixel(u, v, inData.getPixel(x, y));
					outData.setAlpha(u, v, inData.getAlpha(x, y));
				}else{
					outData.setPixel(u, v, 0);
					outData.setAlpha(u, v, 1);
				}
			}
		});
		return outData;
	}
}
