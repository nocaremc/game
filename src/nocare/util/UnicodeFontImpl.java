package nocare.util;

import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BIT;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import nocare.gui.GuiButton;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.ResourceLoader;

public class UnicodeFontImpl {
	private UnicodeFont unicodeFont;
	private Font awtFont;
	private float fontSize = 24f;

	public UnicodeFontImpl() {
	}

	@SuppressWarnings( "unchecked" )
	private void init() {
		// Get truetype font inputstream
		InputStream in = ResourceLoader.getResourceAsStream( "/res/font/DejaVuSans.ttf" );

		// Try to create a truetype awtFont from input stream 
		try {
			awtFont = Font.createFont( Font.TRUETYPE_FONT, in );
		}
		catch ( FontFormatException | IOException e1 ) {
			System.out.println( "Could not create awtFont" );
			e1.printStackTrace();
		}

		// Derive font properties
		awtFont = awtFont.deriveFont( fontSize ); // set font size

		// Create a unicode font from awtFont
		unicodeFont = new UnicodeFont( awtFont );

		// Set the color to white
		unicodeFont.getEffects().add( new ColorEffect( java.awt.Color.white ) );

		// Set the glyps from font
		unicodeFont.addAsciiGlyphs();

		// Attempt to load font glyps
		try {
			unicodeFont.loadGlyphs();
		}
		catch ( SlickException e ) {
			System.out.println( "Could not load glyphs" );
			e.printStackTrace();
		}
	}

	public UnicodeFontImpl setFontSize( float fontSize ) {
		UnicodeFontImpl font = new UnicodeFontImpl();
		font.fontSize = fontSize;
		font.init();
		return font;
	}

	/**
	 * Opening opengl calls before rendering text
	 */
	public void renderBegin() {
		glPushAttrib( GL_TEXTURE_BIT | GL_ENABLE_BIT );
		glEnable( GL_BLEND );
		glBlendFunc( GL_ONE, GL_ONE_MINUS_SRC_ALPHA );
	}

	/**
	 * Ending opengl calls before rendering text
	 */
	public void renderEnd() {
		glDisable( GL_BLEND );
		glPopAttrib();
	}

	public void renderGuiButton( GuiButton button ) {
		/*
		unicode font wants a Color object of the type in slick
		I've used primarily opengl's throughout, and wont stop
		so some extra work is needed. Perhaps in the future i'll create
		a color helper that can return the proper types of color objects
		with just lwjgl, java base, and slick, I have 3 color objects to choose from.
		Quite anoying...
		*/

		// Get opengl color object
		org.lwjgl.util.Color glColor = button.getFontcolor();

		// create slick2d color object from data of opengl one
		org.newdawn.slick.Color slickColor = new Color( glColor.getRed(), glColor.getGreen(), glColor.getRed() );

		float fontHeight = unicodeFont.getLineHeight();
		float fontWidth = unicodeFont.getWidth( button.getText() );

		// draw font - position + dimension/2 - stringDimension/2
		unicodeFont.drawString( button.getX() + ( button.getWidth() / 2 ) - ( fontWidth / 2 ), button.getY() + ( button.getHeight() / 2 ) - ( fontHeight / 2 ),
				button.getText(), slickColor );
	}

	public UnicodeFont getFont() {
		return unicodeFont;
	}
}
