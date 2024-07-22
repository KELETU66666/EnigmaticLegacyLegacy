package keletu.enigmaticlegacy.event;

import baubles.api.BaublesApi;
import com.google.common.collect.Lists;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.cursedRing;
import static keletu.enigmaticlegacy.EnigmaticLegacy.enchanterPearl;
import keletu.enigmaticlegacy.api.ExtendedBaublesApi;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.core.Vector3;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import keletu.enigmaticlegacy.item.ItemInfernalShield;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class SuperpositionHandler {

    public static boolean onDamageSourceBlocking(EntityLivingBase blocker, ItemStack useItem, DamageSource source) {
        if (blocker instanceof EntityPlayer && useItem != null) {
            EntityPlayer player = (EntityPlayer) blocker;
            boolean blocking = player.isActiveItemStackBlocking();

            if (blocking && useItem.getItem() instanceof ItemInfernalShield) {

                // defend against Piercing... for now

                if (!source.isMagicDamage() && player.isActiveItemStackBlocking()) {
                    Vec3d sourcePos = source.getDamageLocation();
                    if (sourcePos != null && source.getTrueSource() != null) {
                        if (!lookPlayersBack(player.rotationYaw, source.getTrueSource().rotationYaw, 50.0D)) {

                            int strength = -1;

                            if (player.isPotionActive(EnigmaticLegacy.blazingStrengthEffect)) {
                                PotionEffect effectInstance = player.getActivePotionEffect(EnigmaticLegacy.blazingStrengthEffect);
                                if (effectInstance != null) {
                                    strength = effectInstance.getAmplifier();
                                    player.removePotionEffect(EnigmaticLegacy.blazingStrengthEffect);
                                    strength = Math.min(strength, 2);
                                }
                            }

                            player.addPotionEffect(new PotionEffect(EnigmaticLegacy.blazingStrengthEffect, 1200, strength + 1, true, true));

                            if (source.getTrueSource() instanceof EntityLivingBase && !source.getTrueSource().isDead) {
                                EntityLivingBase living = (EntityLivingBase) source.getTrueSource();
                                if (!living.isImmuneToFire() && !(living instanceof EntityGuardian)) {
                                    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

                                    // Ensure that we will not be caught in any sort of StackOverflow because of thorns-like effects
                                    if (Arrays.stream(stacktrace).filter(element -> {
                                        return SuperpositionHandler.class.getName().equals(element.getClassName());
                                    }).count() < 2) {
                                        living.setEntityInvulnerable(false);
                                        living.attackEntityFrom(new EntityDamageSource(DamageSource.ON_FIRE.getDamageType(), player), 4F);
                                        living.setFire(4);
                                        ELEvents.knockbackThatBastard.remove(living);
                                    }
                                }
                            }

                            return true;
                        }
                    }
                }
                return false;
            }
            return blocker.isActiveItemStackBlocking();
        }

        return false;
    }

    public static int getCurseAmount(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int totalCurses = 0;

        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment.isCurse() && enchantments.get(enchantment) > 0) {
                totalCurses += 1;
            }
        }

        if (stack.getItem() == EnigmaticLegacy.cursedRing) {
            totalCurses += 7;
        }

        return totalCurses;
    }

    public static int getCurseAmount(EntityPlayer player) {
        int count = 0;
        boolean ringCounted = false;

        for (ItemStack theStack : getFullEquipment(player)) {
            if (theStack != null) {
                if (theStack.getItem() != EnigmaticLegacy.cursedRing || !ringCounted) {
                    count += getCurseAmount(theStack);

                    if (theStack.getItem() == EnigmaticLegacy.cursedRing) {
                        ringCounted = true;
                    }
                }
            }
        }
        if (hasCursed(player))
            count += 7;

        return count;
    }

    public static List<ItemStack> getFullEquipment(EntityPlayer player) {
        List<ItemStack> equipmentStacks = Lists.newArrayList();

        equipmentStacks.add(player.getHeldItemMainhand());
        equipmentStacks.add(player.getHeldItemOffhand());
        equipmentStacks.addAll(player.inventory.armorInventory);

        return equipmentStacks;
    }

    public static boolean shouldPlayerDropSoulCrystal(EntityPlayer player) {
        if (Loader.isModLoaded("gokistats")) {
            return false;
        }
        return EnigmaticLegacy.soulCrystal.getLostCrystals(player) < ELConfigs.heartLoss && hasCursed(player);
    }

    public static void loseSoul(EntityPlayer player) {
        if (hasCursed(player)) {
            if (player instanceof EntityPlayerMP && shouldPlayerDropSoulCrystal(player)) {
                ItemStack soulCrystal = EnigmaticLegacy.soulCrystal.createCrystalFrom(player);
                EntityItemSoulCrystal droppedSoulCrystal = new EntityItemSoulCrystal(player.world, player.posX, player.posY + 1.5, player.posZ, soulCrystal);
                droppedSoulCrystal.setOwnerId(player.getUniqueID());
                player.world.spawnEntity(droppedSoulCrystal);
            }
        }
    }

    /**
     * Accelerates the entity towards specific point.
     *
     * @param originalPosVector Absolute coordinates of the point entity should move
     *                          towards.
     * @param modifier          Applied velocity modifier.
     */

    public static void setEntityMotionFromVector(Entity entity, Vector3 originalPosVector, float modifier) {
        Vector3 entityVector = Vector3.fromEntityCenter(entity);
        Vector3 finalVector = originalPosVector.subtract(entityVector);

        if (finalVector.mag() > 1)
            finalVector = finalVector.normalize();

        entity.motionX = finalVector.x * modifier;
        entity.motionY = finalVector.y * modifier;
        entity.motionZ = finalVector.z * modifier;
    }

    /**
     * Creates and returns simple bounding box of given radius around the entity.
     */

    public static AxisAlignedBB getBoundingBoxAroundEntity(final Entity entity, final double radius) {
        return new AxisAlignedBB(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
    }

    /**
     * @return True if ItemStack can be added to player's inventory (fully or
     * partially), false otherwise.
     */

    public static boolean canPickStack(EntityPlayer player, ItemStack stack) {

        if (player.inventory.getSlotFor(ItemStack.EMPTY) >= 0)
            return true;
        else {
            List<ItemStack> allInventories = new ArrayList<ItemStack>();

            // allInventories.addAll(player.inventory.armorInventory);
            allInventories.addAll(player.inventory.mainInventory);
            allInventories.addAll(player.inventory.offHandInventory);

            for (ItemStack invStack : allInventories) {
                if (canMergeStacks(invStack, stack, player.inventory.getInventoryStackLimit()))
                    return true;
            }
        }

        return false;
    }

    public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2, int invStackLimit) {
        return !stack1.isEmpty() && stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < invStackLimit;
    }

    /**
     * Checks item, NBT, and meta if the item is not damageable
     */
    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private static void giveItems(EntityPlayer player, NonNullList<ItemStack> items) {
        for (ItemStack stack : items) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    public static void genericEnforce(Event event, EntityPlayer player, ItemStack stack) {
        if (!event.isCancelable() || event.isCanceled() || player == null || stack == null || stack.isEmpty() || player.isCreative())
            return;

        if ((!hasCursed(player) && isCursed(stack)) || (!isTheWorthyOne(player) && isEldritch(stack))) {
            event.setCanceled(true);
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0f, 0.5F);

        }
    }

    public static void enforce(PlayerInteractEvent event) {
        genericEnforce(event, event.getEntityPlayer(), event.getItemStack());
    }

    public static boolean isCursed(ItemStack stack) {
        return ELConfigs.cursedItemList.contains(stack.getItem().getRegistryName());
    }

    public static boolean isEldritch(ItemStack stack) {
        return ELConfigs.eldritchItemList.contains(stack.getItem().getRegistryName());
    }

    public static boolean hasCursed(EntityPlayer player) {
        return ExtendedBaublesApi.isBaubleEquipped(player, cursedRing) != -1;
    }

    public static boolean hasPearl(EntityPlayer player) {
        return !hasCursed(player) || player.inventory.hasItemStack(new ItemStack(enchanterPearl));
    }

    public static boolean hasEnderRing(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enderRing) != -1;
    }

    public static void addDrop(LivingDropsEvent event, ItemStack drop) {
        if (drop == null || drop.getItem() == null)
            return;

        EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        entityitem.setPickupDelay(10);
        event.getDrops().add(entityitem);
    }

    public static void addDropWithChance(LivingDropsEvent event, ItemStack drop, int chance) {
        if (new Random().nextInt(100) < chance) {
            addDrop(event, drop);
        }
    }

    public static ItemStack getRandomSizeStack(Item item, int minAmount, int maxAmount) {
        return new ItemStack(item, minAmount + new Random().nextInt(maxAmount - minAmount + 1));
    }

    public static ItemStack getRandomSizeStack(Item item, int minAmount, int maxAmount, int meta) {
        return new ItemStack(item, minAmount + new Random().nextInt(maxAmount - minAmount + 1), meta);
    }

    public static void addOneOf(LivingDropsEvent event, ItemStack... itemStacks) {
        int chosenStack = new Random().nextInt(itemStacks.length);
        addDrop(event, itemStacks[chosenStack]);
    }

    public static boolean isTheWorthyOne(EntityPlayer player) {
        if (hasCursed(player)) {
            IPlaytimeCounter counter = IPlaytimeCounter.get(player);
            long timeWithRing = counter.getTimeWithCurses();
            long timeWithoutRing = counter.getTimeWithoutCurses();

            if (timeWithRing <= 0)
                return false;
            else if (timeWithoutRing <= 0)
                return true;

            return timeWithRing / timeWithoutRing >= 199L;
        } else
            return false;
    }

    public static String getSufferingTime(@Nullable EntityPlayer player) {
        if (player == null)
            return "0%";
        else {
            IPlaytimeCounter counter = IPlaytimeCounter.get(player);
            long timeWithRing = counter.getTimeWithCurses();
            long timeWithoutRing = counter.getTimeWithoutCurses();
            if (timeWithRing <= 0)
                return "0%";
            else if (timeWithoutRing <= 0)
                return "100%";

            if (timeWithRing > 100000 || timeWithoutRing > 100000) {
                timeWithRing = timeWithRing / 100;
                timeWithoutRing = timeWithoutRing / 100;

                if (timeWithRing <= 0)
                    return "0%";
                else if (timeWithoutRing <= 0)
                    return "100%";
            }

            double total = timeWithRing + timeWithoutRing;
            double ringPercent = (timeWithRing / total) * 100;
            ringPercent = Math.round(ringPercent * 10.0) / 10.0;
            String text = "";

            if (ringPercent - Math.round(ringPercent) == 0) {
                text += ((int) ringPercent) + "%";
            } else {
                text += ringPercent + "%";
            }

            if ("99.5%".equals(text) && !isTheWorthyOne(player)) {
                text = "99.4%";
            }

            return text;
        }
    }


    public static boolean lookPlayersBack(double y1, double y2, double back) {
        back = Math.abs(back);
        double d = Math.abs(y1 - y2) % 360;
        double dif = d > 180.0D ? 360.0D - d : d;

        return dif < back;
    }
}
