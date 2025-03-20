package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet for setting the player to look at specific point in space,
 * defined by absolute x/y/z coordinates.
 *
 * @author Integral
 */

public class PacketPlayerSetlook implements IMessage {

    private double x;
    private double y;
    private double z;

    public PacketPlayerSetlook() {
    }

    public PacketPlayerSetlook(double x, double y, double z) {
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

    public static class Handler implements IMessageHandler<PacketPlayerSetlook, IMessage> {

        public IMessage onMessage(PacketPlayerSetlook msg, MessageContext ctx) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;

            SuperpositionHandler.lookAt(msg.x, msg.y, msg.z, player);

            return null;
        }
    }

}