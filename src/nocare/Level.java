package nocare;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import nocare.geometry.Model;
import nocare.util.parser.WavefrontObjectLoader;

public class Level {
	private String name;
	private ArrayList<ArrayList<String>> forces;
	private ArrayList<Model> models;

	// Dimensions of map in pixel precision
	@SuppressWarnings( "unused" )
	// TODO We going to use this? lol
	private int[] pixelDimensions;

	private float tileSizeX = 8f;
	private float tileSizeY = 4f;
	private float tileSizeZ = 8.0f;

	// TODO: remove
	private float zPos = 0.0f;

	/**
	 * I really dont fancy constructors with 50 billion parameters
	 */
	public Level() {
	}

	private ArrayList<Model> initModels( ArrayList<ArrayList<String>> modelList ) {
		ArrayList<Model> tmp = new ArrayList<Model>( modelList.size() );
		for ( int x = 0; x < modelList.size(); x++ ) {
			ArrayList<String> modelData = modelList.get( x );
			// Load a model, passing path and name
			Model model = WavefrontObjectLoader.loadModel( modelData.get( 0 ), modelData.get( 1 ) );

			// Set model's position in modelview
			float xPos = Float.valueOf( modelData.get( 2 ) );
			float yPos = Float.valueOf( modelData.get( 3 ) );
			float zPos = Float.valueOf( modelData.get( 4 ) );

			// In case the tile has a position value, transform it based on tile size and position given.
			// This allows an x of 5 to actually translate to like 40 for position
			if ( xPos != 0 )
				xPos = xPos * tileSizeX;

			if ( yPos != 0 )
				yPos = yPos * tileSizeY;

			if ( zPos != 0 )
				zPos = zPos * tileSizeZ;

			model.setPosition( xPos, yPos, zPos );

			tmp.add( model );
		}

		return tmp;
	}

	/*
	 * Getters
	 */
	public ArrayList<Model> getModels() {
		return models;
	}

	public ArrayList<ArrayList<String>> getForces() {
		return forces;
	}

	public String getName() {
		return name;
	}

	/*
	 * Setters
	 */
	public void setModels( ArrayList<ArrayList<String>> models ) {
		this.models = initModels( models );
	}

	public void setForces( ArrayList<ArrayList<String>> forces ) {
		this.forces = forces;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public void setDimensions( int[] dimension ) {
		this.pixelDimensions = dimension;
	}

	public void update() {
		App.getPlayer().update();
	}

	public void render() {
		// This moves our camera. I haven't the faintest idea why the z translation needs to exist.
		// Models have zPosition, and its rendered properly, but no matter, a z translation seems to need to take place. -2 minimum.
		// Set the x position based on character position, with 5.5f offset to approximately center character
		float cameraPositionX = -( App.getPlayer().getX() - 5.5f );

		if ( Keyboard.isKeyDown( Keyboard.KEY_W ) )
			zPos++;
		else if ( Keyboard.isKeyDown( Keyboard.KEY_S ) )
			zPos--;

		// Now translate screen based on our camera position
		GL11.glTranslatef( cameraPositionX, 0.0f, zPos - 2f );

		// Render models in scene
		// TODO: factor in loaded/unloaded sectors
		for ( Model m : models ) {

			m.render();
		}

		App.getPlayer().render();
	}
}
