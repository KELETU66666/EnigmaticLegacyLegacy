package keletu.enigmaticlegacy.item.etherium;

import keletu.enigmaticlegacy.core.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public interface IEtheriumTool {

	default boolean areaEffectsEnabled(EntityPlayer player, ItemStack stack) {
		return this.areaEffectsAllowed(stack) && !player.isSneaking();
	}

	default boolean areaEffectsAllowed(ItemStack stack) {
		if (stack.getItem() instanceof IEtheriumTool)
			return ItemNBTHelper.getBoolean(stack, "MultiblockEffectsEnabled", true);

		return false;
	}

	default void enableAreaEffects(EntityPlayer player, ItemStack stack) {

		if (stack.getItem() instanceof IEtheriumTool) {
			ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", true);

			if (!player.world.isRemote) {
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2F)));
			}
		}

	}

	default void disableAreaEffects(EntityPlayer player, ItemStack stack) {
		if (stack.getItem() instanceof IEtheriumTool) {
			ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", false);

			if (!player.world.isRemote) {
				player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2F)));
			}
		}
	}

	default void toggleAreaEffects(EntityPlayer player, ItemStack stack) {
		if (stack.getItem() instanceof IEtheriumTool) {
			if (this.areaEffectsAllowed(stack)) {
				this.disableAreaEffects(player, stack);
			} else {
				this.enableAreaEffects(player, stack);
			}
		}
	}

}