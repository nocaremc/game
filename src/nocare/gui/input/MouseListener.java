package nocare.gui.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;

import nocare.App;
import nocare.api.gui.IGuiMouseActions;
import nocare.gui.Gui;

public class MouseListener
{
	// The Maximum amount of gui's we'll poll for events
	private static final int pointBuffer = 1024;

	// List of Gui objects to poll
	List<Gui> guiObjects = new ArrayList<Gui>();

	// Array of points containing the last mouse location
	Point[] lastMouseLocation = new Point[pointBuffer];

	// array of long containing time stamp for last click event handled 
	long[] lastEventTimeStamp = new long[pointBuffer];

	public void update()
	{
		// While there are mouse events to read
		while ( Mouse.next() )
		{
			int mX = Mouse.getX();
			// Need to flip the y coordinate, because we flipped it for gui display. Seems to do the trick
			int mY = App.getScreenHeight() - Mouse.getY();

			// Iterate over objects
			for ( int i = 0; i < guiObjects.size(); i++ )
			{
				// Check if class implements mouse actions interface
				if ( guiObjects.get( i ) instanceof IGuiMouseActions )
				{
					// Cast object to an interface to gain access to it's functions. Fuck I love java
					IGuiMouseActions gui = ( IGuiMouseActions ) guiObjects.get( i );

					// If the mouse is within the gui object's bounds
					if ( gui.isMouseOver( mX, mY ) )
					{
						// Calculate how much time has passed since the last event was handled, and convert to Milliseconds 
						long timeSinceLastEvent = ( System.nanoTime() - lastEventTimeStamp[i] ) / 1000000L;

						// Integer value of mouse button clicked. 0 left, 1 right, 2 scrollwheel, -1 mousemove/none
						int button = Mouse.getEventButton();

						// Only handle click events if last click happened over 80 milliseconds ago
						// This is somewhat system dependant, but shouldnt be a problem with gui items.
						// 10 actions can happen in a second. I can't click that fast
						if ( timeSinceLastEvent > 100L && button != -1 )
						{
							// True of clicked, false if released
							boolean state = Mouse.getEventButtonState();

							// Set the time stamp for this object
							lastEventTimeStamp[i] = System.nanoTime();

							// Send state to appropriate object
							if ( state )
							{
								gui.mouseClicked( button, mX, mY );
							}
							else
							{
								gui.mouseReleased( button, mX, mY );
							}
						}

						// Mouse moved, send it to object
						if ( button == -1 && mousePositionChanged( i ) )
						{
							gui.mouseMoved( lastMouseLocation[i].getX(), lastMouseLocation[i].getY(), Mouse.getDX(), -1 * Mouse.getDY() );

							// Set the last point where a move happened
							lastMouseLocation[i] = new Point( mX, mY );
						}
					}
				}
			}
		}
	}

	/**
	 * Add a Gui object to the listener
	 * @param object
	 */
	public void addObject( Gui object )
	{
		// Ensure gui object implements IGuiMouseActions
		if ( object instanceof IGuiMouseActions )
		{
			// Add object to objects list
			guiObjects.add( object );

			// Set 0'd point at identical index of this object in the objects array
			lastMouseLocation[guiObjects.indexOf( object )] = new Point( 0, 0 );

			// Repeat that wall of text here
			lastEventTimeStamp[guiObjects.indexOf( object )] = System.nanoTime();
		}
		else
		{
			// Object is not of the correct type. Throw exception and quit
			IllegalArgumentException e = new IllegalArgumentException();
			e.printStackTrace();
			App.endGame( true );
		}
	}

	private boolean mousePositionChanged( int objectIndex )
	{
		return ( Mouse.getX() != lastMouseLocation[objectIndex].getX() && App.getScreenHeight() - Mouse.getY() != lastMouseLocation[objectIndex].getY() );
	}
}
