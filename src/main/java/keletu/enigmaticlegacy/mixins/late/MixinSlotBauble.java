package keletu.enigmaticlegacy.mixins.late;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.item.ItemScrollBauble;
import keletu.enigmaticlegacy.item.ItemSpellstoneBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlotBauble.class)
public class MixinSlotBauble extends SlotItemHandler {

    @Shadow(remap = false)
    int baubleSlot;

    @Shadow(remap = false)
    EntityPlayer player;

    public MixinSlotBauble(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack) {
        boolean isScroll = stack.getItem() instanceof ItemScrollBauble;
        boolean isSpellstone = stack.getItem() instanceof ItemSpellstoneBauble;
        if ((isScroll || isSpellstone) && baubleSlot < 10)
            return false;
        switch (baubleSlot) {
            case 7:
                return player.getTags().contains("hasIchorBottle");
            case 8:
                return player.getTags().contains("hasAstralFruit");
            case 9:
                return BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enigmaticEye) != -1;
            case 10:
                return !isScroll;
            case 11:
                return !isSpellstone;
            case 12:
                return /*!(stack.getItem() instanceof ItemSpellstoneBauble)*/ false;
        }

        return ((IBaublesItemHandler) this.getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
    }
}
