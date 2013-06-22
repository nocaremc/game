package nocare;

import nocare.geometry.Skelaton;
import nocare.util.IMaid;
import nocare.util.parser.SkelatonLoader;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class Engine implements IMaid {
	private float fieldOfView = 38f;

	// Title to be used on the window
	private String WINDOW_TITLE = "Game: Want to buy name!";

	// Should this program be running?
	private boolean running = true;

	// Handle to App, our game proxy
	private App app;

	/**
	 * Program entry point. Creates new Engine instance and initializes it
	 * @param args - Not used at this time
	 */
	public static void main( String[] args ) {
		Engine p = new Engine();
		p.init();
	}

	/**
	 * Set up OpenGL, App, and start the Main loop
	 */
	private void init() {
		app = new App( this );

		// This is called inside of app. Sorry... the opengl setup requires the game settings
		// So app needs to set that up, and then it calls opengl in this class
		// It's wonky, but some things app sets up need an opengl context, so this loops round...
		// Could have the gameSettings here, but prefer them to be in App.
		//setupGL();
		mainLoop();
	}

	/**
	 * This is the main application loop
	 */
	private void mainLoop() {
		//float xRot = 0;
		//float yRot = 0;
		//float zRot = 0;

		long loopStart;
		long loopLength;
		long targetLoopTime = ( long ) ( ( 1000L / App.getSettings().getRefreshRate() ) * 1000000000L );

		while ( running && !Display.isCloseRequested() ) {
			// Loop starts now
			loopStart = System.nanoTime();

			glMatrixMode( GL_MODELVIEW );
			glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
			glLoadIdentity();

			// App's update function runs. Handles input, updates game, then renders
			app.update();

			// Update Display
			glMatrixMode( GL_PROJECTION );
			Display.update();

			// Get time took to complete game loop
			loopLength = System.nanoTime() - loopStart;

			// We want to update to the target fps rate.
			// So if its 60fps, then one loop should take 16 miliseconds or 16 billion nano seconds

			// If loop execution time less than desired
			if ( loopLength < targetLoopTime ) {
				// Sleep for the remaining amount of time to get a set execution time
				try {
					Thread.sleep( ( targetLoopTime - loopLength ) / 1000000000L );
				}
				catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}

			//Display.sync( 60 );
		}
		endGame( false );
	}

	/**
	 * Reshapes the game screen and or other related settings
	 */
	private void reShape() {
		int width = App.getSettings().getScreenWidth();
		int height = App.getSettings().getScreenHeight();
		float aspectRatio = ( float ) width / ( float ) height;
		float zNear = 0.3f;
		float zFar = 100f;

		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();

		gluPerspective( fieldOfView, aspectRatio, zNear, zFar );
		//glTranslatef(-viewX,0,0);
		glMatrixMode( GL_MODELVIEW );
		glLoadIdentity();

		glClearColor( 0.4f, 0.6f, 0.9f, 0f );
		glClearDepth( 1.0f );
		glEnable( GL_DEPTH_TEST );
		glDepthFunc( GL_LEQUAL );
		glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST );
		glViewport( 0, 0, width, height );
		System.out.println( "fov: " + fieldOfView );
	}

	public void setupGL() {
		try {
			//Display.setDisplayMode( new DisplayMode( WIDTH, HEIGHT ) );
			Display.setDisplayMode( App.getSettings().getDisplayMode() );
			Display.setFullscreen( App.getSettings().isFullscreen() );
			Display.setTitle( WINDOW_TITLE );
			Display.create();

			glViewport( 0, 0, App.getSettings().getScreenWidth(), App.getSettings().getScreenHeight() );
		}
		catch ( LWJGLException e ) {
			e.printStackTrace();
			System.exit( -1 );
		}

		int width = App.getSettings().getScreenWidth();
		int height = App.getSettings().getScreenHeight();
		float aspectRatio = ( float ) width / ( float ) height;
		float zNear = 0.3f;
		float zFar = 100f;
		// float yScale = ( float ) ( 1f / Math.tan( ( fieldOfView / 2f ) * (
		// float ) ( Math.PI / 180d ) ) );
		// float xScale = yScale / aspectRatio;
		// float frustumLength = zFar - zNear;

		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();

		gluPerspective( fieldOfView, aspectRatio, zNear, zFar );
		//glTranslatef(-viewX,0,0);
		glMatrixMode( GL_MODELVIEW );
		glLoadIdentity();

		glClearColor( 0.4f, 0.6f, 0.9f, 0f );
		glClearDepth( 1.0f );
		glEnable( GL_DEPTH_TEST );
		glDepthFunc( GL_LEQUAL );
		glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST );
		glViewport( 0, 0, width, height );

		glMatrixMode( GL_PROJECTION );
	}

	/**
	 * Ends the game, does any final cleanup
	 * @param flag - System.exit code
	 */
	protected void endGame( boolean crash ) {
		// Tell app to cleanup, which in turn delegates to everyone else
		app.cleanup();

		// Now so we aren't a hypocrite
		cleanup();

		// Exit process, passing a true or false return code
		System.exit( crash ? 1 : 0 );
	}

	@Override
	public void cleanup() {
		if ( Display.isActive() ) {
			Display.destroy();
		}
	}
}
