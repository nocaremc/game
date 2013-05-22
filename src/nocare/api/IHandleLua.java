package nocare.api;

/** 
 * Any Class that handles commands from lua should implement this interface
 * App is a special exception, since it specifically calls this function in other classes
 * @author Nocare
 */
public interface IHandleLua
{
	/**
	 * Handle a command String pulled from a lua file
	 * @param commandFromLua
	 */
	public void handleLua(String commandFromLua);
}
