package keletu.enigmaticlegacy.item;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityBlessedStone;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.helper.ItemNBTHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlessStone extends ItemBase {
    public ItemBlessStone() {
        super("blessed_stone", EnumRarity.EPIC);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            if (ItemNBTHelper.getBoolean(stack, "Hardcore", false)) {
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone1_hardcore"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone2_hardcore"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone_hardcore_info"));
            } else {
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone1"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone2"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone3"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone_info_1"));
                list.add(I18n.format("tooltip.enigmaticaddons.blessStone_info_2"));
            }
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone6"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        list.add("");
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityBlessedStone item = new EntityBlessedStone(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;

        return item;
    }

    public static Vec3i atBottomCenterOf(Vec3i pToCopy) {
        return atLowerCornerWithOffset(pToCopy, 0.5D, 0.0D, 0.5D);
    }

    public static Vec3i atLowerCornerWithOffset(Vec3i pToCopy, double pOffsetX, double pOffsetY, double pOffsetZ) {
        return new Vec3i((double) pToCopy.getX() + pOffsetX, (double) pToCopy.getY() + pOffsetY, (double) pToCopy.getZ() + pOffsetZ);
    }

    public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);
        if (SuperpositionHandler.hasCursed(player) && ItemNBTHelper.getBoolean(item, "Hardcore", false)) {
            for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                EnigmaticLegacy.logger.log("slot" + i);
                EnigmaticLegacy.logger.log("slot" + BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedRing));
                if (i == BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedRing)) {
                    BaublesApi.getBaublesHandler(player).setStackInSlot(i, ItemStack.EMPTY);
                    BaublesApi.getBaublesHandler(player).setStackInSlot(i, new ItemStack(EnigmaticLegacy.blessedRing));
                }
                if (i == BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.desolationRing))
                    BaublesApi.getBaublesHandler(player).setStackInSlot(i, ItemStack.EMPTY);
            }
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.PLAYERS, 1.0F, 0.5F);
            //player.addPotionEffect(new PotionEffect(MobEffects.DARKNESS, 200));
            player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200));
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200));
            player.swingArm(hand);
            item.shrink(1);
            EntityLightningBolt lightningbolt = new EntityLightningBolt(player.world, atBottomCenterOf(player.getPosition()).getX(), atBottomCenterOf(player.getPosition()).getY(), atBottomCenterOf(player.getPosition()).getZ(), false);
            player.world.spawnEntity(lightningbolt);
            List<Entity> entities = player.world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().grow(10));
            for (Entity entity : entities) {
                lightningbolt = new EntityLightningBolt(player.world, atBottomCenterOf(entity.getPosition()).getX(), atBottomCenterOf(entity.getPosition()).getY(), atBottomCenterOf(entity.getPosition()).getZ(), false);
                player.world.spawnEntity(lightningbolt);
            }
            if (!level.isRemote) {
                ((WorldServer) level).spawnParticle(EnumParticleTypes.END_ROD, player.posX, player.posY + 0.75, player.posZ, 24, 0.0, 0.0, 0.0, 0.1D);
                ((WorldServer) level).spawnParticle(EnumParticleTypes.SPELL_WITCH, player.posX, player.posY + 0.75, player.posZ, 24, 0.0, 0.0, 0.0, 0.05D);
            }
            return ActionResult.newResult(EnumActionResult.SUCCESS, item);
        }
        return ActionResult.newResult(EnumActionResult.PASS, item);
    }
}