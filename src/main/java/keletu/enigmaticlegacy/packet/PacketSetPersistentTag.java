package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetPersistentTag implements IMessage {

    private String packet;
    private boolean id;

    public PacketSetPersistentTag() {
    }

    public PacketSetPersistentTag(String packet, boolean id) {
        this.packet = packet;
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.packet = ByteBufUtils.readUTF8String(buf);
        this.id = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, packet);
        buf.writeBoolean(this.id);
    }

    public static class Handler implements IMessageHandler<PacketSetPersistentTag, IMessage> {

        @Override
        public IMessage onMessage(PacketSetPersistentTag message, MessageContext ctx) {
            SuperpositionHandler.setPersistentBoolean(Minecraft.getMinecraft().player, message.packet, message.id);
            return null;
        }
    }

}