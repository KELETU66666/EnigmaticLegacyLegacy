package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.event.ELEvents;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemEarthHeart extends ItemBase {

    public ItemEarthHeart() {
        super("earth_heart", EnumRarity.UNCOMMON);
        this.setHasSubtypes(true);
        this.maxStackSize = 1;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && !entityIn.world.isRemote) {
            if (hasCursed((EntityPlayer) entityIn))
                this.setDamage(stack, 1);
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this.getDamage(stack) != 0) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.tainted1"));
        }
    }
}