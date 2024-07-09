package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for spawning a bunch of portal particles around specific point.
 * @author Integral
 */

public class PacketPortalParticles implements IMessage {
	
	private double x;
	private double y;
	private double z;
	private int num;
	private double rangeModifier;
	private boolean check;

	public PacketPortalParticles() {
	}

	public PacketPortalParticles(double x, double y, double z, int number, double rangeModifier, boolean checkSettings) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.num = number;
	    this.rangeModifier = rangeModifier;
	    this.check = checkSettings;
	  }

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		num = buf.readInt();
		rangeModifier = buf.readDouble();
		check = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeInt(this.num);
		buf.writeDouble(this.rangeModifier);
		buf.writeBoolean(this.check);
	}

	public static class Handler implements IMessageHandler<PacketPortalParticles, IMessage> {
		@Override
		public IMessage onMessage(PacketPortalParticles message, MessageContext ctx) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;


			int amount = message.num;

			for (int counter = 0; counter <= amount; counter++)
				player.world.spawnParticle(EnumParticleTypes.PORTAL, true, message.x, message.y, message.z, ((Math.random()-0.5D)*2.0D)*message.rangeModifier, ((Math.random()-0.5D)*2.0D)*message.rangeModifier, ((Math.random()-0.5D)*2.0D)*message.rangeModifier);

			return null;
		}
	}
}