package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.api.quack.IProperShieldUser;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IProperShieldUser {
	@Shadow protected ItemStack activeItemStack;
	@Shadow protected int activeItemStackUseCount;

	public MixinEntityLivingBase(World world) {
		super(world);
		throw new IllegalStateException("Can't touch this");
	}

	@Inject(method = "canBlockDamageSource", at = @At("HEAD"), cancellable = true)
	private void onDamageSourceBlocking(DamageSource source, CallbackInfoReturnable<Boolean> info) {
		if (SuperpositionHandler.onDamageSourceBlocking(((EntityLivingBase)(Object)this), this.activeItemStack, source, info)) {
			info.setReturnValue(true);
		}
	}

	@Override
	public boolean isActuallyReallyBlocking() {
		if (this.isHandActive() && !this.activeItemStack.isEmpty()) {
			Item item = this.activeItemStack.getItem();
			if (item.getItemUseAction(this.activeItemStack) != EnumAction.BLOCK)
				return false;
			else
				return item.getMaxItemUseDuration(this.activeItemStack) - this.activeItemStackUseCount >= 0; // 0 for no warm-up time
		} else
			return false;
	}

	@Shadow
	public abstract boolean isHandActive();

}