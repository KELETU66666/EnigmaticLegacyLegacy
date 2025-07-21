package keletu.enigmaticlegacy.mixins.early;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityAIAvoidEntity.class)
public abstract class MixinEntityAIAvoidEntity<T extends Entity> extends EntityAIBase {

    @Shadow
    protected EntityCreature entity;

    @Shadow
    protected T closestLivingEntity;

    @ModifyReturnValue(method = "shouldExecute", at = @At(value = "RETURN", ordinal = 3))
    private boolean mixinShouldExecute(boolean original){
        if(entity instanceof AbstractIllager && closestLivingEntity instanceof EntityPlayer){
            if(SuperpositionHandler.isBaubleEquipped((EntityPlayer) closestLivingEntity, EnigmaticLegacy.halfHeartMask)){
                return false;
            }
        }
        return original;
    }
}
