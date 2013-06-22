package nocare.entity;

import nocare.geometry.Model;
import nocare.geometry.Skelaton;
import nocare.util.parser.SkelatonLoader;
import nocare.util.parser.WavefrontObjectLoader;

public class EntityPlayer extends EntityLiving {
	private Model playerModel = WavefrontObjectLoader.loadModel( "res/entity/casey/casey_tree.obj" );
	private Skelaton playerSkelaton = SkelatonLoader.loadSkelaton( "res/entity/casey/casey_rig.json" );

	public EntityPlayer() {
		// Set the player stats to the player template
		stats = EntityStats.playerTemplate;
		xSpeed = 0.01f;
	}

	@Override
	public void update() {
		super.update();
		playerModel.setX( xPos );
	}

	@Override
	public void render() {
		playerModel.render();
		playerSkelaton.render();
	}

}
