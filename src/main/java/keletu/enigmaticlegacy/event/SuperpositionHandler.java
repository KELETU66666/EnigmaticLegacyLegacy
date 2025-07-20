package keletu.enigmaticlegacy.event;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Lists;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.*;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.api.quack.IProperShieldUser;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import keletu.enigmaticlegacy.item.ItemEldritchPan;
import keletu.enigmaticlegacy.item.ItemInfernalShield;
import keletu.enigmaticlegacy.item.ItemSpellstoneBauble;
import keletu.enigmaticlegacy.item.ItemTheCube;
import keletu.enigmaticlegacy.packet.PacketPortalParticles;
import keletu.enigmaticlegacy.util.RealSmoothTeleporter;
import keletu.enigmaticlegacy.util.Vector3;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

public class SuperpositionHandler {

    static Random RANDOM = new Random();

    public static int getBaubleSlots(EntityPlayer player) {
        return BaublesApi.getBaublesHandler(player).getSlots();
    }

    public static ItemStack getSlotBauble(EntityPlayer player, int slotId) {
        return BaublesApi.getBaubles(player).getStackInSlot(slotId);
    }

    public static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation(EnigmaticLegacy.MODID, name);
        return new SoundEvent(location).setRegistryName(name);
    }

    public static ItemStack getAdvancedBaubles(final EntityLivingBase entity) {
        if (entity != null) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
            ItemStack stack;

            for (int i = 0; i < handler.getSlots(); i++) {
                stack = handler.getStackInSlot(i);
                if (stack.getItem() instanceof ItemSpellstoneBauble)
                    return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getTheCube(final EntityLivingBase entity) {
        if (entity != null) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
            ItemStack stack;

            for (int i = 0; i < handler.getSlots(); i++) {
                stack = handler.getStackInSlot(i);
                if (stack.getItem() instanceof ItemTheCube)
                    return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static void sendToDimension(EntityPlayerMP player, int dimension, ITeleporter teleporter) {
        if (player.world.provider.getDimension() != dimension) {
            WorldServer world = SuperpositionHandler.getWorld(dimension);
            if (world != null) {
                player.changeDimension(dimension, teleporter);
            }
        }
    }

    public static void sendToDimension(EntityPlayerMP player, int dimension) {
        sendToDimension(player, dimension, new RealSmoothTeleporter());
    }

    @SafeVarargs
    public static <T> T getRandomElement(List<T> list, T... excluding) {
        List<T> filtered = new ArrayList<>(list);
        Arrays.stream(excluding).forEach(filtered::remove);

        if (filtered.size() <= 0)
            throw new IllegalArgumentException("List has no valid elements to choose");
        else if (filtered.size() == 1)
            return filtered.get(0);
        else
            return filtered.get(RANDOM.nextInt(filtered.size()));
    }

    public static WorldServer getWorld(int key) {
        WorldServer ret = net.minecraftforge.common.DimensionManager.getWorld(key, true);
        if (ret == null) {
            net.minecraftforge.common.DimensionManager.initDimension(key);
            ret = net.minecraftforge.common.DimensionManager.getWorld(key);
        }
        return ret;
    }

    public static WorldServer getOverworld() {
        return SuperpositionHandler.getWorld(0);
    }

    public static WorldServer getNether() {
        return SuperpositionHandler.getWorld(-1);
    }

    public static WorldServer getEnd() {
        return SuperpositionHandler.getWorld(1);
    }

    @Nullable
    public static EntityLivingBase getObservedEntity(final EntityPlayer player, final World world, final float range, final int maxDist) {
        EntityLivingBase newTarget = null;
        Vector3 target = Vector3.fromEntityCenter(player);
        List<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
        for (int distance = 1; entities.size() == 0 && distance < maxDist; ++distance) {
            target = target.add(new Vector3(player.getLookVec()).multiply(distance)).add(0.0, 0.5, 0.0);
            entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target.x - range, target.y - range, target.z - range, target.x + range, target.y + range, target.z + range));
            entities.remove(player);
        }
        if (entities.size() > 0) {
            newTarget = entities.get(0);
        }
        return newTarget;
    }

    /**
     * Attempts to find valid location within given radius and teleport entity
     * there.
     *
     * @return True if teleportation were successfull, false otherwise.
     */
    public static boolean validTeleportRandomly(Entity entity, World world, int radius) {
        int d = radius * 2;

        double x = entity.posX + ((Math.random() - 0.5D) * d);
        double y = entity.posY + ((Math.random() - 0.5D) * d);
        double z = entity.posZ + ((Math.random() - 0.5D) * d);
        return SuperpositionHandler.validTeleport(entity, x, y, z, world, radius);
    }

    /**
     * Attempts to teleport entity at given coordinates, or nearest valid location
     * on Y axis.
     *
     * @return True if successfull, false otherwise.
     */

    public static boolean validTeleport(Entity entity, double x_init, double y_init, double z_init, World world, int checkAxis) {

        int x = (int) x_init;
        int y = (int) y_init;
        int z = (int) z_init;

        IBlockState block = world.getBlockState(new BlockPos(x, y - 1, z));

        if (world.isAirBlock(new BlockPos(x, y - 1, z)) & block.isFullCube()) {

            for (int counter = 0; counter <= checkAxis; counter++) {

                if (!world.isAirBlock(new BlockPos(x, y + counter - 1, z)) & world.getBlockState(new BlockPos(x, y + counter - 1, z)).isFullCube() & world.isAirBlock(new BlockPos(x, y + counter, z)) & world.isAirBlock(new BlockPos(x, y + counter + 1, z))) {

                    world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));

                    EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(entity.posX, entity.posY, entity.posZ, 72, 1.0D, false), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 128));

                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) entity;
                        player.attemptTeleport(x + 0.5, y + counter, z + 0.5);
                    } else {
                        ((EntityLivingBase) entity).attemptTeleport(x + 0.5, y + counter, z + 0.5);
                    }

                    world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));

                    EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(entity.posX, entity.posY, entity.posZ, 48, 1.0D, false), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 128));

                    return true;
                }

            }

        } else {

            for (int counter = 0; counter <= checkAxis; counter++) {

                if (!world.isAirBlock(new BlockPos(x, y - counter - 1, z)) & world.getBlockState(new BlockPos(x, y - counter - 1, z)).isFullCube() & world.isAirBlock(new BlockPos(x, y - counter, z)) & world.isAirBlock(new BlockPos(x, y - counter + 1, z))) {

                    world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
                    EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(entity.posX, entity.posY, entity.posZ, 48, 1.0D, false), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 128));

                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) entity;
                        player.attemptTeleport(x + 0.5, y - counter, z + 0.5);
                    } else {
                        ((EntityLivingBase) entity).attemptTeleport(x + 0.5, y - counter, z + 0.5);
                    }

                    world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
                    EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(entity.posX, entity.posY, entity.posZ, 48, 1.0D, false), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 128));

                    return true;
                }

            }

        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void lookAt(double px, double py, double pz, EntityPlayerSP me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        // to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;

        yaw += 90f;
        me.rotationYaw = (float) pitch;
        me.rotationPitch = (float) yaw;
        // me.rotationYawHead = (float)yaw;
    }

    /**
     * Checks whether or on the player is within the range of any active beacon.
     *
     * @author Integral
     */

    public static boolean isInBeaconRange(EntityPlayer player) {
        if (player.world.isRemote)
            return false;

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

    public static NBTBase getPersistentTag(EntityPlayer player, String tag, NBTBase expectedValue) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persistent;

        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
        } else {
            persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        if (persistent.hasKey(tag))
            return persistent.getTag(tag);
        else {
            persistent.setTag(tag, expectedValue);
            return expectedValue;
        }

    }

    public static void setPersistentInteger(EntityPlayer player, String tag, int value) {
        setPersistentTag(player, tag, new NBTTagInt(value));
    }

    public static int getPersistentInteger(EntityPlayer player, String tag, int expectedValue) {
        NBTBase theTag = getPersistentTag(player, tag, new NBTTagInt(expectedValue));
        return theTag instanceof NBTTagInt ? ((NBTTagInt) theTag).getInt() : expectedValue;
    }

    public static void removePersistentTag(EntityPlayer player, String tag) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persistent;

        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
        } else {
            persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        if (persistent.hasKey(tag)) {
            persistent.removeTag(tag);
        }
    }


    public static void setPersistentBoolean(EntityPlayer player, String tag, boolean value) {
        SuperpositionHandler.setPersistentTag(player, tag, new NBTTagByte((byte) (value ? 1 : 0)));
    }

    public static boolean getPersistentBoolean(EntityPlayer player, String tag, boolean value) {
        if (!hasPersistentTag(player, tag))
            return false;
        return Objects.equals(SuperpositionHandler.getPersistentTag(player, tag, new NBTTagByte((byte) (value ? 1 : 0))), new NBTTagByte((byte) (1)));
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

    public static boolean onDamageSourceBlocking(EntityLivingBase blocker, ItemStack useItem, DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if (blocker instanceof EntityPlayer && useItem != null) {
            EntityPlayer player = (EntityPlayer) blocker;
            boolean blocking = ((IProperShieldUser) blocker).isActuallyReallyBlocking();

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
                                        EnigmaticEvents.knockbackThatBastard.remove(living);
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

    public static boolean canPerformanceSweeping(EntityPlayer player) {
        double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
        boolean flag2 = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding();
        float f2 = player.getCooledAttackStrength(0.5F);
        boolean flag = f2 > 0.9F;

        if (!player.isSprinting() && !flag2 && !flag && player.onGround && d0 < (double) player.getAIMoveSpeed()) {
            ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);

            return itemstack.getItem() instanceof ItemSword;
        }

        return false;
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

        boolean lol = false;
        if (KeepBaublesEvent.cursedPlayers.contains(player.getUniqueID())) {
            lol = true;
            KeepBaublesEvent.cursedPlayers.remove(player.getUniqueID());
        }

        return EnigmaticLegacy.soulCrystal.getLostCrystals(player) < EnigmaticConfigs.heartLoss && lol;
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
            if (!(hasBlessed(player) && isBlessed(stack))) {
                event.setCanceled(true);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0f, 0.5F);
            }

        }
    }

    public static void enforce(PlayerInteractEvent event) {
        genericEnforce(event, event.getEntityPlayer(), event.getItemStack());
    }

    public static boolean isBlessed(ItemStack stack) {
        return EnigmaticConfigs.blessedItemList.contains(stack.getItem().getRegistryName());
    }

    public static boolean isCursed(ItemStack stack) {
        return EnigmaticConfigs.cursedItemList.contains(stack.getItem().getRegistryName());
    }

    public static boolean isEldritch(ItemStack stack) {
        return EnigmaticConfigs.eldritchItemList.contains(stack.getItem().getRegistryName());
    }

    public static boolean hasBlessed(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, blessedRing) != -1;
    }

    public static boolean hasCursed(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, cursedRing) != -1;
    }

    public static boolean hasPearl(EntityPlayer player) {
        return (hasBlessed(player) || hasCursed(player)) && player.inventory.hasItemStack(new ItemStack(enchanterPearl));
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

    /**
     * Merges enchantments from mergeFrom onto input ItemStack, with exact same
     * rules as vanilla Anvil when used in Survival Mode.
     *
     * @param input
     * @param mergeFrom
     * @param overmerge Shifts the rules of merging so that they make more sense with Apotheosis compat
     * @return Copy of input ItemStack with new enchantments merged from mergeFrom
     */

    public static ItemStack mergeEnchantments(ItemStack input, ItemStack mergeFrom, boolean overmerge, boolean onlyTreasure) {
        ItemStack returnedStack = input.copy();
        Map<Enchantment, Integer> inputEnchants = EnchantmentHelper.getEnchantments(returnedStack);
        Map<Enchantment, Integer> mergedEnchants = EnchantmentHelper.getEnchantments(mergeFrom);

        for (Enchantment mergedEnchant : mergedEnchants.keySet()) {
            if (mergedEnchant != null) {
                int inputEnchantLevel = inputEnchants.getOrDefault(mergedEnchant, 0);
                int mergedEnchantLevel = mergedEnchants.get(mergedEnchant);

                if (!overmerge) {
                    mergedEnchantLevel = inputEnchantLevel == mergedEnchantLevel ? (mergedEnchantLevel + 1 > mergedEnchant.getMaxLevel() ? mergedEnchant.getMaxLevel() : mergedEnchantLevel + 1) : Math.max(mergedEnchantLevel, inputEnchantLevel);
                } else {
                    mergedEnchantLevel = inputEnchantLevel > 0 ? Math.max(mergedEnchantLevel, inputEnchantLevel) + 1 : Math.max(mergedEnchantLevel, inputEnchantLevel);
                    mergedEnchantLevel = Math.min(mergedEnchantLevel, 10);
                }

                boolean compatible = mergedEnchant.canApply(input);
                if (input.getItem() instanceof ItemEnchantedBook) {
                    compatible = true;
                }

                for (Enchantment originalEnchant : inputEnchants.keySet()) {
                    if (originalEnchant != mergedEnchant && !mergedEnchant.isCompatibleWith(originalEnchant)) {
                        compatible = false;
                    }
                }

                if (compatible) {
                    if (!onlyTreasure || mergedEnchant.isTreasureEnchantment() || mergedEnchant.isCurse()) {
                        inputEnchants.put(mergedEnchant, mergedEnchantLevel);
                    }
                }
            }
        }

        EnchantmentHelper.setEnchantments(inputEnchants, returnedStack);
        return returnedStack;
    }

    public static double getX(Entity ett, double var1) {
        return ett.posX + (double)ett.width * var1;
    }

    public static double getRandomX(Entity ett, double var1) {
        return getX(ett, (2.0D * ett.world.rand.nextDouble() - 1.0D) * var1);
    }

    public static double getZ(Entity ett, double var1) {
        return ett.posZ + (double)ett.width * var1;
    }

    public static double getRandomZ(Entity ett, double var1) {
        return getZ(ett, (2.0D * ett.world.rand.nextDouble() - 1.0D) * var1);
    }

    public static float nextFloatFromHigher(Random rand, float bound) {
        checkBound(bound);

        return boundedNextFloat(rand, bound);
    }

    public static float boundedNextFloat(Random rng, float bound) {
        // Specialize boundedNextFloat for origin == 0, bound > 0
        float r = rng.nextFloat();
        r = r * bound;
        if (r >= bound) // may need to correct a rounding problem
            r = Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
        return r;
    }

    public static void checkBound(float bound) {
        if (!(bound > 0.0 && bound < Float.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("error!");
        }
    }
}
