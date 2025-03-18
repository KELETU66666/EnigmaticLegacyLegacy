package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUnholyGrail extends ItemBaseFireProof {

	public ItemUnholyGrail() {
		super("unholy_grail", EnumRarity.EPIC);
		this.maxStackSize = 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.unholyGrail1"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer))
			return stack;

		EntityPlayer player = (EntityPlayer) entityLiving;

		if (!worldIn.isRemote) {
			boolean isTheWorthyOne = SuperpositionHandler.hasCursed(player) && IForbiddenConsumed.get(player).isConsumed();

			if (!isTheWorthyOne) {
				player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 2, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.POISON, 160, 1, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 240, 0, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 160, 2, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 240, 0, false, true));

				//UseUnholyGrailTrigger.INSTANCE.trigger((ServerPlayer) player, false);
			} else {
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1000 / 2, 2, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 1600 / 2, 1, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 2400 / 2, 1, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 2000 / 2, 1, false, true));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2400 / 2, 0, false, true));
				//player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0, false, true));

				//UseUnholyGrailTrigger.INSTANCE.trigger((ServerPlayer) player, true);
			}
		}

		player.addStat(StatList.getObjectUseStats(this));

		return stack;
	}

	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		player.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}
}