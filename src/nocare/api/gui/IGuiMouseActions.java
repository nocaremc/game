package nocare.api.gui;

public interface IGuiMouseActions
{
	// Return true if mouse is over bounding box
	public boolean isMouseOver( int mX, int mY );

	// Handle a mouse click
	public void mouseClicked( int mouseEventButton, float mx, float my );

	// Handle a mouse release
	public void mouseReleased( int mouseEventButton, float mx, float my );

	// Handle the mouse moving over
	public void mouseMoved( float lastdx, float lastdy, float dx, float dy );
}
