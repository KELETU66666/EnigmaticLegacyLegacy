package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemCursedRing extends ItemBase implements IBauble {

    public ItemCursedRing() {
        super("cursed_ring", EnumHelper.addRarity("Curses", TextFormatting.DARK_RED, "Curses"));

        this.maxStackSize = 1;
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        if (living instanceof EntityPlayer) {
            living.getAttributeMap().removeAttributeModifiers(this.createAttributeMap((EntityPlayer) living));
        }
    }

    private Multimap<String, AttributeModifier> createAttributeMap(EntityPlayer player) {
        Multimap<String, AttributeModifier> attributesDefault = HashMultimap.create();

        attributesDefault.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(UUID.fromString("371929FC-4CBC-11E8-842F-0ED5F89F718B"), "generic.armor", -armorDebuff, 2));
        attributesDefault.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(UUID.fromString("22E6BD72-4CBD-11E8-842F-0ED5F89F718B"), "generic.armorToughness", -armorDebuff, 2));
		attributesDefault.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(UUID.fromString("6c913e9a-0d6f-4b3b-81b9-4c82f7778b52"), EnigmaticLegacy.MODID+":luck_bonus", 1.0, 0));

		return attributesDefault;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing3"));
            if (painMultiplier == 2.0) {
                list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing4"));
            } else {
                list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing4_alt") + TextFormatting.GOLD + painMultiplier * 100 + "%");
            }
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing6") + TextFormatting.GOLD + Math.round(armorDebuff * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.cursedRing6_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing7") + TextFormatting.GOLD + Math.round(monsterDamageDebuff * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.cursedRing7_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing8"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing9"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing10"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing11"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing12") + TextFormatting.GOLD + fortuneBonus + I18n.format("tooltip.enigmaticlegacy.cursedRing12_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing13") + TextFormatting.GOLD + lootingBonus + I18n.format("tooltip.enigmaticlegacy.cursedRing13_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing14") + TextFormatting.GOLD + Math.round(experienceBonus * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.cursedRing14_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing15") + TextFormatting.GOLD + enchantingBonus + I18n.format("tooltip.enigmaticlegacy.cursedRing15_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing16"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing17"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursedRing18"));
        } else {
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
        return player instanceof EntityPlayer && !SuperpositionHandler.hasCursed((EntityPlayer) player);
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase living) {
        return living instanceof EntityPlayer && ((EntityPlayer) living).isCreative();
    }

    public boolean isItemDeathPersistent(ItemStack stack) {
        return stack.getItem().equals(this);
    }

    /**
     * Creates and returns simple bounding box of given radius around the entity.
     */

    public static AxisAlignedBB getBoundingBoxAroundEntity(final Entity entity, final double radius) {
        return new AxisAlignedBB(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase livingPlayer) {
        if (livingPlayer.world.isRemote || !(livingPlayer instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) livingPlayer;

        player.getAttributeMap().applyAttributeModifiers(this.createAttributeMap(player));

        if (player.isCreative() || player.isSpectator())
            return;

        List<EntityLivingBase> genericMobs = livingPlayer.world.getEntitiesWithinAABB(EntityLivingBase.class, getBoundingBoxAroundEntity(player, neutralAngerRange));
        List<EntityEnderman> endermen = livingPlayer.world.getEntitiesWithinAABB(EntityEnderman.class, getBoundingBoxAroundEntity(player, endermenRandomportRange));

        for (EntityEnderman enderman : endermen) {
            if (ModCompat.COMPAT_TRINKETS && BaublesApi.isBaubleEquipped(player, ForgeRegistries.ITEMS.getValue(new ResourceLocation("xat", "ender_tiara"))) != -1)
                continue;

            if (itemRand.nextDouble() <= (0.002 * endermenRandomportFrequency)) {
                if (enderman.teleportToEntity(player) && player.canEntityBeSeen(enderman)) {
                    enderman.setAttackTarget(player);
                }
            }

        }

        for (EntityLivingBase checkedEntity : genericMobs) {
            double angerDistance = Math.max(neutralAngerRange, neutralXRayRange);

            if (checkedEntity.getDistanceSq(player.posX, player.posY, player.posZ) > angerDistance * angerDistance) {
                continue;
            }

            if (checkedEntity instanceof EntityLiving) {
                EntityLiving neutral = (EntityLiving) checkedEntity;

                if (useWhitelist && !neutralAngerWhitelist.contains(EntityList.getKey(checkedEntity))) {
                    continue;
                }

                if (!useWhitelist && neutralAngerBlacklist.contains(EntityList.getKey(checkedEntity))) {
                    continue;
                }

                if (neutral.isOnSameTeam(player)) {
                    continue;
                }

                if (neutral instanceof EntityAnimal)
                    if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.animalGuide) != -1)
                        continue;

                if (neutral instanceof EntityEnderman)
                    if (ModCompat.COMPAT_TRINKETS && BaublesApi.isBaubleEquipped(player, ForgeRegistries.ITEMS.getValue(new ResourceLocation("xat", "ender_tiara"))) != -1)
                        continue;

                if (neutral instanceof EntityTameable) {
                    if (((EntityTameable) neutral).isTamed()) {
                        continue;
                    }
                } else if (neutral instanceof EntityIronGolem) {
                    if (((EntityIronGolem) neutral).isPlayerCreated()) {
                        continue;
                    }
                } //else if (neutral instanceof BeeEntity) {
                //	if (saveTheBees || SuperpositionHandler.hasItem(player, EnigmaticLegacy.animalGuide)) {
                //		continue;
                //	}
                //}

                if (neutral.getAttackTarget() == null || neutral.getAttackTarget().isDead) {
                    if (player.canEntityBeSeen(checkedEntity) || player.getDistance(checkedEntity) <= neutralXRayRange) {
                        neutral.setAttackTarget(player);
                    } else {
                        continue;
                    }
                }
            }
        }

    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(book);

        if (list.containsKey(Enchantments.VANISHING_CURSE))
            return false;
        else
            return super.isBookEnchantable(stack, book);
    }

    public double getAngerRange() {
        return neutralAngerRange;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }
}