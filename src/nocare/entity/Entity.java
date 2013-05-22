package nocare.entity;

import nocare.api.IDynamicObject2D;

public abstract class Entity implements IDynamicObject2D
{

	protected float xPos, yPos;
	protected float xSpeed, ySpeed;

	@Override
	public void setX( float x )
	{
		xPos = x;
	}

	@Override
	public void setY( float y )
	{
		yPos = y;
	}

	@Override
	public void setPosition2f( float x, float y )
	{
		xPos = x;
		yPos = y;
	}

	@Override
	public void setDX( float dx )
	{
		xSpeed = dx;
	}

	@Override
	public void setDY( float dy )
	{
		ySpeed = dy;
	}

	@Override
	public void incDX( float dx )
	{
		xSpeed = xSpeed + dx;
	}

	@Override
	public void incDY( float dy )
	{
		ySpeed = ySpeed + dy;
	}

	@Override
	public void decDX( float dx )
	{
		xSpeed = xSpeed - dx;
	}

	@Override
	public void decDY( float dy )
	{
		ySpeed = ySpeed - dy;
	}

	@Override
	public void setD2f( float dx, float dy )
	{
		xSpeed = dx;
		ySpeed = dy;
	}

	@Override
	public void incD2f( float dx, float dy )
	{
		xSpeed = xSpeed + dx;
		ySpeed = ySpeed + dy;
	}

	@Override
	public void decD2f( float dx, float dy )
	{
		xSpeed = xSpeed - dx;
		ySpeed = ySpeed - dy;
	}

	@Override
	public float getX()
	{
		return xPos;
	}

	@Override
	public float getY()
	{
		return yPos;
	}

	@Override
	public float getDX()
	{
		return xSpeed;
	}

	@Override
	public float getDY()
	{
		return ySpeed;
	}

	@Override
	public void update()
	{
		xPos = xPos + xSpeed;
		yPos = yPos + ySpeed;
	}
	
	@Override
	public boolean isMoving()
	{
		return xSpeed != 0.0f || ySpeed != 0.0f; 
	}
	
	public abstract void render();

}
