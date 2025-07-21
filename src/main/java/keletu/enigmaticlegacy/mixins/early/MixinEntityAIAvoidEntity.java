package keletu.enigmaticlegacy.mixins.early;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
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
    public void mixinShouldExecute(CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof AbstractIllager && closestLivingEntity instanceof EntityPlayer){
            if(BaublesApi.isBaubleEquipped((EntityPlayer) closestLivingEntity, EnigmaticLegacy.halfHeartMask) != -1){
                cir.setReturnValue(false);
            }
        }
    }
}
