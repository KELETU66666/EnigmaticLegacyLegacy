package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for setting arrows's rotations on client side.
 * @author Integral
 */

public class PacketForceArrowRotations implements IMessage {

	private int entityID;
	private double motionX, motionY, motionZ, posX, posY, posZ;
	private float rotationYaw;
	private float rotationPitch;

	public PacketForceArrowRotations() {
	}

	public PacketForceArrowRotations(int entityID, float rotationYaw, float rotationPitch, double vecX, double vecY, double vecZ, double posX, double posY, double posZ) {
		this.entityID = entityID;
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
		this.motionX = vecX;
		this.motionY = vecY;
		this.motionZ = vecZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		rotationYaw = buf.readFloat();
		rotationPitch = buf.readFloat();
		motionX = buf.readDouble();
		motionY = buf.readDouble();
		motionZ = buf.readDouble();
		posX = buf.readDouble();
		posY = buf.readDouble();
		posZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityID);
		buf.writeFloat(this.rotationYaw);
		buf.writeFloat(this.rotationPitch);
		buf.writeDouble(this.motionX);
		buf.writeDouble(this.motionY);
		buf.writeDouble(this.motionZ);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}

	public static class Handler implements IMessageHandler<PacketForceArrowRotations, IMessage> {

		public IMessage onMessage(PacketForceArrowRotations msg, MessageContext ctx) {
			WorldClient theWorld = Minecraft.getMinecraft().world;
			Entity arrow = theWorld.getEntityByID(msg.entityID);

			if (arrow != null) {
				arrow.addTag("enigmaticlegacy.redirected");

				arrow.setPosition(msg.posX, msg.posY, msg.posZ);
				arrow.motionX = msg.motionX;
				arrow.motionY = msg.motionY;
				arrow.motionZ = msg.motionZ;
				arrow.rotationYaw = msg.rotationYaw;
				arrow.prevRotationYaw = msg.rotationYaw;
				arrow.rotationPitch = msg.rotationPitch;
				arrow.prevRotationPitch = msg.rotationPitch;
			}

			return null;
		}
	}
}