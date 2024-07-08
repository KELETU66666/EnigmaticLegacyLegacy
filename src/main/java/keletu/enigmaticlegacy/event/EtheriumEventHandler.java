package keletu.enigmaticlegacy.event;

import keletu.enigmaticlegacy.core.Vector3;
import keletu.enigmaticlegacy.item.etherium.EtheriumArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EtheriumEventHandler {
    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            /*
             * Handler for knockback feedback and damage reduction of Etherium Armor Shield.
             */

            if (EtheriumArmor.hasShield(player)) {
                if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                    EntityLivingBase attacker = ((EntityLivingBase) event.getSource().getTrueSource());
                    Vector3 vec = Vector3.fromEntityCenter(player).subtract(Vector3.fromEntityCenter(event.getSource().getTrueSource())).normalize();
                    attacker.knockBack(attacker, 0.75F, vec.x, vec.z);
                    player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.9F + (float) (Math.random() * 0.1D));
                    player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.9F + (float) (Math.random() * 0.1D));
                }

                event.setAmount(event.getAmount() * 0.5F);
            }
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;

        /*
         * Handler for immunities and projectile deflection.
         */

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            if (event.getSource().getImmediateSource() instanceof IProjectile || event.getSource().getImmediateSource() instanceof EntityArrow) {
                if (EtheriumArmor.hasShield(player)) {
                    event.setCanceled(true);

                    player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 1.0F, 0.9F + (float) (Math.random() * 0.1D));
                }
            }
        }
    }
}