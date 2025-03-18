package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.item.ItemInfernalShield;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

	protected MixinEntityPlayer(World world) {
		super(world);
		throw new IllegalStateException("Can't touch this");
	}

	@Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
	private void onDisableShield(boolean flag, CallbackInfo info) {
		if (this.activeItemStack != null && this.activeItemStack.getItem() instanceof ItemInfernalShield) {
			info.cancel();
		}
	}

	//@Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
	//private static void onFindRespawnPositionAndUseSpawnBlock(ServerLevel level, BlockPos pos,
	//		float angle, boolean forced, boolean keep, CallbackInfoReturnable<Optional<Vec3>> info) {
	//	AnchorSearchResult result = EndAnchor.findAndUseEndAnchor(level, pos, angle, forced, keep);
//
	//	if (result.found()) {
	//		info.setReturnValue(result.location());
	//	}
	//}

}