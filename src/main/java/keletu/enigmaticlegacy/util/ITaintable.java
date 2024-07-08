package keletu.enigmaticlegacy.util;

import keletu.enigmaticlegacy.core.ItemNBTHelper;
import keletu.enigmaticlegacy.event.ELEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ITaintable {

	default void handleTaintable(ItemStack stack, EntityPlayer player) {
		if (ELEvents.hasCursed(player)) {
			if (!ItemNBTHelper.getBoolean(stack, "isTainted", false)) {
				ItemNBTHelper.setBoolean(stack, "isTainted", true);
			}
		} else {
			if (ItemNBTHelper.getBoolean(stack, "isTainted", false)) {
				ItemNBTHelper.setBoolean(stack, "isTainted", false);
			}
		}
	}

	default boolean isTainted(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, "isTainted", false);
	}

}