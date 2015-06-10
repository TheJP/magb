package utils;

import javax.swing.JOptionPane;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

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
		if (m[2][0] == 0 && m[2][1] == 0 && m[2][2] == 1) {
			double den = m[0][0]*m[1][1] - m[0][1]*m[1][0];
			assert den != 0 : "matrix is singular";

			return new Transformation(new double[][] {
				{ m[1][1]/den,-m[0][1]/den, (m[0][1]*m[1][2] - m[0][2]*m[1][1])/den }, 
				{-m[1][0]/den, m[0][0]/den, (m[0][2]*m[1][0] - m[0][0]*m[1][2])/den }, 
				{ 0, 0 , 1 }
			});
		} else {
			double den = m[0][0]*m[1][1]*m[2][2] + m[0][1]*m[1][2]*m[2][0] + m[0][2]*m[1][0]*m[2][1]
					   - m[0][0]*m[1][2]*m[2][1] - m[0][1]*m[1][0]*m[2][2] - m[0][2]*m[1][1]*m[2][0];
			assert den != 0 : "matrix is singular";

			return new Transformation(new double[][] {
				{ (m[1][1]*m[2][2] - m[1][2]*m[2][1])/den, (m[0][2]*m[2][1] - m[0][1]*m[2][2])/den, (m[0][1]*m[1][2] - m[0][2]*m[1][1])/den },
				{ (m[1][2]*m[2][0] - m[1][0]*m[2][2])/den, (m[0][0]*m[2][2] - m[0][2]*m[2][0])/den, (m[0][2]*m[1][0] - m[0][0]*m[1][2])/den },
				{ (m[1][0]*m[2][1] - m[1][1]*m[2][0])/den, (m[0][1]*m[2][0] - m[0][0]*m[2][1])/den, (m[0][0]*m[1][1] - m[0][1]*m[1][0])/den }
			});
		}
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

	public static enum Strategy {
		NearestNeighbor,
		Bilinear,
		Perfect;

		public void interpolate(ImageData outData, ImageData inData, int u, int v, double x, double y){
			int u0, v0;
			switch (this) {
				case NearestNeighbor:
					u0 = (int)Math.round(x);
					v0 = (int)Math.round(y);
					if(u0 >= 0 && u0 < outData.width && v0 >= 0 && v0 < outData.height){
						outData.setPixel(u, v, inData.getPixel(u0, v0));
						outData.setAlpha(u, v, inData.getAlpha(u0, v0));
					}else{
						outData.setPixel(u, v, 0);
						outData.setAlpha(u, v, 1);
					}
					break;
				case Bilinear:
					u0 = (int)Math.floor(x);
					v0 = (int)Math.floor(y);
					//Interpolate between the following Pixels
					RGB A = getRGB(inData, u0, v0);
					RGB B = getRGB(inData, u0+1, v0);
					RGB C = getRGB(inData, u0, v0+1);
					RGB D = getRGB(inData, u0+1, v0+1);
					//Bilinear interpolation
					//TODO: Add alpha (was not required)
					double a = x - (double)u0;
					RGB E = bilinear(A, B, a);
					RGB F = bilinear(C, D, a);
					double b = y - (double)v0;
					RGB G = bilinear(E, F, b);
					//Set output
					outData.setPixel(u, v, outData.palette.getPixel(G));
					break;
				case Perfect:
					JOptionPane.showMessageDialog(null, "HELL NO!");
					break;
			}
		}

		private RGB getRGB(ImageData inData, int u, int v){
			//Upper bound
			u = u < inData.width ? u : inData.width - u - 1 + inData.width;
			v = v < inData.height ? v : inData.height - v - 1 + inData.height; 
			//Lower bound
			u = Math.abs(u) % inData.width;
			v = Math.abs(v) % inData.height;
			return inData.palette.getRGB(inData.getPixel(u, v));
		}

		private RGB bilinear(RGB A, RGB B, double a){
			return new RGB(
				(int)(A.red   + a * (B.red   - A.red)),
				(int)(A.green + a * (B.green - A.green)),
				(int)(A.blue  + a * (B.blue  - A.blue))
			);
		}
	}

	/**
	 * Transforms the given image.
	 * @param inData
	 * @param t
	 * @return
	 */
	public ImageData targetToSourceMapping(ImageData inData){
		//Compatibility to older versions
		return targetToSourceMapping(inData, Strategy.NearestNeighbor);
	}

	/**
	 * Transforms the given image.
	 * @param inData
	 * @param t
	 * @return
	 */
	public ImageData targetToSourceMapping(ImageData inData, Strategy strategy){
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
				strategy.interpolate(outData, inData, u, v, p[0], p[1]);
			}
		});
		return outData;
	}
}
