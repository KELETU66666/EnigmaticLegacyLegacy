package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.event.special.EnterBlockEvent;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnterBlockTrigger.class)
public class MixinEnterBlockTrigger {

	@Inject(method = "trigger", at = @At("HEAD"))
	private void onTrigger(EntityPlayerMP player, IBlockState state, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new EnterBlockEvent(player, state));
	}

}