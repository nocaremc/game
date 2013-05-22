package nocare;

import nocare.api.IHandleLua;
import nocare.util.parser.GuiLoader;

public class StateMachine implements IHandleLua
{	
	/* Constants */
	private static final String QUIT = "QUIT";
	@SuppressWarnings( "unused" )
	private static final String TITLE = "TITLE";
	@SuppressWarnings( "unused" )
	private static final String CONTINUE = "CONTINUE";
	@SuppressWarnings( "unused" )
	private static final String PAUSE = "PAUSE";
	private static final String GAME = "GAME";
	@SuppressWarnings( "unused" )
	private static final String SETTINGS = "SETTINGS";
	
	public static enum GameState { TITLE, QUIT, CONTINUE, PAUSE, GAME, SETTINGS };
	
	// Current state machine is in
	private GameState currentState;
	
	// Timestamp in nanoseconds of last state change
	private long lastStateChange;
	
	public StateMachine()
	{
		// Init defaults
		currentState = GameState.TITLE;
		lastStateChange = System.nanoTime();
		App.changeCurrentGuiScreen( GuiLoader.loadGui( getGuiScreenPathForState() ) );
	}
	
	/*
	 * Getters
	 */
	
	/**
	 * @return Current state the machine is in
	 */
	public GameState getState()
	{
		return currentState;
	}
	
	/*
	 * Setters
	 */
	
	/**
	 * Public option to change to a target state
	 * @param targetState
	 */
	public void setState(GameState targetState)
	{
		tryChangeState(targetState);
	}
	
	/**
	 * Attempts to change the StateMachine current state to passed state
	 * @param targetState
	 */
	private void tryChangeState(GameState targetState)
	{
		// If we are trying to change to the same state... just abort
		if (targetState == currentState)
			return;
		
		// Current time in nano seconds
		long curTime = System.nanoTime();
		
		// Time passed since last state change
		long timePassed = curTime - lastStateChange;
		
		// Divide time passed by 1 million so we are in millisecond precision
		long timePassedMilli = timePassed / 1000000L;
		
		// Dont allow state changes to occur more than once a second
		if (timePassedMilli < 1000L)
		{
			System.out.println(timePassedMilli);
			return;
		}
		
		// Change the state to one passed in
		currentState = targetState;
		
		// There is no gui for quiting the game, so don't try to load one
		if (currentState != GameState.QUIT)
			App.changeCurrentGuiScreen( GuiLoader.loadGui( getGuiScreenPathForState() ) );
		
		// Update last state change time stamp
		lastStateChange = System.nanoTime();
	}
	
	private String getGuiScreenPathForState()
	{
		switch (currentState)
		{
			case CONTINUE:
				break;
			case GAME:
				return "res/gui/HUD/";
			case PAUSE:
				break;
			case QUIT:
				break;
			case SETTINGS:
				break;
			case TITLE:
				return "res/gui/titleScreen/";
			default:
				return null;
		}
		return null;
	}
	
	/**
	 * Update StateMachine / handle states
	 */
	protected void update()
	{
		if (currentState == GameState.QUIT)
		{
			App.endGame( false );
		}
	}
	
	/**
	 * See IHandleLua.handleLua
	 */
	@Override
	public void handleLua( String commandFromLua )
	{
		// String constants are uppercase, so for comparing purpose ensure input is uppercase
		switch(commandFromLua.toUpperCase())
		{
			case QUIT:
			{
				tryChangeState(GameState.QUIT);
				break;
			}
			case GAME:
			{
				tryChangeState(GameState.GAME);
				break;
			}
			default:
			{
				System.out.println("Unkown command: "+commandFromLua);
				break;
			}
		}
	}
}
