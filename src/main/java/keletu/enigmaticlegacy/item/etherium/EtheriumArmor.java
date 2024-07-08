package keletu.enigmaticlegacy.item.etherium;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.core.ItemNBTHelper;
import keletu.enigmaticlegacy.event.EtheriumEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EtheriumArmor extends ItemArmor {

	public EtheriumArmor(EntityEquipmentSlot slot, int renderIndex, String name) {
		super(EnigmaticLegacy.ARMOR_ETHERIUM, renderIndex, slot);
		this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, name));
		this.setTranslationKey(name);
		MinecraftForge.EVENT_BUS.register(new EtheriumEventHandler());

		this.maxStackSize = 1;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "enigmaticlegacy:textures/models/armor/unseen_armor.png";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.EPIC;
	}

	public static boolean hasFullSet(@Nonnull EntityPlayer player) {
		if (player == null)
			return false;

		for (ItemStack stack : player.getArmorInventoryList()) {
			if (!(stack.getItem().getClass() == EtheriumArmor.class))
				return false;
		}

		return true;
	}

	public static boolean hasShield(@Nonnull EntityPlayer player) {
		if (player != null)
			return hasFullSet(player) && player.getHealth() / player.getMaxHealth() <= 0.4F;

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
			EtheriumArmor armor = (EtheriumArmor) stack.getItem();
			if (armor.getEquipmentSlot() == EntityEquipmentSlot.HEAD) {
				list.add(I18n.format("tooltip.enigmaticlegacy.etheriumHelmet1"));
			} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.CHEST) {
				list.add(I18n.format("tooltip.enigmaticlegacy.etheriumChestplate1"));
			} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.LEGS) {
				list.add(I18n.format("tooltip.enigmaticlegacy.etheriumLeggings1"));
			} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.FEET) {
				list.add(I18n.format("tooltip.enigmaticlegacy.etheriumBoots1"));
			}
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}

		if (hasFullSet(Minecraft.getMinecraft().player) || (ItemNBTHelper.verifyExistance(stack, "forceDisplaySetBonus") && ItemNBTHelper.getBoolean(stack, "forceDisplaySetBonus", false))) {
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus1"));
			list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus2") + Math.round(0.4F * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus2_1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus3"));
			list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus4") + Math.round(0.5F * 100)+ "%" + I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus4_1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus5"));

			//list.add(I18n.format("tooltip.enigmaticlegacy.etheriumArmorSetBonus6");
		}

		if (stack.isItemEnchanted()) {
			list.add("");
		}

	}

}