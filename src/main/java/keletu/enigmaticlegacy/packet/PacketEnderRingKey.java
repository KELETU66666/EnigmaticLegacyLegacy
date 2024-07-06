package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import static keletu.enigmaticlegacy.event.ELEvents.hasCursed;
import static keletu.enigmaticlegacy.event.ELEvents.hasEnderRing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for opening Ender Chest inventory to a player.
 * @author Integral
 */

public class PacketEnderRingKey implements IMessage {

	private boolean pressed;

	public PacketEnderRingKey() {
	}

	public PacketEnderRingKey(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pressed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.pressed);
	}

	public static class Handler implements IMessageHandler<PacketEnderRingKey, IMessage> {

		public IMessage onMessage(PacketEnderRingKey message, MessageContext ctx) {
			EntityPlayerMP playerServ = ctx.getServerHandler().player;

			if (hasCursed(playerServ) || hasEnderRing(playerServ)) {
				playerServ.displayGUIChest(playerServ.getInventoryEnderChest());
				playerServ.world.playSound(null, playerServ.getPosition(), SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
			}

			return null;
		}
	}

}