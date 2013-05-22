package nocare.entity;

import nocare.api.IEntityStats;

public abstract class EntityLiving extends Entity implements IEntityStats
{
	protected EntityStats stats = new EntityStats();


	@Override
	public EntityStats getStats()
	{
		return stats;
	}

}
