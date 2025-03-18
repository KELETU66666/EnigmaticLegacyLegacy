package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRedemptionPotion extends ItemBase {

    public ItemRedemptionPotion() {
        super("redemption_potion", EnumRarity.EPIC);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.redemptionPotion1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.redemptionPotion2"));
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
        return EnumAction.DRINK;
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            IForbiddenConsumed.get(entityplayer).setConsumed(false);
            entityplayer.addStat(StatList.getObjectUseStats(this));

            if (entityplayer instanceof EntityPlayerMP)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
            }
        }

        stack.shrink(1);
        return stack;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        ItemStack itemstack = player.getHeldItem(handIn);
        player.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }
}