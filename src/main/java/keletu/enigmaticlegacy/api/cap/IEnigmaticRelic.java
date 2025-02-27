package keletu.enigmaticlegacy.api.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IEnigmaticRelic {
    public EnigmaticRelicType getSlot(ItemStack stack);
    public boolean canEquip(ItemStack stack, EntityLivingBase player);

    public void onRelicTick(EntityPlayer player, ItemStack stack);

    public void onRelicEquipped(EntityPlayer player, ItemStack stack);
    public void onRelicUnequipped(EntityPlayer player, ItemStack stack);
}
