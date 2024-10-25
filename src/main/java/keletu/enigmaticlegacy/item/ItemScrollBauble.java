package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.util.IScroll;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemScrollBauble extends ItemBaseBauble implements IScroll {
    public ItemScrollBauble(String name, EnumRarity rare) {
        super(name, rare);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) player);

        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack equipped = baubles.getStackInSlot(slot);
            if (!equipped.isEmpty() && equipped.getItem() instanceof IScroll) {
                return false;
            }
        }

        return super.canEquip(itemstack, player);
    }
}
