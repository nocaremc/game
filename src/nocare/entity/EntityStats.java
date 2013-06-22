package nocare.entity;

public class EntityStats {
	protected float currentHealth;
	protected float maxHealth;

	protected float currentEnergy;
	protected float maxEnergy;

	protected int currentLevel;
	protected float currentExp;

	/*
	 * Main Stats
	 */
	protected float dex;
	protected float str;
	protected float wis;
	protected float intel;
	protected float stam;
	protected float luk;

	/*
	 * Derivitave Stats
	 * 	Stats which base value comes from Core stats, but can be modified through items or buffs
	 */
	protected float dodge = 0.5f;
	protected float block = 0.5f;
	protected float parry = 0.5f;
	protected float[] defense = new float[2]; // Melee, Ranged, Laser/Energy, 
	protected float[] resistance = new float[4]; // Fire, Water, Earth, Electric
	protected float hitRate = 0.05f;
	protected float critRate = 0.05f;
	protected float critModifier = 1.25f;
	protected float physicalDamage;
	protected float magicalDamage;

	/*
	 * Other Stats
	 */
	protected float jumpHeight;
	protected float jumpSpeed;
	protected float moveSpeed;
	protected float airMoveSpeed;
	protected boolean onGround = true;

	public static final EntityStats playerTemplate = new EntityStats();

	static {
		playerTemplate.dex = 5.0f;
		playerTemplate.str = 5.0f;
		playerTemplate.intel = 5.0f;
		playerTemplate.wis = 5.0f;
		playerTemplate.luk = 5.0f;
		playerTemplate.stam = 5.0f;
		playerTemplate.jumpHeight = 10.0f;
		playerTemplate.jumpSpeed = 1.0f;
		playerTemplate.moveSpeed = 0.02f;//0.12f;
		playerTemplate.airMoveSpeed = 0.09f;
	}

	public float getRunSpeed() {
		if ( onGround ) {
			return moveSpeed;
		}
		else {
			return airMoveSpeed;
		}

	}
}
