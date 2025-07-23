package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.interfaces.IFortuneBonus;
import keletu.enigmaticlegacy.util.interfaces.IKeptBauble;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlessRing extends ItemBaseBauble implements IFortuneBonus, ILootingBonus, IKeptBauble {
    public static final String CURSED_SPAWN = "CursedNextSpawn";
    public static final String BLESS_SPAWN = "BlessNextSpawn";

    public ItemBlessRing() {
        super("blessed_ring", EnumRarity.EPIC);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing1"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing2"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing3", Math.round((1 - EnigmaticConfigs.blessedOneDamageResistance) * 100) + "%"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing4", Math.round(EnigmaticConfigs.blessedOneDamageBoost * 100) + "%"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing5"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing6", (EnigmaticConfigs.lootingBonus + 1) / 2));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing7", (EnigmaticConfigs.fortuneBonus + 1) / 2));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing8"));
            list.add(I18n.format("tooltip.enigmaticaddons.blessRing9"));
        } else {
            //if (CursedRing.enableLore.getValue()) {
            //    list.add(I18n.format("tooltip.enigmaticaddons.blessRingLore1"));
            //    list.add(I18n.format("tooltip.enigmaticaddons.blessRingLore2"));
            //    list.add(I18n.format("tooltip.enigmaticaddons.blessRingLore3"));
            //    list.add(I18n.format("tooltip.enigmaticaddons.blessRingLore4"));
            //    list.add("");
            //}

            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing1"));

            if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isCreative()) {
                list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing2_creative"));
            } else {
                list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing2"));
            }
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return player instanceof EntityPlayer && !SuperpositionHandler.hasCursed((EntityPlayer) player) && !SuperpositionHandler.hasBlessed((EntityPlayer) player);
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase living) {
        return living instanceof EntityPlayer && ((EntityPlayer) living).isCreative();
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        /*if (entity instanceof EntityPlayer player && SuperAddonHandler.isPunishedOne(player)) {
            SuperpositionHandler.setPersistentInteger(player, "Punishment", 1 + SuperpositionHandler.getPersistentInteger(player, "Punishment", 0));
            if (SuperpositionHandler.getPersistentInteger(player, "Punishment", 0) % 20 == 0) {
                player.playSound(SoundEvents.ITEM_TOTEM_USE, 0.6F, 0.0F);
                World level = player.world;
                if (!level.isRemote) {
                    EntityLightningBolt lightningBolt = new EntityLightningBolt(player.world, atBottomCenterOf(player.getPosition()).getX(), atBottomCenterOf(player.getPosition()).getY(), atBottomCenterOf(player.getPosition()).getZ(), false);
                    player.world.spawnEntity(lightningBolt);
                }
            }
            if (SuperpositionHandler.getPersistentInteger(player, "Punishment", 0) > 100) {
                SuperpositionHandler.removePersistentTag(player, "Punishment");
                SuperpositionHandler.destroyCurio(player, EnigmaticAddonItems.BLESS_RING);
                SuperpositionHandler.destroyCurio(player, EnigmaticItems.CURSED_RING);
                player.attackEntityFrom(DamageSource.OUT_OF_WORLD, player.getMaxHealth() * 1000F);
                player.experience = 0;
                player.experienceLevel = 0;
                player.setDead();
            }
        }*/
        if (entity.ticksExisted % EnigmaticConfigs.blessedOneRegenerationSpeed == 0 && entity.getHealth() < entity.getMaxHealth() * 0.9F) {
            float delta = entity.getMaxHealth() * 0.9F - entity.getHealth();
            entity.heal(delta / 20.0F);
        }
    }

    public int bonusLevelFortune() {
        return 1;
    }

    public int bonusLevelLooting() {
        return 1;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemIndestructible item = new EntityItemIndestructible(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            item.setThrower(((EntityItem) location).getThrower());
            item.setOwner(((EntityItem) location).getOwner());
        }

        return item;
    }
}