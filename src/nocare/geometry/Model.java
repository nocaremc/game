package nocare.geometry;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import nocare.App;
import nocare.util.GeneralUtils;
import nocare.util.parser.WavefrontObjectLoader;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Nocare
 *         This class was inspired by Oskar Veerhoek
 *         Looking over it, I did not really see a better way to do what I
 *         wanted.
 *         There's little code here, so copyright is moot in my book.
 *         Still, thought i'd mention it in case I do distribute without
 *         changing this
 */

public class Model
{
	private List<Vector3f> vertices = new ArrayList<Vector3f>();
	private List<Vector3f> normals = new ArrayList<Vector3f>();
	private List<Face> faces = new ArrayList<Face>();
	private List<Material> materials = new ArrayList<Material>();
	private String modelName = "";

	private float minX = 0;
	private float minY = 0;
	private float minZ = 0;

	private float maxX = 0;
	private float maxY = 0;
	private float maxZ = 0;

	private float xPos;
	private float yPos;
	private float zPos;

	private float xRot;
	private float yRot;
	private float zRot;

	/*
	 *  Getters
	 */
	public List<Vector3f> getVertices()
	{
		return vertices;
	}

	public Vector3f getVertexByIndex( int index )
	{
		return this.vertices.get( index );
	}

	public List<Vector3f> getNormals()
	{
		return normals;
	}

	public Vector3f getNormalByIndex( int index )
	{
		return this.normals.get( index );
	}

	public List<Face> getFaces()
	{
		return faces;
	}

	public String getName()
	{
		return modelName;
	}

	public Vector3f getMin()
	{
		return new Vector3f( minX, minY, minZ );
	}

	public Vector3f getMax()
	{
		return new Vector3f( maxX, maxY, maxZ );
	}

	public List<Material> getMaterials()
	{
		return materials;
	}

	public Material getMaterialAt( int materialIndex )
	{
		return materials.get( materialIndex );
	}
	
	public float getX()
	{
		return xPos;
	}
	
	public float getY()
	{
		return yPos;
	}
	
	public float getZ()
	{
		return zPos;
	}

	/*
	 * Setters
	 */

	/* Set vertices equal to a previously created list */
	public void setVertices( List<Vector3f> vertices, float[] bounds )
	{
		this.vertices = vertices;
	}

	/* Add a new vertex to vertices List */
	public void addVertex( Vector3f vertex )
	{
		this.vertices.add( vertex );

		float x = vertex.getX();
		float y = vertex.getY();
		float z = vertex.getZ();

		if ( x < minX )
			minX = x;
		if ( x > maxX )
			maxX = x;

		if ( y < minY )
			minY = y;
		if ( y > maxY )
			maxY = y;

		if ( z < minZ )
			minZ = z;
		if ( z > maxZ )
			maxZ = z;

	}

	/* Set normals equal to a previously created list */
	public void setNormals( List<Vector3f> normals )
	{
		this.normals = normals;
	}

	/* Add a new normal to normals List */
	public void addNormal( Vector3f normal )
	{
		this.normals.add( normal );
	}

	public void setMaterials( List<Material> materials )
	{
		this.materials = materials;
	}

	/* Set List of faces equal to a previously created list */
	public void setFaces( List<Face> faces )
	{
		this.faces = faces;
	}

	/* Add face to List of faces */
	public void addFace( Face face )
	{
		this.faces.add( face );
	}

	/* Set the name of this model */
	public void setName( String modelName )
	{
		this.modelName = modelName;
	}

	public void setX( float x )
	{
		xPos = x;
	}

	public void setY( float y )
	{
		yPos = y;
	}

	public void setZ( float z )
	{
		zPos = z;
	}

	public void setPosition( float x, float y, float z )
	{
		xPos = x;
		yPos = y;
		zPos = z;
	}

	public void setRotation( float x, float y, float z )
	{
		xRot = x;
		yRot = y;
		zRot = z;
	}

	public void translatef( float x, float y, float z )
	{
		for ( int i = 0; i < normals.size(); i++ )
		{
			normals.get( i ).translate( x, y, z );
		}

		for ( int i = 0; i < vertices.size(); i++ )
		{
			vertices.get( i ).translate( x, y, z );
		}

	}

	public void render()
	{
		// Set up colors for faces. Temporary untill I get texturing in
		Color[] colors =
		{ ( Color ) Color.BLACK, ( Color ) Color.BLUE, ( Color ) Color.CYAN, ( Color ) Color.DKGREY, ( Color ) Color.GREEN, ( Color ) Color.GREY,
				( Color ) Color.LTGREY, ( Color ) Color.ORANGE, ( Color ) Color.PURPLE, ( Color ) Color.RED, ( Color ) Color.WHITE, ( Color ) Color.YELLOW };

		int colorIndex = 0;

		// Single out this part from rest of matrix. Not positive I need to do this yet.
		glPushMatrix();

		// Translate model by its position... 
		// This looks backwards but isn't. Otherwise a negative value will send it to the right. Go figure.
		glTranslatef( xPos, yPos, zPos );
		// Now apply it's rotations
		glRotatef( xRot, 1f, 0f, 0f );
		glRotatef( yRot, 0f, 1f, 0f );
		glRotatef( zRot, 0f, 0f, 1f );
		
		//glEnable(GL_LIGHTING);
		//glEnable(GL_LIGHT0);
		//glEnable(GL_COLOR_MATERIAL);
		//glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		
		// Need to know if we are working with a new material
		int lastMaterialIndex = -1;
		
		for ( Face face : getFaces() )
		{
			// Incrementing which color used, so each face has a different color. Temporary...
			//			GeneralUtils.glColorShortcut( colors[colorIndex] );
			colorIndex++;

		
			
			// Get the material index this face uses
			int materialIndex = face.getMaterialIndex();
			
			if (materialIndex != lastMaterialIndex)
			{
				lastMaterialIndex = materialIndex;
				
				
				// Set the current draw color to that of the material at index. Diffuse color of faces
				GeneralUtils.glColorShortcut( materials.get( materialIndex ).getDiffuse() );
				
				updateGLMaterial(materialIndex);
			}
			

			
			
			
			//GeneralUtils.glColorShortcut( colors[materialIndex] );		
			if ( colorIndex >= colors.length )
				colorIndex = 0;

			// Get each vertex and Normal, create a triangle
			glBegin( GL_TRIANGLES );
			{
				Vector3f n1 = getNormalByIndex( ( int ) face.getNormalIndex().x - 1 );
				glNormal3f( n1.x, n1.y, n1.z );
				Vector3f v1 = getVertexByIndex( ( int ) face.getVertexIndex().x - 1 );
				glVertex3f( v1.x, v1.y, v1.z );
				Vector3f n2 = getNormalByIndex( ( int ) face.getNormalIndex().y - 1 );
				glNormal3f( n2.x, n2.y, n2.z );
				Vector3f v2 = getVertexByIndex( ( int ) face.getVertexIndex().y - 1 );
				glVertex3f( v2.x, v2.y, v2.z );
				Vector3f n3 = getNormalByIndex( ( int ) face.getNormalIndex().z - 1 );
				glNormal3f( n3.x, n3.y, n3.z );
				Vector3f v3 = getVertexByIndex( ( int ) face.getVertexIndex().z - 1 );
				glVertex3f( v3.x, v3.y, v3.z );
			}
			glEnd();
		}
		glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_LIGHT0);
		glDisable(GL_LIGHTING);
		
		
		
		// Translate the model back from the origin
		glTranslatef( -xPos, -yPos, -zPos );

		// Leave our isolated matrix
		glPopMatrix();
	}

	private void updateGLMaterial( int materialIndex )
	{
		Material m = materials.get( materialIndex );
		
		glShadeModel(GL_SMOOTH);
		glMaterial(GL_FRONT, GL_SPECULAR, m.getSpecular());				// sets specular material color
		glMaterialf(GL_FRONT, GL_SHININESS, 96.078431f);					// sets shininess
		
		glLight(GL_LIGHT0, GL_POSITION, m.lightPosition());				// sets light position
		glLight(GL_LIGHT0, GL_SPECULAR, m.getSpecular());				// sets specular light to white
		glLight(GL_LIGHT0, GL_DIFFUSE, m.getDiffuse());					// sets diffuse light to white
		glLightModel(GL_LIGHT_MODEL_AMBIENT, m.getAmbience());		// global ambient light 
		
		
		
		glEnable(GL_LIGHTING);										// enables lighting
		glEnable(GL_LIGHT0);										// enables light0
		
		//glEnable(GL_COLOR_MATERIAL);								// enables opengl to use glColor3f to define material color
		//glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		
		// If the material has emission set it, otherwise, disable emission for this material
		if (materials.get( materialIndex ).hasEmission())
		{
			glMaterial(GL_FRONT, GL_EMISSION, m.getEmission());
		}
		else
		{
			glMaterial(GL_FRONT, GL_EMISSION, Material.noEmission);
		}
		
	}
}