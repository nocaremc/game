package nocare.util.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nocare.App;
import nocare.geometry.Face;
import nocare.geometry.Icosidodecahedron;
import nocare.geometry.Material;
import nocare.geometry.Model;

import org.lwjgl.util.vector.Vector3f;

public class WavefrontObjectLoader {
	// Load a model, and assign its name from within it's .obj code
	public static Model loadModel( String path ) {
		return loadModel( path, "default" );
	}

	/*
	 * Entry point of class.
	 * Returns a Model object, created by parsing a wavefront .obj file
	 * Returns "Truncated Icosidodecahedron" in the case model parsing fails.
	 * It just looks cool.. okay?
	 */
	public static Model loadModel( String path, String givenName ) {
		// Create a file object at path
		File file = new File( path );

		// Check if our requested file exists..
		if ( !file.exists() ) {
			System.out.println( "Model: " + path + " was not found!" );
			return new Icosidodecahedron();
		}

		// Create filereader of our file path
		FileReader fileReader;
		try {
			fileReader = new FileReader( file );
		}
		catch ( FileNotFoundException e ) {
			e.printStackTrace();
			return new Icosidodecahedron();
		}

		// Create a buffered reader from our file reader
		BufferedReader bufferedReader = new BufferedReader( fileReader );

		// Create a new Model object. At this point it is instantiated but holds no values
		Model loadedModel = new Model();
		String line;
		int materialIndex = -1;
		List<Material> materials = new ArrayList<Material>();

		// Try to read and parse file
		try {
			while ( ( line = bufferedReader.readLine() ) != null ) {
				String cleanString = "";
				String splitValues[] = { "", "", "" };
				float emptyVector[] = { 0, 0, 0 };

				if ( line.startsWith( "mtllib " ) ) // Get the path of the material list, if any
				{
					String materialLib = line.substring( 7 );
					// Build material list
					materials = getMaterialsFromFile( file.getParent() + "/" + materialLib );
					// Add to model
					loadedModel.setMaterials( materials );
				}
				else if ( line.startsWith( "usemtl " ) ) // Set the index of material to be used on this particular face
				{
					String mat = line.substring( 7 );

					// Get the index for the material. Materials.indexOf(string) doesnt work here :/
					for ( int i = 0; i < materials.size(); i++ ) {
						if ( materials.get( i ).getName().equals( mat ) ) {
							materialIndex = i;
						}
					}
				}
				else if ( line.startsWith( "o " ) ) // Get and set name of model
				{
					if ( givenName.equals( "default" ) ) {
						// Lets just cut off our "startsWith"
						loadedModel.setName( line.substring( 2 ) );
					}
					else {
						loadedModel.setName( givenName );
					}
				}
				else if ( line.startsWith( "v " ) ) // Get all the vertices
				{
					cleanString = line.substring( 2 );
					splitValues = cleanString.split( " " ); // Break the line up into array of values

					// Convert values to floats
					emptyVector[0] = Float.valueOf( splitValues[0] );
					emptyVector[1] = Float.valueOf( splitValues[1] );
					emptyVector[2] = Float.valueOf( splitValues[2] );

					// Add values for this vertex into model object
					loadedModel.addVertex( new Vector3f( emptyVector[0], emptyVector[1], emptyVector[2] ) );
				}
				else if ( line.startsWith( "vn " ) ) // Get all the normals
				{
					// Sub 3 here because vn is two chars... derp
					cleanString = line.substring( 3 );
					splitValues = cleanString.split( " " );

					emptyVector[0] = Float.valueOf( splitValues[0] );
					emptyVector[1] = Float.valueOf( splitValues[1] );
					emptyVector[2] = Float.valueOf( splitValues[2] );

					loadedModel.addNormal( new Vector3f( emptyVector[0], emptyVector[1], emptyVector[2] ) );
				}
				else if ( line.startsWith( "f " ) ) // Get all the faces
				{
					// Split line by spaces
					splitValues = line.split( " " );

					// create 2d float array 
					float[][] forwardSplit = new float[4][2];
					boolean expectedFormat = false;

					// Iterate for length of string array
					for ( int i = 1; i < splitValues.length; i++ ) {
						if ( splitValues[i].split( "/" ).length >= 2 ) {
							expectedFormat = true;
							// Get both values we'll be using and place into float array
							String a = splitValues[i].split( "/" )[0];
							String b = splitValues[i].split( "/" )[2];
							float[] values = { Float.valueOf( a ), Float.valueOf( b ) };

							// Set value of this array index to the previously created float array
							forwardSplit[i] = values;
						}
					}

					// Sometimes blender exports strange faces, I don't know the use of such faces
					// So I just exclude them if they have not been removed from the file.
					if ( expectedFormat ) {
						// Create vectors from values
						Vector3f vertexIndices = new Vector3f( forwardSplit[1][0], forwardSplit[2][0], forwardSplit[3][0] );
						Vector3f normalIndices = new Vector3f( forwardSplit[1][1], forwardSplit[2][1], forwardSplit[3][1] );

						// Add vectors to model, as face objects
						loadedModel.addFace( new Face( vertexIndices, normalIndices, materialIndex ) );
					}
				}
			}
			bufferedReader.close(); // Done reading file, close handle
		}
		catch ( IOException e ) {
			e.printStackTrace();
			return new Icosidodecahedron();
		}
		return loadedModel;
	}

	/**
	 * Get the materials from the .mtl file
	 * @param materialLibPath
	 * @return
	 */
	private static List<Material> getMaterialsFromFile( String materialLibPath ) {
		// Create file object from path
		File file = new File( materialLibPath );

		// If the material file doesnt exist, raise error and end game
		if ( !file.exists() ) {
			try {
				throw new FileNotFoundException( "Could not locate material library file: " + materialLibPath );
			}
			catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}

			// File doesnt exist, yet was specified. End game
			App.endGame( true );
		}

		FileReader fileReader;
		try {
			// Create bufferedReader to read file, and initize variables we'll need to use
			fileReader = new FileReader( file );
			BufferedReader bufferedReader = new BufferedReader( fileReader );
			String line; // Current line of the file

			// List of materials we'll be returning
			List<Material> materials = new ArrayList<Material>();

			// Properties needed to construct material object
			String name = "";
			float shininess = 0f;
			float[] ambience = new float[3];
			float[] diffuse = new float[3];
			float[] specular = new float[3];
			float emit = 0f;
			float dissolve = 0f; // alpha
			int illumination = 0;

			// Read file
			while ( ( line = bufferedReader.readLine() ) != null ) {
				if ( line.startsWith( "newmtl " ) ) {
					name = line.substring( 7 );
				}
				else if ( line.startsWith( "emit " ) ) {
					emit = Float.valueOf( line.substring( 5 ) );
				}
				else if ( line.startsWith( "Ns " ) ) {
					shininess = Float.valueOf( line.substring( 3 ) );
				}
				else if ( line.startsWith( "Ka " ) ) {
					line = line.substring( 3 );
					String[] values = line.split( " " );
					ambience[0] = Float.valueOf( values[0] );
					ambience[1] = Float.valueOf( values[1] );
					ambience[2] = Float.valueOf( values[2] );
				}
				else if ( line.startsWith( "Kd " ) ) {
					line = line.substring( 3 );
					String[] values = line.split( " " );
					diffuse[0] = Float.valueOf( values[0] );
					diffuse[1] = Float.valueOf( values[1] );
					diffuse[2] = Float.valueOf( values[2] );
				}
				else if ( line.startsWith( "Ks " ) ) {
					line = line.substring( 3 );
					String[] values = line.split( " " );
					specular[0] = Float.valueOf( values[0] );
					specular[1] = Float.valueOf( values[1] );
					specular[2] = Float.valueOf( values[2] );
				}
				else if ( line.startsWith( "Ni " ) ) {
					// Unused atm
				}
				else if ( line.startsWith( "d " ) ) {
					shininess = Float.valueOf( line.substring( 2 ) );
				}
				else if ( line.startsWith( "illum " ) ) {
					illumination = Integer.valueOf( line.substring( 6 ) );

					// Final line we expect for the material object. Create new material object
					materials.add( new Material( name, emit, shininess, ambience, diffuse, specular, dissolve, illumination ) );

					// Null/re-initialize variables to avoid carrying over values to new material
					name = "";
					shininess = 0f;
					ambience = new float[3];
					diffuse = new float[3];
					specular = new float[3];
					dissolve = 0f;
					illumination = 0;
				}
			}

			// Close and return
			fileReader.close();
			return materials;
		}
		catch ( IOException e ) {
			e.printStackTrace();
			App.endGame( true );
		}
		return null; // Should never be reached...
	}
}
