package keletu.enigmaticlegacy.event.special;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired when player enters block.
 * Not cancelable, no result, fired on {@link MinecraftForge#EVENT_BUS}.
 * @author Aizistral
 */

public class EnterBlockEvent extends PlayerEvent {
	private final IBlockState blockState;

	public EnterBlockEvent(EntityPlayer player, IBlockState blockState) {
		super(player);
		this.blockState = blockState;
	}

	public IBlockState getBlockState() {
		return this.blockState;
	}
}