package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.util.ITaintable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemEarthHeart extends ItemBase implements ITaintable {

	public ItemEarthHeart() {
		super("earth_heart", EnumRarity.UNCOMMON);
		this.maxStackSize = 1;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof EntityPlayer && !entityIn.world.isRemote) {
			EntityPlayer player = (EntityPlayer) entityIn;
			this.handleTaintable(stack, player);
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (this.isTainted(stack)) {
			tooltip.add("tooltip.enigmaticlegacy.tainted1");
		}
	}

}