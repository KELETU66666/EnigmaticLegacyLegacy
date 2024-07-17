package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import static keletu.enigmaticlegacy.event.ELEvents.hasPearl;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

public class PacketEnchantedWithPearl implements IMessage {

	private int id;

	public PacketEnchantedWithPearl() {
	}

	public PacketEnchantedWithPearl(int id) {
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
	}

	public static class Handler implements IMessageHandler<PacketEnchantedWithPearl, IMessage> {

		public IMessage onMessage(PacketEnchantedWithPearl message, MessageContext ctx) {
			EntityPlayerMP playerIn = ctx.getServerHandler().player;
			Container ctr = playerIn.openContainer;

			if (!playerIn.world.isRemote && ctr instanceof ContainerEnchantment) {
				ContainerEnchantment container = (ContainerEnchantment) playerIn.openContainer;

				Field field = ReflectionHelper.findField(ContainerEnchantment.class, "world", "field_75172_h");
				field.setAccessible(true);
				World world = null;
				try {
					world = (World) field.get(container);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				Field field1 = ReflectionHelper.findField(ContainerEnchantment.class, "rand", "field_75169_l");
				field1.setAccessible(true);
				Random rand = null;
				try {
					rand = (Random) field1.get(container);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				Field field2 = ReflectionHelper.findField(ContainerEnchantment.class, "position", "field_178150_j");
				field2.setAccessible(true);
				BlockPos pos = null;
				try {
					pos = (BlockPos) field2.get(container);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				ItemStack itemstack = container.tableInventory.getStackInSlot(0);
				ItemStack itemstack1 = container.tableInventory.getStackInSlot(1);
				int i = message.id + 1;

				rand.setSeed(container.xpSeed + message.id);
				List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(rand, itemstack, container.enchantLevels[message.id], hasPearl(playerIn));

				if (itemstack.getItem() == Items.BOOK && list.size() > 1) {
					list.remove(rand.nextInt(list.size()));
				}

				if (!list.isEmpty()) {
					playerIn.onEnchant(itemstack, i);
					boolean flag = itemstack.getItem() == Items.BOOK;

					if (flag) {
						itemstack = new ItemStack(Items.ENCHANTED_BOOK);
						container.tableInventory.setInventorySlotContents(0, itemstack);
					}

					for (int j = 0; j < list.size(); ++j) {
						EnchantmentData enchantmentdata = list.get(j);

						if (flag) {
							ItemEnchantedBook.addEnchantment(itemstack, enchantmentdata);
						} else {
							itemstack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
						}
					}

					// Remove the logic that consumes lapis lazuli
					if (!playerIn.capabilities.isCreativeMode && !hasPearl(playerIn)) {
						itemstack1.shrink(i);

						if (itemstack1.isEmpty()) {
							container.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
						}
					}

					playerIn.addStat(StatList.ITEM_ENCHANTED);

					if (playerIn instanceof EntityPlayerMP) {
						CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) playerIn, itemstack, i);
					}

					container.tableInventory.markDirty();
					container.xpSeed = playerIn.getXPSeed();
					container.onCraftMatrixChanged(container.tableInventory);
					world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.1F + 0.9F);
				}
			}

			return null;
		}
	}

}