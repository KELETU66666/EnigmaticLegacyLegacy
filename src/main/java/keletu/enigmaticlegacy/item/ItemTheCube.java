package keletu.enigmaticlegacy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.packet.PacketRecallParticles;
import keletu.enigmaticlegacy.util.interfaces.IFortuneBonus;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ItemTheCube extends ItemSpellstoneBauble implements IFortuneBonus {
    private final List<Potion> randomBuffs;
    private final List<Potion> randomDebuffs;
    private final List<Integer> worlds;
    private final Map<EntityPlayerMP, Future<CachedTeleportationLocation>> locationCache = new WeakHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Random random = new Random();

    public ItemTheCube() {
        super("the_cube", EnumRarity.EPIC);

        this.worlds = ImmutableList.of(0, 1, -1);
        this.randomBuffs = ImmutableList.of(MobEffects.ABSORPTION, MobEffects.STRENGTH, MobEffects.REGENERATION,
                MobEffects.HASTE, MobEffects.JUMP_BOOST, MobEffects.SPEED, MobEffects.RESISTANCE);
        this.randomDebuffs = ImmutableList.of(MobEffects.BLINDNESS, MobEffects.NAUSEA, MobEffects.MINING_FATIGUE,
                MobEffects.HUNGER, MobEffects.LEVITATION, MobEffects.SLOWNESS, MobEffects.WEAKNESS,
                MobEffects.POISON, MobEffects.WITHER);

        this.immunityList.add(DamageSource.ON_FIRE.damageType);
        this.immunityList.add(DamageSource.IN_FIRE.damageType);
        this.immunityList.add(DamageSource.LAVA.damageType);
        this.immunityList.add(DamageSource.HOT_FLOOR.damageType);
        this.immunityList.add(DamageSource.CRAMMING.damageType);
        this.immunityList.add(DamageSource.DROWN.damageType);
        this.immunityList.add(DamageSource.FALL.damageType);
        this.immunityList.add(DamageSource.FLY_INTO_WALL.damageType);
        this.immunityList.add(DamageSource.CACTUS.damageType);
        this.immunityList.add(DamageSource.IN_WALL.damageType);
        this.immunityList.add(DamageSource.FALLING_BLOCK.damageType);
        //this.immunityList.add(DamageSource.SWEET_BERRY_BUSH);
    }

    public int getCooldown(EntityPlayer player) {
        return 3200;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            boolean cursed = Minecraft.getMinecraft().player != null && SuperpositionHandler.hasCursed(Minecraft.getMinecraft().player);

            list.add(I18n.format("tooltip.enigmaticlegacy.theCube1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube2"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube3", 120));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube6"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube7"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube8"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube9"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube10"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube11", (int) this.getDamageLimit(cursed)));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube12"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube13"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube14"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube15"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube16"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube17"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube18"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube19"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube20"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube21"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theCube22"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        //try {
        //	list.add("");
        //	list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase()));
        //} catch (NullPointerException ex) {
        //	// Just don't do it lol
        //}
    }

    public void applyRandomEffect(EntityLivingBase entity, boolean positive) {
        List<Potion> effects = positive ? this.randomBuffs : this.randomDebuffs;
        Potion effect = effects.get(random.nextInt(effects.size()));

        if (positive) {
            int time = 100 + random.nextInt(500);
            int amplifier = random.nextDouble() <= 0.25 ? 1 : 0;
            entity.addPotionEffect(new PotionEffect(effect, time, amplifier, false, true));
        } else {
            int time = 200 + random.nextInt(1000);
            int amplifier = random.nextDouble() <= 0.15 ? 2 : random.nextDouble() <= 0.4 ? 1 : 0;
            entity.addPotionEffect(new PotionEffect(effect, time, amplifier, false, true));
        }
    }

    public float getDamageLimit(EntityPlayer player) {
        return this.getDamageLimit(SuperpositionHandler.hasCursed(player));
    }

    private float getDamageLimit(boolean cursed) {
        return cursed ? 150 : 100;
    }

    public Multimap<String, AttributeModifier> getCurrentModifiers(EntityPlayer player) {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("a601a528-fbf3-49bb-84af-f65023c1a188"), EnigmaticLegacy.MODID + ":sprint_bonus", player.isSprinting() ? 0.35F : 0F, 2));
        return attributes;
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            AbstractAttributeMap map = player.getAttributeMap();
            map.removeAttributeModifiers(this.getCurrentModifiers(player));
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase context) {
        if (context instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) context;

            if (player.getAir() < 300) {
                player.setAir(300);
            }

            if (player.isBurning()) {
                player.extinguish();
            }

            for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
                if (effect.getPotion().isBadEffect()) {
                    player.removePotionEffect(effect.getPotion());
                }
            }

            AbstractAttributeMap map = player.getAttributeMap();
            map.applyAttributeModifiers(this.getCurrentModifiers(player));

            if (context instanceof EntityPlayerMP) {
                if (!this.locationCache.containsKey(player)) {
                    this.generateCachedLocation((EntityPlayerMP) player);
                } else {
                    Future<CachedTeleportationLocation> future = this.locationCache.get(player);

                    if (future.isDone() && !future.isCancelled()) {
                        try {
                            CachedTeleportationLocation location = future.get();

                            if (location.dimension == player.world.provider.getDimension()) {
                                this.generateCachedLocation((EntityPlayerMP) player);
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
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
    
    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (IForbiddenConsumed.get(player).getSpellstoneCooldown() > 0)
            return;

        CachedTeleportationLocation location = null;

        if (this.locationCache.containsKey(player)) {
            try {
                Future<CachedTeleportationLocation> future = this.locationCache.get(player);

                if (future.isDone()) {
                    location = this.locationCache.get(player).get();
                } else {
                    future.cancel(true);
                }

                this.locationCache.remove(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (location == null) {
            EnigmaticLegacy.logger.info("No cached location found for" + player.getGameProfile().getName() + ", generating new one synchronously.");
            location = this.findBlockPos(player.world, player, 0);
        }

        int key = location.dimension;
        MinecraftServer server = player.getServer();

        server.addScheduledTask(() -> {
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
            EnigmaticLegacy.packetInstance.sendToAllAround(new PacketRecallParticles(player.posX, player.posY + (player.getEyeHeight() / 2), player.posZ, 48, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 128));
            //player.attemptTeleport(finalLocation.x, finalLocation.y, finalLocation.z);

            if (player.world.provider.getDimension() != key) {
                SuperpositionHandler.sendToDimension(player, key);
                this.findBlockPos(SuperpositionHandler.getWorld(key), player, 0).placeEntity(world, player, 0);
            }
            //player.attemptTeleport(finalLocation.x, finalLocation.y, finalLocation.z);


            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
            EnigmaticLegacy.packetInstance.sendToAllAround(new PacketRecallParticles(player.posX, player.posY + (player.getEyeHeight() / 2), player.posZ, 48, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 128));

            IForbiddenConsumed.get(player).setSpellstoneCooldown(this.getCooldown(player));

            EnigmaticLegacy.logger.info("Player" + player.getGameProfile().getName() + "triggered active ability of Non-Euclidean Cube. Teleported to D: " + player.world.provider.getDimension() + ", X: " + player.posX + ", Y: " + player.posY + ", Z: " + player.posZ + ".");
        });
    }

    private void generateCachedLocation(EntityPlayerMP player) {
        Future<CachedTeleportationLocation> future = this.executor.submit(() -> {
            try {
                CachedTeleportationLocation location = this.findBlockPos(player.world, player, 0);
                EnigmaticLegacy.logger.debug("Found random location: " + location);
                return location;
            } catch (Exception ex) {
                EnigmaticLegacy.logger.error("Could not find random location for:" + player.getGameProfile().getName());
                ex.printStackTrace();
                throw ex;
            }
        });

        this.locationCache.put(player, future);
    }

    private CachedTeleportationLocation findBlockPos(World world, EntityPlayerMP player, int depth) {
        if (++depth > 10000) {
            return new CachedTeleportationLocation(player);
        }

        double dist = 100 + world.rand.nextDouble() * (10000 - 100);
        double angle = world.rand.nextDouble() * Math.PI * 2D;

        int x = MathHelper.floor(Math.cos(angle) * dist);
        int y = world.provider.getDimension() == -1 ? 110 : 256;
        int z = MathHelper.floor(Math.sin(angle) * dist);

        if (!isInsideWorldBorder(world, x, y, z)) {
            return findBlockPos(world, player, depth);
        }

        Chunk chunk = world.getChunk(x >> 4, z >> 4);

        while (y > 0) {
            y--;

            for (int i = 0; i < player.getEyeHeight(); i++)
                if (!chunk.getBlockState(x, y + i, z).isFullCube() && chunk.getBlockState(x, y, z).getMaterial() != Material.AIR) {
                    return new CachedTeleportationLocation(SuperpositionHandler.getRandomElement(this.worlds, player.world.provider.getDimension()), x + 0.5D, y + 2.5D, z + 0.5D);
                }
        }

        return findBlockPos(world, player, depth);
    }

    private boolean isInsideWorldBorder(World world, double x, double y, double z) {
        WorldBorder border = world.getWorldBorder();
        return border.contains(new BlockPos(x, y, z));
    }

    public void clearLocationCache() {
        this.locationCache.clear();
    }

    @Override
    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(UUID.fromString("d171890c-ba68-42e3-ba2e-ac275e8de595"), EnigmaticLegacy.MODID + ":attack_speed_modifier", 0.4F, 2));
        attributes.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(UUID.fromString("7652a7d5-1e7c-4c8e-8bd2-b0dd38411581"), EnigmaticLegacy.MODID + ":swim_bonus", 1.0F, 2));
        attributes.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(UUID.fromString("290d5f76-87aa-4f7c-9c1a-9aef2fe25d05"), EnigmaticLegacy.MODID + ":luck_bonus", 1, 0));
    }

    public static class CachedTeleportationLocation implements ITeleporter {

        final int dimension;
        final double x;
        final double y;
        final double z;

        public CachedTeleportationLocation(int dimension, double x, double y, double z) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public CachedTeleportationLocation(Entity entity) {
            this(entity.dimension, entity.posX, entity.posY, entity.posZ);
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw) {
            entity.motionX = entity.motionY = entity.motionZ = 0D;
            entity.fallDistance = 0F;

            if (entity instanceof EntityPlayerMP && ((EntityPlayerMP) entity).connection != null) {
                ((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, yaw, entity.rotationPitch);
            } else {
                entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
            }
        }
    }

    @Override
    public int bonusLevelFortune() {
        return 1;
    }
}