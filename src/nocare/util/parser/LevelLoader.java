package nocare.util.parser;

import java.util.ArrayList;

import org.newdawn.slick.SlickException;

import nocare.App;
import nocare.Level;
import nocare.util.xml.XMLElement;
import nocare.util.xml.XMLElementList;
import nocare.util.xml.XMLParser;

import static nocare.util.xml.XMLUtil.*;

/*
 * Loads a level.xml file, parsing into a Level object
 */
public class LevelLoader {
	// Don't Instantiate this class
	private LevelLoader() {
	}

	/* Loads xml file, parses, and returns a level object */
	public static Level loadLevel( String path ) {
		// Export variables
		ArrayList<ArrayList<String>> forceList;
		ArrayList<ArrayList<String>> modelList;
		String levelName;
		int[] pixelDimension;

		XMLParser parser = new XMLParser();
		XMLElement root = null;
		try {
			root = parser.parse( path );
		}
		catch ( SlickException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ( root == null )
			App.endGame( true );

		XMLElement titleTag = root.getChildrenByName( "Title" ).get( 0 );
		assertNotNull( titleTag.getContent() );
		levelName = titleTag.getContent();

		// Constants
		XMLElement constantTag = root.getChildrenByName( "Constants" ).get( 0 );
		forceList = getForces( constantTag );

		// Models
		XMLElement modelTag = root.getChildrenByName( "Models" ).get( 0 );
		XMLElementList models = modelTag.getChildrenByName( "model" );
		modelList = getModels( models );

		// Get Geometry tag
		XMLElement geometryTag = root.getChildrenByName( "Geometry" ).get( 0 );

		// Get Map Size in pixels
		XMLElement dimensionTag = geometryTag.getChildrenByName( "dimensions" ).get( 0 );
		pixelDimension = getDimensions( dimensionTag );

		// Create level object and set values
		Level level = new Level();
		level.setName( levelName );
		level.setForces( forceList );
		level.setModels( modelList );
		level.setDimensions( pixelDimension );

		return level;
	}

	/* retrieve x,y,z dimension of the map, in pixel precision */
	private static int[] getDimensions( XMLElement dimensionTag ) {
		int[] dimensions = new int[3];

		assertNotNull( dimensionTag.getAttribute( "width" ) );
		assertNotNull( dimensionTag.getAttribute( "height" ) );
		assertNotNull( dimensionTag.getAttribute( "depth" ) );

		dimensions[0] = Integer.valueOf( dimensionTag.getAttribute( "width" ) );
		dimensions[1] = Integer.valueOf( dimensionTag.getAttribute( "height" ) );
		dimensions[2] = Integer.valueOf( dimensionTag.getAttribute( "depth" ) );

		return dimensions;
	}

	/* Retrieve forces from xml file */
	private static ArrayList<ArrayList<String>> getForces( XMLElement constant ) {
		// Forces
		XMLElementList forces = constant.getChildrenByName( "force" );
		ArrayList<ArrayList<String>> forceList = new ArrayList<ArrayList<String>>( forces.size() );
		for ( int i = 0; i < forces.size(); i++ ) {
			XMLElement element = forces.get( i );
			assertNotNull( element );
			ArrayList<String> tmp = new ArrayList<String>( 2 );
			tmp.add( element.getAttribute( "name" ) + "" );
			tmp.add( element.getAttribute( "power" ) + "" );
			forceList.add( tmp );
		}

		assertNotNull( forceList );

		return forceList;
	}

	private static ArrayList<ArrayList<String>> getModels( XMLElementList models ) {
		ArrayList<ArrayList<String>> modelList = new ArrayList<ArrayList<String>>( models.size() );

		for ( int i = 0; i < models.size(); i++ ) {
			XMLElement element = models.get( i );
			assertNotNull( element );
			ArrayList<String> tmp = new ArrayList<String>( 2 );
			tmp.add( element.getAttribute( "path" ) );
			tmp.add( element.getAttribute( "name" ) );

			tmp.add( element.getAttribute( "posX" ) );
			tmp.add( element.getAttribute( "posY" ) );
			tmp.add( element.getAttribute( "posZ" ) );

			modelList.add( tmp );
		}

		assertNotNull( modelList );

		return modelList;
	}
}
