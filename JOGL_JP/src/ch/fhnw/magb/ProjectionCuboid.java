package ch.fhnw.magb;

import org.la4j.Matrix;

public class ProjectionCuboid {
	//Values with defaults
	private float left = -1;
	private float right = 1;
	private float top = -1;
	private float bottom = 1;
	private float near = -100;
	private float far = 100;

	/**
	 * Default constructor.
	 */
	public ProjectionCuboid() { }

	/**
	 * Constructor with values.
	 */
	public ProjectionCuboid(float left, float right, float top, float bottom, float near, float far) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.near = near;
		this.far = far;
	}

	/**
	 * Constructor with values excluding top, bottom.
	 */
	public ProjectionCuboid(float left, float right, float near, float far) {
		this.left = left;
		this.right = right;
		this.near = near;
		this.far = far;
	}

	/**
	 * Returns the projection matrix
	 * @return
	 */
	public Matrix getProjectionMatrix(){
		return Matrix.from2DArray(new double[][]{
    		{2.0f / (right-left),                   0,                  0, - (right + left) / (right-left)},
    		{                  0, 2.0f / (top-bottom),                  0, - (top + bottom) / (top-bottom)},
    		{                  0,                   0, -2.0f / (far-near),     - (far + near) / (far-near)},
    		{                  0,                   0,                  0,                               1}
    	});
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getRight() {
		return right;
	}

	public void setRight(float right) {
		this.right = right;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getBottom() {
		return bottom;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}

	public float getNear() {
		return near;
	}

	public void setNear(float near) {
		this.near = near;
	}

	public float getFar() {
		return far;
	}

	public void setFar(float far) {
		this.far = far;
	}
}
