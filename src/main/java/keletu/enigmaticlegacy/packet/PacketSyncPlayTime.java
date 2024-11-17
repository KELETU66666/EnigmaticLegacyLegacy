package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketSyncPlayTime implements IMessage {
    private UUID playerUUID;
    private long timeWithCurses, timeWithoutCurses;

    public PacketSyncPlayTime() {}

    public PacketSyncPlayTime(UUID playerUUID, long timeWithCurses, long timeWithoutCurses) {
        this.playerUUID = playerUUID;
        this.timeWithCurses = timeWithCurses;
        this.timeWithoutCurses = timeWithoutCurses;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long mostSigBits = buf.readLong();
        long leastSigBits = buf.readLong();
        this.playerUUID = new UUID(mostSigBits, leastSigBits);
        this.timeWithCurses = buf.readLong();
        this.timeWithoutCurses = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.playerUUID.getMostSignificantBits());
        buf.writeLong(this.playerUUID.getLeastSignificantBits());
        buf.writeLong(this.timeWithCurses);
        buf.writeLong(this.timeWithoutCurses);
    }

    public static class Handler implements IMessageHandler<PacketSyncPlayTime, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncPlayTime message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                handleClientSide(message);
            } else {
                handleServerSide(message, ctx.getServerHandler().player);
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handleClientSide(PacketSyncPlayTime message) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null) {
                return;
            }

            EntityPlayer player = mc.world.getPlayerEntityByUUID(message.playerUUID);
            if (player != null) {
                IPlaytimeCounter counter = IPlaytimeCounter.get(player);
                counter.setTimeWithCurses(message.timeWithCurses);
                counter.setTimeWithoutCurses(message.timeWithoutCurses);
            }
        }

        private void handleServerSide(PacketSyncPlayTime message, EntityPlayerMP player) {
            if (player.getUniqueID().equals(message.playerUUID)) {
                IPlaytimeCounter counter = IPlaytimeCounter.get(player);
                counter.setTimeWithCurses(message.timeWithCurses);
                counter.setTimeWithoutCurses(message.timeWithoutCurses);
            }
        }
    }
}