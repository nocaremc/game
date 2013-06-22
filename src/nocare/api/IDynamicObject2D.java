package nocare.api;

public interface IDynamicObject2D {
	/*
	 * Setters
	 */

	/**
	 * Set the X position
	 * @param x
	 */
	public void setX( float x );

	/**
	 * Set the Y position
	 * @param y
	 */
	public void setY( float y );

	/**
	 * Set the X and Y position
	 * @param x
	 * @param y
	 */
	public void setPosition2f( float x, float y );

	/**
	 * Set the X speed
	 * @param dx
	 */
	public void setDX( float dx );

	/** 
	 * Set the Y speed
	 * @param dy
	 */
	public void setDY( float dy );

	/**
	 * Set the X and Y speed
	 * @param dx
	 * @param dy
	 */
	public void setD2f( float dx, float dy );

	/**
	 * Increment X speed by value
	 * @param dx
	 */
	public void incDX( float dx );

	/**
	 * Increment Y speed by value
	 * @param dy
	 */
	public void incDY( float dy );

	/**
	 * Increment X and Y speed by values
	 * @param dx
	 * @param dy
	 */
	public void incD2f( float dx, float dy );

	/**
	 * Decrement X speed by value
	 * @param dx
	 */
	public void decDX( float dx );

	/**
	 * Decrement Y speed by value
	 * @param dy
	 */
	public void decDY( float dy );

	/**
	 * Decrement X and Y speed by values
	 * @param dx
	 * @param dy
	 */
	public void decD2f( float dx, float dy );

	/*
	 * Getters
	 */

	/**
	 * Return X position
	 * @return
	 */
	public float getX();

	/**
	 * Return Y position
	 * @return
	 */
	public float getY();

	/**
	 * Return X speed
	 * @return
	 */
	public float getDX();

	/**
	 * Return Y speed
	 * @return
	 */
	public float getDY();

	/*
	 * Other 
	 */

	/**
	 * Update this object
	 */
	public abstract void update();

	/**
	 * Does this object have an X or Y speed?
	 * @return
	 */
	public boolean isMoving();
}
