package nocare;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import nocare.StateMachine.GameState;
import nocare.entity.EntityPlayer;
import nocare.gui.GuiScreen;
import nocare.util.IMaid;
import nocare.util.parser.GuiLoader;
import nocare.util.parser.LevelLoader;

/**
 * This class serves as a static proxy for various classes.
 * Engine, Rendering, Font, etc. It sets up a singular location
 * all classes can access all of the data they need, while
 * also limiting the amount of influence said classes can have on the
 * functionality of those protected classes
 * @author Nocare
 */
public class App implements IMaid {
	private static Engine engine;
	private static GameSettings gameSettings;
	private static StateMachine stateMachine;
	private static GuiScreen currentGuiScreen;
	private static Level currentLevel;
	private static boolean isInstanced = false;
	private static LuaValue Lua;
	private static EntityPlayer entityPlayer;
	private static InputManager inputManager;

	public App( Engine engine ) {
		// Ensure class isn't being instanced by anything but Engine... well more than once
		if ( isInstanced ) {
			try {
				throw new Exception( "App cannot be instanciated again." );
			}
			catch ( Exception e ) {
				System.out.println( "App Threw and Exception while trying to throw and exception.\nDivided by Zero?" );
				e.printStackTrace();
			}
		}
		else {
			// Good to, init.
			initApp( engine );
		}
	}

	private void initApp( Engine engine ) {
		// Set instanced flag
		isInstanced = true;

		// Assign Engine reference handle
		this.engine = engine;
		gameSettings = GameSettings.loadSettingsFile();

		engine.setupGL();

		// Setup our lua referance handle
		Lua = JsePlatform.standardGlobals();

		// Set current, default gui screen
		currentGuiScreen = GuiLoader.loadGui( "res/gui/titleScreen/" );

		// Call currentScreen's init, because content of loadgui needs to be fully loaded before init is called.
		currentGuiScreen.init();

		stateMachine = new StateMachine();

		// Load entity player. Handle is held in App
		entityPlayer = new EntityPlayer();

		// Set the current level to the default level
		currentLevel = LevelLoader.loadLevel( "res/level/level_one/level_one.xml" );

		// Create input manager
		inputManager = new InputManager();

		// Set state, avoid titlescreen. TODO: remove
		stateMachine.setState( GameState.GAME );
	}

	protected void update() {
		inputManager.handleInput();

		stateMachine.update();

		if ( stateMachine.getState() == GameState.GAME ) {
			currentLevel.update();
			currentLevel.render();
		}

		currentGuiScreen.update();
		currentGuiScreen.render();
	}

	public static void changeCurrentGuiScreen( GuiScreen guiScreen ) {
		currentGuiScreen = guiScreen;
	}

	/**
	 * @param crash See Engine.endGame
	 */
	public static void endGame( boolean crash ) {
		engine.endGame( crash );
	}

	/**
	 * @return See Engine.getScreenWidth
	 */
	public static int getScreenWidth() {
		return gameSettings.getScreenWidth();
	}

	/**
	 * @return See Engine.getScreenHeight
	 */
	public static int getScreenHeight() {
		return gameSettings.getScreenHeight();
	}

	/**
	 * @return EntityPlayer handle
	 */
	public static EntityPlayer getPlayer() {
		return entityPlayer;
	}

	/**
	 * @return Currently loaded Level handle
	 */
	public static Level getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * @return Lua globals/engine handle
	 */
	public static LuaValue getLua() {
		return Lua;
	}

	public static GameSettings getSettings() {
		return gameSettings;
	}

	public static GameState getcurrentState() {
		return stateMachine.getState();
	}

	@Override
	public void cleanup() {
		//font.cleanup();
	}

	/**
	 * Gui objects pass a response from their respective action lua file and functions
	 * This function's purpose is to sort them and send them to appropriate handlers
	 * 
	 * For example, an input could be StateMachine.Continue
	 * Since the first part is StateMachine, App will send the rest of the string to the state machine
	 * 
	 * @param sReturnValue
	 */
	public static void delegateLuaActionResponse( String sReturnValue ) {
		// Expecting string parts like Target.Command. Split into parts "." needs to be escaped like you see here
		String[] splitAction = sReturnValue.split( "\\." );

		// In event the string splitting fails (invalid command)
		if ( splitAction.length < 2 ) {
			StringBuilder sb = new StringBuilder().append( "App was given a lua return target that is invalid: " );

			// Append each part of array, though it should be just one
			for ( String s : splitAction ) {
				sb.append( s );
			}

			System.out.println( sb.toString() );
			return;
		}

		// Switch the first part of string (target), and send away
		switch ( splitAction[0] ) {
			case "StateMachine": {
				stateMachine.handleLua( splitAction[1] );
				break;
			}
			default: {
				System.out.println( "App was given a lua return target it does not reconize!" );
				break;
			}
		}

	}
}
