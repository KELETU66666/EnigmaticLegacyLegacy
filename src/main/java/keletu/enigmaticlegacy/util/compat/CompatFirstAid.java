package keletu.enigmaticlegacy.util.compat;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import net.minecraft.entity.player.EntityPlayerMP;

public class CompatFirstAid {
    public static void onFirstAidHlfHealth(EntityPlayerMP player){
        AbstractPlayerDamageModel damageModel = player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, null);
        for (final EnumPlayerPart partEnum : EnumPlayerPart.values()) {
            final AbstractDamageablePart part = damageModel.getFromEnum(partEnum);
            if (part.currentHealth > part.getMaxHealth() * 0.5) {
                part.currentHealth = part.getMaxHealth() * 0.5F;
                FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, true), player);
            }
        }
    }
}