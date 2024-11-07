package keletu.enigmaticlegacy.event.special;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired when player activates vanilla End Portal.
 * Not cancelable, no result, fired on {@link MinecraftForge#EVENT_BUS}.
 * @author Aizistral
 */

public class EndPortalActivatedEvent extends PlayerEvent {
	private final BlockPos pos;

	public EndPortalActivatedEvent(EntityPlayer player, BlockPos pos) {
		super(player);
		this.pos = pos;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}