package keletu.enigmaticlegacy.event;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.ELConfigs;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.*;
import keletu.enigmaticlegacy.api.DimensionalPosition;
import keletu.enigmaticlegacy.api.ExtendedBaublesApi;
import keletu.enigmaticlegacy.api.cap.IExtendedBaublesItemHandler;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.entity.EntityItemImportant;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.*;
import keletu.enigmaticlegacy.item.ItemEldritchPan;
import keletu.enigmaticlegacy.item.ItemInfernalShield;
import keletu.enigmaticlegacy.item.ItemMonsterCharm;
import keletu.enigmaticlegacy.item.ItemSpellstoneBauble;
import keletu.enigmaticlegacy.packet.PacketSyncPlayTime;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;

@Mod.EventBusSubscriber(modid = MODID)
public class ELEvents {

    private static final String SPAWN_WITH_BOOK = EnigmaticLegacy.MODID + ".acknowledgment";
    private static final String SPAWN_WITH_AMULET = EnigmaticLegacy.MODID + ".enigmatic_amulet";
    private static final String SPAWN_WITH_CURSE = EnigmaticLegacy.MODID + ".cursedring";
    public static final Map<EntityLivingBase, Float> knockbackThatBastard = new WeakHashMap<>();

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone evt) {
        EntityPlayer newPlayer = evt.getEntityPlayer();
        EntityPlayer player = evt.getOriginal();

        EnigmaticLegacy.soulCrystal.updatePlayerSoulMap(newPlayer);
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
            boolean found = false;

            for (EntityItem entityItem : event.getDrops()) {
                ItemStack itemStack = entityItem.getItem();
                if (itemStack.getItem() == enigmaticAmulet || itemStack.getItem() == ascensionAmulet) {
                    found = true;
                    break;
                }
            }

            if (found && !event.getDrops().isEmpty() && ELConfigs.vesselEnabled) {
                ItemStack soulCrystal = SuperpositionHandler.shouldPlayerDropSoulCrystal(player) ? EnigmaticLegacy.soulCrystal.createCrystalFrom(player) : null;
                ItemStack storageCrystal = EnigmaticLegacy.storageCrystal.storeDropsOnCrystal(event.getDrops(), player, soulCrystal);
                EntityItemSoulCrystal droppedStorageCrystal = new EntityItemSoulCrystal(dimPoint.world, dimPoint.getPosX(), dimPoint.getPosY() + 1.5, dimPoint.getPosZ(), storageCrystal);
                droppedStorageCrystal.setOwnerId(player.getUniqueID());
                dimPoint.world.spawnEntity(droppedStorageCrystal);
                EnigmaticLegacy.logger.info("Summoned Extradimensional Storage Crystal for " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
                event.getDrops().clear();

                if (soulCrystal != null) {
                    droppedCrystal = true;
                }

            } else if (SuperpositionHandler.shouldPlayerDropSoulCrystal(player)) {
                ItemStack soulCrystal = EnigmaticLegacy.soulCrystal.createCrystalFrom(player);
                EntityItemSoulCrystal droppedSoulCrystal = new EntityItemSoulCrystal(dimPoint.world, dimPoint.getPosX(), dimPoint.getPosY() + 1.5, dimPoint.getPosZ(), soulCrystal);
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
                addDrop(event, getRandomSizeStack(EnigmaticLegacy.ingotWitherite, 1, 3));
            }

            if (hasCursed((EntityPlayer) event.getSource().getTrueSource())) {


                if (!ELConfigs.enableSpecialDrops)
                    return;

                if (killed.getClass() == EntityShulker.class) {
                    addDropWithChance(event, new ItemStack(EnigmaticLegacy.astralDust, 1), 20);
                } else if (killed.getClass() == EntitySkeleton.class || killed.getClass() == EntityStray.class) {
                    addDrop(event, getRandomSizeStack(Items.ARROW, 3, 15));
                } else if (killed.getClass() == EntityZombie.class || killed.getClass() == EntityHusk.class) {
                    addDropWithChance(event, getRandomSizeStack(Items.SLIME_BALL, 1, 3), 25);
                } else if (killed.getClass() == EntitySpider.class || killed.getClass() == EntityCaveSpider.class) {
                    addDrop(event, getRandomSizeStack(Items.STRING, 2, 12));
                } else if (killed.getClass() == EntityGuardian.class) {
                    addDropWithChance(event, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("oe", "nautilus_shell")), 1), 15);
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_CRYSTALS, 2, 5));
                } else if (killed.getClass() == EntityElderGuardian.class) {
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_CRYSTALS, 4, 16));
                    addDrop(event, getRandomSizeStack(Items.PRISMARINE_SHARD, 7, 28));
                    addOneOf(event,
                            //new ItemStack(guardianHeart, 1),
                            new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("oe", "heart_of_the_sea")), 1),
                            new ItemStack(Items.GOLDEN_APPLE, 1, 1),
                            new ItemStack(Items.ENDER_EYE, 1),
                            EnchantmentHelper.addRandomEnchantment(new Random(), new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("oe", "trident")), 1), 25 + new Random().nextInt(15), true));
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
                    addDropWithChance(event, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("futuremc", "netherite_scrap")), 1), 7);
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

    public static float getMissingHealthPool(EntityPlayer player) {
        return (player.getMaxHealth() - Math.min(player.getHealth(), player.getMaxHealth())) / player.getMaxHealth();
    }

    @SubscribeEvent(priority = HIGH)
    public static void hurtEvent(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            ItemStack advancedCurioStack = SuperpositionHandler.getAdvancedBaubles(player);
            if (advancedCurioStack != ItemStack.EMPTY) {
                ItemSpellstoneBauble advancedCurio = (ItemSpellstoneBauble) advancedCurioStack.getItem();

                if (advancedCurio.immunityList.contains(event.getSource().damageType)) {
                    event.setCanceled(true);
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

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ELDRITCH_AMULET)) {
                if (SuperpositionHandler.isTheWorthyOne(player)) {
                    lifesteal += event.getAmount() * 0.15F;
                }
            }
*/
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

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onConfirmedDeath(LivingDeathEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP attacker = (EntityPlayerMP) event.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();

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
            miningBoost += ELConfigs.breakSpeedBonus;
        }

        if (ExtendedBaublesApi.isBaubleEquipped(event.getEntityPlayer(), EnigmaticLegacy.cursedScroll) != -1) {
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

			/*
			 * Regeneration slowdown handler for Pearl of the Void.
			 * Removed as of Release 2.5.0.

			if (SuperpositionHandler.hasCurio(player, EnigmaticLegacy.voidPearl)) {
				if (event.getAmount() <= 1.0F) {
					event.setAmount((float) (event.getAmount() / (1.5F * ConfigHandler.VOID_PEARL_REGENERATION_MODIFIER.getValue())));
				}
			}
			 */

            if (event.getAmount() <= 1.0F)
                //if (forbiddenFruit.haveConsumedFruit(player)) {
                //    event.setAmount(event.getAmount()*ForbiddenFruit.regenerationSubtraction.getValue().asModifierInverted());
                //}

                if (ExtendedBaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedScroll) != -1) {
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
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        for (ResourceLocation rl : ELConfigs.cursedItemList) {
            if (event.getItemStack().getItem() == ForgeRegistries.ITEMS.getValue(rl)) {
                TextFormatting color = !hasCursed(event.getEntityPlayer()) ? TextFormatting.DARK_RED : TextFormatting.GRAY;
                event.getToolTip().add(1, color + I18n.format("tooltip.enigmaticlegacy.cursedOnesOnly1"));
                event.getToolTip().add(2, color + I18n.format("tooltip.enigmaticlegacy.cursedOnesOnly2"));
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void inventoryInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Minecraft.getMinecraft().player == null)
            return;

        if (event.getGui() instanceof GuiInventory) {
            if (hasCursed(Minecraft.getMinecraft().player) || hasEnderRing(Minecraft.getMinecraft().player))
                event.getButtonList().add(new EnderChestInventoryButton(7501, (event.getGui().width / 2) + ELConfigs.iconOffset, (event.getGui().height / 2) - 111, ""));
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

        IExtendedBaublesItemHandler extraBaublesHandler = ExtendedBaublesApi.getBaublesHandler(player);
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
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (!player.capabilities.isFlying && !player.onGround) {
                if (SuperpositionHandler.getAdvancedBaubles(player).getItem() == oceanStone && player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ)).getBlock() == Blocks.WATER || player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ)).getBlock() == Blocks.FLOWING_WATER) {
                    player.motionY = 0;
                    if (player.isSneaking())
                        player.motionY -= 0.2;
                } else if (SuperpositionHandler.isWearEnigmaticAmulet(player, 3)) {
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
        if (SuperpositionHandler.isWearEnigmaticAmulet(player, 3)) {
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
            event.setLevel(event.getLevel() + ELConfigs.enchantingBonus);
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
            event.setStrength(event.getStrength() * ELConfigs.knockbackDebuff);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.isPlayerSleeping() && player.sleepTimer > 90 && hasCursed(player)) {
                player.sleepTimer = 90;
            }
            if (player.isBurning() && hasCursed(player)) {
                player.setFire(player.fire + 2);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void preRender(RenderGameOverlayEvent.Pre event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        Minecraft mc = Minecraft.getMinecraft();

        if (event.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            if (ExtendedBaublesApi.isBaubleEquipped(mc.player, EnigmaticLegacy.oceanStone) != -1/* || SuperpositionHandler.hasCurio(mc.player, EnigmaticLegacy.voidPearl)*/) {
                //  if (OceanStone.preventOxygenBarRender.getValue()) {
                event.setCanceled(true);
                // }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getAmount() >= Float.MAX_VALUE)
            return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            Entity immediateSource = event.getSource().getImmediateSource();

            if (player.getHeldItemMainhand() != ItemStack.EMPTY) {
                ItemStack mainhandStack = player.getHeldItemMainhand();

                if (mainhandStack.getItem() == EnigmaticLegacy.theTwist) {
                    if (hasCursed(player)) {
                        if (!event.getEntityLiving().isNonBoss() || event.getEntityLiving() instanceof EntityPlayer) {
                            event.setAmount(event.getAmount() * ELConfigs.bossDamageBonus);
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
                }
            }

            float damageBoost = 0F;

            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.berserkEmblem) != -1) {
                damageBoost += event.getAmount() * (getMissingHealthPool(player) * (float) ELConfigs.attackDamage);
            }

            if (ExtendedBaublesApi.isBaubleEquipped(player, EnigmaticLegacy.cursedScroll) != -1) {
                damageBoost += event.getAmount() * (cursedScrollDamageBoost * getCurseAmount(player));
            }

            event.setAmount(event.getAmount() + damageBoost);
        }

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            ItemStack advancedCurioStack = SuperpositionHandler.getAdvancedBaubles(player);
            if (advancedCurioStack != ItemStack.EMPTY) {
                ItemSpellstoneBauble advancedCurio = (ItemSpellstoneBauble) advancedCurioStack.getItem();
                EntityCreature trueSource = event.getSource().getTrueSource() instanceof EntityCreature ? (EntityCreature) event.getSource().getTrueSource() : null;

                if (event.getSource().damageType.startsWith("explosion") && advancedCurio == EnigmaticLegacy.golemHeart && SuperpositionHandler.hasAnyArmor(player)) {
                    event.setCanceled(true);
                } /*else if (advancedCurio == magmaHeart && trueSource != null && (trueSource.getMobType() == CreatureAttribute.WATER || trueSource instanceof DrownedEntity)) {
                event.setAmount(event.getAmount() * 2F);
            } else if (advancedCurio == eyeOfNebula && player.isInWater()) {
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
            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.berserkEmblem) != -1) {
                event.setAmount(event.getAmount() * (1.0F - (getMissingHealthPool(player) * (float) ELConfigs.damageResistance)));
            }

            /*
             * Handler for increasing damage on users of Bulwark of Blazing Pride.
             */
            if (event.getSource().getTrueSource() != null) {
                if (player.getActiveItemStack().getItem() instanceof ItemInfernalShield && player.isActiveItemStackBlocking()) {
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

        if (event.getEntityLiving() instanceof EntityMob) {
            EntityMob monster = (EntityMob) event.getEntityLiving();

            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
                /*
                 * Handler for damage bonuses of Charm of Monster Slayer.
                 */

                if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.monsterCharm) != -1) {
                    if (monster.isEntityUndead()) {
                        event.setAmount(event.getAmount() * (1 + ELConfigs.undeadDamageBonus));
                    } else if (monster.getAttackTarget() == player || monster instanceof EntityCreeper) {

                        if (monster instanceof EntityEnderman || monster instanceof EntityPigZombie || monster instanceof EntityBlaze || monster instanceof EntityGuardian || monster instanceof EntityElderGuardian/* || !monster.canChangeDimensions()*/) {
                            // NO-OP
                        } else {
                            event.setAmount(event.getAmount() * (1 + ELConfigs.hostileDamageBonus));
                        }

                    }
                }
                if (hasCursed(player)) {
                    if (event.getSource().getImmediateSource() != player || (player.getHeldItemMainhand().getItem() != EnigmaticLegacy.theTwist && player.getHeldItemMainhand().getItem() != theInfinitum && player.getHeldItemMainhand().getItem() != eldritchPan)) {
                        event.setAmount(event.getAmount() * (1 - ELConfigs.monsterDamageDebuff));
                    }
                }
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

                if (ExtendedBaublesApi.isBaubleEquipped(player, EnigmaticLegacy.animalGuide) != -1) {
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
            bonusExp += event.getOriginalExperience() * ELConfigs.experienceBonus;
        }

        event.setDroppedExperience(event.getDroppedExperience() + bonusExp);
    }

    @SubscribeEvent(priority = HIGH)
    public static void keepRingCurses(LivingDeathEvent event) {
        EntityLivingBase living = event.getEntityLiving();

        if (!living.world.isRemote && living instanceof EntityDragon && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            if (hasCursed(player)) {
                if (SuperpositionHandler.isTheWorthyOne(player)) {
                    EntityItemImportant abyssalHeart = new EntityItemImportant(living.world, living.posX, living.posY, living.posZ, new ItemStack(EnigmaticLegacy.abyssalHeart));
                    living.world.spawnEntity(abyssalHeart);
                }
            }
        }
        if (!living.world.isRemote && living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            if (SuperpositionHandler.isTheWorthyOne((EntityPlayer) living)) {
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
    public static void onPlayerJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        NBTTagCompound playerData = event.player.getEntityData();
        NBTTagCompound data = playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG) ? playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG) : new NBTTagCompound();

        if (!data.getBoolean(SPAWN_WITH_BOOK) && spawnWithBook) {
            ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(theAcknowledgment));
            data.setBoolean(SPAWN_WITH_BOOK, true);
        }

        if (!data.getBoolean(SPAWN_WITH_AMULET)) {
            ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(enigmaticAmulet, 1, 0));
            data.setBoolean(SPAWN_WITH_AMULET, true);
        }

        if (!data.getBoolean(SPAWN_WITH_CURSE)) {
            if (ultraHardcore) {
                {
                    IExtendedBaublesItemHandler baubles = ExtendedBaublesApi.getBaublesHandler(event.player);
                    if (ExtendedBaublesApi.getBaublesHandler(event.player).getStackInSlot(2) == ItemStack.EMPTY)
                        baubles.setStackInSlot(2, new ItemStack(cursedRing));
                    else
                        ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(cursedRing));
                }
            } else if (!ultraNoobMode) {
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
