package nocare.gui;

import static org.lwjgl.opengl.GL11.*;

import nocare.api.gui.ButtonState;
import nocare.api.gui.IGuiMouseActions;
import nocare.api.gui.IGuiMouseState;
import nocare.util.GeneralUtils;
import nocare.util.math.Math2D;

import org.lwjgl.util.Color;

public class GuiButton extends Gui implements IGuiMouseActions, IGuiMouseState {
	private String text;

	private ButtonState buttonState;

	private Color backgroundColorOver;

	public GuiButton() {
	}

	@Override
	public void render() {
		// Color we use here will depend on whethere the mouse is over the button or not
		// A second one of these could set a third color while clicking.
		Color c = ( buttonState == ButtonState.OVER ) ? backgroundColorOver : backgroundColor;

		// Set the color to use
		GeneralUtils.glColorShortcut( c );

		// Draw verts
		glBegin( GL_QUADS );
		{
			glVertex2f( x, y );
			glVertex2f( x, y + height );
			glVertex2f( x + width, y + height );
			glVertex2f( x + width, y );
		}
		glEnd();
	}

	public void renderText() {
		if ( text != null ) {
			GeneralUtils.glColorShortcut( fontColor );
			font.renderGuiButton( this );
		}
	}

	/*
	 * Setters
	 */

	/**
	 * Set the text value for this button
	 * @param text
	 */
	public void setText( String text ) {
		this.text = text;
	}

	public void setBackgroundColorOver( Color color ) {
		this.backgroundColorOver = color;
	}

	/**
	 * Set the click state of this button
	 */
	@Override
	public void setState( ButtonState state ) {
		buttonState = state;
	}

	/*
	 * Getters
	 */

	/**
	 * @return Text value of this button's text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return ButtonState of this button
	 */
	@Override
	public ButtonState getState() {
		return buttonState;
	}

	@Override
	public boolean isMouseOver( int mX, int mY ) {
		boolean ret = Math2D.quadContainsPoint( new Math2D.Square( x, y, width, height ), new Math2D.Point( mX, mY ) );
		if ( !ret ) {
			buttonState = ButtonState.NONE;
		}
		return ret;
	}

	@Override
	public void mouseClicked( int mouseEventButton, float mx, float my ) {
		buttonState = ButtonState.CLICKED;
	}

	@Override
	public void mouseReleased( int mouseEventButton, float mx, float my ) {
		buttonState = ButtonState.RELEASED;
	}

	@Override
	public void mouseMoved( float lastdx, float lastdy, float dx, float dy ) {
		buttonState = ButtonState.OVER;
	}
}
