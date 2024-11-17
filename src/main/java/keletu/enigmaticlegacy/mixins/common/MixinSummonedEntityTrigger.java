package keletu.enigmaticlegacy.mixins.common;

import keletu.enigmaticlegacy.event.special.SummonedEntityEvent;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SummonedEntityTrigger.class)
public class MixinSummonedEntityTrigger {

	@Inject(method = "trigger", at = @At("HEAD"))
	private void onTrigger(EntityPlayerMP player, Entity entity, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new SummonedEntityEvent(player, entity));
	}

}