package nocare.geometry;

import org.lwjgl.util.vector.Vector3f;

public class Face {
	private Vector3f vertexIndex = new Vector3f();
	private Vector3f normalIndex = new Vector3f();
	private int materialIndex;

	public Face( Vector3f vertexIndex, Vector3f normalIndex, int materialIndex ) {
		this.vertexIndex = vertexIndex;
		this.normalIndex = normalIndex;
		this.materialIndex = materialIndex;
	}

	/*
	 * Getters
	 */
	public Vector3f getVertexIndex() {
		return vertexIndex;
	}

	public Vector3f getNormalIndex() {
		return normalIndex;
	}

	public int getMaterialIndex() {
		return materialIndex;
	}
}