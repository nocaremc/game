package nocare;

import java.util.ArrayList;
import java.util.List;

import nocare.geometry.Face;

/**
 * @author Nocare
 *         Class represents a tile object in the game
 *         Various flag determine which faces of a cube are rendered
 */
public class Tile
{
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	public static final int UP = 4;
	public static final int DOWN = 5;

	public static enum Direction
	{
		NORTH, SOUTH, EAST, WEST, UP, DOWN
	};

	private List<Face> visibleFaces = new ArrayList<Face>( 6 );

	protected void setVisibleFaces( Face north, Face south, Face east, Face west, Face up, Face down )
	{
		visibleFaces.set( NORTH, north );
		visibleFaces.set( SOUTH, south );
		visibleFaces.set( EAST, east );
		visibleFaces.set( WEST, west );
		visibleFaces.set( UP, up );
		visibleFaces.set( DOWN, down );
	}
}