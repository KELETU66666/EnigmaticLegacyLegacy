package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.EnigmaticEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCosmicRevive implements IMessage {
	private int entityID;
	private int reviveType;

	public PacketCosmicRevive() {
	}

	public PacketCosmicRevive(int entityID, int reviveType) {
		this.entityID = entityID;
		this.reviveType = reviveType;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.reviveType = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityID);
		buf.writeInt(this.reviveType);
	}

	public static class Handler implements IMessageHandler<PacketCosmicRevive, IMessage> {

		public IMessage onMessage(PacketCosmicRevive msg, MessageContext ctx) {
			if (msg.reviveType == 0) {
				EnigmaticLegacy.proxy.displayReviveAnimation(msg.entityID, msg.reviveType);
			} else {
				EntityPlayer player = EnigmaticLegacy.proxy.getClientPlayer();
				Entity entity = player.world.getEntityByID(msg.entityID);

				if (entity == player) {
					EnigmaticEvents.scheduledCubeRevive = 5;
				} else {
					EnigmaticLegacy.proxy.displayReviveAnimation(msg.entityID, msg.reviveType);
				}
			}

			return null;
		}
	}
}