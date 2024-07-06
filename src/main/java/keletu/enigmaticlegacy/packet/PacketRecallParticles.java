package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for bursting out a bunch of dragon breath particles.
 * @author Integral
 */

public class PacketRecallParticles implements IMessage{
	
	private double x;
	private double y;
	private double z;
	private int num;
	private boolean check;

	public PacketRecallParticles() {
	}

	public PacketRecallParticles(double x, double y, double z, int number, boolean checkSettings) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.num = number;
	    this.check = checkSettings;
	  }

	  @Override
	  public void toBytes(ByteBuf buf) {
	    buf.writeDouble(this.x);
	    buf.writeDouble(this.y);
	    buf.writeDouble(this.z);
	    buf.writeInt(this.num);
	    buf.writeBoolean(this.check);
	  }

	  @Override
	  public void fromBytes(ByteBuf buf) {
		  x = buf.readDouble();
		  y = buf.readDouble();
		  z = buf.readDouble();
		  num = buf.readInt();
		  check = buf.readBoolean();
	  }


	public static class Handler implements IMessageHandler<PacketRecallParticles, IMessage> {

		  public IMessage onMessage(PacketRecallParticles message, MessageContext ctx) {
		      EntityPlayerSP player = Minecraft.getMinecraft().player;
		      
		    	int amount = message.num;
			      
			      if (message.check)
			    	  amount *= 1.0F;
			      
			      for (int counter = 0; counter <= amount; counter++)
		    		player.world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, true, message.x, message.y, message.z, (Math.random()-0.5D)*0.2D, (Math.random()-0.5D)*0.2D, (Math.random()-0.5D)*0.2D);
		      
		      return null;
		    }
	  }

}