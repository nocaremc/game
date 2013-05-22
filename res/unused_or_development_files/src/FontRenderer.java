package nocare.util;

import static org.lwjgl.opengl.GL11.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import nocare.App;
import nocare.gui.Gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.PNGDecoder.Format;

/**
 * My First attempt at rendering text from an image in opengl
 * Overall... it works okay.... The texture is admitidely not perfectly made
 * but the bigger issue came from just how it scaled horribly.
 * I think this is worth more time, but I will just go ahead and use a TrueType font 
 * class. I believe slick has one anyway
 * @author Nocare
 */
public class FontRenderer implements IMaid
{
	private static int fontTexture;
	private static final int gridSize = 10;
	private static final char[][] charOrder =
	{
	{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' },
	{ 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't' },
	{ 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D' },
	{ 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N' },
	{ 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X' },
	{ 'Y', 'Z', '`', '1', '2', '3', '4', '5', '6', '7' },
	{ '8', '9', '0', '-', '=', '~', '!', '@', '#', '$' },
	{ '%', '^', '&', '*', '(', ')', '_', '+', ',', '.' },
	{ '/', ';', '\'', '[', ']', '\\', '<', '>', '?', ':' },
	{ '"', '{', '}', '|', ' ', ' ', ' ', ' ', ' ', ' ' } };

	public FontRenderer()
	{
		try
		{
			setupTexture();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sets up the font texture so it can be called and rendered
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void setupTexture() throws FileNotFoundException, IOException
	{
		// Get a next/random texture id
		fontTexture = glGenTextures();

		// Bind texture to opengl
		glBindTexture( GL_TEXTURE_2D, fontTexture );

		// Create new instance of PNGDecoder with our font image
		PNGDecoder decoder = new PNGDecoder( new FileInputStream( "res/font/font1.png" ) );

		// Load ALL png data into buffer. width * height * 4 channels (rgba)
		ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * decoder.getWidth() * decoder.getHeight() );

		// Decode the buffer
		decoder.decode( buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA );

		// Flip buffer
		buffer.flip();

		// Define the texture
		glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer );

		// Unbind it, since no longer working on it
		glBindTexture( GL_TEXTURE_2D, 0 );
	}

	public void renderString( Gui parent, String string, float x, float y, float xLimit, float yLimit, float fontSize, Color fontColor )
	{
		float characterWidth = fontSize * 3f; // 0.075
		float characterHeight = fontSize * 2.25f; // 0.05625

		// Center text vertically
		y = y + ( yLimit / 2 );
		//x = x + (xLimit /2);

		glPushAttrib( GL_TEXTURE_BIT | GL_ENABLE_BIT );
		{
			//glEnable( GL_CULL_FACE ); // Culling just completely fucks this up, likely due to orth2D mode
			glEnable( GL_TEXTURE_2D );
			// Bind our texture for drawing
			glBindTexture( GL_TEXTURE_2D, fontTexture );
			// Enable linear texture filtering for smoothed results.
			glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
			glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );

			// Enable Buffered
			glEnable( GL_BLEND );
			//glBlendFunc( GL_ONE, GL_ONE );
			// Blend color to color without alpha
			// Produces best blending result (no black and no color bleed. Images cannot have any alpha :[ )
			glBlendFunc( GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA );

			// Store the current model-view matrix.
			glPushMatrix();
			{
				glBegin( GL_QUADS );
				{
					// Positional correction offset
					float xCorrection = 0;

					// Iterate over all the characters in the string.
					for ( int i = 0; i < string.length(); i++ )
					{
						// Transform on x based on the length of string and width of characters
						float xStringTransform = ( string.length() * characterWidth ) / 2;

						// Transform on x some more, based on character width and the current character
						// This just shifts the text so it doesnt bunch up
						float xCharCountTransform = ( i * characterWidth );

						// Shorten earlier X offsets
						float xTrans = -xStringTransform + xCharCountTransform;

						// Shorten our x values some more to ease fingers
						float finalX = x + xTrans;
						float finalXWidth = finalX + characterWidth;

						// If we are on first letter, and its position is to the left (-x) of the bounds, make a corrective offset
						if ( i == 0 && x + xTrans < x )
							xCorrection = x - finalX;

						// Apply offset to x coordinate
						finalX += xCorrection;
						finalXWidth += xCorrection;

						// If we are on last letter, and it's far right (+x) edge is past the bounds of object,
						if ( i == string.length() - 1 && finalXWidth >= x + xLimit )
						{
							// Scale font down by 0.1f
							parent.setFontSize( parent.getFontSize() - 0.1f );

							// Well something has gone terribly wrong, so report it and exit 
							if ( parent.getFontSize() <= 0 )
							{
								try
								{
									throw new Exception( "I scaled the font to 0 and it still could not fit!" );
								}
								catch ( Exception e )
								{
									e.printStackTrace();
								}
								App.endGame( true );
							}
						}
						/*else if (i == string.length() - 1)
							System.out.println(parent.getFontSize());*/

						// Current character char/int value we are working on
						char curChar = string.charAt( i );

						// Create variables for y,x glyph position Y->X
						int[] glyphCoordinate = getMatchedGlyphCoordinate( ( int ) curChar );

						// Size of each cell, or glyph
						float cellSize = 1.0f / gridSize;

						// X and Y offsets for the the texture coordinates we'll be using
						float cellX = glyphCoordinate[0] * cellSize;
						float cellY = glyphCoordinate[1] * cellSize;

						// Set the color of the font to whatever we want
						GeneralUtils.glColorShortcut( fontColor );

						// Draw the quad we'll be texturing. It is drawn CLOCKWISE
						// Because we switched to glOrtho2D, and then flipped the y coordinate
						// The normal was backwards, so drew it in reverse.
						// TODO: Can I just flip the normal????

						// Top Left
						glTexCoord2f( cellX, cellY );
						glVertex2f( finalX, y - ( characterHeight / 2 ) );
						// Top Right
						glTexCoord2f( cellX + cellSize, cellY );
						glVertex2f( finalXWidth, y - ( characterHeight / 2 ) );
						// Bottom Right
						glTexCoord2f( cellX + cellSize, cellY + cellSize );
						glVertex2f( finalXWidth, y + ( characterHeight / 2 ) );
						// Bottom Left
						glTexCoord2f( cellX, cellY + cellSize );
						glVertex2f( finalX, y + ( characterHeight / 2 ) );
					}
				}
				// End Drawing
				glEnd();
			}
			// Pop out of this "subMatrix"
			glPopMatrix();
		}
		// Stop applying attributes
		glPopAttrib();
	}

	private int[] getMatchedGlyphCoordinate( int curChar )
	{
		int xGlyph = 0;
		int yGlyph = 0;

		for ( int yChar = 0; yChar < charOrder.length; yChar++ )
		{
			// For each column of each row
			for ( int xChar = 0; xChar < charOrder[yChar].length; xChar++ )
			{
				// If current char is equal to charOrder[9][9]... or is equal to a space
				// NOTE: This code assumes the last character in the character array is a space
				if ( curChar == ( int ) ( charOrder[gridSize - 1][gridSize - 1] ) )
				{
					xGlyph = gridSize - 1;
					yGlyph = gridSize - 1;
					return new int[]
					{ xGlyph, yGlyph };
				}
				else if ( curChar == ( int ) charOrder[yChar][xChar] )
				{
					// Found glyph, so we'll use it
					xGlyph = xChar;
					yGlyph = yChar;
					return new int[]
					{ xGlyph, yGlyph };
				}
			}
		}

		return new int[]
		{ 9, 9 };
	}

	@Override
	public void cleanup()
	{
		glDeleteTextures( fontTexture );
	}
}
