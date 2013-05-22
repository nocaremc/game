package nocare.util.math;

public class Math2D
{

	/**
	 * Compares object 2 to object 1.
	 * If object 2 is a point, return the point function, else the quad
	 * If object 1 is a point raise exception
	 * If any variable is null, raise exception
	 * 1 - Object we are comparing to
	 * 2 - Object we are comparing to 1
	 * 
	 * This Function's use is best suited for comparing morphing objects.
	 * Such as as shrinking or growing object which may intersect with another
	 * TODO: This does not take rotation into account. A 90f rotated square would return true to a point located at its 0,0. This would cause ghost detections.
	 * @param xPos1 - Target xPos
	 * @param yPos1 - Target yPos
	 * @param width1 - Target Width
	 * @param height1 - Target Height
	 * @param xPos2 - Subject xPos
	 * @param yPos2 - Subject yPos
	 * @param width2 - Subject Width
	 * @param height2 - Subject Height
	 * @return Boolean - object 1 contains object 2
	 */
	public static boolean quadContains( float xPos1, float yPos1, float width1, float height1, float xPos2, float yPos2, float width2, float height2 )
	{
		Square s1 = new Square( xPos1, yPos1, width1, height1 );
		Square s2 = new Square( xPos2, yPos2, width2, height2 );

		// If any value is null
		if ( Float.valueOf( xPos1 ) == null || Float.valueOf( xPos2 ) == null || Float.valueOf( yPos1 ) == null || Float.valueOf( yPos2 ) == null
				|| Float.valueOf( width1 ) == null || Float.valueOf( width2 ) == null || Float.valueOf( height1 ) == null || Float.valueOf( height2 ) == null )
		{
			try
			{
				throw new NullPointerException( "quadContains() was passed null value" );
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}

		// If either object doesnt have dimensions (is a point)
		if ( ( width1 == 0 && height1 == 0 ) || ( width2 == 0 && height2 == 0 ) )
		{
			// If first object is a point, throw exception (points can't contain anything)
			if ( width1 == 0 && height1 == 0 )
			{
				try
				{
					throw new Exception( "Quadcontains used incorrectly, usage: quadContains(quad, point||quad)" );
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}

				return false;
			}
			else
			{
				return quadContainsPoint( s1, ( Point ) s2 );
			}
		}
		else
		{
			return quadContainsQuad( s1, s2 );
		}
	}

	/**
	 * @param s1
	 * @param s2
	 * @return Boolean - Square1 contains Square2
	 */
	public static boolean quadContainsQuad( Square s1, Square s2 )
	{
		return ( s2.x > s1.x && s2.x + s2.w < s1.x + s1.w && s2.y > s1.y && s2.y + s2.h < s1.y + s1.h );
	}

	/**
	 * @param s
	 * @param p
	 * @return Boolean - Square1 contains Point
	 */
	public static boolean quadContainsPoint( Square s, Point p )
	{
		return ( p.x > s.x && p.x < s.x + s.w && p.y > s.y && p.y < s.y + s.h );
	}

	public static class Square extends Point
	{
		public float w, h;

		public Square( float x, float y, float w, float h )
		{
			super( x, y );
			this.w = w;
			this.h = h;
		}
	}

	public static class Point
	{
		public float x, y;

		public Point( float x, float y )
		{
			this.x = x;
			this.y = y;
		}
	}
}
