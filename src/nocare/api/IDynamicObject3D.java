package nocare.api;

public interface IDynamicObject3D extends IDynamicObject2D {
	/*
	 * Setters
	 */

	/**
	 * Set Z position
	 * @param z
	 */
	public void setZ( float z );

	/**
	 * Set the X, Y, and Z position
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition3f( float x, float y, float z );

	/**
	 * Set the Z speed
	 * @param dz
	 */
	public void setDZ( float dz );

	/**
	 * Set the X, Y, and Z speed
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void setD3f( float dx, float dy, float dz );

	/** 
	 * Increment Z speed by value
	 */
	public void incDZ( float dz );

	/**
	 * Increment X, Y, and Z speed by values
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void incD3f( float dx, float dy, float dz );

	/**
	 * Decrement Z speed by value
	 * @param dz
	 */
	public void decDZ( float dz );

	/**
	 * Decrement X, Y, and Z by values
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void decD3f( float dx, float dy, float dz );

	/*
	 * Getters
	 */

	/**
	 * @return Z position
	 */
	public float getZ();

	/**
	 * @return Z speed
	 */
	public float getDZ();
}
