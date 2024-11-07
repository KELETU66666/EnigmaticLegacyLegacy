package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.helper.ExperienceHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFabulousScroll extends ItemHeavenScroll {

	public ItemFabulousScroll() {
		super("fabulous_scroll", EnumRarity.EPIC);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("");

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.fabulousScroll1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.fabulousScroll2"));
			list.add(I18n.format("tooltip.enigmaticlegacy.fabulousScroll3"));
			list.add(I18n.format("tooltip.enigmaticlegacy.fabulousScroll4"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.fabulousScroll5"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase living) {
		if (living.world.isRemote)
			return;

		if (living instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) living;

			boolean inRange = SuperpositionHandler.isInBeaconRange(player);

			if (!SuperpositionHandler.isInBeaconRange(player))
				if (Math.random() <= (this.baseXpConsumptionProbability*8) * ELConfigs.flyingScrollXpCostModifier && player.capabilities.isFlying) {
					ExperienceHelper.drainPlayerXP(player, 1);
				}

			this.handleFabulousFlight(player, inRange);
		}

	}

	protected void handleFabulousFlight(EntityPlayer player, boolean inRange) {
		try {
			if (ExperienceHelper.getPlayerXP(player) > 0 || inRange) {

				if (!player.capabilities.allowFlying) {
					player.capabilities.allowFlying = true;
				}

				player.sendPlayerAbilities();
				this.flyMap.put(player, 100);

			} else if (this.flyMap.get(player) > 1) {
				this.flyMap.put(player, this.flyMap.get(player)-1);
			} else if (this.flyMap.get(player) == 1) {
				if (!player.isCreative()) {
					player.capabilities.allowFlying = false;
					player.capabilities.isFlying = false;
					player.sendPlayerAbilities();
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 99, true, false));
				}

				this.flyMap.put(player, 0);
			}

		} catch (NullPointerException ex) {
			this.flyMap.put(player, 0);
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase entityLivingBase) {

		if (entityLivingBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLivingBase;

			if (!player.isCreative()) {
				player.capabilities.allowFlying = false;
				player.capabilities.isFlying = false;
				player.sendPlayerAbilities();
			}

			this.flyMap.put(player, 0);

		}
	}
}