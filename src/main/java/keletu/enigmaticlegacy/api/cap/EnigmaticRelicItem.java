package keletu.enigmaticlegacy.api.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EnigmaticRelicItem implements IEnigmaticRelic
{
	private EnigmaticRelicType baubleType;

	public EnigmaticRelicItem(EnigmaticRelicType type) {
		baubleType = type;
	}

	@Override
	public EnigmaticRelicType getSlot(ItemStack stack) {
		return baubleType;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityLivingBase player) {
		return false;
	}

	@Override
	public void onRelicTick(EntityPlayer player, ItemStack stack) {

	}

	@Override
	public void onRelicEquipped(EntityPlayer player, ItemStack stack) {

	}

	@Override
	public void onRelicUnequipped(EntityPlayer player, ItemStack stack) {

	}
}