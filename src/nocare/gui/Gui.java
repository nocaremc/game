package nocare.gui;

import nocare.api.gui.IGui;
import nocare.util.UnicodeFontImpl;

import org.lwjgl.util.Color;

public abstract class Gui implements IGui
{
	protected Color backgroundColor;
	protected Color backgroundGradientStart;
	protected Color backgroundGradientEnd;
	protected UnicodeFontImpl font;
	protected float fontSize;
	protected Color fontColor;
	public int ID;
	protected int x, y, width, height;

	public Gui()
	{
		font = ( new UnicodeFontImpl() ).setFontSize( 24f );
	}

	protected abstract void render();

	/*
	 * Getters
	 */
	@Override
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	@Override
	public Color[] getBackgroundGradient()
	{
		Color[] tmp =
		{ backgroundGradientStart, backgroundGradientEnd };
		return tmp;
	}

	@Override
	public Color getFontcolor()
	{
		return fontColor;
	}

	@Override
	public float getFontSize()
	{
		return fontSize;
	}

	@Override
	public int getID()
	{
		return ID;
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	/*
	 * Setters
	 */

	@Override
	public void setBackgroundColor( Color color )
	{
		this.backgroundColor = color;
	}

	@Override
	public void setBackgroundGradient( Color start, Color end )
	{
		this.backgroundGradientStart = start;
		this.backgroundGradientEnd = end;
	}

	@Override
	public void setFontColor( Color color )
	{
		this.fontColor = color;
	}

	@Override
	public void setFontSize( float size )
	{
		this.fontSize = size;
		font = ( new UnicodeFontImpl() ).setFontSize( fontSize );
	}

	@Override
	public void setID( int ID )
	{
		this.ID = ID;
	}

	@Override
	public void setX( int x )
	{
		this.x = x;
	}

	@Override
	public void setY( int y )
	{
		this.y = y;
	}

	@Override
	public void setPosition( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public void setWidth( int width )
	{
		this.width = width;
	}

	@Override
	public void setHeight( int height )
	{
		this.height = height;
	}

	@Override
	public void setDimension( int width, int height )
	{
		this.width = width;
		this.height = height;
	}

	public String toString()
	{
		return new StringBuilder().append( this.getClass() + ":\n\t" ).append( "[ ID: " ).append( ID ).append( ", backgroundColor: " ).append( backgroundColor )
				.append( ", backgroundGradientStart: " ).append( backgroundGradientStart ).append( ", backgroundGradientEnd: " ).append( backgroundGradientEnd )
				.append( ", fontColor: " ).append( fontColor ).append( ", fontSize: " ).append( fontSize ).append( ", x: " ).append( x ).append( ", y: " )
				.append( y ).append( ", width: " ).append( width ).append( ", height: " ).append( height ).append( " ]" ).toString();
	}
}
