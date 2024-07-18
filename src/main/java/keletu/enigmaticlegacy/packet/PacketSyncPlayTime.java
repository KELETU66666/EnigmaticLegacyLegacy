package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncPlayTime implements IMessage {
	private int playerID;
	private long timeWithCurses, timeWithoutCurses;

	public PacketSyncPlayTime() {
	}

	public PacketSyncPlayTime(int playerID, long timeWithCurses, long timeWithoutCurses) {
		this.playerID = playerID;
		this.timeWithCurses = timeWithCurses;
		this.timeWithoutCurses =  timeWithoutCurses;
	}


	@Override
	public void fromBytes(ByteBuf buf) {
		this.playerID = buf.readInt();
		this.timeWithCurses = buf.readLong();
		this.timeWithoutCurses =   buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.playerID);
		buf.writeLong(this.timeWithCurses);
		buf.writeLong(this.timeWithoutCurses);
	}

	public static class Handler implements IMessageHandler<PacketSyncPlayTime, IMessage> {

		@Override
		public IMessage onMessage(PacketSyncPlayTime message, MessageContext ctx) {
			Minecraft.getMinecraft().world.playerEntities.stream().filter(player -> player.getUniqueID().hashCode() == message.playerID).findAny().ifPresent(player -> {
						IPlaytimeCounter counter = IPlaytimeCounter.get(player);
						counter.setTimeWithCurses(message.timeWithCurses);
						counter.setTimeWithoutCurses(message.timeWithoutCurses);
					});
			return null;
		}
	}
}