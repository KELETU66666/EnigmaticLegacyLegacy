package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.interfaces.ISpellstone;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for triggering active ability of the spellstone.
 * @author Integral
 */

public class PacketSpellstoneKey implements IMessage {

	private boolean pressed;

	public PacketSpellstoneKey() {
	}

	public PacketSpellstoneKey(boolean pressed) {
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

	public static class Handler implements IMessageHandler<PacketSpellstoneKey, IMessage> {

		public IMessage onMessage(PacketSpellstoneKey message, MessageContext ctx) {
			EntityPlayerMP playerServ = ctx.getServerHandler().player;

			if (SuperpositionHandler.getAdvancedBaubles(playerServ).getItem() instanceof ISpellstone) {
				ItemStack spellstone = SuperpositionHandler.getAdvancedBaubles(playerServ);
				ISpellstone function = (ISpellstone) spellstone.getItem();

				function.triggerActiveAbility(playerServ.world, playerServ, spellstone);
			}

			return null;
		}
	}
}