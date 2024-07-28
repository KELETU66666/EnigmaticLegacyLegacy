package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.core.ItemNBTHelper;
import keletu.enigmaticlegacy.util.IFortuneBonus;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemMiningCharm extends ItemBaseBauble implements IFortuneBonus {

    public final int nightVisionDuration = 210;

    public ItemMiningCharm() {
        super("mining_charm", EnumRarity.RARE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        String mode = I18n.format("tooltip.enigmaticlegacy.enabled");

        if (ItemNBTHelper.verifyExistance(stack, "nightVisionEnabled"))
            if (!ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true)) {
                mode = I18n.format("tooltip.enigmaticlegacy.disabled");
            }

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.miningCharm1") + Math.round(ELConfigs.breakSpeedBonus * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.miningCharm1_1"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.miningCharm2") + 1 + I18n.format("tooltip.enigmaticlegacy.miningCharm2_1"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.miningCharm3"));
            list.add(I18n.format("tooltip.enigmaticlegacy.miningCharm4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.miningCharm5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.miningCharm6"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        list.add("");
        list.add(I18n.format("tooltip.enigmaticlegacy.miningCharmNightVision") + mode);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        super.onWornTick(stack, living);
        if (living instanceof EntityPlayer & !living.world.isRemote)
            if (BaublesApi.isBaubleEquipped((EntityPlayer) living, EnigmaticLegacy.miningCharm) != -1) {
                EntityPlayer player = (EntityPlayer) living;

                if (ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true) && player.posY < 50 && player.world.provider.getDimension() != -1 && player.world.provider.getDimension() != 1 && !player.isInWater() && !player.world.canBlockSeeSky(player.getPosition()) && player.world.getLightBrightness(player.getPosition()) <= 8) {

                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, this.nightVisionDuration, 0, true, false));
                } else {
                    this.removeNightVisionEffect(player, this.nightVisionDuration);
                }

            }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            this.removeNightVisionEffect((EntityPlayer) living, this.nightVisionDuration);
        }
    }

    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(UUID.fromString("08c3c83d-7137-4b42-880f-b146bcb64d2e"), "Reach bonus", ELConfigs.reachDistanceBonus, 0).setSaved(false));
        attributes.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(UUID.fromString("6c913e9a-0d6f-4b3b-81b9-4c82f7778b53"), EnigmaticLegacy.MODID+":luck_bonus", 1.0, 0));
    }

    public void removeNightVisionEffect(EntityPlayer player, int duration) {
        PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect != null) {

            if (effect.getDuration() <= (duration - 1)) {
                player.removePotionEffect(MobEffects.NIGHT_VISION);
            }

        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {

        ItemStack stack = player.getHeldItem(handIn);

        if (ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true)) {
            ItemNBTHelper.setBoolean(stack, "nightVisionEnabled", false);
            world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, (float) (0.8F + (Math.random() * 0.2F)), (float) (0.8F + (Math.random() * 0.2F)));
        } else {
            ItemNBTHelper.setBoolean(stack, "nightVisionEnabled", true);
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, (float) (0.8F + (Math.random() * 0.2F)), (float) (0.8F + (Math.random() * 0.2F)));
        }

        player.swingArm(handIn);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);

    }

    @Override
    public int bonusLevelFortune() {
        return 1;
    }
}