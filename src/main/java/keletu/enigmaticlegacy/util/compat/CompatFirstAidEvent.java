package keletu.enigmaticlegacy.util.compat;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatFirstAidEvent {
    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP && BaublesApi.isBaubleEquipped((EntityPlayerMP) event.getEntityLiving(), EnigmaticLegacy.halfHeartMask) != -1) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            if(player.getHealth() >= player.getMaxHealth() * 0.5F){
                event.setCanceled(true);
            }
        }
    }
}