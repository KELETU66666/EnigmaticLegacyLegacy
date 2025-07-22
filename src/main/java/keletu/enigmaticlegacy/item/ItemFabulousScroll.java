package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.helper.ExperienceHelper;
import keletu.enigmaticlegacy.util.interfaces.IScroll;
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

public class ItemFabulousScroll extends ItemHeavenScroll implements IScroll {

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

            if (player.ticksExisted % 20 != 0)
                return;

            boolean inRange = SuperpositionHandler.isInBeaconRange(player);

            final int fabConsumptionProbMod = 8;
            //only check xp drain if flying.
            //because we're only checking once per 20 ticks, increase the probability by 20
            if (this.shouldCheckXpDrain(player) && !inRange && Math.random() <= ((this.baseXpConsumptionProbability * fabConsumptionProbMod) * 20)) {
                //cost modifier hooked up to drain xp cost
                if (ExperienceHelper.getPlayerXP(player) >= EnigmaticConfigs.flyingScrollXpCostModifier)
                    ExperienceHelper.drainPlayerXP(player, (int) (EnigmaticConfigs.flyingScrollXpCostModifier));
            }

            this.handleFlight(player, inRange);
        }

    }

    @Override
    protected boolean canFly(EntityPlayer player, boolean inRangeCheckedAndSucceeded) {
        return inRangeCheckedAndSucceeded || ExperienceHelper.getPlayerXP(player) > 0 || (SuperpositionHandler.isInBeaconRange(player));
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