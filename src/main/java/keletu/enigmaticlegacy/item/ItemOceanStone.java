package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.EnigmaticConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.util.helper.ExperienceHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemOceanStone extends ItemSpellstoneBauble {

    public final int xpCostBase = 150;
    public final int nightVisionDuration = 210;

    public ItemOceanStone() {
        super("ocean_stone", EnumRarity.RARE);

        this.immunityList.add(DamageSource.DROWN.damageType);

        this.resistanceList.put(DamageSource.IN_FIRE.damageType, () -> 2F);
        this.resistanceList.put(DamageSource.ON_FIRE.damageType, () -> 2F);
        this.resistanceList.put(DamageSource.LAVA.damageType, () -> 2F);
        this.resistanceList.put(DamageSource.HOT_FLOOR.damageType, () -> 2F);
        this.resistanceList.put("fireball", () -> 2F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone2"));
            //list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone3"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStoneCooldown")/* + TextFormatting.GOLD + oceanStoneSpellstoneCooldown / 20.0F*/);
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone5") + TextFormatting.GOLD + oceanStoneUnderwaterCreaturesResistance * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.oceanStone5_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone6"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone7"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone8"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone9"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone10"));
            list.add(I18n.format("tooltip.enigmaticlegacy.oceanStone11"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        //try {
        //    list.add("");
        //    list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind") + TextFormatting.LIGHT_PURPLE + KeyBinding.getDisplayString("key.spellstoneAbility").get().toUpperCase());
        //} catch (NullPointerException ex) {
        //    // Just don't do it lol
        //}

    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (IForbiddenConsumed.get(player).getSpellstoneCooldown() > 0)
            return;

        if (player.world.provider.getDimension() == 0)
           /* if (!world.getWorldInfo().isThundering())*/ {
                boolean paybackReceived = false;

                /*
                 * ItemStack scroll = SuperpositionHandler.getCurioStack(player,
                 * EnigmaticLegacy.xpScroll);
                 *
                 * if (scroll != null && ItemNBTHelper.getInt(scroll, "XPStored", 0) >=
                 * xpCostBase*2) { ItemNBTHelper.setInt(scroll, "XPStored",
                 * ItemNBTHelper.getInt(scroll, "XPStored", 0) - (int)
                 * ((xpCostBase+(Math.random()*xpCostBase))*ConfigHandler.
                 * OCEAN_STONE_XP_COST_MODIFIER.getValue())); paybackReceived = true; } else
                 */

                if (ExperienceHelper.getPlayerXP(player) >= this.xpCostBase * 2) {
                    ExperienceHelper.drainPlayerXP(player, (int) ((this.xpCostBase + (Math.random() * this.xpCostBase)) * EnigmaticConfigs.oceanStoneXpCostModifier));
                    paybackReceived = true;
                }

                if (paybackReceived) {

                    if (world instanceof WorldServer) {
                        WorldServer WorldServer = (WorldServer) world;

                        int thunderstormTime = (int) (10000 + (Math.random() * 20000));

                        WorldServer.getWorldInfo().setRaining(true);
                        WorldServer.getWorldInfo().setThundering(true);
                        WorldServer.getWorldInfo().setThunderTime(thunderstormTime);
						/*
							info.setWanderingTraderSpawnDelay(delay);
							info.setRaining(true);
							info.setThundering(true);
							info.setRainTime(thunderstormTime);
							info.setThunderTime(thunderstormTime);
						 */
                    }

                    world.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.NEUTRAL, 2.0F, (float) (0.7F + (Math.random() * 0.3D)));

                    IForbiddenConsumed.get(player).setSpellstoneCooldown(EnigmaticConfigs.oceanStoneSpellstoneCooldown);
                }

            }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            EnigmaticLegacy.miningCharm.removeNightVisionEffect((EntityPlayer) living, this.nightVisionDuration);
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        super.onWornTick(stack, living);
        if (living instanceof EntityPlayer & !living.world.isRemote) {
            EntityPlayer player = (EntityPlayer) living;

            if (player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ)).getBlock() == Blocks.WATER || player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ)).getBlock() == Blocks.FLOWING_WATER) {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, this.nightVisionDuration, 0, true, false));
                player.setAir(300);
            } else {
                EnigmaticLegacy.miningCharm.removeNightVisionEffect(player, this.nightVisionDuration);
            }
        }

    }

    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(UUID.fromString("13faf191-bf38-4654-b369-cc1f4f1143bf"), "Swim speed bonus", oceanStoneSwimmingSpeedBoost, 2));
    }

}