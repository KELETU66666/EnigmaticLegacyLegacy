package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.item.ItemInfernalShield;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CooldownTracker.class)
public class MixinCooldownTracker {

    @Inject(method = "setCooldown", at = @At(value = "HEAD"), cancellable = true)
    private void shouldnotSetCooldown(Item item, int time, CallbackInfo ci){
        if(item instanceof ItemInfernalShield)
            ci.cancel();
    }
}
