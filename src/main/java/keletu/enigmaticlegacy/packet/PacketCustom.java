package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketCustom implements IMessage {

    private String tag;

    public PacketCustom() {
    }

    public PacketCustom(String tag) {
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tag = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.tag);
    }

    public static class Handler implements IMessageHandler<PacketCustom, IMessage> {

        public IMessage onMessage(PacketCustom message, MessageContext ctx) {
            Minecraft.getMinecraft().player.addTag(message.tag);
            return null;
        }
    }
}
