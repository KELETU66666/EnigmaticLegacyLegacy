package keletu.enigmaticlegacy.event.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired when player summons entity, like Wither or Iron Golem.
 * Not cancelable, no result, fired on {@link MinecraftForge#EVENT_BUS}.
 * @author Aizistral
 */

public class SummonedEntityEvent extends PlayerEvent {
	private final Entity summonedEntity;

	public SummonedEntityEvent(EntityPlayer player, Entity entity) {
		super(player);
		this.summonedEntity = entity;
	}

	public Entity getSummonedEntity() {
		return this.summonedEntity;
	}
}