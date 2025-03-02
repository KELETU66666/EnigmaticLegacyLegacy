package keletu.enigmaticlegacy.item;

import baubles.api.BaublesApi;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.EnigmaticConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.util.compat.CompatSimpledDifficulty;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemVoidPearl extends ItemSpellstoneBauble {

    public List<String> healList = new ArrayList<>();
    public DamageSource theDarkness;

    public ItemVoidPearl() {
        super("void_pearl", EnumRarity.EPIC);

        this.immunityList.add(DamageSource.DROWN.damageType);
        this.immunityList.add(DamageSource.IN_WALL.damageType);

        this.healList.add(DamageSource.WITHER.damageType);
        this.healList.add(DamageSource.MAGIC.damageType);

        this.theDarkness = new DamageSource("darkness");
        this.theDarkness.setDamageIsAbsolute();
        this.theDarkness.setDamageBypassesArmor();
        this.theDarkness.setMagicDamage();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl2"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearlCooldown")/* + TextFormatting.GOLD + spellstoneCooldown / 20.0F*/);
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl3"));
            // These lines describe hunger negation trait that was removed since release 2.5.0
            //list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl4"));
            //list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl6"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl7"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl8"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl9"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl10"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl11"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl12"));
            list.add(I18n.format("tooltip.enigmaticlegacy.voidPearl13") + TextFormatting.GOLD + Math.round(voidPearlUndeadProbability * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.voidPearl13_1"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {

        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

			/*

			FoodStats stats = player.getFoodStats();
			stats.setFoodLevel(20);

			//((AccessorFoodStats) stats).setFoodSaturationLevel(0);


			try {
				ObfuscatedFields.foodSaturationField.setFloat(stats, 0F);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			 */

            player.setAir(300);

            if (player.isBurning()) {
                player.extinguish();
            }

            for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
                if (effect.getPotion() == MobEffects.NIGHT_VISION) {
                    if (effect.getDuration() >= EnigmaticLegacy.miningCharm.nightVisionDuration - 10 && effect.getDuration() <= EnigmaticLegacy.miningCharm.nightVisionDuration) {
                        continue;
                    }
                } /*else if (effect.getPotion().getRegistryName().equals(new ResourceLocation("mana-and-artifice", "chrono-exhaustion"))) {
                    continue;
                }*/

                player.removePotionEffect(effect.getPotion());
            }

            if (player.ticksExisted % 10 == 0) {
                List<EntityLivingBase> entities = living.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(player.posX - shadowRange, player.posY - shadowRange, player.posZ - shadowRange, player.posX + shadowRange, player.posY + shadowRange, player.posZ + shadowRange));

                entities.remove(player);

                for (EntityLivingBase victim : entities) {

                    if (this.getCombinedLight(victim.world, victim.getPosition(), 0) < 3) {

                        if (victim instanceof EntityPlayer) {
                            EntityPlayer playerVictim = (EntityPlayer) victim;
                            if (BaublesApi.isBaubleEquipped(playerVictim, EnigmaticLegacy.voidPearl) != -1) {
                                playerVictim.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1, false, true));
                                continue;
                            }
                        }

                        if (!(victim instanceof EntityPlayer) || player.canAttackPlayer((EntityPlayer) victim)) {
                            DamageSource darkness = new DamageSource("darkness");
                            darkness.setDamageIsAbsolute().setDamageBypassesArmor().setMagicDamage();

                            boolean attack = victim.attackEntityFrom(darkness, (float) baseDarknessDamage);

                            if (attack) {
                                living.world.playSound(null, victim.getPosition(), SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0F, (float) (0.3F + (Math.random() * 0.4D)));

                                victim.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1, false, true));
                                victim.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2, false, true));
                                victim.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, false, true));
                                victim.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 160, 2, false, true));
                                victim.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 3, false, true));
                            }
                        }
                    }
                }
            }

            if (ModCompat.COMPAT_SIMPLED_DIFFICULTY)
                CompatSimpledDifficulty.ClearTempurature(player);
        }


    }

    public int getCombinedLight(World world, BlockPos pos, int lightValue) {
        int i = this.getLightFromNeighborsFor(world, EnumSkyBlock.SKY, pos);
        int j = this.getLightFromNeighborsFor(world, EnumSkyBlock.BLOCK, pos);

        if (j < lightValue) {
            j = lightValue;
        }

        return i << 20 | j << 4;
    }

    public int getLightFromNeighborsFor(World world, EnumSkyBlock type, BlockPos pos) {
        if (!world.provider.hasSkyLight() && type == EnumSkyBlock.SKY) {
            return 0;
        } else {
            if (pos.getY() < 0) {
                pos = new BlockPos(pos.getX(), 0, pos.getZ());
            }

            if (!world.isValid(pos)) {
                return type.defaultLightValue;
            } else if (!world.isBlockLoaded(pos)) {
                return type.defaultLightValue;
            } else if (world.getBlockState(pos).useNeighborBrightness()) {
                int i1 = world.getLightFor(type, pos.up());
                int i = world.getLightFor(type, pos.east());
                int j = world.getLightFor(type, pos.west());
                int k = world.getLightFor(type, pos.south());
                int l = world.getLightFor(type, pos.north());

                if (i > i1) {
                    i1 = i;
                }

                if (j > i1) {
                    i1 = j;
                }

                if (k > i1) {
                    i1 = k;
                }

                if (l > i1) {
                    i1 = l;
                }

                return i1;
            } else {
                Chunk chunk = world.getChunk(pos);
                return chunk.getLightFor(type, pos);
            }
        }
    }

    @Override
    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {

    }

}