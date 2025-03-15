package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketItemNBTSync;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemMagmaHeart extends ItemSpellstoneBauble {

	public List<String> nemesisList = new ArrayList<String>();

	public ItemMagmaHeart() {
		super("magma_heart", EnumRarity.UNCOMMON);

		//this.immunityList.add(DamageSource.LAVA.damageType);
		this.immunityList.add(DamageSource.IN_FIRE.damageType);
		this.immunityList.add(DamageSource.ON_FIRE.damageType);
		this.immunityList.add(DamageSource.HOT_FLOOR.damageType);
		//immunityList.add("fireball");

		this.nemesisList.add("mob");
		this.nemesisList.add(DamageSource.GENERIC.damageType);
		this.nemesisList.add("player");
		//nemesisList.add("arrow");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

		list.add("");

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore2"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCoreCooldown") + TextFormatting.GOLD + EnigmaticConfigs.magmaHeartSpellstoneCooldown / 20.0F + I18n.format("tooltip.enigmaticlegacy.blazingCoreCooldown_1"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore3"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore4"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore5"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore6"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore7"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore8"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore9"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore10"));
			list.add(I18n.format("tooltip.enigmaticlegacy.blazingCore11"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}

		//try {
		//	list.add("");
		//	list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind") + TextFormatting.LIGHT_PURPLE + KeyBinding.getDisplayString("key.spellstoneAbility"));
		//} catch (NullPointerException ex) {
		//	// Just don't do it lol
		//}
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player)
	{
		if (player.isBurning()) {
			player.extinguish();
		}

		if (!player.world.isRemote)
		{
			if (itemstack.getTagCompound() == null)
			{
				itemstack.setTagCompound(new NBTTagCompound());
			}

			NBTTagCompound compound = itemstack.getTagCompound();

			int chargeCooldown = compound.getInteger("blazingCoreCooldown");
			if (chargeCooldown > 0)
			{
				compound.setInteger("blazingCoreCooldown", chargeCooldown - 1);
			}
			else
			{
				int charge = compound.getInteger("blazingCoreCharge");
				if (charge < 200)
				{
					compound.setInteger("blazingCoreCharge", charge + 1);
				}
			}

			if (player instanceof EntityPlayerMP) {
				syncItemNBT((EntityPlayerMP) player, itemstack);
			}
		}
	}

	public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
	}


	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	private void syncItemNBT(EntityPlayerMP player, ItemStack stack) {
		EnigmaticLegacy.packetInstance.sendTo(new PacketItemNBTSync(stack, player), player);
	}
}