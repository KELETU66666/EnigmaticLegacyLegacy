package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIAvoidEntity.class)
public class MixinEntityAIAvoidEntity {

    @Shadow
    protected EntityCreature entity;

    @Shadow
    protected Entity closestLivingEntity;

    @Inject(method = "shouldExecute", at = @At(value = "HEAD"), cancellable = true)
    private void mixinShouldExecute(CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof AbstractIllager && closestLivingEntity instanceof EntityPlayer){
            if(SuperpositionHandler.isBaubleEquipped((EntityPlayer) closestLivingEntity, EnigmaticLegacy.halfHeartMask)){
                cir.setReturnValue(false);
            }
        }
    }
}
