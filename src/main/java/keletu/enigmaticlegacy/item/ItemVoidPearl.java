package keletu.enigmaticlegacy.item;

import baubles.api.BaublesApi;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
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
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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

            if (player.getAir() < 300) {
                player.setAir(300);
            }

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

                if (entities.contains(player)) {
                    entities.remove(player);
                }

                for (EntityLivingBase victim : entities) {
                    if (victim.world.getCombinedLight(victim.getPosition(), 0) < 3) {

                        if (victim instanceof EntityPlayer) {
                            EntityPlayer playerVictim = (EntityPlayer) victim;
                            if (BaublesApi.isBaubleEquipped(playerVictim, EnigmaticLegacy.voidPearl) != -1) {
                                playerVictim.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1, false, true));
                                continue;
                            }
                        }

                        if (!(victim instanceof EntityPlayer) || player.canAttackPlayer((EntityPlayer) victim)) {
                            EntityDamageSourceIndirect darkness = new EntityDamageSourceIndirect("darkness", player, null);
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
        }

    }

    @Override
    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {

    }

}