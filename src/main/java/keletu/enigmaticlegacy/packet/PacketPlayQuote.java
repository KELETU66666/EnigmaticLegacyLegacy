package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.util.Quote;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayQuote implements IMessage {
	private Quote quote;
	private int delay;

	public PacketPlayQuote() {
	}

	public PacketPlayQuote(Quote quote, int delay) {
		this.quote = quote;
		this.delay = delay;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.quote = Quote.getByID(buf.readInt());
		this.delay = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.quote.getID());
		buf.writeInt(this.delay);
	}

	public static class Handler implements IMessageHandler<PacketPlayQuote, IMessage> {

		public IMessage onMessage(PacketPlayQuote message, MessageContext ctx) {
			message.quote.play(message.delay);
			return null;
		}
	}
}