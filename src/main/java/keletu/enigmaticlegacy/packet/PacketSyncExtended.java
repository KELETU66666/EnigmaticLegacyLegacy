package keletu.enigmaticlegacy.packet;

import baubles.common.Baubles;
import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.api.ExtendedBaublesApi;
import keletu.enigmaticlegacy.api.cap.IExtendedBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncExtended implements IMessage {

	int playerId;
	byte slot=0;
	ItemStack bauble;

	public PacketSyncExtended() {}

	public PacketSyncExtended(EntityPlayer p, int slot, ItemStack bauble) {
		this.slot = (byte) slot;
		this.bauble = bauble;
		this.playerId = p.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(playerId);
		buffer.writeByte(slot);
		ByteBufUtils.writeItemStack(buffer, bauble);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		playerId = buffer.readInt();
		slot = buffer.readByte();
		bauble = ByteBufUtils.readItemStack(buffer);
	}

	public static class Handler implements IMessageHandler<PacketSyncExtended, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSyncExtended message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable(){ public void run() {
				World world = Baubles.proxy.getClientWorld();
				if (world==null) return;
				Entity p = world.getEntityByID(message.playerId);
				if (p !=null && p instanceof EntityPlayer) {
					IExtendedBaublesItemHandler baubles = ExtendedBaublesApi.getBaublesHandler((EntityPlayer) p);
					baubles.setStackInSlot(message.slot, message.bauble);
				}
			}});
			return null;
		}
	}
}