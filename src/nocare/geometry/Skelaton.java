package nocare.geometry;

import java.util.ArrayList;

import nocare.App;

import static org.lwjgl.opengl.GL11.*;

public class Skelaton {

	private String name;
	private ArrayList<Bone> boneList = new ArrayList<Bone>();

	public Skelaton() {
	}

	public Skelaton( ArrayList<Bone> bones ) {
		boneList = bones;
	}

	public void addBone( Bone bone ) {
		boneList.add( bone );
	}

	public void removeBone( Bone bone ) {
		boneList.remove( bone );
	}

	public void render() {
		glPushMatrix();
		float tileSizeX = 8f;
		float tileSizeY = 4f;
		float tileSizeZ = 8.0f;
		glTranslatef( App.getPlayer().getX() - 5.5f, App.getPlayer().getY() - 1.5f, -8f );
		glBegin( GL_LINES );
		{
			for ( Bone b : boneList ) {
				b.render();
			}
		}
		glEnd();
		glPopMatrix();
	}

	public String toString() {
		String string = name + "\nboneList\n{\n";

		for ( Bone b : boneList ) {
			string += "\tBone:\n\t{\n\t\t" + b.toString() + "\n\t}\n";
		}

		string += "\n}";

		System.out.println( string );
		return string;
	}
}
