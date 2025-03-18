package keletu.enigmaticlegacy.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.client.gui.GuiPlayerExpanded;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.EnigmaticConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.*;
import keletu.enigmaticlegacy.api.DimensionalPosition;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.api.quack.IProperShieldUser;
import keletu.enigmaticlegacy.client.ModelCharm;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.*;
import keletu.enigmaticlegacy.event.special.EndPortalActivatedEvent;
import keletu.enigmaticlegacy.event.special.EnterBlockEvent;
import keletu.enigmaticlegacy.event.special.SummonedEntityEvent;
import keletu.enigmaticlegacy.item.*;
import keletu.enigmaticlegacy.packet.PacketCustom;
import keletu.enigmaticlegacy.packet.PacketPortalParticles;
import keletu.enigmaticlegacy.packet.PacketRecallParticles;
import keletu.enigmaticlegacy.packet.PacketSyncPlayTime;
import keletu.enigmaticlegacy.util.Quote;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import keletu.enigmaticlegacy.util.helper.ItemNBTHelper;
import keletu.enigmaticlegacy.util.helper.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.List;

@Mod.EventBusSubscriber(modid = MODID)
public class EnigmaticEvents {

    private static final String SPAWN_WITH_BOOK = EnigmaticLegacy.MODID + ".acknowledgment";
    private static final String SPAWN_WITH_AMULET = EnigmaticLegacy.MODID + ".enigmatic_amulet";
    private static final String SPAWN_WITH_CURSE = EnigmaticLegacy.MODID + ".cursedring";
    public static final Map<EntityPlayer, AxisAlignedBB> DESOLATION_BOXES = new WeakHashMap<>();
    private static EntityPlayer abyssalHeartOwner;
    public static final Random THEY_SEE_ME_ROLLIN = new Random();
    public static final Map<EntityLivingBase, Float> knockbackThatBastard = new WeakHashMap<>();
    public static boolean dropCursedStone = false;

    public static final ResourceLocation FIREBAR_LOCATION = new ResourceLocation(EnigmaticLegacy.MODID, "textures/gui/firebar.png");
    public static final ResourceLocation ICONS_LOCATION = new ResourceLocation(EnigmaticLegacy.MODID, "textures/gui/generic_icons.png");
    private static final ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/bar.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGui(GuiScreenEvent.BackgroundDrawnEvent evt) {
        if (evt.getGui() instanceof GuiPlayerExpanded) {
            evt.getGui().mc.getTextureManager().bindTexture(texture);
            ScaledResolution sr = new ScaledResolution(evt.getGui().mc);
            int startX = (sr.getScaledWidth() - 176) / 2;
            int startY = (sr.getScaledHeight() - 166) / 2;
            evt.getGui().drawTexturedModalRect(startX, startY - 6 - 18, 0, 0, 6, 6);
            evt.getGui().drawTexturedModalRect(startX + 18 * 6 + 6, startY - 6 - 18, 58, 0, 6, 6);
            for (int i = 0; i < 6; i++) {
                evt.getGui().drawTexturedModalRect(6 + startX + i * 18, startY - 6 - 18, 4, 0, 18, 6);
                evt.getGui().drawTexturedModalRect(6 + startX + i * 18, startY - 18, 6, 6, 18, 18);
            }
            evt.getGui().drawTexturedModalRect(startX, startY - 18, 0, 6, 6, 18);
            evt.getGui().drawTexturedModalRect(startX + 18 * 6 + 6, startY - 18, 58, 6, 6, 30);

            if (Minecraft.getMinecraft().player.getTags().contains("hasIchorBottle"))
                evt.getGui().drawTexturedModalRect(startX + 5, startY - 18 - 1, 0, 46, 18, 18);
            if (Minecraft.getMinecraft().player.getTags().contains("hasAstralFruit"))
                evt.getGui().drawTexturedModalRect(startX + 5 + 18 * 1, startY - 18 - 1, 18, 46, 18, 18);
            if (BaublesApi.isBaubleEquipped(Minecraft.getMinecraft().player, EnigmaticLegacy.enigmaticEye) != -1)
                evt.getGui().drawTexturedModalRect(startX + 5 + 18 * 2, startY - 18 - 1, 36, 46, 18, 18);
            evt.getGui().drawTexturedModalRect(startX + 5 + 18 * 3, startY - 18 - 1, 54, 46, 18, 18);
            evt.getGui().drawTexturedModalRect(startX + 5 + 18 * 4, startY - 18 - 1, 72, 46, 18, 18);
            if (false)
                evt.getGui().drawTexturedModalRect(startX + 5 + 18 * 5, startY - 18 - 1, 90, 46, 18, 18);
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        EntityPlayer newPlayer = event.getEntityPlayer();
        EntityPlayer oldPlayer = event.getOriginal();

        EnigmaticLegacy.soulCrystal.updatePlayerSoulMap(newPlayer);

        if (event.isWasDeath() && newPlayer instanceof EntityPlayerMP && oldPlayer instanceof EntityPlayerMP && getPersistentBoolean(oldPlayer, "dropEldritchAmulet", true)) {
            SuperpositionHandler.setPersistentBoolean(newPlayer, "dropEldritchAmulet", false);
            eldritchAmulet.reclaimInventory((EntityPlayerMP) oldPlayer, (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            boolean droppedCrystal = false;

            DimensionalPosition dimPoint = /*hadEscapeScroll ? SuperpositionHandler.getRespawnPoint(player) : */new DimensionalPosition(player.posX, player.posY, player.posZ, player.world);

            /*if (hadEscapeScroll) {
                player.level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
                EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128, player.level.dimension())), new PacketPortalParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 100, 1.25F, false));

                for (ItemEntity dropIt : event.getDrops()) {
                    ItemEntity alternativeDrop = new ItemEntity(dimPoint.world, dimPoint.posX, dimPoint.posY, dimPoint.posZ, dropIt.getItem());
                    alternativeDrop.teleportTo(dimPoint.posX, dimPoint.posY, dimPoint.posZ);
                    alternativeDrop.setDeltaMovement(theySeeMeRollin.nextDouble()-0.5, theySeeMeRollin.nextDouble()-0.5, theySeeMeRollin.nextDouble()-0.5);
                    dimPoint.world.addFreshEntity(alternativeDrop);
                    dropIt.setItem(ItemStack.EMPTY);
                }

                event.getDrops().clear();

                dimPoint.world.playSound(null, new BlockPos(dimPoint.posX, dimPoint.posY, dimPoint.posZ), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
                EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(dimPoint.posX, dimPoint.posY, dimPoint.posZ, 128, dimPoint.world.dimension())), new PacketRecallParticles(dimPoint.posX, dimPoint.posY, dimPoint.posZ, 48, false));
            }*/
            boolean hasEldritchAmulet = false;
            boolean found = false;

            for (EntityItem entityItem : event.getDrops()) {
                ItemStack itemStack = entityItem.getItem();
                if (itemStack.getItem() == eldritchAmulet) {
                    hasEldritchAmulet = true;
                    break;
                } else if (itemStack.getItem() == enigmaticAmulet || itemStack.getItem() == ascensionAmulet) {
                    found = true;
                    break;
                }
            }
            double y = dimPoint.getPosY() + 1.5;
            if (y < 0)
                y = 3;

            if (hasEldritchAmulet && !event.getDrops().isEmpty() && SuperpositionHandler.isTheWorthyOne(player) && !dropCursedStone) {
                ItemStack soulCrystal = EnigmaticLegacy.soulCrystal.createCrystalFrom(player);
                EntityItemSoulCrystal droppedSoulCrystal = new EntityItemSoulCrystal(dimPoint.world, dimPoint.getPosX(), y, dimPoint.getPosZ(), soulCrystal);
                droppedSoulCrystal.setOwnerId(player.getUniqueID());
                dimPoint.world.spawnEntity(droppedSoulCrystal);
                EnigmaticLegacy.logger.info("Teared Soul Crystal from " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
            } else if (found && !event.getDrops().isEmpty() && EnigmaticConfigs.vesselEnabled) {
                ItemStack soulCrystal = SuperpositionHandler.shouldPlayerDropSoulCrystal(player) ? EnigmaticLegacy.soulCrystal.createCrystalFrom(player) : null;
                ItemStack storageCrystal = EnigmaticLegacy.storageCrystal.storeDropsOnCrystal(event.getDrops(), player, soulCrystal);
                EntityItemSoulCrystal droppedStorageCrystal = new EntityItemSoulCrystal(dimPoint.world, dimPoint.getPosX(), y, dimPoint.getPosZ(), storageCrystal);
                droppedStorageCrystal.setOwnerId(player.getUniqueID());
                dimPoint.world.spawnEntity(droppedStorageCrystal);
                EnigmaticLegacy.logger.info("Summoned Extradimensional Storage Crystal for " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
                event.getDrops().clear();

                if (soulCrystal != null) {
                    droppedCrystal = true;
                }
            } else if (SuperpositionHandler.shouldPlayerDropSoulCrystal(player)) {
                ItemStack soulCrystal = EnigmaticLegacy.soulCrystal.createCrystalFrom(player);
                EntityItemSoulCrystal droppedSoulCrystal = new EntityItemSoulCrystal(dimPoint.world, dimPoint.getPosX(), y, dimPoint.getPosZ(), soulCrystal);
                droppedSoulCrystal.setOwnerId(player.getUniqueID());
                dimPoint.world.spawnEntity(droppedSoulCrystal);
                EnigmaticLegacy.logger.info("Teared Soul Crystal from " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());

                droppedCrystal = true;
            }

            return;
        }

        if (event.isRecentlyHit() && event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityLivingBase killed = event.getEntityLiving();
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            if (enableWitherite && killed.getClass() == EntityWither.class && player.getActivePotionEffect(MobEffects.WEAKNESS) != null) {
                addDrop(event, getRandomSizeStack(EnigmaticLegacy.ingotWitherite, 3, 8));
            }

            if (hasCursed((EntityPlayer) event.getSource().getTrueSource())) {


                if (!EnigmaticConfigs.enableSpecialDrops)
                    return;

                if (killed instanceof EntityDragon) {
                    if (SuperpositionHandler.isTheWorthyOne(player)) {
                        int heartsGained = SuperpositionHandler.getPersistentInteger(player, "AbyssalHeartsGained", 0);

                        if (heartsGained < 5) { // Only as many as there are unique items from them, +1
                            abyssalHeartOwner = player;
                        }
                    }
                }

                if (killed.getClass() == EntityShulker.class) {
                    addDropWithChance(event, new ItemStack(EnigmaticLegacy.astralDust, 1), 20);
                } else if (killed.getClass() == EntitySkeleton.class || killed.getClass() == EntityStray.class) {
                    addDrop(event, getRandomSizeStack(Items.ARROW, 3, 15));
                } else if (killed.getClass() == EntityZombie.class || killed.getClass() == EntityHusk.class) {
                    addDropWithChance(event, getRandomSizeStack(Items.SLIME_BALL, 1, 3), 25);
                } else if (killed.getClass() == EntitySpider.class || killed.getClass() == EntityCaveSpider.class) {
                    addDrop(event, getRandomSizeStack(Items.STRING, 2, 12));
                } else if (killed.getClass() == EntityGuardian.class) {
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_CRYSTALS, 2, 5));
                } else if (killed.getClass() == EntityElderGuardian.class) {
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_CRYSTALS, 4, 16));
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_SHARD, 7, 28));
                    addOneOf(event,
                            //new ItemStack(guardianHeart, 1),
                            new ItemStack(Items.GOLDEN_APPLE, 1, 1),
                            new ItemStack(Items.ENDER_EYE, 1));
                } else if (killed.getClass() == EntityEnderman.class) {
                    addDropWithChance(event, getRandomSizeStack(Items.ENDER_EYE, 1, 2), 40);
                } else if (killed.getClass() == EntityBlaze.class) {
                    addDrop(event, getRandomSizeStack(Items.BLAZE_POWDER, 0, 5));
                    //addDropWithChance(event, new ItemStack(EnigmaticLegacy.livingFlame, 1), 15);
                } else if (killed.getClass() == EntityPigZombie.class) {
                    addDropWithChance(event, getRandomSizeStack(Items.GOLD_INGOT, 1, 3), 40);
                    addDropWithChance(event, getRandomSizeStack(Items.GLOWSTONE_DUST, 1, 7), 30);
                } else if (killed.getClass() == EntityWitch.class) {
                    addDropWithChance(event, new ItemStack(Items.GHAST_TEAR, 1), 30);
                    //addDrop(event, getRandomSizeStack(Items.PHANTOM_MEMBRANE, 1, 3));
                    addDropWithChance(event, new ItemStack(Items.GHAST_TEAR, 1), 30);
                    //addDropWithChance(event, getRandomSizeStack(Items.PHANTOM_MEMBRANE, 1, 3), 50);
                } else if ((Loader.isModLoaded("raids") && killed.getClass().toString().equals("net.smileycorp.raids.common.entities.EntityPillager.class")) || killed.getClass() == EntityVindicator.class) {
                    addDrop(event, getRandomSizeStack(Items.EMERALD, 0, 4));
                } else if (killed.getClass() == EntityVillager.class) {
                    addDrop(event, getRandomSizeStack(Items.EMERALD, 2, 6));
                } else if (killed.getClass() == EntityCreeper.class) {
                    addDrop(event, getRandomSizeStack(Items.GUNPOWDER, 4, 12));
                } else if (killed.getClass() == EntityEvoker.class) {
                    addDrop(event, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
                    addDrop(event, getRandomSizeStack(Items.EMERALD, 5, 20));
                    addDropWithChance(event, new ItemStack(Items.GOLDEN_APPLE, 1, 1), 10);
                    addDropWithChance(event, getRandomSizeStack(Items.ENDER_PEARL, 1, 3), 30);
                    addDropWithChance(event, getRandomSizeStack(Items.BLAZE_ROD, 2, 4), 30);
                    addDropWithChance(event, getRandomSizeStack(Items.EXPERIENCE_BOTTLE, 4, 10), 50);
                } else if (killed.getClass() == EntityWitherSkeleton.class) {
                    addDrop(event, getRandomSizeStack(Items.BLAZE_POWDER, 0, 3));
                    addDropWithChance(event, new ItemStack(Items.GHAST_TEAR, 1), 20);
                } else if (Loader.isModLoaded("oe") && killed.toString().equals("com.sirsquidly.oe.entity.EntityDrowned.class")) {
                    addDropWithChance(event, getRandomSizeStack(Items.DYE, 1, 3, 4), 30);
                    //} else if (killed.getClass() == EntityGhast.class) {
                    //    addDrop(event, getRandomSizeStack(Items.PHANTOM_MEMBRANE, 1, 4));
                } else if (killed.getClass() == EntityVex.class) {
                    addDrop(event, getRandomSizeStack(Items.GLOWSTONE_DUST, 0, 2));
                    //   addDropWithChance(event, new ItemStack(Items.PHANTOM_MEMBRANE, 1), 30);
                    //}  else if (killed.getClass() == PiglinEntity.class) {
                    //    addDropWithChance(event, getRandomSizeStack(Items.GOLD_INGOT, 2, 4), 50);
                } else if (Loader.isModLoaded("raids") && killed.getClass().toString().equals("net.smileycorp.raids.common.entities.EntityRavager.class")) {
                    addDrop(event, getRandomSizeStack(Items.EMERALD, 3, 10));
                    addDrop(event, getRandomSizeStack(Items.LEATHER, 2, 7));
                    addDropWithChance(event, getRandomSizeStack(Items.DIAMOND, 0, 4), 50);
                } else if (killed.getClass() == EntityMagmaCube.class) {
                    addDrop(event, getRandomSizeStack(Items.BLAZE_POWDER, 0, 1));
                } else if (killed.getClass() == EntityChicken.class) {
                    addDropWithChance(event, new ItemStack(Items.EGG, 1), 50);
                } else if (killed.getClass() == EntityWither.class) {
                    addDrop(event, getRandomSizeStack(EnigmaticLegacy.evilEssence, 1, 4));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCursedDrops(PlayerDropsEvent event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            boolean hasCursedStone = false;

            for (EntityItem entityItem : event.getDrops()) {
                ItemStack itemStack = entityItem.getItem();
                if (itemStack.getItem() == cursedStone) {
                    hasCursedStone = true;
                    itemStack.shrink(1);
                }
            }

            if (hasCursedStone && player.world.provider instanceof WorldProviderHell) {
                BlockPos deathPos = player.getPosition();

                if (isThereLava(player.world, deathPos)) {
                    BlockPos surfacePos = deathPos;

                    while (true) {
                        BlockPos nextAbove = surfacePos.add(0, 1, 0);
                        if (isThereLava(player.world, nextAbove)) {
                            surfacePos = nextAbove;
                            continue;
                        } else {
                            break;
                        }
                    }

                    boolean confirmLavaPool = true;

                    for (int i = -3; i <= 2; ++i) {

                        boolean checkArea = false;
                        for (BlockPos blockPos : BlockPos.getAllInBox(surfacePos.add(-3, i, -3), surfacePos.add(3, i, 3))) {
                            if (i <= 0)
                                checkArea = isThereLava(player.world, blockPos);
                            else
                                checkArea = player.world.isAirBlock(blockPos) || isThereLava(player.world, blockPos);
                        }

                        confirmLavaPool = confirmLavaPool && checkArea;
                    }

                    if (confirmLavaPool) {
                        dropCursedStone = true;
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.PLAYERS, 1.0F, 0.5F);
                    }
                }
            }
        }
    }

    private static boolean isThereLava(World world, BlockPos pos) {
        IBlockState fluidState = world.getBlockState(pos);
        if (fluidState.getMaterial() == Material.LAVA && !(fluidState.getBlock() instanceof BlockDynamicLiquid))
            return true;
        else
            return false;
    }

    public static float getMissingHealthPool(EntityPlayer player) {
        return (player.getMaxHealth() - Math.min(player.getHealth(), player.getMaxHealth())) / player.getMaxHealth();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        ModelCharm.textureAtlasEye = event.getMap().registerSprite(new ResourceLocation(EnigmaticLegacy.MODID, "items/enigmatic_eye"));
    }

    @SubscribeEvent(priority = HIGH)
    public static void hurtEvent(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            ItemStack advancedCurioStack = SuperpositionHandler.getAdvancedBaubles(player);
            if (advancedCurioStack != ItemStack.EMPTY && advancedCurioStack.getItem() instanceof ItemSpellstoneBauble) {
                ItemSpellstoneBauble advancedCurio = (ItemSpellstoneBauble) advancedCurioStack.getItem();

                if (advancedCurio.immunityList.contains(event.getSource().damageType)) {
                    event.setCanceled(true);
                }

                if (event.getSource() == DamageSource.LAVA) {
                    handleLavaProtection(event);
                }

                /*
                 * This used to heal player from debuff damage if they have Pearl of the Void.
                 * Removed in 2.5.0 Release.
                 */

					/*
					if (advancedCurio == EnigmaticLegacy.voidPearl && EnigmaticLegacy.voidPearl.healList.contains(event.getSource().damageType)) {
						player.heal(event.getAmount());
						event.setCanceled(true);
					}
					 */
            }
        }

        if (!event.isCanceled() && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            genericEnforce(event, player, player.getHeldItemMainhand());


            if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == extraDimensionalEye)
                if (ItemNBTHelper.verifyExistance(player.getHeldItemMainhand(), "BoundDimension"))
                    if (ItemNBTHelper.getInt(player.getHeldItemMainhand(), "BoundDimension", 0) == event.getEntity().world.provider.getDimension()) {
                        event.setCanceled(true);
                        ItemStack stack = player.getHeldItemMainhand();

                        EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(event.getEntity().posX, event.getEntity().posY + (event.getEntity().getEyeHeight() / 2), event.getEntity().posZ, 96, 1.5D, false), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 128));

                        event.getEntity().world.playSound(null, event.getEntity().getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
                        event.getEntity().setPosition(ItemNBTHelper.getDouble(stack, "BoundX", 0D), ItemNBTHelper.getDouble(stack, "BoundY", 0D), ItemNBTHelper.getDouble(stack, "BoundZ", 0D));
                        event.getEntity().world.playSound(null, event.getEntity().getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));

                        EnigmaticLegacy.packetInstance.sendToAllAround(new PacketRecallParticles(event.getEntity().posX, event.getEntity().posY + (event.getEntity().getEyeHeight() / 2), event.getEntity().posZ, 48, false), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 128));

                        if (!player.capabilities.isCreativeMode)
                            stack.shrink(1);
                    }

            float knockbackPower = 1F;

            if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == EnigmaticLegacy.theTwist && hasCursed(player)) {
                knockbackPower += knockbackBonus;
            } else if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == EnigmaticLegacy.theTwist && isTheWorthyOne(player)) {
                knockbackPower += infinitumKnockbackBonus;
            }

            knockbackThatBastard.put(event.getEntityLiving(), knockbackPower);
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void leftClick(PlayerInteractEvent.LeftClickBlock event) {
        enforce(event);
        if (event.isCanceled()) {
            return;
        }
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        Block block = state.getBlock();
        ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
        if (stack.isEmpty()) {
            stack = block.getItem(event.getWorld(), event.getPos(), state);
        }
        if (block.hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
                stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
        }
        genericEnforce(event, event.getEntityPlayer(), stack);
    }


    @SubscribeEvent(priority = HIGH)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        enforce(event);
        if (event.isCanceled()) {
            return;
        } else if (event.getItemStack().isEmpty()) {
            //Don't let the block get activated just because this hand is empty
            EntityPlayer player = event.getEntityPlayer();
            genericEnforce(event, player, event.getHand().equals(EnumHand.MAIN_HAND) ? player.getHeldItemOffhand() : player.getHeldItemMainhand());
            if (event.isCanceled()) {
                return;
            }
        }
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        Block block = state.getBlock();
        ItemStack stack = new ItemStack(block, 1, state.getBlock().getMetaFromState(state));
        if (stack.isEmpty()) {
            stack = block.getItem(event.getWorld(), event.getPos(), state);
        }
        if (block.hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
                stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
        }
        genericEnforce(event, event.getEntityPlayer(), stack);
    }

    @SubscribeEvent(priority = HIGH)
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        enforce(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityDamaged(LivingDamageEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityPlayer && !event.getSource().getTrueSource().world.isRemote) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            float lifesteal = 0;
            float hungersteal = 0;
/*
            if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.BLACK)) {
                lifesteal += event.getAmount() * 0.1F;
            }
*/
            if (BaublesApi.isBaubleEquipped(player, eldritchAmulet) != -1) {
                if (SuperpositionHandler.isTheWorthyOne(player)) {
                    lifesteal += event.getAmount() * 0.15F;
                }
            }

            if (SuperpositionHandler.isTheWorthyOne(player)) {
                if (player.getHeldItemMainhand().getItem() == theInfinitum) {

                    lifesteal += event.getAmount() * 0.1F;

                }

                if (player.getHeldItemMainhand().getItem() == eldritchPan) {
                    hungersteal += panHungerSteal;
                    lifesteal += panLifeSteal;
                }
            }

            if (SuperpositionHandler.isWearEnigmaticAmulet(player, 6)) {
                lifesteal += event.getAmount() * 0.1F;
            }

            if (player.isPotionActive(growingBloodlust)) {
                int amplifier = 1 + player.getActivePotionEffect(growingBloodlust).getAmplifier();
                lifesteal += event.getAmount() * (infinitumLifestealBonus * amplifier);
            }

            if (lifesteal > 0) {
                player.heal(lifesteal);
            }

            if (hungersteal > 0) {
                //boolean noHunger = SuperpositionHandler.cannotHunger(player);

                if (event.getEntity() instanceof EntityPlayerMP) {
                    EntityPlayerMP victim = (EntityPlayerMP) event.getEntity();
                    FoodStats victimFood = victim.getFoodStats();
                    FoodStats attackerFood = player.getFoodStats();

                    int foodSteal = Math.min((int) Math.ceil(hungersteal), victimFood.getFoodLevel());
                    float saturationSteal = Math.min(hungersteal / 5F, victimFood.getSaturationLevel());

                    victimFood.foodSaturationLevel = (victimFood.getSaturationLevel() - saturationSteal);
                    victimFood.setFoodLevel(victimFood.getFoodLevel() - foodSteal);

                    //if (noHunger) {
                    //    player.heal((float) foodSteal / 2);
                    //} else {
                    attackerFood.addStats(foodSteal, saturationSteal);
                    //}
                } else {
                    //if (noHunger) {
                    //    player.heal(hungersteal / 2);
                    //} else {
                    player.getFoodStats().addStats((int) Math.ceil(hungersteal), hungersteal / 5F);
                    //}
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTravel(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            //LAST_SOUL_COMPASS_UPDATE.remove(player);

            if (event.player.world.provider instanceof WorldProviderHell) {
                Quote.SULFUR_AIR.playOnceIfUnlocked(player, 240);
            } else if (event.player.world.provider instanceof WorldProviderEnd) {
                Quote.TORTURED_ROCKS.playOnceIfUnlocked(player, 240);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onConfirmedDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();

            if (event.isCanceled()) {
                return;
            }

            SuperpositionHandler.setPersistentBoolean(player, "DeathFromEntity", event.getSource().getTrueSource() instanceof EntityLivingBase);
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP attacker = (EntityPlayerMP) event.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();

            if (event.getEntity() instanceof EntityWither) {
                int killedWither = SuperpositionHandler.getPersistentInteger(attacker, "TimesKilledWither", 0);

                if (killedWither <= 0) {
                    Quote.BREATHES_RELIEVED.play(attacker, 140);
                    killedWither++;
                } else if (killedWither == 1) {
                    Quote.APPALING_PRESENCE.play(attacker, 140);
                    killedWither++;
                } else if (killedWither == 2) {
                    Quote.TERRIFYING_FORM.play(attacker, 140);
                    killedWither++;
                } else if (killedWither > 2 && killedWither < 5) {
                    killedWither++;
                } else if (killedWither == 4) {
                    Quote.WHETHER_IT_IS.play(attacker, 140);
                    killedWither++;
                }

                SuperpositionHandler.setPersistentInteger(attacker, "TimesKilledWither", killedWither);
            }

            if (weapon.getItem().equals(eldritchPan)) {
                ResourceLocation killedType = EntityList.getKey(event.getEntity());

                if (ItemEldritchPan.addKillIfNotPresent(weapon, killedType)) {
                    attacker.sendStatusMessage(new TextComponentTranslation("message.enigmaticlegacy.eldritch_pan_buff"), false);
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDropsLowest(LivingDropsEvent event) {
        /*
         * Eldritch Pan cooking logic.
         */

        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {

            EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
            if (SuperpositionHandler.isTheWorthyOne(player))
                if (player.getHeldItemMainhand().getItem() == eldritchPan
                        || player.getHeldItemOffhand().getItem() == eldritchPan)
                    for (EntityItem drop : new ArrayList<>(event.getDrops())) {
                        ItemStack stack = drop.getItem();
                        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(stack);

                        if (!smelted.isEmpty()) {
                            ItemStack cooked = smelted.copy();
                            cooked.setCount(smelted.getCount());

                            event.getDrops().remove(drop);
                            addDrop(event, smelted);
                        }

                    }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() != null) {
            RayTraceResult result = event.getRayTraceResult();

            if (result.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) result.entityHit;
                Entity arrow = event.getEntity();

                if (!player.world.isRemote) {
                    if (arrow instanceof EntityArrow) {
                        EntityArrow projectile = (EntityArrow) arrow;

                        if (projectile.shootingEntity == player) {
                            for (String tag : arrow.getTags()) {
                                if (tag.startsWith("AB_DEFLECTED")) {
                                    try {
                                        int time = Integer.parseInt(tag.split(":")[1]);
                                        if (arrow.ticksExisted - time < 10)
                                            // If we cancel the event here it gets stuck in the infinite loop
                                            return;
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    boolean trigger = false;
                    double chance = 0.0D;

                    if (BaublesApi.isBaubleEquipped(player, angelBlessing) != -1) {
                        trigger = true;
                        chance += angelBlessingDeflectChance;
                    }

                    if (SuperpositionHandler.isWearEnigmaticAmulet(player, 3)) {
                        trigger = true;
                        chance += 0.15;
                    }

                    if (trigger && Math.random() <= chance) {
                        event.setCanceled(true);

                        arrow.motionX -= 1.0D;
                        arrow.motionY -= 1.0D;
                        arrow.motionZ -= 1.0D;
                        arrow.prevPosY = arrow.posY + 180.0F;
                        arrow.posY = arrow.posY + 180.0F;

                        arrow.getTags().removeIf(tag -> tag.startsWith("AB_DEFLECTED"));
                        arrow.addTag("AB_DEFLECTED:" + arrow.ticksExisted);

                        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_GUITAR, SoundCategory.PLAYERS, 1.0F, 0.95F + (float) (Math.random() * 0.1D));
                    }
                } /*else {
					if (event.getEntity().getTags().contains("enigmaticlegacy.redirected")) {
						event.setCanceled(true);
						event.getEntity().removeTag("enigmaticlegacy.redirected");
					}
				}*/
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miningStuff(PlayerEvent.BreakSpeed event) {
        /*
         * Handler for calculating mining speed boost from wearing Charm of Treasure Hunter.
         */

        float originalSpeed = event.getOriginalSpeed();
        float correctedSpeed = originalSpeed;

        float miningBoost = 1.0F;
        if (BaublesApi.isBaubleEquipped(event.getEntityPlayer(), EnigmaticLegacy.miningCharm) != -1) {
            miningBoost += EnigmaticConfigs.breakSpeedBonus;
        }

        if (BaublesApi.isBaubleEquipped(event.getEntityPlayer(), EnigmaticLegacy.cursedScroll) != -1) {
            miningBoost += cursedScrollMiningBoost * getCurseAmount(event.getEntityPlayer());
        }

        if (SuperpositionHandler.isWearEnigmaticAmulet(event.getEntityPlayer(), 5)) {
            miningBoost += 0.25F;
        }

        correctedSpeed = correctedSpeed * miningBoost;
        correctedSpeed -= event.getOriginalSpeed();

        event.setNewSpeed(event.getNewSpeed() + correctedSpeed);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            if (event.getAmount() <= 1.0F)
                if (IForbiddenConsumed.get(player).isConsumed()) {
                    event.setAmount(event.getAmount() * EnigmaticConfigs.regenerationSubtraction);
                }

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedScroll) != -1) {
                event.setAmount(event.getAmount() + (event.getAmount() * (cursedScrollRegenBoost * getCurseAmount(player))));
            }
        }

    }

    @SubscribeEvent(priority = HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
                stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
        }
        genericEnforce(event, event.getPlayer(), stack);
    }

    @SubscribeEvent
    public static void onArmorEquip(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if ((!player.isCreative()) && !(player instanceof FakePlayer)) {
                EntityEquipmentSlot slot = event.getSlot();
                if (slot.getSlotType().equals(EntityEquipmentSlot.Type.ARMOR)) {
                    ItemStack stack = player.inventory.armorInventory.get(slot.getIndex());
                    if ((isCursed(stack) && !hasCursed(player)) || (!isTheWorthyOne(player) && isEldritch(stack))) {
                        if (!player.inventory.addItemStackToInventory(stack)) {
                            player.dropItem(stack, false);
                        }
                        player.inventory.armorInventory.set(slot.getIndex(), ItemStack.EMPTY);
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0f, 0.5F);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        enforce(event);
    }

    @SubscribeEvent
    public static void onEntitySpawn(LivingSpawnEvent event) {
        if (event.getEntityLiving().isCreatureType(EnumCreatureType.MONSTER, false)) {
            EntityLivingBase entity = event.getEntityLiving();

            if (desolationRingExtraMonstersList.contains(EntityList.getKey(entity)) || /*entity instanceof Piglin || */entity instanceof EntityPigZombie || entity instanceof EntityIronGolem || entity instanceof EntityEnderman) {
                if (DESOLATION_BOXES.values().stream().anyMatch(entity.getEntityBoundingBox()::intersects)) {
                    event.setResult(Event.Result.DENY);
                    //event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        for (ResourceLocation rl : EnigmaticConfigs.cursedItemList) {
            if (event.getItemStack().getItem() == ForgeRegistries.ITEMS.getValue(rl)) {
                TextFormatting color = !hasCursed(event.getEntityPlayer()) ? TextFormatting.DARK_RED : TextFormatting.GRAY;
                event.getToolTip().add(1, color + I18n.format("tooltip.enigmaticlegacy.cursedOnesOnly1"));
                event.getToolTip().add(2, color + I18n.format("tooltip.enigmaticlegacy.cursedOnesOnly2"));
            }
        }
    }

    @SubscribeEvent
    public static void onEndPortal(EndPortalActivatedEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            Quote.END_DOORSTEP.playOnceIfUnlocked(player, 40);
        }
    }

    @SubscribeEvent
    public static void onEntitySummon(SummonedEntityEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            if (event.getSummonedEntity() instanceof EntityWither) {
                Quote.COUNTLESS_DEAD.playOnceIfUnlocked(player, 20);
            } else if (event.getSummonedEntity() instanceof EntityDragon) {
                Quote.HORRIBLE_EXISTENCE.playOnceIfUnlocked(player, 100);
            }
        }
    }

    @SubscribeEvent
    public static void onEnteredBlock(EnterBlockEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP && event.getBlockState().getBlock() == Blocks.END_GATEWAY) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            Quote.I_WANDERED.playOnceIfUnlocked(player, 160);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;

        /*
         * Handler for re-enabling disabled Curio slots that are supposed to be
         * permanently unlocked, when the player respawns.
         */

        if (!player.world.isRemote) {
            //LAST_SOUL_COMPASS_UPDATE.remove(player);

            if (!event.isEndConquered()) {
                if (SuperpositionHandler.getPersistentBoolean(player, "DestroyedCursedRing", false)) {
                    SuperpositionHandler.removePersistentTag(player, "DestroyedCursedRing");
                    Quote.getRandom(Quote.RING_DESTRUCTION).playIfUnlocked((EntityPlayerMP) player, 10);
                } else if (THEY_SEE_ME_ROLLIN.nextDouble() > 0.2)
                    if (SuperpositionHandler.getPersistentBoolean(player, "DeathFromEntity", false)) {
                        Quote.getRandom(Quote.DEATH_QUOTES_ENTITY).playIfUnlocked((EntityPlayerMP) player, 10);
                    } else {
                        Quote.getRandom(Quote.DEATH_QUOTES).playIfUnlocked((EntityPlayerMP) player, 10);
                    }
                //if (!player.world.getGameRules().getBoolean("keepInventory")) {
                //    if (SuperpositionHandler.hasPersistentTag(player, EnigmaticEventHandler.NBT_KEY_ENABLESCROLL)) {
                //        SuperpositionHandler.unlockSpecialSlot("scroll", event.getEntity());
                //    }
                //    if (SuperpositionHandler.hasPersistentTag(player, EnigmaticEventHandler.NBT_KEY_ENABLESPELLSTONE)) {
                //        SuperpositionHandler.unlockSpecialSlot("spellstone", event.getEntity());
                //    }
                //}
            }

            // SuperpositionHandler.setCurrentWorldCursed(SuperpositionHandler.isTheCursedOne(player));
        }

    }

    @SubscribeEvent
    public static void tickHandler(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote)
            return;

        EntityPlayer player = event.player;

        IPlaytimeCounter counter = IPlaytimeCounter.get(player);

        if (SuperpositionHandler.hasCursed(player)) {
            counter.incrementTimeWithCurses();
        } else {
            counter.incrementTimeWithoutCurses();
        }

        if (BaublesApi.isBaubleEquipped(player, desolationRing) != -1 && SuperpositionHandler.isTheWorthyOne(player)) {
            DESOLATION_BOXES.put(player, SuperpositionHandler.getBoundingBoxAroundEntity(player, 128));
        } else {
            DESOLATION_BOXES.remove(player);
        }

        if (player.ticksExisted % 100 == 0) {
            syncPlayTime(player);
        }

        IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baublesHandler.getSlots(); i++) {
            ItemStack stack = baublesHandler.getStackInSlot(i);
            if ((isCursed(stack) && !hasCursed(player)) || (!isTheWorthyOne(player) && isEldritch(stack))) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
                baublesHandler.setStackInSlot(i, ItemStack.EMPTY);
                player.world.playSound(null, event.player.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0f, 0.5F);
            }
        }

        IBaublesItemHandler extraBaublesHandler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < extraBaublesHandler.getSlots(); i++) {
            ItemStack stack = extraBaublesHandler.getStackInSlot(i);
            if ((isCursed(stack) && !hasCursed(player)) || (!isTheWorthyOne(player) && isEldritch(stack))) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
                extraBaublesHandler.setStackInSlot(i, ItemStack.EMPTY);
                player.world.playSound(null, event.player.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0f, 0.5F);
            }
        }

        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEldritchPan) {
            int currentTicks = ItemEldritchPan.HOLDING_DURATIONS.getOrDefault(player, 0);
            if (IForbiddenConsumed.get(player).isConsumed()) {
                int bloodlustAmplifier = currentTicks / bloodLustTicksPerLevel;

                bloodlustAmplifier = Math.min(bloodlustAmplifier, 9);

                player.addPotionEffect(new PotionEffect(growingBloodlust, 200, bloodlustAmplifier, true, true));
            } else {
                int hungerAmplifier = currentTicks / 300;

                hungerAmplifier = Math.min(hungerAmplifier, 9);
                player.addPotionEffect(new PotionEffect(EnigmaticLegacy.growingHungerEffect, 200, hungerAmplifier, true, true));
            }

            ItemEldritchPan.HOLDING_DURATIONS.put(player, currentTicks + 1);
        } else {
            ItemEldritchPan.HOLDING_DURATIONS.put(player, 0);
            player.removePotionEffect(EnigmaticLegacy.growingHungerEffect);
            player.removePotionEffect(growingBloodlust);
        }

        if (!BaublesApi.getBaublesHandler(player).getStackInSlot(7).isEmpty() && !player.getTags().contains("hasIchorBottle")) {
            player.dropItem(BaublesApi.getBaublesHandler(player).getStackInSlot(7), false);
            BaublesApi.getBaublesHandler(player).setStackInSlot(7, ItemStack.EMPTY);
        }
        if (!BaublesApi.getBaublesHandler(player).getStackInSlot(8).isEmpty() && !player.getTags().contains("hasAstralFruit")) {
            player.dropItem(BaublesApi.getBaublesHandler(player).getStackInSlot(8), false);
            BaublesApi.getBaublesHandler(player).setStackInSlot(8, ItemStack.EMPTY);
        }
        if (!BaublesApi.getBaublesHandler(player).getStackInSlot(9).isEmpty() && BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enigmaticEye) == -1) {
            player.dropItem(BaublesApi.getBaublesHandler(player).getStackInSlot(9), false);
            BaublesApi.getBaublesHandler(player).setStackInSlot(9, ItemStack.EMPTY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (!player.capabilities.isFlying && !player.onGround) {
                if (BaublesApi.isBaubleEquipped(player, oceanStone) != -1 && player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ)).getMaterial() == Material.WATER) {
                    player.motionY = 0;
                    if (player.isSneaking())
                        player.motionY -= 0.2;
                } else if (SuperpositionHandler.isWearEnigmaticAmulet(player, 4)) {
                    if (player.motionY < 0)
                        player.motionY *= 0.9;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (SuperpositionHandler.isWearEnigmaticAmulet(player, 4)) {
            event.getEntityLiving().motionY *= 1.25F;
        }
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        BlockPos where = event.getPos();
        boolean shouldBoost = false;

        int radius = 16;
        List<EntityPlayer> players = event.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(where.add(-radius, -radius, -radius), where.add(radius, radius, radius)));

        for (EntityPlayer player : players)
            if (hasCursed(player)) {
                shouldBoost = true;
            }

        if (shouldBoost) {
            event.setLevel(event.getLevel() + EnigmaticConfigs.enchantingBonus);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onOverlayRender(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isSpectator()) {

            ItemStack lavaProtector = ItemStack.EMPTY;
            ItemStack lavaCharm = SuperpositionHandler.getAdvancedBaubles(mc.player);

            if (!lavaCharm.isEmpty() && lavaCharm.getItem() instanceof ItemMagmaHeart) {
                lavaProtector = lavaCharm;
            }

            if (!lavaProtector.isEmpty()) {
                NBTTagCompound compound = lavaProtector.getTagCompound();
                if (compound != null) {
                    int charge = compound.getInteger("blazingCoreCharge");

                    if (event instanceof RenderGameOverlayEvent.Post && event.getType() == RenderGameOverlayEvent.ElementType.ALL && (mc.player.isInLava() || charge < 200)) {
                        renderLavaChargeBar(event.getResolution(), charge, 200);
                    }
                }
            }

            if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD && event instanceof RenderGameOverlayEvent.Pre) {

                if (IForbiddenConsumed.get(mc.player).isConsumed()) {

                    event.setCanceled(true);
                    mc.getTextureManager().bindTexture(ICONS_LOCATION);

                    int width = event.getResolution().getScaledWidth();
                    int height = event.getResolution().getScaledHeight();

                    EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
                    GlStateManager.enableBlend();
                    int left = width / 2 + 91;
                    int top = height - GuiIngameForge.right_height;

                    GuiIngameForge.right_height += 10;
                    boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

                    FoodStats stats = mc.player.getFoodStats();
                    int level = stats.getFoodLevel();

                    for (int i = 0; i < 10; ++i) {
                        int idx = i * 2 + 1;
                        int x = left - i * 8 - 9;
                        int y = top;
                        int icon = 16;
                        byte background = 0;
/*
                        if (mc.player.isPotionActive(MobEffects.HUNGER))
                        {
                            icon += 36;
                            background = 13;
                        }
                        if (unused) background = 1; //Probably should be a += 1 but vanilla never uses this
*/
                        if (player.getFoodStats().getSaturationLevel() <= 0.0F && mc.ingameGUI.updateCounter % (level * 3 + 1) == 0) {
                            y = top + (THEY_SEE_ME_ROLLIN.nextInt(3) - 1);
                        }

                        mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 9, 9);

                        /*
                        if (idx < level)
                            mc.ingameGUI.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                        else if (idx == level)
                            mc.ingameGUI.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);

                         */
                    }
                    GlStateManager.disableBlend();

                    mc.getTextureManager().bindTexture(Gui.ICONS);
                }

            }
        }
    }

    /**
     * From Mod Botania
     * Botania is Open Source and distributed under the
     * Botania License: http://botaniamod.net/license.php
     */

    @SideOnly(Side.CLIENT)
    private static void renderLavaChargeBar(ScaledResolution res, int totalMana, int totalMaxMana) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = 182;
        int x = res.getScaledWidth() / 2 - width / 2;
        int y = res.getScaledHeight() - 29;

        if (totalMaxMana == 0)
            width = 0;
        else width *= (double) totalMana / (double) totalMaxMana;

        if (width == 0) {
            if (totalMana > 0)
                width = 1;
            else return;
        }

        mc.renderEngine.bindTexture(FIREBAR_LOCATION);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.drawTexturedModalRect(x, y, 0, 0, 251, width, 5);
        GlStateManager.disableBlend();
    }

    //From RandomThings mod, Thank you author :)
    private static void handleLavaProtection(LivingAttackEvent event) {
        ItemStack lavaProtector = ItemStack.EMPTY;
        ItemStack lavaCharm = SuperpositionHandler.getAdvancedBaubles(event.getEntityLiving());

        if (!lavaCharm.isEmpty() && lavaCharm.getItem() instanceof ItemMagmaHeart) {
            lavaProtector = lavaCharm;
        }

        if (!lavaProtector.isEmpty()) {
            NBTTagCompound compound = lavaProtector.getTagCompound();
            if (compound != null) {
                int charge = compound.getInteger("blazingCoreCharge");
                if (charge > 0) {
                    compound.setInteger("blazingCoreCharge", charge - 1);
                    compound.setInteger("blazingCoreCooldown", 40);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        if (knockbackThatBastard.containsKey(event.getEntityLiving())) {
            float knockbackPower = knockbackThatBastard.get(event.getEntityLiving());
            event.setStrength(event.getStrength() * knockbackPower);
            knockbackThatBastard.remove(event.getEntityLiving());
        }

        if (event.getEntityLiving() instanceof EntityPlayer && hasCursed((EntityPlayer) event.getEntityLiving())) {
            event.setStrength(event.getStrength() * EnigmaticConfigs.knockbackDebuff);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (IForbiddenConsumed.get(player).isConsumed()) {
                FoodStats foodStats = player.getFoodStats();
                foodStats.setFoodLevel(20);
                foodStats.foodSaturationLevel = 0F;

                if (player.isPotionActive(MobEffects.HUNGER)) {
                    player.removePotionEffect(MobEffects.HUNGER);
                }
            }
            if (player.isPlayerSleeping() && player.sleepTimer > 90 && hasCursed(player) && EnigmaticConfigs.enableInsomnia && (!ModCompat.COMPAT_TRINKETS || BaublesApi.isBaubleEquipped(player, ForgeRegistries.ITEMS.getValue(new ResourceLocation("xat", "teddy_bear"))) == -1)) {
                player.sleepTimer = 90;
            }
            if (player.isBurning() && hasCursed(player)) {
                player.setFire(player.fire + 2);
            }
            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.blazingCore) != -1)
                if (!player.getActivePotionEffects().isEmpty()) {
                    Collection<PotionEffect> effects = new ArrayList<>();
                    effects.addAll(player.getActivePotionEffects());

                    for (PotionEffect effect : effects) {
                        if (effect.getPotion().equals(MobEffects.FIRE_RESISTANCE)) {
                            if (player.ticksExisted % 2 == 0 && effect.duration > 0) {
                                effect.duration += 1;
                            }
                        } else {
                            effect.onUpdate(player);
                        }
                    }

                }

            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (SuperpositionHandler.hasPersistentTag(playerMP, "ConsumedIchorBottle")) {
                    packetInstance.sendTo(new PacketCustom("hasIchorBottle"), playerMP);
                    player.addTag("hasIchorBottle");
                }
                if (SuperpositionHandler.hasPersistentTag(playerMP, "ConsumedAstralFruit")) {
                    packetInstance.sendTo(new PacketCustom("hasAstralFruit"), playerMP);
                    player.addTag("hasAstralFruit");
                }
            }
        }
    }

    //@SideOnly(Side.CLIENT)
    //@SubscribeEvent
    //public static void preRender(RenderGameOverlayEvent.Pre event) {
    //    RenderGameOverlayEvent.ElementType type = event.getType();
    //    Minecraft mc = Minecraft.getMinecraft();
//
    //    if (event.getType() == RenderGameOverlayEvent.ElementType.AIR) {
    //        if (BaublesApi.isBaubleEquipped(mc.player, EnigmaticLegacy.oceanStone) != -1/* || SuperpositionHandler.hasCurio(mc.player, EnigmaticLegacy.voidPearl)*/) {
    //            //  if (OceanStone.preventOxygenBarRender.getValue()) {
    //            event.setCanceled(true);
    //            // }
    //        }
    //    }
    //}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onFogRender(EntityViewRenderEvent.FogDensity event) {
        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player, (float) event.getRenderPartialTicks());
        if (iblockstate.getMaterial() == Material.LAVA && BaublesApi.isBaubleEquipped(Minecraft.getMinecraft().player, EnigmaticLegacy.blazingCore) != -1) {
            event.setCanceled(true);
            event.setDensity((float) magmaHeartLavafogDensity);
        }

    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getAmount() >= Float.MAX_VALUE)
            return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.voidPearl) != -1) {
                event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WITHER, witheringTime, witheringLevel, false, true));
            }

            Entity immediateSource = event.getSource().getImmediateSource();

            if (player.getHeldItemMainhand() != ItemStack.EMPTY) {
                ItemStack mainhandStack = player.getHeldItemMainhand();

                if (mainhandStack.getItem() == EnigmaticLegacy.theTwist) {
                    if (hasCursed(player)) {
                        if (!event.getEntityLiving().isNonBoss() || event.getEntityLiving() instanceof EntityPlayer) {
                            event.setAmount(event.getAmount() * EnigmaticConfigs.bossDamageBonus);
                        }
                    } else {
                        event.setCanceled(true);
                    }
                } else if (mainhandStack.getItem() == theInfinitum) {
                    if (SuperpositionHandler.isTheWorthyOne(player)) {
                        if (!event.getEntityLiving().isNonBoss() || event.getEntityLiving() instanceof EntityPlayer) {
                            //event.setAmount(10000000);
                            event.setAmount(event.getAmount() * infinitumBossDamageBonus);
                        }
                    } else {
                        event.setCanceled(true);

                        player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 160, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 500, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 3, false, true));
                    }
                } else if (mainhandStack.getItem() == eldritchPan) {
                    if (!SuperpositionHandler.isTheWorthyOne(player)) {
                        event.setCanceled(true);
                    }
                }
            }

            float damageBoost = 0F;

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.berserkEmblem) != -1) {
                damageBoost += event.getAmount() * (getMissingHealthPool(player) * (float) EnigmaticConfigs.attackDamage);
            }

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedScroll) != -1) {
                damageBoost += event.getAmount() * (cursedScrollDamageBoost * getCurseAmount(player));
            }

            event.setAmount(event.getAmount() + damageBoost);
        }

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            ItemStack advancedCurioStack = SuperpositionHandler.getAdvancedBaubles(player);
            if (advancedCurioStack != ItemStack.EMPTY && advancedCurioStack.getItem() instanceof ItemSpellstoneBauble) {
                ItemSpellstoneBauble advancedCurio = (ItemSpellstoneBauble) advancedCurioStack.getItem();
                EntityCreature trueSource = event.getSource().getTrueSource() instanceof EntityCreature ? (EntityCreature) event.getSource().getTrueSource() : null;

                if (event.getSource().damageType.startsWith("explosion") && advancedCurio == EnigmaticLegacy.golemHeart && SuperpositionHandler.hasAnyArmor(player)) {
                    event.setCanceled(true);
                } else if (advancedCurio == blazingCore && trueSource != null && trueSource.getClass().toString().equals("com.sirsquidly.oe.entity.EntityDrowned.class")) {
                    event.setAmount(event.getAmount() * 2F);
                } /*else if (advancedCurio == eyeOfNebula && player.isInWater()) {
                event.setAmount(event.getAmount() * 2F);
            } */ else if (advancedCurio == oceanStone && trueSource != null && (trueSource.isWet() || trueSource.isInWater())) {
                    event.setAmount(event.getAmount() * oceanStoneUnderwaterCreaturesResistance);
                }

                if (advancedCurio.resistanceList.containsKey(event.getSource().damageType)) {
                    event.setAmount(event.getAmount() * advancedCurio.resistanceList.get(event.getSource().damageType).get());
                }
            }

            if (event.getSource() == DamageSource.FALL) {
                if (SuperpositionHandler.isWearEnigmaticAmulet(player, 4) && event.getAmount() <= 2.0f) {
                    event.setCanceled(true);
                }
            }

            if (hasCursed(player)) {
                event.setAmount(event.getAmount() * painMultiplier);
            }


            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.blazingCore) != -1) {
                if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                    EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                    if (!attacker.isImmuneToFire()) {
                        attacker.attackEntityFrom(new EntityDamageSource(DamageSource.ON_FIRE.damageType, player), (float) magmaHeartDamageFeedback);
                        attacker.setFire(magmaHeartIgnitionFeedback);
                    }
                }

            }

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.berserkEmblem) != -1) {
                event.setAmount(event.getAmount() * (1.0F - (getMissingHealthPool(player) * (float) EnigmaticConfigs.damageResistance)));
            }

            /*
             * Handler for increasing damage on users of Bulwark of Blazing Pride.
             */
            if (event.getSource().getTrueSource() != null) {
                if (player.getActiveItemStack().getItem() instanceof ItemInfernalShield && ((IProperShieldUser) player).isActuallyReallyBlocking()) {
                    Vec3d sourcePos = event.getSource().getDamageLocation();
                    if (sourcePos != null) {
                        if (lookPlayersBack(player.rotationYaw, event.getSource().getTrueSource().rotationYaw, 50.0D)) {
                            event.setAmount(event.getAmount() * 1.5F);
                        }
                    }
                }
            }

            if (player.isPotionActive(blazingStrengthEffect) && event.getAmount() > 0) {
                player.removePotionEffect(blazingStrengthEffect);
            }
        }

        if (event.getEntityLiving() instanceof EntityMob || event.getEntityLiving() instanceof EntityDragon) {
            EntityLiving monster = (EntityLiving) event.getEntityLiving();

            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

                if (monster instanceof EntityDragon && player instanceof EntityPlayerMP) {
                    EntityPlayerMP splayer = (EntityPlayerMP) player;
                    Quote.POOR_CREATURE.playOnceIfUnlocked(splayer, 60);
                }

                /*
                 * Handler for damage bonuses of Charm of Monster Slayer.
                 */

                if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.monsterCharm) != -1) {
                    if (monster.isEntityUndead()) {
                        event.setAmount(event.getAmount() * (1 + EnigmaticConfigs.undeadDamageBonus));
                    } else if (monster.getAttackTarget() == player || monster instanceof EntityCreeper) {

                        if (monster instanceof EntityEnderman || monster instanceof EntityPigZombie || monster instanceof EntityBlaze || monster instanceof EntityGuardian || monster instanceof EntityElderGuardian/* || !monster.canChangeDimensions()*/) {
                            // NO-OP
                        } else {
                            event.setAmount(event.getAmount() * (1 + EnigmaticConfigs.hostileDamageBonus));
                        }

                    }
                }
                if (hasCursed(player)) {
                    if (event.getSource().getImmediateSource() != player || (player.getHeldItemMainhand().getItem() != EnigmaticLegacy.theTwist && player.getHeldItemMainhand().getItem() != theInfinitum && player.getHeldItemMainhand().getItem() != eldritchPan)) {
                        event.setAmount(event.getAmount() * (1 - EnigmaticConfigs.monsterDamageDebuff));
                    }
                }
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

                if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.animalGuide) != -1) {
                    if (!(event.getEntityLiving() instanceof EntityWolf)) {
                        event.setCanceled(true);
                    }
                }

            }
        }


        if (event.getEntityLiving() instanceof EntityTameable) {
            EntityTameable pet = (EntityTameable) event.getEntityLiving();

            if (pet.isTamed()) {
                EntityLivingBase owner = pet.getOwner();

                //if (owner instanceof EntityPlayer && SuperpositionHandler.hasItem((EntityPlayer)owner, EnigmaticLegacy.hunterGuide)) {
                //    if (owner.level == pet.level && owner.distanceTo(pet) <= HunterGuide.effectiveDistance.getValue()) {
                //        event.setCanceled(true);
                //        owner.hurt(event.getSource(), SuperpositionHandler.hasItem((EntityPlayer)owner, EnigmaticLegacy.animalGuide) ? (event.getAmount()*HunterGuide.synergyDamageReduction.getValue().asModifierInverted()) : event.getAmount());
                //    }
                //}
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        EntityPlayer player = event.getAttackingPlayer();
        int bonusExp = 0;

        if (event.getEntityLiving() instanceof EntityMob) {
            if (doubleXPEnabled && player != null && BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.monsterCharm) != -1) {
                bonusExp += event.getOriginalExperience() * (((ItemMonsterCharm) EnigmaticLegacy.monsterCharm).bonusXPModifier - 1.0F);
            }
        }

        if (player != null && hasCursed(player)) {
            bonusExp += event.getOriginalExperience() * EnigmaticConfigs.experienceBonus;
        }

        event.setDroppedExperience(event.getDroppedExperience() + bonusExp);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase living = event.getEntityLiving();

        if (!living.world.isRemote && living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            if (BaublesApi.isBaubleEquipped(player, voidPearl) != -1 && Math.random() <= voidPearlUndeadProbability) {
                event.setCanceled(true);
                player.setHealth(1);
            } else if (SuperpositionHandler.isTheWorthyOne((EntityPlayer) living)) {
                boolean infinitum = false;

                if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == theInfinitum) {
                    infinitum = true;
                } else if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == theInfinitum) {
                    infinitum = true;
                }

                if (infinitum) {
                    if (Math.random() <= infinitumUndeadProbability) {
                        event.setCanceled(true);
                        player.setHealth(1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityDragon && !event.getEntity().world.isRemote) {
            EntityDragon dragon = (EntityDragon) event.getEntity();
            // final burst of XP/actual death is at 200 ticks
            if (dragon.deathTicks == 199 && abyssalHeartOwner != null) {
                int heartsGained = SuperpositionHandler.getPersistentInteger(abyssalHeartOwner, "AbyssalHeartsGained", 0);

                Vec3d center = new Vec3d(dragon.posX, dragon.posY, dragon.posZ);
                EntityItemSoulCrystal heart = new EntityItemSoulCrystal(event.getEntity().world, center.x, center.y, center.z, new ItemStack(abyssalHeart, 1));
                heart.setOwnerId(abyssalHeartOwner.getUniqueID());
                event.getEntity().world.spawnEntity(heart);

                SuperpositionHandler.setPersistentInteger(abyssalHeartOwner, "AbyssalHeartsGained", heartsGained + 1);
                abyssalHeartOwner = null;

            }

            List<EntityPlayerMP> players = dragon.world.getEntitiesWithinAABB(EntityPlayerMP.class,
                    SuperpositionHandler.getBoundingBoxAroundEntity(dragon, 256));

            players.forEach(player -> Quote.WITH_DRAGONS.playOnceIfUnlocked(player, 140));
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void keepRingCurses(LivingDeathEvent event) {
        EntityLivingBase living = event.getEntityLiving();

        if (!living.world.isRemote && living instanceof EntityPlayerMP && BaublesApi.isBaubleEquipped((EntityPlayer) living, eldritchAmulet) != -1) {
            EntityPlayerMP player = (EntityPlayerMP) living;
            eldritchAmulet.storeInventory(player);

            SuperpositionHandler.setPersistentBoolean(player, "dropEldritchAmulet", true);

        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        NBTTagCompound playerData = event.player.getEntityData();
        NBTTagCompound data = playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG) ? playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG) : new NBTTagCompound();

        if (!data.getBoolean(SPAWN_WITH_BOOK) && spawnWithBook) {
            ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(theAcknowledgment));
            data.setBoolean(SPAWN_WITH_BOOK, true);
        }

        if (!data.getBoolean(SPAWN_WITH_AMULET) && spawnWithAmulet) {
            ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(enigmaticAmulet, 1, 0));
            data.setBoolean(SPAWN_WITH_AMULET, true);
        }

        if (!data.getBoolean(SPAWN_WITH_CURSE) && !ultraNoobMode) {
            if (ultraHardcore) {
                {
                    IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(event.player);
                    if (BaublesApi.getBaublesHandler(event.player).getStackInSlot(BaubleType.RING.getValidSlots()[0]) == ItemStack.EMPTY)
                        baubles.setStackInSlot(BaubleType.RING.getValidSlots()[0], new ItemStack(cursedRing));
                    else
                        ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(cursedRing));
                }
            } else {
                ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(cursedRing));
            }

            data.setBoolean(SPAWN_WITH_CURSE, true);
        }

        playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            EnigmaticLegacy.soulCrystal.updatePlayerSoulMap(player);
        }
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            EnigmaticLegacy.soulCrystal.updatePlayerSoulMap(player);
        }

    }

    private static void syncPlayTime(EntityPlayer player) {
        if (!player.world.isRemote) {
            IPlaytimeCounter counter = IPlaytimeCounter.get(player);
            counter.matchStats();
            EnigmaticLegacy.packetInstance.sendToAllAround(new PacketSyncPlayTime(player.getUniqueID(), counter.getTimeWithCurses(), counter.getTimeWithoutCurses()),
                    new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64));
        }
    }
}
