package keletu.enigmaticlegacy.mixins.early;

import baubles.api.BaublesApi;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @ModifyReturnValue(method = "getLootingModifier", at = @At("RETURN"))
    private static int enchantment_getLootingLevel(int original, EntityLivingBase entity) {
        int bonus = 0;
        if (entity instanceof EntityPlayer) {
            for (int i = 0; i < BaublesApi.getBaublesHandler((EntityPlayer) entity).getSlots(); i++) {
                ItemStack bStack = BaublesApi.getBaublesHandler((EntityPlayer) entity).getStackInSlot(i);
                if (!bStack.isEmpty() && bStack.getItem() instanceof ILootingBonus) {
                    bonus += ((ILootingBonus) bStack.getItem()).bonusLevelLooting();
                }
            }
        }
        return original + bonus;
    }

    @Inject(method = "getSweepingDamageRatio", at = @At("RETURN"), cancellable = true)
    private static void getSweeping(EntityLivingBase entity, CallbackInfoReturnable<Float> cir) {
        if (entity instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) entity, EnigmaticLegacy.thunderScroll) != -1) {
            cir.setReturnValue(Math.max(1.0F, cir.getReturnValue()));
        }
    }
}
