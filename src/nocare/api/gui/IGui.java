package nocare.api.gui;

import org.lwjgl.util.Color;

public interface IGui {
	/*
	 * Getters
	 */
	public Color getBackgroundColor();

	public Color[] getBackgroundGradient();

	public Color getFontcolor();

	public float getFontSize();

	public int getID();

	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	/*
	 * Setters
	 */
	public void setBackgroundColor( Color color );

	public void setBackgroundGradient( Color start, Color end );

	public void setFontColor( Color color );

	public void setFontSize( float size );

	public void setID( int ID );

	public void setX( int x );

	public void setY( int y );

	public void setPosition( int x, int y );

	public void setWidth( int width );

	public void setHeight( int height );

	public void setDimension( int width, int height );

	/*
	 * Other
	 */
}
