package nocare.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class GeneralUtils
{
	public static float[] colorToFloat( Color color )
	{
		float[] rgb = new float[3];

		rgb[0] = color.getRed() / 255.0f;
		rgb[1] = color.getGreen() / 255.0f;
		rgb[2] = color.getBlue() / 255.0f;
		return rgb;
	}

	public static void glColorShortcut( Color c )
	{
		float[] f = colorToFloat( c );
		glColorShortcut( f );
	}

	public static void glColorShortcut( float[] c )
	{
		GL11.glColor3f( c[0], c[1], c[2] );
	}

	public static void glColorShortcut( FloatBuffer diffuse )
	{
		float[] f = new float[3];
		f[0] = diffuse.get( 0 );
		f[1] = diffuse.get( 1 );
		f[2] = diffuse.get( 2 );

		GL11.glColor3f( f[0], f[1], f[2] );
	}

	public static FloatBuffer floatBufferFromArray( float[] array )
	{
		FloatBuffer fb = BufferUtils.createFloatBuffer( 4 );
		for ( int i = 0; i < array.length; i++ )
		{
			fb.put( blenderMTLValueToRGB( array[i] ) );
		}

		if ( array.length < 4 )
			fb.put( 1.0f );

		fb.flip();
		return fb;
	}

	/**
	 * Scales a blender rgb value from mtl export to one useable by opengl (0 - 1f)
	 * @param value
	 * @return
	 */
	public static float blenderMTLValueToRGB( float value )
	{
		return ( value * 361.3428815452336f ) / 255f;
	}
}
