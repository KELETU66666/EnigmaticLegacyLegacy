package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ItemHalfHeartMask extends ItemBaseFireProof implements IBauble {

    public ItemHalfHeartMask() {
        super("half_heart_mask", EnumRarity.UNCOMMON);
        this.maxStackSize = 1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.HEAD;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayerMP) {
            AbstractPlayerDamageModel damageModel = Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, null));
            for (final EnumPlayerPart partEnum : EnumPlayerPart.values()) {
                final AbstractDamageablePart part = damageModel.getFromEnum(partEnum);
                if (part.currentHealth > part.getMaxHealth() * 0.5) {
                    part.currentHealth = part.getMaxHealth() * 0.5F;
                    FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, true), (EntityPlayerMP) player);
                }
            }
        }
    }
}
