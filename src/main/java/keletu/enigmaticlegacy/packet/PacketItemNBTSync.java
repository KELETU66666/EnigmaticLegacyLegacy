package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.item.ItemMagmaHeart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemNBTSync implements IMessage {
    private NBTTagCompound nbt;
    private int entityId;

    public PacketItemNBTSync() {}

    public PacketItemNBTSync(ItemStack stack, EntityPlayerMP player) {
        this.nbt = stack.getTagCompound();
        this.entityId = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class Handler implements IMessageHandler<PacketItemNBTSync, IMessage> {
        @Override
        public IMessage onMessage(PacketItemNBTSync message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    ItemStack stack = SuperpositionHandler.getAdvancedBaubles(player);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemMagmaHeart) {
                        stack.setTagCompound(message.nbt);
                    }
                }
            });
            return null;
        }
    }
}