package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import keletu.enigmaticlegacy.util.compat.CompatBubbles;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemEnchanterPearl extends ItemBaseFireProof implements IBauble {

	public ItemEnchanterPearl() {
		super("enchanter_pearl", EnumRarity.EPIC);
		this.maxStackSize = 1;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("");
		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl2"));
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl3"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl4"));
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl5"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.enchanterPearl6"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}
	}

	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if(ModCompat.COMPAT_BUBBLES){
			CompatBubbles.onEquippedBubbles(stack, player);
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase living) {
		if(ModCompat.COMPAT_BUBBLES){
			CompatBubbles.onUnequippedBubbles(stack, living);
		}
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.CHARM;
	}
}