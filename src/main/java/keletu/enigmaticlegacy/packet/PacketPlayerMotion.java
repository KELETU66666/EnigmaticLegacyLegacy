package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for setting player's motion on client side.
 * Required since motion of the player can only be affected there.
 * @author Integral
 */

public class PacketPlayerMotion implements IMessage {
	
	private double x;
	private double y;
	private double z;

	public PacketPlayerMotion() {
	}

	public PacketPlayerMotion(double x, double y, double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	  }

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	public static class Handler implements IMessageHandler<PacketPlayerMotion, IMessage> {

		public IMessage onMessage(PacketPlayerMotion msg, MessageContext ctx) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			player.motionX = msg.x;
			player.motionY = msg.y;
			player.motionZ = msg.z;

			return null;
		}
	}
}