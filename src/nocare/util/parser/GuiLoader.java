package nocare.util.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lwjgl.util.Color;
import org.newdawn.slick.SlickException;

import nocare.App;
import nocare.gui.GuiButton;
import nocare.gui.GuiScreen;
import nocare.util.xml.XMLElement;
import nocare.util.xml.XMLElementList;
import nocare.util.xml.XMLException;
import nocare.util.xml.XMLParser;

import static nocare.util.xml.XMLUtil.*;

public class GuiLoader {
	private static String[] requiredFiles = { "structure.xml", "buttonActions.lua" };

	// Don't allow instantiation
	private GuiLoader() {
	}

	/**
	 * Loads a Gui object with all the data in associated zip file
	 * 
	 * @param path
	 * @return
	 */
	public static GuiScreen loadGui( String file ) {
		File f = new File( file );

		if ( f.isDirectory() ) {
			return loadGuiPath( file );
		}

		Structure structure = new Structure();

		System.out.println( "Opening zip file: " + file + "\n[" );
		// Open given zip file
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile( file );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

		// Get a list of the files inside the zip
		Enumeration<? extends ZipEntry> fileList = zipFile.entries();

		// Test the list to ensure required files exist
		ensureContainsNeededFiles( fileList );

		// Parse structure.xml
		structure = openZippedStructure( zipFile, zipFile.getEntry( "structure.xml" ) );

		// Print a file list of the zip
		while ( fileList.hasMoreElements() ) {
			System.out.println( "d" + fileList.nextElement().getName() );
		}

		try {
			zipFile.close();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

		System.out.println( "]" );

		return setupGuiScreen( structure, file );
	}

	private static GuiScreen loadGuiPath( String folder ) {
		ensureContainsNeededFiles( folder );
		Structure structure = openStructure( folder );

		return setupGuiScreen( structure, folder );
	}

	private static GuiScreen setupGuiScreen( Structure structure, String path ) {
		GuiScreen g = new GuiScreen();
		g.setButtons( structure.buttons );
		g.setBackgroundColor( structure.backgroundColor );
		g.setResourcePath( path );
		g.init();
		return g;
	}

	private static void ensureContainsNeededFiles( String folder ) {
		for ( int i = 0; i < requiredFiles.length; i++ ) {
			File f = new File( folder + requiredFiles[i] );
			try {
				if ( !f.exists() )
					throw new FileNotFoundException( "Could not locate: " + requiredFiles[i] );
			}
			catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ensures the supplied file list contains those
	 * neccessary to create a gui with.
	 * 
	 * @param fileList
	 * @throws FileNotFoundException
	 */
	private static void ensureContainsNeededFiles( Enumeration<? extends ZipEntry> fileList ) {
		List<String> zippedFiles = new ArrayList<String>();

		for ( ; fileList.hasMoreElements(); ) {
			zippedFiles.add( String.valueOf( fileList.nextElement().getName() ) );
		}

		for ( int i = 0; i < requiredFiles.length; i++ ) {
			boolean fileExists = false;

			for ( Iterator<String> iter = zippedFiles.iterator(); iter.hasNext(); ) {
				String name = iter.next();
				if ( requiredFiles[i].equals( name ) )
					fileExists = true;
			}

			try {
				if ( !fileExists )
					throw new FileNotFoundException( "Could not locate: " + requiredFiles[i] );
			}
			catch ( FileNotFoundException e ) {
				System.out.println( "Could not locate: " + requiredFiles[i] );
				e.printStackTrace();
			}
		}
		System.out.println( "\tZip contains required files." );
	}

	private static Structure parseStructure( XMLElement root ) {
		Structure structure = new Structure();

		// Ensure this tag exists, and there is only 1
		assertSize( root, "background", 1 );

		// Create background element
		XMLElement backgroundFile = root.getFirstChild( "background" );
		structure.backgroundImg = backgroundFile.getAttribute( "value" );

		if ( root.hasChild( "backgroundColor" ) ) {
			XMLElement backgroundColor = root.getFirstChild( "backgroundColor" );
			int r, g, b, a;
			try {
				r = backgroundColor.getIntAttribute( "r" );
				g = backgroundColor.getIntAttribute( "g" );
				b = backgroundColor.getIntAttribute( "b" );
				a = backgroundColor.getIntAttribute( "a" );
				structure.backgroundColor = new Color( r, g, b, a );
			}
			catch ( XMLException e ) {
				e.printStackTrace();
			}
		}

		// Ensure this tag exists, and there is only 1
		assertSize( root, "Objects", 1 );

		// Get element object for Objects tag
		XMLElement objects = root.getFirstChild( "Objects" );

		// Bring all objects into a list
		XMLElementList obs = objects.getChildren();

		// For each object, associate with type.
		XMLElementList buttons = new XMLElementList();
		for ( int i = 0; i < obs.size(); i++ ) {
			if ( obs.get( i ).getName().equals( "Button" ) ) {
				buttons.add( obs.get( i ) );
			}
			else {
				// More if statements for other obects
				System.out.println( "Other objects exist: " + obs.get( i ).getName() );
			}
		}
		// Parse buttons
		structure.buttons = parseButtons( buttons );

		return structure;
	}

	private static Structure openStructure( String path ) {
		XMLParser xmlFile = new XMLParser();
		XMLElement root = null;

		try {
			root = xmlFile.parse( path + "structure.xml" );
		}
		catch ( SlickException e ) {
			System.out.println( "GuiLoader: xmlFile failed to parse - " + path + "structure.xml" );
			e.printStackTrace();
		}

		return parseStructure( root );
	}

	private static Structure openZippedStructure( ZipFile zipFile, ZipEntry structureFile ) {
		System.out.println( "\tBegin parsing..." );
		InputStream inputStream = null;

		// Get an input stream for xmlfile from zip file
		try {
			inputStream = zipFile.getInputStream( structureFile );
		}
		catch ( IOException e ) {
			System.out.println( "GuiLoader: openZippedStructure - Failed to get structureFile's InputStream from zip file." );
			e.printStackTrace();
		}

		XMLParser xmlFile = new XMLParser();

		// Open xml file for parsing from the inputstream
		XMLElement root = null;
		try {
			root = xmlFile.parse( "structure.xml", inputStream );
		}
		catch ( XMLException e ) {
			e.printStackTrace();
		}

		return parseStructure( root );
	}

	private static GuiButton[] parseButtons( XMLElementList buttons ) {
		GuiButton[] returnList = new GuiButton[buttons.size()];
		int lastID = -1;
		for ( int i = 0; i < buttons.size(); i++ ) {
			GuiButton thisButton = new GuiButton();
			XMLElement button = buttons.get( i );

			// Get button id
			int id = Integer.valueOf( button.getFirstChild( "id" ).getContent() );

			// Check if duplicate, raise error and increment id
			if ( id == lastID ) {
				System.out.println( "Duplicate Button ID detected" );
				id += 1;
			}

			lastID = id;

			// Set ID
			thisButton.setID( id );
			// Set Width
			thisButton.setWidth( Integer.valueOf( button.getFirstChild( "width" ).getContent() ) );
			// Set Height
			thisButton.setHeight( Integer.valueOf( button.getFirstChild( "height" ).getContent() ) );
			// Set Title
			thisButton.setText( String.valueOf( button.getFirstChild( "title" ).getContent() ) );
			// Set Font Size
			thisButton.setFontSize( Float.valueOf( button.getFirstChild( "font" ).getFirstChild( "size" ).getContent() ) );

			String x = button.getFirstChild( "x" ).getContent();
			int xOffset;

			// Setup position offsets
			if ( button.getFirstChild( "x" ).hasAttribute( "offset" ) ) {
				xOffset = Integer.valueOf( button.getFirstChild( "x" ).getAttribute( "offset" ) );
			}
			else {
				xOffset = 0;
			}

			String y = button.getFirstChild( "y" ).getContent();
			int yOffset;

			if ( button.getFirstChild( "y" ).hasAttribute( "offset" ) ) {
				yOffset = Integer.valueOf( button.getFirstChild( "y" ).getAttribute( "offset" ) );
			}
			else {
				yOffset = 0;
			}

			// Set position of button, taking strings as shortcut options
			if ( x.equals( "center" ) ) {
				int width = App.getScreenWidth();
				int buttonWidth = thisButton.getWidth();
				int center = ( width / 2 ) - ( buttonWidth / 2 );

				thisButton.setX( center + xOffset );
			}
			else if ( x.equals( "max" ) ) {
				int width = App.getScreenWidth();
				int buttonWidth = thisButton.getWidth();

				thisButton.setX( width - buttonWidth + xOffset );
			}
			else if ( x.equals( "min" ) ) {
				thisButton.setX( 0 + xOffset );
			}
			else {
				thisButton.setX( Integer.valueOf( x ) + xOffset );
			}

			if ( y.equals( "center" ) ) {
				int height = App.getScreenHeight();
				int buttonHeight = thisButton.getHeight();
				int center = ( height / 2 ) - ( buttonHeight / 2 );

				thisButton.setY( center + yOffset );
			}
			else if ( y.equals( "min" ) ) {
				thisButton.setY( 0 + yOffset );
			}
			else if ( y.equals( "max" ) ) {
				int height = App.getScreenHeight();
				int buttonHeight = thisButton.getHeight();

				thisButton.setY( height - buttonHeight + yOffset );
			}
			else {
				thisButton.setY( Integer.valueOf( y ) + yOffset );
			}

			// Background
			int[] rgb = new int[4];
			rgb[0] = Integer.valueOf( button.getFirstChild( "backgroundColor" ).getAttribute( "r" ) );
			rgb[1] = Integer.valueOf( button.getFirstChild( "backgroundColor" ).getAttribute( "g" ) );
			rgb[2] = Integer.valueOf( button.getFirstChild( "backgroundColor" ).getAttribute( "b" ) );
			rgb[3] = Integer.valueOf( button.getFirstChild( "backgroundColor" ).getAttribute( "a" ) );

			thisButton.setBackgroundColor( new Color( rgb[0], rgb[1], rgb[2], rgb[3] ) );

			// Hover background
			int[] rgbOver = new int[4];
			rgbOver[0] = Integer.valueOf( button.getFirstChild( "backgroundColorOver" ).getAttribute( "r" ) );
			rgbOver[1] = Integer.valueOf( button.getFirstChild( "backgroundColorOver" ).getAttribute( "g" ) );
			rgbOver[2] = Integer.valueOf( button.getFirstChild( "backgroundColorOver" ).getAttribute( "b" ) );
			rgbOver[3] = Integer.valueOf( button.getFirstChild( "backgroundColorOver" ).getAttribute( "a" ) );

			thisButton.setBackgroundColorOver( new Color( rgbOver[0], rgbOver[1], rgbOver[2], rgbOver[3] ) );

			// Gradient Start
			int[] rgbStart = new int[4];
			rgbStart[0] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "start" ).getAttribute( "r" ) );
			rgbStart[1] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "start" ).getAttribute( "g" ) );
			rgbStart[2] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "start" ).getAttribute( "b" ) );
			rgbStart[3] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "start" ).getAttribute( "a" ) );

			Color startColor = new Color( rgbStart[0], rgbStart[1], rgbStart[2], rgbStart[3] );

			// Gradient End
			int[] rgbEnd = new int[4];
			rgbEnd[0] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "end" ).getAttribute( "r" ) );
			rgbEnd[1] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "end" ).getAttribute( "g" ) );
			rgbEnd[2] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "end" ).getAttribute( "b" ) );
			rgbEnd[3] = Integer.valueOf( button.getFirstChild( "gradient" ).getFirstChild( "end" ).getAttribute( "a" ) );

			Color endColor = new Color( rgbEnd[0], rgbEnd[1], rgbEnd[2], rgbEnd[3] );

			thisButton.setBackgroundGradient( startColor, endColor );

			int[] rgbFont = new int[4];
			rgbFont[0] = Integer.valueOf( button.getFirstChild( "font" ).getFirstChild( "color" ).getAttribute( "r" ) );
			rgbFont[1] = Integer.valueOf( button.getFirstChild( "font" ).getFirstChild( "color" ).getAttribute( "g" ) );
			rgbFont[2] = Integer.valueOf( button.getFirstChild( "font" ).getFirstChild( "color" ).getAttribute( "b" ) );
			rgbFont[3] = 255;//Integer.valueOf( button.getFirstChild( "font" ).getFirstChild( "color" ).getAttribute( "a" ) );

			Color fontColor = new Color( rgbFont[0], rgbFont[1], rgbFont[2], rgbFont[3] );

			thisButton.setFontColor( fontColor );

			//System.out.println( thisButton );
			returnList[i] = thisButton;
		}

		return returnList;
	}

	private static class Structure {
		@SuppressWarnings( "unused" )
		public String backgroundImg;
		public Color backgroundColor;
		public GuiButton[] buttons;
	}

	/*
	 * <GuiScreen>
	 * <background value="background.png" />
	 * <Objects>
	 * <Button>
	 * <id>0</id>
	 * <width>200</width>
	 * <height>30</height>
	 * <x offset="-100">center</x>
	 * <y offset="90">height</y>
	 * <backgroundColor r="255" g="0" b="0" />
	 * <font>
	 * <color r="120" g="40" b="200" />
	 * <size>16</size>
	 * </font>
	 * </Button>
	 * </Objects>
	 * </GuiScreen>
	 */
}
