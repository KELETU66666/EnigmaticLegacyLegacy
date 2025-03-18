package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemForbiddenFruit extends ItemBaseFireProof {

	public ItemForbiddenFruit() {
		super("forbidden_fruit", EnumRarity.RARE);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.forbiddenFruit1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.forbiddenFruit2"));
			list.add(I18n.format("tooltip.enigmaticlegacy.forbiddenFruit3", Math.round(EnigmaticConfigs.regenerationSubtraction * 100) + "%"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}
	}

	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.EAT;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			this.onFoodEaten(stack, worldIn, entityplayer);
			entityplayer.addStat(StatList.getObjectUseStats(this));

			if (entityplayer instanceof EntityPlayerMP)
			{
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
			}
		}

		stack.shrink(1);
		return stack;
	}

	public void onFoodEaten(ItemStack food, World worldIn, EntityPlayer player) {
		IForbiddenConsumed.get(player).setConsumed(true);

		if (player instanceof EntityPlayerMP) {
			player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 3, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 400, 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 500, 2, false, true));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
	{
		ItemStack itemstack = player.getHeldItem(handIn);

		if (!IForbiddenConsumed.get(player).isConsumed())
		{
			player.setActiveHand(handIn);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
		}
		else
		{
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		}
	}

}