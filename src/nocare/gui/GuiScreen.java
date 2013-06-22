package nocare.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.luaj.vm2.LuaValue;
import nocare.App;
import nocare.api.gui.ButtonState;
import nocare.api.gui.IGuiMouseState;
import nocare.api.gui.IGuiHandleInput;
import nocare.gui.input.MouseListener;
import nocare.util.GeneralUtils;

public class GuiScreen extends Gui implements IGuiHandleInput {
	private GuiButton[] buttons;
	private MouseListener mouse;
	private String resourcePath;
	private String name;

	public GuiScreen() {
		mouse = new MouseListener();
	}

	public void init() {
		for ( GuiButton button : buttons ) {
			mouse.addObject( button );
		}
	}

	/*
	 * Setters
	 */
	public void setName( String name ) {
		this.name = name;
	}

	public void setButtons( GuiButton[] buttons ) {
		this.buttons = buttons;
	}

	public void setResourcePath( String path ) {
		resourcePath = path;
	}

	/*
	 * Getters
	 */

	public String getName() {
		return name;
	}

	public Gui[] getButtons() {
		return buttons;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void update() {
		mouse.update();
		for ( GuiButton button : buttons ) {
			handleInput( button );
		}
	}

	@Override
	public void render() {
		int width = App.getScreenWidth();
		int height = App.getScreenHeight();

		glMatrixMode( GL_PROJECTION );
		glPushMatrix();
		glLoadIdentity();
		gluOrtho2D( 0, width, 0, height );
		glScalef( 1, -1, 1 );
		glTranslatef( 0, -height, 0 );
		glMatrixMode( GL_MODELVIEW );
		glDisable( GL_DEPTH_TEST );
		// Draw background
		glBegin( GL_QUADS );
		{
			if ( backgroundColor != null )
				GeneralUtils.glColorShortcut( backgroundColor );

			glVertex2f( 0, 0 );
			glVertex2f( 0, height );
			glVertex2f( width, height );
			glVertex2f( width, 0 );
		}
		glEnd();

		for ( GuiButton b : buttons ) {
			b.render();
			//System.out.println(b.isMouseOver());

		}

		// I don't want to set the same opengl settings for every button. 
		// We can reduce a least a tiny bit of overhead by doing it in one go
		font.renderBegin();
		for ( GuiButton b : buttons ) {
			b.renderText();
		}
		font.renderEnd();

		glMatrixMode( GL_PROJECTION );
		glPopMatrix();
		glMatrixMode( GL_MODELVIEW );
		glEnable( GL_DEPTH_TEST );
	}

	@Override
	public void handleInput( IGuiMouseState gui ) {
		if ( ( gui.getState() == ButtonState.CLICKED ) ) {
			// Associate lua action file for this guiscreen with the lua engine
			App.getLua().get( "dofile" ).call( LuaValue.valueOf( resourcePath + "buttonActions.lua" ) );

			// The function we call for clicks, within lua fine
			LuaValue clickAction = App.getLua().get( "returnClick" );

			// Value we pass to lua function. Button ID in this case
			LuaValue returnValue = clickAction.call( LuaValue.valueOf( ( ( GuiButton ) gui ).getID() ) );

			// Set the button's state to none/null so we don't grab it again
			( ( GuiButton ) gui ).setState( ButtonState.NONE );

			// String value of returnValue
			String sReturnValue = returnValue.tojstring();

			// Have App send the command to the proper place
			App.delegateLuaActionResponse( sReturnValue );
		}
	}
}
