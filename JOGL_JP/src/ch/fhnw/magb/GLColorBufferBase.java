package ch.fhnw.magb;

public abstract class GLColorBufferBase extends GLBufferBase {

    /**
     * Current color.
     */
    private float[] color = new float[]{ 0, 0, 0, 0 };

	/**
	 * Current color.
	 * @return
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * Change to color.
	 * @param color
	 */
	public void setColor(float... color) {
		this.color = color;
	}

}
