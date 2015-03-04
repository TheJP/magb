package ch.fhnw.magb;

import org.la4j.Matrix;

public class Camera {
	//Values with defaults
	private float r = 10;
	private float elevation = 10;
	private float azimut=45;

	/**
	 * Default constructor.
	 */
	public Camera() { }

	/**
	 * Constructor with values.
	 * @param r
	 * @param elevation
	 * @param azimut
	 */
	public Camera(float r, float elevation, float azimut) {
		this.r = r;
		this.elevation = elevation;
		this.azimut = azimut;
	}

	/**
	 * Return the matrix which defines the transformation, for the camera system.
	 * @return
	 */
	public Matrix getCameraSystem() {
		float toRad = (float)(Math.PI/180);
		float c = (float)Math.cos(toRad*elevation);
		float s = (float)Math.sin(toRad*elevation);
		float cc = (float)Math.cos(toRad*azimut);
		float ss = (float)Math.sin(toRad*azimut);
		return Matrix.from2DArray(new double[][]{
			{  cc, -s*ss, c*ss, 0 },
			{   0,     c,    s, 0 },
			{ -ss, -s*cc, c*cc, 0 },
			{   0,     0,   -r, 1 },
		});
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public float getAzimut() {
		return azimut;
	}

	public void setAzimut(float azimut) {
		this.azimut = azimut;
	}

}
