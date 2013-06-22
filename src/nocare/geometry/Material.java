package nocare.geometry;

import java.nio.FloatBuffer;

import nocare.util.GeneralUtils;

/**
 * This class holds data for a material one may find in a .mtl object associated with a .obj file<br />
 * Specification followed: http://people.sc.fsu.edu/~jburkardt/data/mtl/mtl.html
 * http://www.fileformat.info/format/material/
 * @author Nocare
 */
public class Material {
	public static final float[] empty = { 0.0f, 0.0f, 0.0f, 0.0f };
	public static final FloatBuffer noEmission = GeneralUtils.floatBufferFromArray( empty );

	private String name;

	// range 0-2 float.
	private FloatBuffer emit;
	private boolean hasEmit = false;
	// range 0-100
	@SuppressWarnings( "unused" )
	private float shininess;
	// Ka rgb
	private FloatBuffer ambience;
	// Kd rgb
	private FloatBuffer diffuse;
	// Ks rgb
	private FloatBuffer specular;

	private FloatBuffer lightPosition;

	// d - dissolve/alpha
	@SuppressWarnings( "unused" )
	private float dissolve;
	// illum
	@SuppressWarnings( "unused" )
	private int illumination;

	// map_Ka texture map - unused
	@SuppressWarnings( "unused" )
	private String textureMap;
	// Ni optical densisty - unused
	@SuppressWarnings( "unused" )
	private float opticalDensity;

	public Material( String name, float emit, float shiny, float[] ambience, float[] diffuse, float[] specular, float dissolve, int illum ) {
		this.name = name;
		this.shininess = shiny;
		this.ambience = GeneralUtils.floatBufferFromArray( ambience );
		this.diffuse = GeneralUtils.floatBufferFromArray( diffuse );
		this.specular = GeneralUtils.floatBufferFromArray( specular );
		this.dissolve = dissolve;
		this.illumination = illum;

		float[] f = new float[4];
		f[0] = 1.0f;
		f[1] = 1.0f;
		f[2] = 1.0f;
		f[3] = 0.0f;

		lightPosition = GeneralUtils.floatBufferFromArray( f );

		if ( emit != 0.0f ) {
			hasEmit = true;
			f[0] = diffuse[0] * ( emit / 2f );
			f[1] = diffuse[1] * ( emit / 2f );
			f[2] = diffuse[2] * ( emit / 2f );
			f[3] = 0.0f;
			this.emit = GeneralUtils.floatBufferFromArray( f );
		}
	}

	public String getName() {
		return name;
	}

	public FloatBuffer getSpecular() {
		return specular;
	}

	public FloatBuffer getDiffuse() {
		return diffuse;
	}

	public FloatBuffer getAmbience() {
		return ambience;
	}

	public FloatBuffer lightPosition() {
		return lightPosition;
	}

	public FloatBuffer getEmission() {
		return emit;
	}

	public boolean hasEmission() {
		return hasEmit;
	}
}
