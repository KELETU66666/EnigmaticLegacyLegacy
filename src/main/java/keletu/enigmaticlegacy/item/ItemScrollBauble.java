package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.util.interfaces.IScroll;
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
        return BaublesApi.isBaubleEquipped((EntityPlayer) player, this) == -1;
    }
}
