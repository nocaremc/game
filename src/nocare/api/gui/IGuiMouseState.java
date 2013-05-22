package nocare.api.gui;

public interface IGuiMouseState
{
	/**
	 * Set the click state of this gui object
	 * @param state
	 */
	public void setState(ButtonState state);
	
	
	/**
	 * @return the click state of this gui object
	 */
	public ButtonState getState();
}
