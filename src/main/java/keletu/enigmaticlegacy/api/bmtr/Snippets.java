package keletu.enigmaticlegacy.api.bmtr;

import baubles.api.BaublesApi;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.container.SlotBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Snippets {
	public static void addSlotsToContainer(ContainerPlayerExpanded c, Object p) {
		if (!(p instanceof EntityPlayer)) {
			throw new ASMException("Not an EntityPlayer: "+p);
		}
		EntityPlayer ep = (EntityPlayer) p;
		for (int i = 0; i < 7; i++) {
			int r = 7 + 18 * i;
			SlotBauble sb = new SlotBauble(ep, BaublesApi.getBaublesHandler(ep), i + 7, r, -18);
			sb.slotNumber = c.inventorySlots.size();
			c.inventorySlots.add(sb);
	        c.inventoryItemStacks.add(new ItemStack(Items.BLAZE_POWDER));
		}
	}
}