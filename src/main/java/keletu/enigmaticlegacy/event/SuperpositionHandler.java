package keletu.enigmaticlegacy.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Lists;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.cursedRing;
import static keletu.enigmaticlegacy.EnigmaticLegacy.enchanterPearl;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.core.Vector3;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import keletu.enigmaticlegacy.item.ItemEldritchPan;
import keletu.enigmaticlegacy.item.ItemInfernalShield;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class SuperpositionHandler {

    public static ItemStack getAdvancedBaubles(final EntityLivingBase entity) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);

        for (int i : BaubleType.TRINKET.getValidSlots())
            return handler.getStackInSlot(i);
        return ItemStack.EMPTY;
    }

    /**
     * Checks whether or on the player is within the range of any active beacon.
     *
     * @author Integral
     */

    public static boolean isInBeaconRange(EntityPlayer player) {
        List<TileEntityBeacon> list = new ArrayList<TileEntityBeacon>();
        boolean inRange = false;

        for (TileEntity tile : player.world.loadedTileEntityList) {
            if (tile instanceof TileEntityBeacon) {
                list.add((TileEntityBeacon) tile);
            }
        }

        if (list.size() > 0) {
            for (TileEntityBeacon beacon : list)
                if (beacon.getLevels() > 0) {
                    try {
                        if (beacon.getBeamSegments().isEmpty()) {
                            continue;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    int range = (beacon.getLevels() + 1) * 10;
                    double distance = Math.sqrt(beacon.getPos().distanceSq(player.getPosition()));

                    if (distance <= range) {
                        inRange = true;
                    }
                }
        }

        return inRange;
    }

    public static boolean hasPersistentTag(EntityPlayer player, String tag) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persistent;

        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
        } else {
            persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        return persistent.hasKey(tag);

    }

    public static void setPersistentTag(EntityPlayer player, String tag, NBTBase value) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persistent;

        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
        } else {
            persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        persistent.setTag(tag, value);
    }

    public static void setPersistentBoolean(EntityPlayer player, String tag, boolean value) {
        SuperpositionHandler.setPersistentTag(player, tag, new NBTTagByte((byte) (value ? 1 : 0)));
    }

    /**
     * Basically, expands given int array to write given Integer into there.
     */

    public static int[] addInt(int[] series, int newInt) {
        int[] newSeries = new int[series.length + 1];

        System.arraycopy(series, 0, newSeries, 0, series.length);

        newSeries[newSeries.length - 1] = newInt;
        return newSeries;

    }

    public static boolean hasAnyArmor(EntityLivingBase entity) {
        int armorAmount = 0;

        for (ItemStack stack : entity.getArmorInventoryList()) {
            if (!stack.isEmpty()) {
                armorAmount++;
            }
        }

        return armorAmount != 0;
    }

    public static boolean isWearEnigmaticAmulet(EntityPlayer player, int meta) {
        if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.ascensionAmulet) != -1)
            return true;
        else
            return BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enigmaticAmulet) != -1 && BaublesApi.getBaubles(player).getStackInSlot(0).getMetadata() == meta;
    }

    public static Vec3d vectorTo(Vec3d vec1, Vec3d vec2) {
        double x = vec2.x - vec1.x;
        double y = vec2.y - vec1.y;
        double z = vec2.z - vec1.z;
        return new Vec3d(x, y, z);
    }

    public static Vec3d multiplyVector(Vec3d vec, double x, double y, double z) {
        double newX = vec.x * x;
        double newY = vec.y * y;
        double newZ = vec.z * z;
        return new Vec3d(newX, newY, newZ);
    }

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
            } else if (useItem.getItem() instanceof ItemEldritchPan) {
                Entity projectile = source.getImmediateSource();

                if (!(projectile instanceof EntityArrow) && !(projectile instanceof EntityFireball))
                    return false;

                if (!source.isUnblockable()) {
                    Vec3d sourcePos = source.getDamageLocation();

                    if (sourcePos != null) {
                        Vec3d lookVec = blocker.getLook(1.0F);
                        Vec3d sourceToSelf = vectorTo(sourcePos, new Vec3d(blocker.getPosition())).normalize();
                        sourceToSelf = new Vec3d(sourceToSelf.x, 0.0D, sourceToSelf.z);

                        if (sourceToSelf.dotProduct(lookVec) < 0.0D) {
                            projectile.setDead();

                            FoodStats data = player.getFoodStats();
                            data.addStats(4, 0.5F);

                            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);

                            //player.gameEvent(GameEvent.EAT);

                            if (projectile instanceof EntityLargeFireball) {
                                EntityLargeFireball fireball = (EntityLargeFireball) projectile;
                                fireball.explosionPower = 0;
                            }

                            if (player.world instanceof WorldServer) {
                                WorldServer level = (WorldServer) player.world;
                                Vec3d angle = player.getLookVec();
                                angle = multiplyVector(multiplyVector(angle, 1, 0, 1).normalize(), 0.5, 0.5, 0.5);

                                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, new ItemStack(Items.ROTTEN_FLESH));
                                level.spawnParticle(EnumParticleTypes.ITEM_CRACK, player.posX + angle.x, player.posY + player.getEyeHeight() - 0.1, player.posZ + angle.z, 10, 0.3D, 0.3D, 0.3D, 0.03D, Item.getIdFromItem(Items.FIRE_CHARGE), 0);
                            }

                            return true;
                        }
                    }
                }
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

        return count;
    }

    public static List<ItemStack> getFullEquipment(EntityPlayer player) {
        List<ItemStack> equipmentStacks = Lists.newArrayList();

        equipmentStacks.add(player.getHeldItemMainhand());
        equipmentStacks.add(player.getHeldItemOffhand());
        equipmentStacks.addAll(player.inventory.armorInventory);
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++)
            equipmentStacks.add(BaublesApi.getBaubles(player).getStackInSlot(i));
        
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
        return BaublesApi.isBaubleEquipped(player, cursedRing) != -1;
    }

    public static boolean hasPearl(EntityPlayer player) {
        return hasCursed(player) && player.inventory.hasItemStack(new ItemStack(enchanterPearl));
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

    public static Entity getPointedEntity(World world, Entity entity, double minrange, double range, float padding, boolean nonCollide) {
        return getPointedEntity(world, new RayTraceResult(entity, entity.getPositionVector().add(0.0, entity.getEyeHeight(), 0.0)), entity.getLookVec(), minrange, range, padding, nonCollide);
    }

    public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
        Entity pointedEntity = null;
        Vec3d entityVec = new Vec3d(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
        Vec3d vec3d2 = entityVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AxisAlignedBB bb = ray.entityHit != null ? ray.entityHit.getEntityBoundingBox() : (new AxisAlignedBB(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z, ray.hitVec.x, ray.hitVec.y, ray.hitVec.z)).grow(0.5);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ray.entityHit, bb.expand(lookVec.x * range, lookVec.y * range, lookVec.z * range).grow(padding, padding, padding));
        double d2 = 0.0;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!(ray.hitVec.distanceTo(entity.getPositionVector()) < minrange) && (entity.canBeCollidedWith() || nonCollide) && world.rayTraceBlocks(ray.hitVec, new Vec3d(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                float f2 = Math.max(0.8F, entity.getCollisionBorderSize());
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                RayTraceResult RayTraceResult = axisalignedbb.calculateIntercept(entityVec, vec3d2);
                if (axisalignedbb.contains(entityVec)) {
                    if (0.0 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = 0.0;
                    }
                } else if (RayTraceResult != null) {
                    double d3 = entityVec.distanceTo(RayTraceResult.hitVec);
                    if (d3 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }
}
