package keletu.enigmaticlegacy.mixins.early;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "getLootingModifier", at = @At(value = "HEAD"), cancellable = true)
    private static void enchantment_getLootingLevel(EntityLivingBase living, CallbackInfoReturnable<Integer> cir) {
        int base = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, living.getHeldItemMainhand());
        if (living instanceof EntityPlayer) {
            if (EnigmaticConfigs.lootingBonus > 0 && hasCursed((EntityPlayer) living))
                base += EnigmaticConfigs.lootingBonus;

            for (int i = 0; i < BaublesApi.getBaublesHandler((EntityPlayer) living).getSlots(); i++) {
                ItemStack bStack = BaublesApi.getBaublesHandler((EntityPlayer) living).getStackInSlot(i);
                if (!bStack.isEmpty() && bStack.getItem() instanceof ILootingBonus) {
                    base += ((ILootingBonus) bStack.getItem()).bonusLevelLooting();
                }
            }
        }
        cir.setReturnValue(base);
    }

    @Inject(method = "getSweepingDamageRatio", at = @At("RETURN"), cancellable = true)
    private static void getSweeping(EntityLivingBase entity, CallbackInfoReturnable<Float> cir) {
        if (entity instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) entity, EnigmaticLegacy.thunderScroll) != -1) {
            cir.setReturnValue(Math.max(1.0F, cir.getReturnValue()));
        }
    }
}
