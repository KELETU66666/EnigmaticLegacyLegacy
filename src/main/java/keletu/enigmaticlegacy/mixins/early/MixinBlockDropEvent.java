package keletu.enigmaticlegacy.mixins.early;

import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.*;
import keletu.enigmaticlegacy.util.interfaces.IFortuneBonus;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlockDropEvent {

    @Shadow
    public abstract void dropBlockAsItem(World worldIn, BlockPos pos, IBlockState state, int fortune);

    @Shadow(remap = false)
    protected ThreadLocal<EntityPlayer> harvesters;

    @Inject(method = "harvestBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", ordinal = 1), cancellable = true)
    private void mixinHarvestLevel(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity p_180657_5_, ItemStack stack, CallbackInfo ci){
        int base = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        if (player instanceof EntityPlayer) {
            for (int i = 0; i < getBaubleSlots(player); i++) {
                ItemStack bStack = getSlotBauble(player, i);
                if (!bStack.isEmpty() && bStack.getItem() instanceof IFortuneBonus) {
                    base += ((IFortuneBonus) bStack.getItem()).bonusLevelFortune();
                }
            }
        }

        this.dropBlockAsItem(world, pos, state, base);
        harvesters.set(null);
        ci.cancel();
    }
}
