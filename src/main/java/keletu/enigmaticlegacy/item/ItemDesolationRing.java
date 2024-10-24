package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDesolationRing extends ItemBaseBauble {

	public ItemDesolationRing() {
		super("desolation_ring", EnumRarity.EPIC);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("");

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.desolationRing1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.desolationRing2"));
			list.add("");
			//ItemLoreHelper.indicateWorthyOnesOnly(list);
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.eternallyBound1"));

			if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isCreative()) {
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing2_creative"));
			} else {
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing2"));
			}

			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
			list.add("");
			//ItemLoreHelper.indicateCursedOnesOnly(list);
		}
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.RING;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityLivingBase living) {
		return living instanceof EntityPlayer && ((EntityPlayer) living).isCreative();
	}
}