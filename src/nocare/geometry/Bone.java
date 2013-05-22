package nocare.geometry;

import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;
public class Bone
{
	private String name;
	private float xPos;
	private float yPos;
	private float zPos;
	private float xSpeed;
	private float ySpeed;
	private float zSpeed;

	private String parent;
	
	private Vector3f vector;
	private float length;

	public Bone( float x, float y, float z, String parent, Vector3f dir, float length )
	{
		xPos = x;
		yPos = y;
		zPos = z;
		this.parent = parent;
		vector = dir;
		this.length = length;
	}
	
	public void render()
	{
		glColor3f(0,0,0);
		glVertex3f(xPos, yPos, zPos);
		Vector3f cPos = new Vector3f(xPos, yPos, zPos);
		float angle = Vector3f.angle( cPos, vector );
		
		float newX = vector.getX();// * length;
		float newY = vector.getY();// * length;
		float newZ = vector.getZ();// * length;
		
		glVertex3f(newX, newY, newZ);
		//glVertex3f(newX+1,newY+1,newZ+1);
	}
	
	public String toString()
	{

		String sb = new StringBuilder()
		.append( xPos ).append(", ")
		.append(yPos).append(", ")
		.append( zPos ).append("\n\t\t")
		.append( name ).append("\n\t\t")
		.append( parent ).append("\n\t\t")
		.append( vector ).append("\n\t\t")
		.append( length )
		.toString();
		return sb;
	}
}
